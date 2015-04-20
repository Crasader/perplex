package editor;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

interface Painter {
	public int getGroupID();

	public int getID();

	public int getImageWidth();

	public int getImageHeight();

	public String getName();

	public void setSelected(boolean selected);

	public void paintLeftTop(Graphics g, int left, int top);

	public void paintOrigin(Graphics g, int originX, int originY);
}

interface PainterGroup {
	public int getID();

	public String getName();

	public void setSelected(boolean selected);
}

abstract class PainterPanel
	extends JPanel
	implements Painter {
	private final static int LEFT_GAP = 0;
	private final static int RIGHT_GAP = 0;
	private final static int TOP_GAP = 0;
	private final static int BOTTOM_GAP = 5;

	private final static int IMAGE_BORDER_LEFT = 2;
	private final static int IMAGE_BORDER_RIGHT = 2;
	private final static int IMAGE_BORDER_TOP = 2;
	private final static int IMAGE_BORDER_BOTTOM = 2;

	private final static int NAME_BORDER_LEFT = 2;
	private final static int NAME_BORDER_RIGHT = 2;
	private final static int NAME_BORDER_TOP = 2;
	private final static int NAME_BORDER_BOTTOM = 2;

	protected boolean selected;

	private int imageLeft, imageTop,
		nameLeft, nameBottom;

	public PainterPanel() {
		selected = false;
	}

	protected void computeSize() {
		BufferedImage tmpImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmpImage.createGraphics();

		Rectangle2D rect = MainFrame.DEF_FONT.getStringBounds(getName(), g2.getFontRenderContext());

		imageLeft = nameLeft = LEFT_GAP;
		imageTop = TOP_GAP;

		int nameWidth = (int) (rect.getWidth() + NAME_BORDER_LEFT + NAME_BORDER_RIGHT);
//		int nameHeight = (int)(rect.getHeight() + NAME_BORDER_TOP + NAME_BORDER_BOTTOM);
		int nameHeight = (int) (MainFrame.DEF_FONT.getSize() + NAME_BORDER_TOP + NAME_BORDER_BOTTOM);
		int imageWidth = (int) (getImageWidth() + IMAGE_BORDER_LEFT + IMAGE_BORDER_RIGHT);
		int imageHeight = (int) (getImageHeight() + IMAGE_BORDER_TOP + IMAGE_BORDER_BOTTOM);
		int width = Math.max(imageWidth, nameWidth);
		int height = imageHeight + nameHeight;

		nameBottom = TOP_GAP + height;

		this.setPreferredSize(new Dimension(width + LEFT_GAP + RIGHT_GAP,
											height + TOP_GAP + BOTTOM_GAP));
		this.setSize(getPreferredSize());
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color oldColor = g.getColor();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		paintLeftTop(g, imageLeft + IMAGE_BORDER_LEFT, imageTop + IMAGE_BORDER_TOP);
		if (selected) {
			paintImageBorder(g, getBorderColor());
		}

		g.setColor(Color.BLACK);
		g.drawString(getName(), nameLeft + NAME_BORDER_LEFT, nameBottom - NAME_BORDER_BOTTOM);

		g.setColor(oldColor);
	}

	protected Color getBorderColor() {
		return Color.BLUE;
	}

	protected void paintImageBorder(Graphics g, Color c) {
		Color oldColor = g.getColor();
		g.setColor(c);

		g.fillRect(imageLeft, imageTop,
				   getImageWidth() + IMAGE_BORDER_LEFT + IMAGE_BORDER_RIGHT, IMAGE_BORDER_TOP);
		g.fillRect(imageLeft, imageTop,
				   IMAGE_BORDER_LEFT, getImageHeight() + IMAGE_BORDER_TOP + IMAGE_BORDER_BOTTOM);
		g.fillRect(imageLeft + getImageWidth() + IMAGE_BORDER_LEFT, imageTop,
				   IMAGE_BORDER_RIGHT, getImageHeight() + IMAGE_BORDER_TOP + IMAGE_BORDER_BOTTOM);
		g.fillRect(imageLeft, imageTop + getImageHeight() + IMAGE_BORDER_TOP,
				   getImageWidth() + IMAGE_BORDER_LEFT + IMAGE_BORDER_RIGHT, IMAGE_BORDER_BOTTOM);

		g.setColor(oldColor);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		repaint();
	}

	public String toString() {
		return getName();
	}
}

class PainterTreeNode
	extends DefaultMutableTreeNode {
	private Painter painter;

	public PainterTreeNode(Painter painter) {
		super(painter);
		this.painter = painter;
	}

	public Painter getPainter() {
		return painter;
	}

	public void setSelected(boolean selected) {
		painter.setSelected(selected);
	}
}

class PainterGroupTreeNode
	extends DefaultMutableTreeNode {
	private PainterGroup painterGroup;

	public PainterGroupTreeNode(PainterGroup painterGroup) {
		this.painterGroup = painterGroup;
	}

	public PainterGroup getPainterGroup() {
		return painterGroup;
	}

	public void setSelected(boolean selected) {
		painterGroup.setSelected(selected);
	}

	public String toString() {
		return painterGroup.getName();
	}
}

