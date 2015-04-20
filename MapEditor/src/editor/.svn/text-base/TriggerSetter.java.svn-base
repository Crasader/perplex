package editor;

//should modify

import javax.swing.*;

/**
 ´¥·¢Æ÷ÉèÖÃ´°¿Ú¡£
 */
public abstract class TriggerSetter
	extends OKCancelDialog {
	protected Trigger trigger;
	protected MainFrame mainFrame;

	public TriggerSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		super(owner);
		init(mainFrame, trigger);
	}

	public TriggerSetter(JFrame owner, MainFrame mainFrame, Trigger trigger) {
		super(owner);
		init(mainFrame, trigger);
	}

	private void init(MainFrame mainFrame, Trigger trigger) {
		this.trigger = trigger;
		this.mainFrame = mainFrame;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void show() {
		if (getTrigger() == null) {
			return;
		}
		super.show();
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public static TriggerSetter createSetter(JDialog owner, MainFrame mainFrame, Trigger trigger) {
		if (owner == null || trigger == null) {
			return null;
		}
		TriggerSetter result = null;
		switch (trigger.getType()) {
			case Trigger.SWITCH:
				result = new SwitchTriggerSetter(owner, mainFrame, trigger);
				break;
			case Trigger.RANDOM:
				result = new RandomSetter(owner, mainFrame, trigger);
				break;
			case Trigger.UNIT_IN_MAP_AREA:
				result = new UnitInMapAreaSetter(owner, mainFrame, trigger);
				break;
			case Trigger.COUNTER:
				result = new CounterTriggerSetter(owner, mainFrame, trigger);
				break;
			case Trigger.UNIT_HP:
				result = new UnitPropTriggerSetter(owner, mainFrame, trigger);
				break;
			case Trigger.BUILDING_HP:
				result = new BuildingPropTriggerSetter(owner, mainFrame, trigger);
				break;
			default:
				result = null;
				break;
		}
		return result;
	}
}
