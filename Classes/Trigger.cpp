#include "Trigger.h"
#include "EventManager.h"
#include "GameScene.h"


bool Trigger::perform()
{
	return true;
}

Trigger::~Trigger()
{

}

Trigger::Trigger(EventManager* eventManger, GameScene* gameLayer, int type)
:_eventManager(eventManger)
, _gameLayer(gameLayer)
, _type(type)
{

}
