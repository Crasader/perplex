#include "EffectSwitch.h"
#include "EventManager.h"



bool EffectSwitch::perform()
{
	_eventManager->setSwitch(_switchID, _set);
	return true;
}

EffectSwitch::EffectSwitch(EventManager* eventManager, GameScene* gameLayer, int switchID, bool set)
:Effect(eventManager, gameLayer, 0)
, _switchID(switchID)
, _set(set)
{

}