class PainterTreeRenderer
	implements TreeCellRenderer {
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	public Component getTreeCellRendererComponent(JTree tree, Object value,
												  boolean selected, boolean expanded,
												  boolean leaf, int row, boolean hasFocus) {

		if (value != null) {
			if (value instanceof PainterTreeNode) {
				Painter painter = ( (PainterTreeNode) value).getPainter();
				if (painter != null) {
					if (painter instanceof PainterPanel) {
						return (PainterPanel) painter;
					}
				}
			}
		}

		return defaultRenderer.getTreeCellRendererComponent(tree, value,
			selected, expanded,
			leaf, row, hasFocus);
	}
}

class PainterTree
	extends JTree {
	private String name;
	protected DefaultTreeModel model;
	protected DefaultMutableTreeNode root;
	private JScrollPane scrollPane;
	private MouseInfo mouseInfo;

	public PainterTree(String name, PainterGroup[] groups, Painter[] painters) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(name)));
		init(name, groups, painters, null);
	}

	public PainterTree(String name, PainterGroup[] groups, Painter[] painters, MouseInfo mouseInfo) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(name)));
		init(name, groups, painters, mouseInfo);
	}

	private void init(String name, PainterGroup[] groups, Painter[] painters, MouseInfo mouseInfo) {
		this.mouseInfo = mouseInfo;
		initProp(name);
		initNodes(groups, painters);
		expandRow(0);
	}

	private void initProp(String name) {
		this.name = name;
		model = (DefaultTreeModel) getModel();
		root = (DefaultMutableTreeNode) (model.getRoot());
		scrollPane = new JScrollPane(this);
		SwingUtil.setDefScrollIncrement(scrollPane);
		scrollPane.setName(name);
		setCellRenderer(new PainterTreeRenderer());
		setEditable(false);
		setDragEnabled(false);
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(selectionModel);
		addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectionChanged(e);
			}
		});
		if (mouseInfo != null) {
			mouseInfo.addListener(new MouseInfoAdapter() {
				public void resetAll() {
					clearSelection();
				}
			});
		}
	}

	private void initNodes(PainterGroup[] groups, Painter[] painters) {
		PainterGroupTreeNode[] groupNodes = null;
		if (groups != null) {
			groupNodes = new PainterGroupTreeNode[groups.length];
			for (int i = 0; i < groups.length; ++i) {
				groupNodes[i] = new PainterGroupTreeNode(groups[i]);
				model.insertNodeInto(groupNodes[i], root, i);
			}
		}
		if (painters != null) {
			for (int i = 0; i < painters.length; ++i) {
				PainterTreeNode painterNode = new PainterTreeNode(painters[i]);
				boolean added = false;
				if (groupNodes != null) {
					for (int j = 0; j < groupNodes.length; ++j) {
						if (groupNodes[j].getPainterGroup().getID() == painters[i].getGroupID()) {
							model.insertNodeInto(painterNode, groupNodes[j],
												 groupNodes[j].getChildCount());
							added = true;
							break;
						}
					}
				}
				if (!added) {
					model.insertNodeInto(painterNode, root, root.getChildCount());
				}
			}
		}
	}

	private void selectionChanged(TreeSelectionEvent e) {
		TreePath oldPath = e.getOldLeadSelectionPath();
		TreePath newPath = e.getNewLeadSelectionPath();
		Object treeNode;

		if (oldPath != null) {
			unselectOldPath(oldPath);
			treeNode = oldPath.getLastPathComponent();
			if (treeNode instanceof PainterGroupTreeNode) {
				( (PainterGroupTreeNode) treeNode).setSelected(false);
				unselectOldGroup( (PainterGroupTreeNode) treeNode);
			}
			else if (treeNode instanceof PainterTreeNode) {
				( (PainterTreeNode) treeNode).setSelected(false);
				unselectOldPainter( (PainterTreeNode) treeNode);
			}
		}

		if (newPath != null) {
			selectNewPath(newPath);
			treeNode = newPath.getLastPathComponent();
			if (treeNode instanceof PainterGroupTreeNode) {
				( (PainterGroupTreeNode) treeNode).setSelected(true);
				selectNewGroup( (PainterGroupTreeNode) treeNode);
			}
			else if (treeNode instanceof PainterTreeNode) {
				( (PainterTreeNode) treeNode).setSelected(true);
				selectNewPainter( (PainterTreeNode) treeNode);
			}
		}
	}

	protected void unselectOldPath(TreePath oldPath) {
	}

	protected void unselectOldGroup(PainterGroupTreeNode node) {
	}

	protected void unselectOldPainter(PainterTreeNode node) {
	}

	protected void selectNewPath(TreePath newPath) {
		if (mouseInfo != null) {
			mouseInfo.resetSelf();
		}
	}

	protected void selectNewGroup(PainterGroupTreeNode newGroup) {
	}

	protected void selectNewPainter(PainterTreeNode newPainter) {
		if (mouseInfo != null) {
			mouseInfo.setInfo(MouseInfo.NEW_SPRITE);
			mouseInfo.setPainter(newPainter.getPainter());
		}
	}

	public String getName() {
		return name;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void refresh(PainterGroup[] groups, Painter[] painters) {
		mouseInfo.resetAll();
		while (root.getChildCount() > 0) {
			TreeNode node = root.getChildAt(0);
			if (node instanceof MutableTreeNode) {
				model.removeNodeFromParent( (MutableTreeNode) node);
			}
		}
		initNodes(groups, painters);
		expandRow(0);
	}
}
