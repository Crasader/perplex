package editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class RandomMove {
	private int[] dirProbs;
	private IntPair[] playerProbs;
	private IntPair[] tickProbs;
	
    public RandomMove() {
		init();
    }
	
	private void init() {
		dirProbs = new int[Dir.FULL_MOVE_DIRS.length];
		for(int i = 0; i < dirProbs.length; ++i) {
			dirProbs[i] = 0;
		}
		playerProbs = null;
		tickProbs = null;
	}
	
	public int[] getDirProbs() {
		return XUtil.copyArray(dirProbs);
	}
	
	public void setDirProbs(int[] dirProbs) {
		for(int i = 0; i < this.dirProbs.length; ++i) {
			this.dirProbs[i] = 0;
			if(dirProbs != null) {
				if(i < dirProbs.length) {
					this.dirProbs[i] = dirProbs[i];
				}
			}
		}
	}
	
	public IntPair[] getPlayerProbs() {
		return XUtil.copyArray(playerProbs);
	}
	
	public void setPlayerProbs(IntPair[] playerProbs) {
		this.playerProbs = XUtil.copyArray(playerProbs);
	}
	
	public IntPair[] getTickProbs() {
		return XUtil.copyArray(tickProbs);
	}
	
	public void setTickProbs(IntPair[] tickProbs) {
		this.tickProbs = XUtil.copyArray(tickProbs);
	}
	
	public String getDescription() {
		String result = "方向[";
		for(int i = 0; i < dirProbs.length; ++i) {
			result += Dir.DESCRIPTIONS[Dir.FULL_MOVE_DIRS[i]] + "：" + dirProbs[i] + "；";
		}
		result += "]    朝向Player[";
		if(playerProbs != null) {
			for(int i = 0; i < playerProbs.length; ++i) {
				result += "距离: " + playerProbs[i].x + ",概率：" + playerProbs[i].y + "；";
			}
		}
		result += "]    时间[";
		if(tickProbs != null) {
			for(int i = 0; i < tickProbs.length; ++i) {
				result += tickProbs[i].x + "Tick：" + tickProbs[i].y + "；";
			}
		}
		result += "]";
		return result;
	}
}

class RandomMoveSetter extends OKCancelDialog {
	private RandomMove rm;
	private DefaultTableModel dirModel;
	private JTable dirTable;
	private DefaultTableModel playerModel;
	private JTable playerTable;
	private DefaultTableModel tickModel;
	private JTable tickTable;
	
	public RandomMoveSetter(JDialog owner, RandomMove rm) {
		super(owner);
		init(rm);
	}
	
