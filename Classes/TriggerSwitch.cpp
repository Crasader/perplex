#include "TriggerSwitch.h"
#include "EventManager.h"

bool TriggerSwitch::perform()
{
	_eventManager->setSwitch(_switchID, _set);
	return true;
}

TriggerSwitch::TriggerSwitch(EventManager* eventManager, GameScene* gameLayer, int switchID, bool set) 
:Trigger(eventManager, gameLayer, 0)
, _switchID(switchID)
, _set(set)
{

}