#include "TriggerSwitch.h"
#include "EventManager.h"

bool TriggerSwitch::perform()
{
	if (_eventManager->getSwitch(_switchID) == _set)
	{
		return true;
	}
	return false;
}

TriggerSwitch::TriggerSwitch(EventManager* eventManager, GameScene* gameLayer, int switchID, bool set) 
:Trigger(eventManager, gameLayer, 0)
, _switchID(switchID)
, _set(set)
{

}