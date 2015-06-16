package editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.swing.UIAction;

import com.sun.java_cup.internal.runtime.virtual_parse_stack;
import com.sun.xml.internal.bind.v2.model.core.ID;


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
	
//	public final static UnitPath getPath(IntPair[] data, int[] animaID) {
//		UnitPath result = null;
//		if(data != null) {
//			if(data.length >= 2) {
//				IntPair[] path = new IntPair[data.length - 1];
//				for(int i = 1; i < data.length; ++i) {
//					path[i - 1] = data[i].getCopy();
//				}
//				int[] id = null;
//				if (animaID != null) {
//					if (animaID.length >= 2) {
//						id = new int[animaID.length - 1];
//						for (int i = 1; i < animaID.length; ++i) {
//							id[i - 1] = animaID[i];
//						}
//					}
//				}
//				result = new UnitPath(path, id);
//			}
//		}
//		return result;
//	}
//	
//	public final static int[] combine(int a, UnitPath up)
//	{
//		int[] id = null;
//		if (up != null) {
//			id = up.getAnimaID();
//		}
//		return combine(a, id);
//	}
	
	public final static int[] combine(int a, int[] id) {
		int[] result = null;
		int length = 1;
		if(id != null) {
			length += id.length;
		}
		if(length > 0) {
			result = new int[length];
			result[0] = a;
			for(int i = 1; i < length; ++i) {
				result[i] = id[i - 1];
			}
		}
		return result;
	}
	
	public final static IntPair[] combine(IntPair point, UnitPath up) {
		IntPair[] path = null;
		if(up != null) {
			path = up.getData().getPath();
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
			result = getDescription(um.getMode(), um.pathData.getPath());
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
			return UnitPath.getPathDescription(data);
		}
		
		return "";
	}
	
	private int mode;
	private UnitPathData pathData;
	private int animaCount;
	
	public UnitPathData getData()
	{
		return pathData;
	}
	
	public void setData(UnitPathData data) {
		this.pathData = data;
	}
    public UnitMoveMode() {
    	pathData = new UnitPathData();
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
		this.animaCount = dest.animaCount;
		this.pathData = dest.pathData.getCopy();
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void setAnimaCount(int animaCount)
	{
		this.animaCount = animaCount;
	}
	
	public int getAnimaCount()
	{
		return animaCount;
	}
	
	public void saveMobileData(DataOutputStream out) throws Exception {
		SL.writeInt(mode, out);
		if (mode == PATH) {
			IntPair[] temp = null;
			if (pathData != null && pathData.getPath() != null) {
				temp = new IntPair[ pathData.getPath().length];
				MapInfo info = MainFrame.self.getMapInfo();
				for (int i = 0; i < pathData.getPath().length; i++) {
					temp[i] = new IntPair();
					temp[i].x = info.changeToMobileX(pathData.getPath()[i].x);
					temp[i].y = info.changeToMobileY(pathData.getPath()[i].y,0);
				}
			}
			SL.writeIntPairArrayMobile(temp, out);
			SL.writeIntArrayMobile(pathData.getAnimaID(), out);
			SL.writeIntArrayMobile(pathData.getSpeed(), out);
			SL.writeIntArrayMobile(pathData.getOrientation(), out);
			SL.writeIntArrayMobile(pathData.getDelay(), out);
		}
		else {
			SL.writeIntPairArrayMobile(pathData.getPath(), out);
		}
	}

	public void save(DataOutputStream out) throws Exception {
		out.writeInt(mode);
		SL.writeIntPairArray(pathData.getPath(), out);
		SL.writeIntArray(pathData.getAnimaID(), out);
		SL.writeIntArray(pathData.getSpeed(), out);
		SL.writeIntArray(pathData.getOrientation(), out);
		SL.writeIntArray(pathData.getDelay(), out);
	}

	public final static UnitMoveMode createViaFile(DataInputStream in) throws Exception {
		UnitMoveMode um = new UnitMoveMode();
		um.load(in);
		return um;
	}
	
	private void load(DataInputStream in) throws Exception {
		mode = in.readInt();
		pathData.setPath(SL.readIntPairArray(in));
		pathData.setAnimaID(SL.readIntArray(in)); 
		pathData.setSpeed(SL.readIntArray(in));
		pathData.setOrientation(SL.readIntArray(in));
		pathData.setDelay(SL.readIntArray(in));
	}
}

class UnitMoveModePanel extends JPanel {
	private JDialog owner;
	private UnitMoveMode um;
	private ValueChooser modeChooser;
	private ButtonText dataText;
	private UnitPathData data;
	private int animaCount;
	private IntPair origin;
	
	public UnitMoveModePanel(JDialog owner, UnitMoveMode um, int animaCount) {
		super();
		this.owner = owner;
		this.animaCount = animaCount;
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
		origin = data.getOrigin();
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
		this.animaCount = um.getAnimaCount();
	}
	
	public void updateUM() {
		if(um != null) {
			um.setMode(modeChooser.getValue());
			um.setData(data);
			um.setAnimaCount(animaCount);
		}
	}
	
	public UnitMoveMode getUM() {
		return um;
	}
	
	private void modeChanged() {
		data.clear();
		int mode = modeChooser.getValue();
		if(mode == UnitMoveMode.RANDOM || mode == UnitMoveMode.AUTO) {
			RandomMove rm = UnitMoveMode.getRandomMove(data.getPath());
			data.setPath(UnitMoveMode.combine(rm));
		}
		else {
			
		}
		dataText.setValue(UnitMoveMode.getDescription(modeChooser.getValue(), data.getPath()));
	}
	
	private void setData() {
		int mode = modeChooser.getValue();
		if(mode == UnitMoveMode.RANDOM || mode == UnitMoveMode.AUTO) {
			RandomMove rm = UnitMoveMode.getRandomMove(data.getPath());
			RandomMoveSetter setter = new RandomMoveSetter(owner, rm);
			setter.show();
			if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				data.setPath(UnitMoveMode.combine(rm));
			}
		}
		else if(mode == UnitMoveMode.PATH) {
			if(data != null) data.addOrigin(origin);
			UnitPathSetter setter = new UnitPathSetter(owner, MainFrame.self, 
				null, new UnitPath(data), animaCount);
			setter.show();
			if(setter.getCloseType() == OKCancelDialog.OK_PERFORMED) {
				
				data = setter.getUnitPath().getData();
			}
		
			if(data != null)data.removeOrigin();
		}
		dataText.setValue(UnitMoveMode.getDescription(mode, data.getPath()));
	}
}














