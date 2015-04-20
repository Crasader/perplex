package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

class ColorChooseButton extends JButton {	
	private JColorChooser colorChooser;
	
	public ColorChooseButton() {
		init(Color.BLACK);
	}
	
	public ColorChooseButton(Color color) {
		init(color);
	}
	
	private void init(Color color) {
		setBackground(color);
		colorChooser = new JColorChooser();
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseColor();
			}
		});
	}
	
	public void setBackground(Color color) {
		super.setBackground(color);
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		setForeground(new Color(0xFF - r, 0xFF - g, 0xFF - b));
		setText("当前" + Event.getColorDescription(color) + "，单击设置颜色");
	}
	
	private void chooseColor() {
		Color color = colorChooser.showDialog(this, "选择颜色", this.getBackground());
		if(color != null) {
			setBackground(color);
		}
	}
	
	public void setColor(Color color) {
		setBackground(color);
	}
	
	public Color getColor() {
		return this.getBackground();
	}
}

class CameraFadeIn
	extends Operation {

	private Color color;
	private int x;
	private int y;

	public CameraFadeIn(int id) {
		super(id, CAMERA_FADE_IN);
		init();
	}

	public CameraFadeIn(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		color = Color.BLACK;
		x = 0;
		y = 0;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRGB());
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(x, out);
		SL.writeInt(y, out);
		SL.writeInt(Event.getColorValue(color), out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(color.getRGB());
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		x = in.readInt();
		y = in.readInt();
		int rgb = in.readInt();
		color = new Color(rgb);
	}

	public String getListItemDescription() {
		String result = "镜头以" + Event.getColorDescription(color) +  
						"淡入到" + Event.getPointDescription(new int[] {x, y});
		return result;
	}

	public Operation getCopy() {
		CameraFadeIn result = (CameraFadeIn) (Operation.createInstance(this.id, this.type));
		result.color = new Color(this.color.getRGB());
		result.x = this.x;
		result.y = this.y;
		return result;
	}
}