	private void init(RandomMove rm) {
		setTitle("设置随机移动的数据");
		this.rm = rm;
		
		//Dir Table
		dirModel = new DefaultTableModel();
		dirModel.addColumn("方向");
		dirModel.addColumn("概率");
		dirTable = new JTable(dirModel);
		dirTable.setRowSelectionAllowed(false);
		dirTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		TableColumnModel columnModel = dirTable.getColumnModel();
		
		//dirProb column
		TableColumn dirProbColumn = columnModel.getColumn(1);
		dirProbColumn.setCellRenderer(new SpinnerTableCellRenderer());
		dirProbColumn.setCellEditor(new SpinnerTableCellEditor());
		
		JScrollPane dirScroll = new JScrollPane(dirTable);
		SwingUtil.setDefScrollIncrement(dirScroll);
		
		JPanel dirPanel = new JPanel();
		dirPanel.setLayout(new BorderLayout());
		dirPanel.add(new JLabel("设置方向上的概率："), BorderLayout.NORTH);
		dirPanel.add(dirScroll, BorderLayout.CENTER);
		
		//Player Table
		playerModel = new DefaultTableModel();
		playerModel.addColumn("距离");
		playerModel.addColumn("概率");
		playerTable = new JTable(playerModel);
		playerTable.setRowSelectionAllowed(false);
		playerTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		columnModel = playerTable.getColumnModel();
		
		//player column
		TableColumn playerColumn = columnModel.getColumn(0);
		playerColumn.setCellRenderer(new SpinnerTableCellRenderer());
		playerColumn.setCellEditor(new SpinnerTableCellEditor());
		
		//playerProb column
		TableColumn playerProbColumn = columnModel.getColumn(1);
		playerProbColumn.setCellRenderer(new SpinnerTableCellRenderer());
		playerProbColumn.setCellEditor(new SpinnerTableCellEditor());

		JScrollPane playerScroll = new JScrollPane(playerTable);
		SwingUtil.setDefScrollIncrement(playerScroll);
		
		//Tick Table
		tickModel = new DefaultTableModel();
		tickModel.addColumn("Tick");
		tickModel.addColumn("概率");
		tickTable = new JTable(tickModel);
		tickTable.setRowSelectionAllowed(false);
		tickTable.setRowHeight(XUtil.getDefPropInt("UPTableRowHeight"));
		columnModel = tickTable.getColumnModel();
		
		//tick column
		TableColumn tickColumn = columnModel.getColumn(0);
		tickColumn.setCellRenderer(new SpinnerTableCellRenderer());
		tickColumn.setCellEditor(new SpinnerTableCellEditor());
		
		//tickProb column
		TableColumn tickProbColumn = columnModel.getColumn(1);
		tickProbColumn.setCellRenderer(new SpinnerTableCellRenderer());
		tickProbColumn.setCellEditor(new SpinnerTableCellEditor());

		JScrollPane tickScroll = new JScrollPane(tickTable);
		SwingUtil.setDefScrollIncrement(tickScroll);

		int[] dirProbs = rm.getDirProbs();	
		for(int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			dirModel.addRow(new Object[] {
							Dir.DESCRIPTIONS[Dir.FULL_MOVE_DIRS[i]], 
							new Integer(dirProbs[i])});
		}
		
		IntPair[] playerProbs = rm.getPlayerProbs();
		if(playerProbs != null) {
			for(int i = 0 ; i < playerProbs.length; ++i) {
				playerModel.addRow(new Object[]{
									new Integer(playerProbs[i].x), 
								 	new Integer(playerProbs[i].y)
				});
			}
		}
		
		IntPair[] tickProbs = rm.getTickProbs();
		if(tickProbs != null) {
			for(int i = 0 ; i < tickProbs.length; ++i) {
				tickModel.addRow(new Object[]{
								 new Integer(tickProbs[i].x), 
								 new Integer(tickProbs[i].y)
				});
			}
		}


		JButton addPlayerButton = new JButton("添加");
		addPlayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPlayer();
			}
		});

		JButton removePlayerButton = new JButton("删除");
		removePlayerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removePlayer();
			}
		});
		
		JPanel playerButtonPanel = new JPanel();
		playerButtonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		playerButtonPanel.add(addPlayerButton, c);
		c.gridx = 1;
		playerButtonPanel.add(removePlayerButton, c);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new BorderLayout());
		playerPanel.add(new JLabel("设置与Player的距离以及其走向Player的概率："), BorderLayout.NORTH);
		playerPanel.add(playerScroll, BorderLayout.CENTER);
		playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);


		JButton addTickButton = new JButton("添加");
		addTickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTick();
			}
		});

		JButton removeTickButton = new JButton("删除");
		removeTickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTick();
			}
		});
		
		JPanel tickButtonPanel = new JPanel();
		tickButtonPanel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		tickButtonPanel.add(addTickButton, c);
		c.gridx = 1;
		tickButtonPanel.add(removeTickButton, c);
		
		JPanel tickPanel = new JPanel();
		tickPanel.setLayout(new BorderLayout());
		tickPanel.add(new JLabel("设置移动时间以及其概率："), BorderLayout.NORTH);
		tickPanel.add(tickScroll, BorderLayout.CENTER);
		tickPanel.add(tickButtonPanel, BorderLayout.SOUTH);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 3, 10, 10));
		mainPanel.add(dirPanel);
		mainPanel.add(playerPanel);
		mainPanel.add(tickPanel);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addPlayer() {
		playerModel.addRow(new Object[] {
						  new Integer(0),
						  new Integer(0)
		});
	}
	
	private void removePlayer() {
		playerTableStopEditing();
		int row = playerTable.getSelectedRow();
		if (row >= 0 && row < playerModel.getRowCount()) {
			playerModel.removeRow(row);
		}
		if (row > playerModel.getRowCount() - 1) {
			row = playerModel.getRowCount() - 1;
		}
		if (row >= 0 && row < playerModel.getRowCount()) {
			playerTable.setRowSelectionInterval(row, row);
			playerTable.requestFocus();
		}
	}
	
	private void addTick() {
		tickModel.addRow(new Object[] {
						  new Integer(0),
						  new Integer(0)
		});
	}
	
	private void removeTick() {
		tickTableStopEditing();
		int row = tickTable.getSelectedRow();
		if (row >= 0 && row < tickModel.getRowCount()) {
			tickModel.removeRow(row);
		}
		if (row > tickModel.getRowCount() - 1) {
			row = tickModel.getRowCount() - 1;
		}
		if (row >= 0 && row < tickModel.getRowCount()) {
			tickTable.setRowSelectionInterval(row, row);
			tickTable.requestFocus();
		}
	}
	
	private void dirTableStopEditing() {
		TableCellEditor editor = dirTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	private void playerTableStopEditing() {
		TableCellEditor editor = playerTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	private void tickTableStopEditing() {
		TableCellEditor editor = tickTable.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	protected void okPerformed() {
		dirTableStopEditing();
		playerTableStopEditing();
		tickTableStopEditing();
		int[] dirProbs = new int[Dir.FULL_MOVE_DIRS.length];
		for(int i = 0; i < Dir.FULL_MOVE_DIRS.length; ++i) {
			dirProbs[i] = ((Integer)(dirModel.getValueAt(i, 1))).intValue();
		}
		ArrayList players = new ArrayList();
		for(int i = 0; i < playerModel.getRowCount(); ++i) {
			int player = ((Integer)(playerModel.getValueAt(i, 0))).intValue();
			int prob = ((Integer)(playerModel.getValueAt(i, 1))).intValue();
			if(player > 0 & prob > 0) {
				players.add(new IntPair(player, prob));
			}
		}
		IntPair[] playerProbs = null;
		if(players.size() > 0) {
			playerProbs = new IntPair[players.size()];
			for(int i = 0; i < players.size(); ++i) {
				playerProbs[i] = (IntPair)(players.get(i));
			}
		}
		
		ArrayList ticks = new ArrayList();
		for(int i = 0; i < tickModel.getRowCount(); ++i) {
			int tick = ((Integer)(tickModel.getValueAt(i, 0))).intValue();
			int prob = ((Integer)(tickModel.getValueAt(i, 1))).intValue();
			if(tick > 0 & prob > 0) {
				ticks.add(new IntPair(tick, prob));
			}
		}
		IntPair[] tickProbs = null;
		if(ticks.size() > 0) {
			tickProbs = new IntPair[ticks.size()];
			for(int i = 0; i < ticks.size(); ++i) {
				tickProbs[i] = (IntPair)(ticks.get(i));
			}
		}
		rm.setDirProbs(dirProbs);
		rm.setPlayerProbs(playerProbs);
		rm.setTickProbs(tickProbs);
		this.closeType = OK_PERFORMED;
		dispose();
	}
	
	protected void cancelPerformed() {
		dispose();
	}
}


















