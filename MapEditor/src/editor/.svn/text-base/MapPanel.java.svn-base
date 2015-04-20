package editor;

import java.io.*;

import java.awt.*;
import javax.swing.*;

/**
 用以在非主窗口中显示地图的基本控件，其中的内容默认是不能修改的。
 */

public class MapPanel
	extends ScrollablePanel {
	private final static int[] FLOOR_LAYERS = {MainFrame.LAYER_FLOOR};
	protected MainFrame mainFrame;
	protected SpriteManager[] floors;
	protected Unit[] units;
	protected Building[] buildings;
	protected SpriteManager manager;
	private SpriteInfoPanel infoPanel;
	private JPanel backPanel;

	public MapPanel(JDialog owner, MainFrame mainFrame) {
		super(mainFrame.getMapInfo().getWidth(), mainFrame.getMapInfo().getHeight());
		init(mainFrame);
	}

	private void init(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		SpriteManagerPanel[] panels = mainFrame.getPanels();

		floors = new SpriteManager[FLOOR_LAYERS.length];
		for (int i = 0; i < FLOOR_LAYERS.length; ++i) {
			floors[i] = panels[FLOOR_LAYERS[i]].getManager();
		}

		Sprite[] sprites = panels[MainFrame.LAYER_UNIT].getManager().getSprites();
		if (sprites != null) {
			units = new Unit[sprites.length];
			for (int i = 0; i < sprites.length; ++i) {
				units[i] = ( (Unit) (sprites[i])).getCopy();
			}
		}
		
		sprites = panels[MainFrame.LAYER_BUILDING].getManager().getSprites();
		if(sprites != null) {
			buildings = new Building[sprites.length];
			for(int i = 0; i < sprites.length; ++i) {
				buildings[i] = ((Building)(sprites[i])).getCopy();
			}
		}

		manager = new SpriteManager(this, new MouseInfo()) {
			public void saveMobileData(DataOutputStream out, Object[] resManagers) {};
			public void save(DataOutputStream out, Object[] resManagers) {};
			public void load(DataInputStream in, Object[] resManagers) {};
		};
		manager.setCopyable(false);
		manager.setDeleteable(false);
		manager.setLayerable(false);
		manager.setMoveable(false);
		manager.setNewspriteMode(SpriteManager.NONE_NEWSPRITE);
		manager.setSelectionMode(SpriteManager.NONE_SELECTION);
		manager.setUndoable(false);

		infoPanel = new SpriteInfoPanel(false, true, true);
		infoPanel.setScrollablePanel(this);
		infoPanel.setSpriteManager(manager);

		backPanel = new JPanel();
		backPanel.setLayout(new BorderLayout());
		backPanel.add(super.getBackPanel(), BorderLayout.CENTER);
		backPanel.add(infoPanel, BorderLayout.SOUTH);
	}

	public JPanel getBackPanel() {
		return backPanel;
	}

	public SpriteManager getManager() {
		return manager;
	}

	protected void paintFloors(Graphics g) {
		for (int i = 0; i < floors.length; ++i) {
			floors[i].paintStatic(g);
		}
	}

	protected void paintUnits(Graphics g) {
		if (units != null) {
			for (int i = 0; i < units.length; ++i) {
				units[i].paintIdle(g);
			}
		}
	}
	
	protected void paintBuildings(Graphics g) {
		if(buildings != null) {
			for(int i = 0; i < buildings.length; ++i) {
			    buildings[i].paintIdle(g);
			}
		}
	}
}