class CameraFadeInSetter
	extends OperationSetter {

	private CameraFadeIn cameraFadeIn;
	private ColorChooseButton colorButton;
	private MapPointSetPanel pointPanel;

	public CameraFadeInSetter(JDialog owner, MainFrame mainFrame, CameraFadeIn cameraFadeIn) {
		super(owner, mainFrame);
		this.setTitle("设置屏幕淡入");
		this.cameraFadeIn = cameraFadeIn;
		this.mainFrame = mainFrame;

		colorButton = new ColorChooseButton(cameraFadeIn.getColor());

		pointPanel = new MapPointSetPanel(this, mainFrame, cameraFadeIn.getX(), cameraFadeIn.getY());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(colorButton, c);

		c.gridy = 1;
		cp.add(pointPanel, c);

		c.gridy = 2;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraFadeIn;
	}

	public void okPerformed() {
		cameraFadeIn.setColor(colorButton.getColor());
		cameraFadeIn.setX(pointPanel.getPointX());
		cameraFadeIn.setY(pointPanel.getPointY());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}


class CameraFadeOut
	extends Operation {

	private Color color;

	public CameraFadeOut(int id) {
		super(id, CAMERA_FADE_OUT);
		init();
	}

	public CameraFadeOut(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		color = Color.BLACK;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = new Color(color.getRGB());
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(Event.getColorValue(color), out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(color.getRGB());
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		int rgb = in.readInt();
		color = new Color(rgb);
	}

	public String getListItemDescription() {
		String result = "镜头以" + Event.getColorDescription(color) + "淡出";
		return result;
	}

	public Operation getCopy() {
		CameraFadeOut result = (CameraFadeOut) (Operation.createInstance(this.id, this.type));
		result.color = new Color(this.color.getRGB());
		return result;
	}
}

class CameraFadeOutSetter
	extends OperationSetter {

	private CameraFadeOut cameraFadeOut;
	private ColorChooseButton colorButton;

	public CameraFadeOutSetter(JDialog owner, MainFrame mainFrame, CameraFadeOut cameraFadeOut) {
		super(owner, mainFrame);
		this.setTitle("设置屏幕淡出");
		this.cameraFadeOut = cameraFadeOut;
		this.mainFrame = mainFrame;

		colorButton = new ColorChooseButton(cameraFadeOut.getColor());
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(colorButton, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraFadeOut;
	}

	public void okPerformed() {
		cameraFadeOut.setColor(colorButton.getColor());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class CameraMove
	extends Operation {

	private int x;
	private int y;

	public CameraMove(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		x = 0;
		y = 0;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(x, out);
		SL.writeInt(y, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(x);
		out.writeInt(y);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		x = in.readInt();
		y = in.readInt();
	}

	public String getListItemDescription() {
		String result = "镜头移动到" + Event.getPointDescription(new int[] {x, y});
		return result;
	}

	public Operation getCopy() {
		CameraMove result = (CameraMove) (Operation.createInstance(this.id, this.type));
		result.x = this.x;
		result.y = this.y;
		return result;
	}
}

class CameraMoveSetter
	extends OperationSetter {

	private CameraMove cameraMove;
	private MapPointSetPanel pointPanel;

	public CameraMoveSetter(JDialog owner, MainFrame mainFrame, CameraMove cameraMove) {
		super(owner, mainFrame);
		switch(cameraMove.getType()) {
		case Operation.CAMERA_MOVE:
		    this.setTitle("设置镜头移动");
			break;
		case Operation.CAMERA_INSTANT_MOVE:
			this.setTitle("设置镜头切换");
			break;
		}
		
		this.cameraMove = cameraMove;
		this.mainFrame = mainFrame;

		pointPanel = new MapPointSetPanel(this, mainFrame, cameraMove.getX(), cameraMove.getY());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(pointPanel, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraMove;
	}

	public void okPerformed() {
		cameraMove.setX(pointPanel.getPointX());
		cameraMove.setY(pointPanel.getPointY());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}



class CameraFocusUnit
	extends Operation {
	
	public final static int CAMERA_FREE = 0;
	public final static int CAMERA_CENTER = 1;
	
	public final static int[] CAMERA_TYPES = {
									  CAMERA_FREE, 
									  CAMERA_CENTER
	};
	
	public final static String[] CAMERA_DESCS = {
										 "自由", 
										 "居中"
	};
	
	public final String getCameraDesc(int cameraType) {
		String result = "未知";
		if(cameraType >= 0 && cameraType < CAMERA_DESCS.length) {
			result = CAMERA_DESCS[cameraType];
		}
		return result;
	}
	
	private int unitID;
	private int cameraType;

	public CameraFocusUnit(int id) {
		super(id, CAMERA_FOCUS_UNIT);
		init();
	}

	public CameraFocusUnit(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		unitID = UnitManager.PLAYER_ID;
		cameraType = CAMERA_FREE;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}
	
	public int getCameraType() {
		return cameraType;
	}
	
	public void setCameraType(int cameraType) {
		this.cameraType = cameraType;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(unitID, out);
		SL.writeInt(cameraType, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(unitID);
		out.writeInt(cameraType);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		unitID = in.readInt();
		cameraType = in.readInt();
	}

	public String getListItemDescription() {
		String result = "镜头跟踪" + Event.getUnitDescription(unitID) + 
						"（" + getCameraDesc(cameraType) + "）";
		return result;
	}

	public Operation getCopy() {
		CameraFocusUnit result = (CameraFocusUnit) (Operation.createInstance(this.id, this.type));
		result.unitID = this.unitID;
		result.cameraType = this.cameraType;
		return result;
	}
}

class CameraFocusUnitSetter
	extends OperationSetter {

	private CameraFocusUnit cameraFocusUnit;
	private UnitChoosePanel unitChooser;
	private RadioPanel radioPanel;

	public CameraFocusUnitSetter(JDialog owner, MainFrame mainFrame, CameraFocusUnit cameraFocusUnit) {
		super(owner, mainFrame);
		this.setTitle("设置镜头跟踪Unit");
		this.cameraFocusUnit = cameraFocusUnit;
		this.mainFrame = mainFrame;

		unitChooser = new UnitChoosePanel(this, mainFrame);
		unitChooser.setSelectedUnitID(cameraFocusUnit.getUnitID());
		radioPanel = new RadioPanel(CameraFocusUnit.CAMERA_TYPES, CameraFocusUnit.CAMERA_DESCS);
		radioPanel.setValue(cameraFocusUnit.getCameraType());
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(unitChooser, c);
		
		c.gridy = 1;
		cp.add(radioPanel, c);

		c.gridy = 2;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraFocusUnit;
	}

	public void okPerformed() {
		cameraFocusUnit.setUnitID(unitChooser.getSelectedUnitID());
		cameraFocusUnit.setCameraType(radioPanel.getIntValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class CameraShake
	extends Operation {

	private boolean white;

	public CameraShake(int id) {
		super(id, CAMERA_SHAKE);
		init();
	}

	public CameraShake(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		white = false;
	}

	public boolean isWhite() {
		return white;
	}

	public void setWhite(boolean white) {
		this.white = white;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(white ? 1 : 0, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeBoolean(white);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		white = in.readBoolean();
	}

	public String getListItemDescription() {
		String result = "震屏（" + (white ? "白屏" : "无白屏") + "）";
		return result;
	}

	public Operation getCopy() {
		CameraShake result = (CameraShake) (Operation.createInstance(this.id, this.type));
		result.white = this.white;
		return result;
	}
}

class CameraShakeSetter
	extends OperationSetter {

	private CameraShake cameraShake;
	private RadioPanel radioPanel;

	public CameraShakeSetter(JDialog owner, MainFrame mainFrame, CameraShake cameraShake) {
		super(owner, mainFrame);
		this.setTitle("设置震屏时是否白屏");
		this.cameraShake = cameraShake;
		this.mainFrame = mainFrame;

		radioPanel = new RadioPanel(RadioPanel.DESCS_YES);
		radioPanel.setValue(cameraShake.isWhite());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(radioPanel, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraShake;
	}

	public void okPerformed() {
		cameraShake.setWhite(radioPanel.getBoolValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}


class CameraRect
	extends Operation {
	
	private int left;
	private int top;
	private int width;
	private int height;

	public CameraRect(int id) {
		super(id, CAMERA_RECT);
		init();
	}

	public CameraRect(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		left = top = width = height = 0;
	}
	
	public int getLeft() {
		return left;
	}
	
	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getTop() {
		return top;
	}
	
	public void setTop(int top) {
		this.top = top;
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

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(left, out);
		SL.writeInt(top, out);
		SL.writeInt(width, out);
		SL.writeInt(height, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeInt(left);
		out.writeInt(top);
		out.writeInt(width);
		out.writeInt(height);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		left = in.readInt();
		top = in.readInt();
		width = in.readInt();
		height = in.readInt();
	}

	public String getListItemDescription() {
		String result = "设置镜头范围为" + Event.getAreaDescription(left, top, width, height);
		return result;
	}

	public Operation getCopy() {
		CameraRect result = (CameraRect) (Operation.createInstance(this.id, this.type));
		result.left = this.left;
		result.top = this.top;
		result.width = this.width;
		result.height = this.height;
		return result;
	}
}

class CameraRectSetter
	extends OperationSetter {

	private CameraRect cameraRect;
	private MapAreaSetPanel areaPanel;

	public CameraRectSetter(JDialog owner, MainFrame mainFrame, CameraRect cameraRect) {
		super(owner, mainFrame);
		this.setTitle("设置镜头范围");
		this.cameraRect = cameraRect;
		this.mainFrame = mainFrame;

		areaPanel = new MapAreaSetPanel(this, mainFrame, 
										cameraRect.getLeft(), cameraRect.getTop(), 
										cameraRect.getWidth(), cameraRect.getHeight());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(areaPanel, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraRect;
	}

	public void okPerformed() {
		cameraRect.setLeft(areaPanel.getAreaLeft());
		cameraRect.setTop(areaPanel.getAreaTop());
		cameraRect.setWidth(areaPanel.getAreaWidth());
		cameraRect.setHeight(areaPanel.getAreaHeight());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}


class CameraEffect
	extends Operation {

	private boolean open;

	public CameraEffect(int id) {
		super(id, CAMERA_EFFECT);
		init();
	}

	public CameraEffect(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		open = false;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(open ? 1 : 0, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeBoolean(open);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		open = in.readBoolean();
	}

	public String getListItemDescription() {
		String result = (open ? "打开" : "关闭") + "剧情效果";
		return result;
	}

	public Operation getCopy() {
		CameraEffect result = (CameraEffect) (Operation.createInstance(this.id, this.type));
		result.open = this.open;
		return result;
	}
}

class CameraEffectSetter
	extends OperationSetter {

	private CameraEffect cameraEffect;
	private RadioPanel radioPanel;

	public CameraEffectSetter(JDialog owner, MainFrame mainFrame, CameraEffect cameraEffect) {
		super(owner, mainFrame);
		this.setTitle("设置是否打开剧情效果");
		this.cameraEffect = cameraEffect;
		this.mainFrame = mainFrame;

		radioPanel = new RadioPanel(RadioPanel.DESCS_YES);
		radioPanel.setValue(cameraEffect.isOpen());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cp = this.getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		cp.add(radioPanel, c);

		c.gridy = 1;
		c.weighty = 0;
		cp.add(buttonPanel, c);
	}

	public Operation getOperation() {
		return cameraEffect;
	}

	public void okPerformed() {
		cameraEffect.setOpen(radioPanel.getBoolValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}




