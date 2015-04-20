package editor;

import java.awt.*;
import javax.swing.*;

class StateCheckBox
	extends JPanel
	implements ListCellRenderer {
	public final static int CHECK_NONE = 0;
	private final static int CHECK_OK = 1;

	private class CheckRect
		extends JPanel {
		private final static int LEFT_GAP = 1;
		private final static int TOP_GAP = 1;

		public CheckRect() {
			super();
			setBackground(Color.WHITE);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int left = LEFT_GAP,
					   top = TOP_GAP,
							 width = getHeight() - LEFT_GAP * 2,
									 height = getHeight() - TOP_GAP * 2;

			Color oldColor = g.getColor();
			g.setColor(Color.WHITE);
			g.fillRect(left, top, width, height);
			g.setColor(new Color(28, 81, 128));
			g.drawRect(left, top, width - 1, height - 1);
			paintCheckState(g, left, top, width, height);
			g.setColor(oldColor);
		}
	}

	private int checkState;
	private CheckRect checkRect;
	private JLabel textLabel;
	private DefaultListCellRenderer defaultRenderer;

	public StateCheckBox() {
		init(CHECK_NONE, "");
	}

	private void init(int checkState, String text) {
		this.checkState = checkState;
		defaultRenderer = new DefaultListCellRenderer();
		setLayout(new BorderLayout(2, 2));
		checkRect = new CheckRect();
		checkRect.setPreferredSize(new Dimension(18, 18));
		add(checkRect, BorderLayout.WEST);

		textLabel = new JLabel(text);
		add(textLabel, BorderLayout.CENTER);

		setBackground(Color.WHITE);
	}

	public int getNextCheckState(int checkState) {
		switch (checkState) {
			case CHECK_NONE:
				return CHECK_OK;
			case CHECK_OK:
				return CHECK_NONE;
		}
		return CHECK_NONE;
	}

	public int getCheckState() {
		return checkState;
	}

	public void setCheckState(int checkState) {
		this.checkState = checkState;
		checkRect.repaint();
	}

	public String getText() {
		return textLabel.getText();
	}

	public void setText(String text) {
		textLabel.setText(text);
	}

	public void setBackground(Color color) {
		super.setBackground(color);
		if (textLabel != null) {
			textLabel.setBackground(color);
		}
		if (checkRect != null) {
			checkRect.setBackground(color);
		}
	}

	public boolean isInCheckRect(int x, int y) {
		return checkRect.contains(x, y);
	}

	protected void paintCheckState(Graphics g, int left, int top, int width, int height) {
		switch (checkState) {
			case CHECK_OK:
				g.setColor(new Color(167, 33, 33));
				g.fillRect(left + 2, top + height / 2 - 1, width - 4, 2);
				g.fillRect(left + width / 2 - 1, top + 2, 2, height - 4);
				break;
		}
	}

	public Component getListCellRendererComponent(JList list, Object value,
												  int index, boolean isSelected,
												  boolean cellHasFocus) {
		Component co = defaultRenderer.getListCellRendererComponent(list, value, index,
			isSelected, cellHasFocus);
		if (co == null || value == null) {
			return co;
		}
		if (! (value instanceof Pair)) {
			return co;
		}
		Pair pair = (Pair) value;
		setText(pair.second.toString());
		setCheckState( ( (Integer) (pair.first)).intValue());
		setBackground(co.getBackground());
		setForeground(co.getForeground());
		return this;
	}
}
//
//public class AnimCheckList extends JList {
//	private DefaultListModel model;
//	private StateCheckBox renderer;
//	private ARManager arManager;
//
//	public AnimCheckList(ARManager arManager) {
//		super();
//		init(null);
//	}
//
//	public AnimCheckList(Animation[] animIDs, ARManager arManager;) {
//		super();
//		this.arManager = arManager;
//		init(states);
//	}
//
//	private void init(int[] states) {
//		initModel();
//		setModel(model);
//		setDragEnabled(false);
//		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		renderer = new StateCheckBox();
//		setCellRenderer(renderer);
//
//		addMouseListener(new MouseAdapter() {
//
////			public void mousePressed(MouseEvent e) {
////				selfMouseReleased(e);
////			}
//
//			public void mouseReleased(MouseEvent e) {
//				selfMouseReleased(e);
//			}
//		});
//
//		setStates(states);
//	}
//
//	public void initModel() {
//		model = new DefaultListModel();
//		for(int i = 0;i < BattleUtil.STATES.length;++i) {
//			model.addElement(new IntPair(BattleUtil.STATES[i],StateCheckBox.CHECK_NONE));
//		}
//	}
//
//	public void setStates(int[] states) {
//		for(int i = 0;i < BattleUtil.STATES.length;++i) {
//			boolean hasState = false;
//			int state = 0;
//			if(states != null) {
//				for(int j = 0;j < states.length;++j) {
//					if(BattleUtil.STATES[i] == Math.abs(states[j])) {
//						hasState = true;
//						state = states[j];
//						break;
//					}
//				}
//			}
//
//			model.set(i,
//					new IntPair(
//						BattleUtil.STATES[i]
//						,hasState ?
//							(state > 0 ?
//								StateCheckBox.CHECK_PLUS
//								: StateCheckBox.CHECK_MINUS)
//							: StateCheckBox.CHECK_NONE
//						)
//			);
//		}
//	}
//
//	public int[] getStates() {
//		ArrayList tmp = new ArrayList();
//		for(int i = 0;i < BattleUtil.STATES.length;++i) {
//			IntPair value = (IntPair)(model.get(i));
//			switch(value.y) {
//			case StateCheckBox.CHECK_PLUS:
//				tmp.add(new Integer(value.x));
//				break;
//			case StateCheckBox.CHECK_MINUS:
//				tmp.add(new Integer(-value.x));
//				break;
//			case StateCheckBox.CHECK_NONE:
//				break;
//			}
//		}
//
//		if(tmp.isEmpty()) return null;
//		int[] result = new int[tmp.size()];
//		for(int i = 0;i < result.length;++i) {
//			result[i] = ((Integer)(tmp.get(i))).intValue();
//		}
//		return result;
//	}
//
//	private void selfMouseReleased(MouseEvent e) {
//		if(e.getButton() != MouseInfo.LEFT_BUTTON) return;
//
//		int index = getSelectedIndex();
//		Rectangle cellBounds = getCellBounds(index,index);
//		if(cellBounds == null) return;
//		int x = e.getX(),y = e.getY();
//		if(!cellBounds.contains(x,y)) return;
//		x -= cellBounds.x;y -= cellBounds.y;
//
//		Object value = model.get(index);
//		if(value == null) return;
//		if(!(value instanceof IntPair)) return;
//		IntPair pair = (IntPair)value;
//
//		if(renderer.isInCheckRect(x,y)) {
//			pair.y = StateCheckBox.getNextCheckState(pair.y);
//			repaint(cellBounds.x,cellBounds.y,cellBounds.width,cellBounds.height);
//		}
//	}
//}