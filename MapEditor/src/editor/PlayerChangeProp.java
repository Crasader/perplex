package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

class PlayerReceiveKey
	extends Operation {
	private boolean receive;

	public PlayerReceiveKey(int id) {
		super(id, PLAYER_RECEIVE_KEY);
		init();
	}

	public PlayerReceiveKey(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		receive = true;
	}
	
	public boolean isReceive() {
		return receive;
	}
	
	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(receive ? 1 : 0, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeBoolean(receive);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		receive = in.readBoolean();
	}

	public String getListItemDescription() {
		String result = "设置主角";
		if(receive) {
			result += "能够";
		}
		else {
			result += "不能";
		}
		result += "接收按键";
		return result;
	}

	public Operation getCopy() {
		PlayerReceiveKey result = (PlayerReceiveKey) (Operation.createInstance(this.id, this.type));
		result.receive = this.receive;
		return result;
	}
}

class PlayerReceiveKeySetter
	extends OperationSetter {

	private PlayerReceiveKey playerReceiveKey;
	private RadioPanel radioPanel;

	public PlayerReceiveKeySetter(JDialog owner, MainFrame mainFrame, PlayerReceiveKey playerReceiveKey) {
		super(owner, mainFrame);
		this.setTitle("设置主角能否接收按键");
		this.playerReceiveKey = playerReceiveKey;
		this.mainFrame = mainFrame;
		
		radioPanel = new RadioPanel(RadioPanel.DESCS_CAN);
		radioPanel.setValue(playerReceiveKey.isReceive());
		
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
		return playerReceiveKey;
	}

	public void okPerformed() {
		playerReceiveKey.setReceive(radioPanel.getBoolValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}

class PlayerOutScreen
	extends Operation {
	private boolean mustIn;

	public PlayerOutScreen(int id) {
		super(id, PLAYER_OUT_SCREEN);
		init();
	}

	public PlayerOutScreen(int id, int type) {
		super(id, type);
		init();
	}

	private void init() {
		mustIn = true;
	}

	public boolean isMustIn() {
		return mustIn;
	}

	public void setMustIn(boolean mustIn) {
		this.mustIn = mustIn;
	}

	public void saveMobileData(DataOutputStream out, StringManager stringManager) throws Exception {
		super.saveMobileData(out, stringManager);
		SL.writeInt(mustIn ? 1 : 0, out);
	}

	public void save(DataOutputStream out, StringManager stringManager) throws Exception {
		super.save(out, stringManager);
		out.writeBoolean(mustIn);
	}

	protected void load(DataInputStream in, StringManager stringManager) throws Exception {
		super.load(in, stringManager);
		mustIn = in.readBoolean();
	}

	public String getListItemDescription() {
		String result = "设置主角";
		if(mustIn) {
			result += "必须";
		}
		else {
			result += "非必须";
		}
		result += "位于屏幕内";
		return result;
	}

	public Operation getCopy() {
		PlayerOutScreen result = (PlayerOutScreen) (Operation.createInstance(this.id, this.type));
		result.mustIn = this.mustIn;
		return result;
	}
}

class PlayerOutScreenSetter
	extends OperationSetter {

	private PlayerOutScreen playerOutScreen;
	RadioPanel radioPanel;

	public PlayerOutScreenSetter(JDialog owner, MainFrame mainFrame, PlayerOutScreen playerOutScreen) {
		super(owner, mainFrame);
		this.setTitle("设置主角是否必须位于屏幕");
		this.playerOutScreen = playerOutScreen;
		this.mainFrame = mainFrame;

		radioPanel = new RadioPanel(RadioPanel.DESCS_YES);
		radioPanel.setValue(playerOutScreen.isMustIn());

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
		return playerOutScreen;
	}

	public void okPerformed() {
		playerOutScreen.setMustIn(radioPanel.getBoolValue());
		this.closeType = OK_PERFORMED;
		dispose();
	}

	public void cancelPerformed() {
		dispose();
	}
}
