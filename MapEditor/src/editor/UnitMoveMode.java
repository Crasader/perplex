package editor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import jdk.internal.util.xml.impl.Input;


public class UnitMoveMode {
	public final static int STAND = 0;
	public final static int RANDOM = 1;
	public final static int PATH = 2;
	public final static int AUTO = 3;
	
	public final static int[] MODES = {
									  STAND, 
									  RANDOM, 
									  PATH, 
									  AUTO
	};
	
	public final static String[] MODE_NAMES = {
											  "站立不动", 
											  "随机移动",  
											  "沿着固定路径移动", 
											  "控制移动"
	};
	
	public final static IntPair getPoint(IntPair[] data) {
		IntPair result = null;
		if(data != null) {
			if(data.length >= 1) {
				result = data[0].getCopy();
			}
		}
		return result;
	}
	
	public final static UnitPath getPath(IntPair[] data) {
		UnitPath result = null;
		if(data != null) {
			if(data.length >= 2) {
				IntPair[] path = new IntPair[data.length - 1];
				for(int i = 1; i < data.length; ++i) {
					path[i - 1] = data[i].getCopy();
				}
				result = new UnitPath(path);
			}
		}
		return result;
	}
	
	public final static IntPair[] combine(IntPair point, UnitPath up) {
		IntPair[] path = null;
		if(up != null) {
			path = up.getPath();
		}
		return combine(point, path);
	}
	
	public final static IntPair[] combine(IntPair point, IntPair[] path) {
		IntPair[] result = null;
		int length = 0;
		if(point != null) {
			length += 1;
			if(path != null) {
				length += path.length;
			}
		}
		if(length > 0) {
			result = new IntPair[length];
			result[0] = point.getCopy();
			for(int i = 1; i < length; ++i) {
				result[i] = path[i - 1].getCopy();
			}
		}
		return result;
	}
	
	public final static RandomMove getRandomMove(IntPair[] data) {
		RandomMove result = new RandomMove();
		if(data != null) {
			int index = 0;
			if(data.length >= Dir.FULL_MOVE_DIRS.length / 2) {
				int[] dirProbs = new int[Dir.FULL_MOVE_DIRS.length];
				for(int i = 0; i < Dir.FULL_MOVE_DIRS.length / 2; ++i) {
					dirProbs[i * 2] = data[index].x;
					dirProbs[i * 2 + 1] = data[index].y;
					++index;
				}
				IntPair[] playerProbs = null;
				IntPair[] tickProbs = null;
				if(data.length > index) {
					int playerProbLength = data[index].x;
					int tickProbLength = data[index].y;
					++index;
					playerProbs = new IntPair[playerProbLength];
					tickProbs = new IntPair[tickProbLength];
					for(int i = 0; i < playerProbLength; ++i) {
						playerProbs[i] = data[index].getCopy();
						++index;
					}
					for(int i = 0; i < tickProbLength; ++i) {
						tickProbs[i] = data[index].getCopy();
						++index;
					}
				}
				result.setDirProbs(dirProbs);
				result.setPlayerProbs(playerProbs);
				result.setTickProbs(tickProbs);
			}
		}
		return result;
	}
	
	public final static IntPair[] combine(RandomMove rm) {
		IntPair[] result = null;
		if(rm != null) {
			int[] dirProbs = rm.getDirProbs();
			IntPair[] playerProbs = rm.getPlayerProbs();
			IntPair[] tickProbs = rm.getTickProbs();
			int length = Dir.FULL_MOVE_DIRS.length / 2;
			int playerProbLength = 0;
			int tickProbLength = 0;
			if(playerProbs != null) {
				playerProbLength = playerProbs.length;
				length += playerProbLength;
			}
			if(tickProbs != null) {
				tickProbLength = tickProbs.length;
				length += tickProbLength;
			}
			result = new IntPair[length + 1];
			int index = 0;
			for(int i = 0; i < Dir.FULL_MOVE_DIRS.length / 2; ++i) {
				result[index] = new IntPair(dirProbs[i * 2], dirProbs[i * 2 + 1]);
				++index;
			}
			result[index] = new IntPair(playerProbLength, tickProbLength);
			++index;
			if(playerProbs != null) {
				for(int i = 0; i < playerProbs.length; ++i) {
					result[index] = playerProbs[i].getCopy();
					++index;
				}
			}
			if(tickProbs != null) {
				for(int i = 0; i < tickProbs.length; ++i) {
					result[index] = tickProbs[i].getCopy();
					++index;
				}
			}
		}
		return result;
	}
	
	public final static String getDescription(UnitMoveMode um) {
		String result = "";
		if(um != null) {
			result = getDescription(um.getMode(), um.getData());
		}
		return result;
	}
	
