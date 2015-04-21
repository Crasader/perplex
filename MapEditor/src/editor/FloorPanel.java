/*******************************************************************************
 * Copyright (C)   Inc.All Rights Reserved.
 * FileName: FloorPanel.java 
 * Description:
 * History:  
 * 版本号 作者 日期 简要介绍相关操作 
 * 1.0 cole  2015年4月21日 上午9:59:23
 *******************************************************************************/
package editor;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JFrame;


class FloorManager
	extends SISpriteManager {
	
	private Color color;
	
	public FloorManager(ScrollablePanel scrollablePanel, MouseInfo mouseInfo) {
		super(scrollablePanel, mouseInfo);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		scrollablePanel.repaint();
	}
	
	public void paintStatic(Graphics g) {
		if(color != null) {
			Color oldColor = g.getColor();
			g.setColor(color);
			g.fillRect(0, 0, scrollablePanel.getBasicWidth(), scrollablePanel.getBasicHeight());
			g.setColor(oldColor);
		}
		super.paintStatic(g);
	}
	
	public void paintSprites(Graphics g) {
		if(color != null) {
			Color oldColor = g.getColor();
			g.setColor(color);
			g.fillRect(0, 0, scrollablePanel.getBasicWidth(), scrollablePanel.getBasicHeight());
			g.setColor(oldColor);
		}
		super.paintSprites(g);
	}
}

class FloorPanel
	extends SISpritePanel {

	public FloorPanel(JFrame owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	public FloorPanel(JDialog owner, MouseInfo mouseInfo) {
		super(owner, mouseInfo);
	}

	protected SpriteManager createManager() {
		return new FloorManager(this, mouseInfo);
	}
}
