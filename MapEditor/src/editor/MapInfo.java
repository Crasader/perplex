package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class MapInfo {
	private int width;
	private int height;
	private String name;
	private Color color;
	private boolean colorInit;
	private int realLeft;
	private int realWidth;
	private int realTop;
	private int realHeight;
	
	public MapInfo(int width, int height, String name) {
		this.width = width;
		this.height = height;
		this.name = name;
		this.color = Color.WHITE;
		colorInit = false;
		realLeft = 0;
		realWidth = width;
		realTop = 0;
		realHeight = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRealLeft() {
		return this.realLeft;
	}

	public void setRealLeft(int realLeft) {
		this.realLeft = realLeft;
	}

	public int getRealWidth() {
		return this.realWidth;
	}

	public void setRealWidth(int realWidth) {
		this.realWidth = realWidth;
	}
	
	public int getRealTop() {
		return this.realTop;
	}

	public void setRealTop(int realTop) {
		this.realTop = realTop;
	}

	public int getRealHeight() {
		return this.realHeight;
	}

	public void setRealHeight(int realHeight) {
		this.realHeight = realHeight;
	}
	
	public void saveMobileData() throws Exception {
		File f = new File(XUtil.getDefPropStr("MapInfoFilePath") + "\\" + name + "_MapInfo_Mobile.dat");
		DataOutputStream out = 
				  new DataOutputStream(
						new BufferedOutputStream(
							  new FileOutputStream(f)));
		SL.writeInt(realWidth, out);
		SL.writeInt(realHeight, out);
		SL.writeInt(color.getRGB(), out);
		out.flush();
		out.close();
	}

	public void save() throws Exception {
		File f = new File(XUtil.getDefPropStr("MapInfoFilePath") + "\\" + name + "_MapInfo.dat");
		DataOutputStream out = 
				  new DataOutputStream(
						new BufferedOutputStream(
							  new FileOutputStream(f)));
		out.writeInt(width);
		out.writeInt(height);
		out.writeInt(color.getRGB());
		out.writeInt(realLeft);
		out.writeInt(realWidth);
		out.writeInt(realTop);
		out.writeInt(realHeight);
		out.flush();
		out.close();
	}

	public void load(String name) throws Exception {
		this.name = name;
		File f = new File(XUtil.getDefPropStr("MapInfoFilePath") + "\\" + name + "_MapInfo.dat");
		if(!f.exists()) return;
		DataInputStream in = 
				  new DataInputStream(
						new BufferedInputStream(
							  new FileInputStream(f)));
		width = in.readInt();
		height = in.readInt();
		color = new Color(in.readInt(), true);
		colorInit = true;
		realLeft = in.readInt();
		realWidth = in.readInt();
		realTop = in.readInt();
		realHeight = in.readInt();
		in.close();
	}
	
	public void paintBorder(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(Color.WHITE);
		g.drawRect(realLeft - 1, realTop - 1, realWidth + 1, realHeight + 1);
		g.setColor(oldColor);
	}
	
	public void paintBackground(Graphics g) {
		if (colorInit && color != null) {
			Color oldColor = g.getColor();
			g.setColor(color);
			g.fillRect(realLeft, realTop, realWidth, realHeight);
			g.setColor(oldColor);
		}
	}
}

class NewMapDialog extends OKCancelDialog {
	private JTextField nameText;
	private NumberSpinner widthSpinner;
	private NumberSpinner heightSpinner;
	private JButton colorButton;
	private JColorChooser colorChooser;
	private NumberSpinner realLeftSpinner;
	private NumberSpinner realWidthSpinner;
	private NumberSpinner realTopSpinner;
	private NumberSpinner realHeightSpinner;
	
	public NewMapDialog(JFrame owner) {
		super(owner);
		init();
	}

	private void init() {
		nameText = new JTextField(XUtil.getDefPropStr("DefMapName"));
		widthSpinner = new NumberSpinner();
		widthSpinner.setIntValue(XUtil.getDefPropInt("DefMapWidth"));
		heightSpinner = new NumberSpinner();
		heightSpinner.setIntValue(XUtil.getDefPropInt("DefMapHeight"));
		colorButton = new JButton("设置颜色");
		colorButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				chooseColor();
			}
		});
		realLeftSpinner = new NumberSpinner();
		realLeftSpinner.setIntValue(XUtil.getDefPropInt("RealMapLeft"));
		realWidthSpinner = new NumberSpinner();
		realWidthSpinner.setIntValue(XUtil.getDefPropInt("RealMapWidth"));
		realTopSpinner = new NumberSpinner();
		realTopSpinner.setIntValue(XUtil.getDefPropInt("RealMapTop"));
		realHeightSpinner = new NumberSpinner();
		realHeightSpinner.setIntValue(XUtil.getDefPropInt("RealMapHeight"));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		centerPanel.add(new JLabel("名称："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(nameText, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		centerPanel.add(new JLabel("宽："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(widthSpinner, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		centerPanel.add(new JLabel("高："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(heightSpinner, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		centerPanel.add(new JLabel("背景色："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(colorButton, c);

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		centerPanel.add(new JLabel("左边空隙："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(realLeftSpinner, c);

		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0;
		centerPanel.add(new JLabel("实际宽度："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(realWidthSpinner, c);

		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0;
		centerPanel.add(new JLabel("上边空隙："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(realTopSpinner, c);

		c.gridx = 0;
		c.gridy = 7;
		c.weightx = 0;
		centerPanel.add(new JLabel("实际高度："), c);

		c.gridx = 1;
		c.weightx = 1;
		centerPanel.add(realHeightSpinner, c);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(centerPanel, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void chooseColor() {
		Color color = JColorChooser.showDialog(this, "选择颜色", colorButton.getBackground());
		if (color != null) {
			colorButton.setBackground(color);
		}
	}

	public void setMapInfo(MapInfo mapInfo) {
		colorButton.setBackground(mapInfo.getColor());
		nameText.setText(mapInfo.getName());
		widthSpinner.setIntValue(mapInfo.getWidth());
		heightSpinner.setIntValue(mapInfo.getHeight());
		realLeftSpinner.setIntValue(mapInfo.getRealLeft());
		realWidthSpinner.setIntValue(mapInfo.getRealWidth());
		realTopSpinner.setIntValue(mapInfo.getRealTop());
		realHeightSpinner.setIntValue(mapInfo.getRealHeight());
	}
	
	public void updateMapInfo(MapInfo mapInfo) {
		if(mapInfo == null) return;
		mapInfo.setName(nameText.getText());
		mapInfo.setColor(colorButton.getBackground());
		mapInfo.setWidth(widthSpinner.getIntValue());
		mapInfo.setHeight(heightSpinner.getIntValue());
		mapInfo.setRealLeft(realLeftSpinner.getIntValue());
		mapInfo.setRealWidth(realWidthSpinner.getIntValue());
		mapInfo.setRealTop(realTopSpinner.getIntValue());
		mapInfo.setRealHeight(realHeightSpinner.getIntValue());
	}
	
	public String getName() {
		return nameText.getText();
	}

	public int getWidth() {
		return widthSpinner.getIntValue();
	}

	public int getHeight() {
		return heightSpinner.getIntValue();
	}

	public Color getColor() {
		return colorButton.getBackground();
	}

	public void okPerformed() {
		closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