	public final static String getDescription(int mode, IntPair[] data) {
		if(mode == STAND) {
			return "无";
		}
		if(mode == RANDOM || mode == AUTO) {
			return getRandomMove(data).getDescription();
		}
		
		if(mode == PATH) {
			return UnitPath.getPathDescription(getPath(data));
		}
		
		UnitPath up = getPath(data);
		
		
		return "";
	}
	
	private int mode;
	private IntPair[] data;
	
    public UnitMoveMode() {
		init();
    }
	
	private void init() {
		mode = STAND;
	}
	
	public UnitMoveMode getCopy() {
		UnitMoveMode result = new UnitMoveMode();
		result.copyFrom(this);
		return result;
	}
	
	private void copyFrom(UnitMoveMode dest) {
		this.mode = dest.mode;
		this.data = XUtil.copyArray(dest.data);
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public IntPair[] getData() {
		return XUtil.copyArray(data);
	}
	
	public void setData(IntPair[] data) {
		this.data = XUtil.copyArray(data);
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(mode, out);
		if (mode == PATH && data != null) {
			IntPair[] temp = new IntPair[ data.length];
			MapInfo info = MainFrame.self.getMapInfo();
			for (int i = 0; i < data.length; i++) {
				temp[i] = new IntPair();
				temp[i].x = info.changeToMobileX(data[i].x);
				temp[i].y = info.changeToMobileY(data[i].y,0);
			}
			SL.writeIntPairArrayMobile(temp, out);
		}
		else {
			SL.writeIntPairArrayMobile(data, out);
		}
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(mode);
		SL.writeIntPairArray(data, out);
	}

	public final static UnitMoveMode createViaFile(DataInputStream in) throws Exception {
		UnitMoveMode um = new UnitMoveMode();
		um.load(in);
		return um;
	}
	
	private void load(DataInputStream in) throws Exception {
		mode = in.readInt();
		data = SL.readIntPairArray(in);
	}
}

class UnitMoveModePanel extends JPanel {
	private JDialog owner;
	private UnitMoveMode um;
	private ValueChooser modeChooser;
	private ButtonText dataText;
	private IntPair[] data;
	
	public UnitMoveModePanel(JDialog owner, UnitMoveMode um) {
		super();
		this.owner = owner;
		init(um);
	}
	
	private void init(UnitMoveMode um) {
		this.um = um;
		modeChooser = new ValueChooser(um.getMode(), UnitMoveMode.MODES, UnitMoveMode.MODE_NAMES);
		modeChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modeChanged();
			}
		});
		dataText = new ButtonText(UnitMoveMode.getDescription(um));
		dataText.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setData();
			}
		});
		this.data = um.getData();
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 3, 3);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		this.add(new JLabel("选择移动模式："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		this.add(modeChooser, c);
		
		c.gridy = 1;
		c.gridx = 0;
		c.weightx = 0;
		this.add(new JLabel("设置移动数据："), c);
		
		c.gridx = 1;
		c.weightx = 1;
		this.add(dataText, c);
	}
	
	public void SetUM(UnitMoveMode um) {
		this.um = um;
		modeChooser.setValue(um.getMode());
		dataText.setValue(UnitMoveMode.getDescription(um));
		this.data = um.getData();
	}
	
	public void updateUM() {
		if(um != null) {
			um.setMode(modeChooser.getValue());
			um.setData(data);
		}
	}
	
	public UnitMoveMode getUM() {
		return um;
	}
	
	private void modeChanged() {
		
		int mode = modeChooser.getValue();
		if(mode == UnitMoveMode.RANDOM || mode == UnitMoveMode.AUTO) {
			RandomMove rm = UnitMoveMode.getRandomMove(data);
			data = UnitMoveMode.combine(rm);
		}
		else {
			data = null;
		}
		dataText.setValue(UnitMoveMode.getDescription(modeChooser.getValue(), data));
	}
	
	private void setData() {
		int mode = modeChooser.getValue();
		if(mode == UnitMoveMode.RANDOM || mode == UnitMoveMode.AUTO) {
			RandomMove rm = UnitMoveMode.getRandomMove(data);
			RandomMoveSetter setter = new RandomMoveSetter(owner, rm);
			setter.show();
			if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				data = UnitMoveMode.combine(rm);
			}
		}
		else if(mode == UnitMoveMode.PATH) {
			UnitPathSetter setter = new UnitPathSetter(owner, MainFrame.self, 
				UnitMoveMode.getPoint(data), UnitMoveMode.getPath(data));
			setter.show();
			if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				data = UnitMoveMode.combine(setter.getStartPoint(), setter.getUnitPath());
			}
		}
		dataText.setValue(UnitMoveMode.getDescription(mode, data));
	}
}














