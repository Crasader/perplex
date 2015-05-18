#include "Effect.h"

#include "EventManager.h"
#include "GameScene.h"


bool Effect::perform()
{
	return true;
}

int Effect::getType() const
{
	return _type;
}

Effect::Effect(EventManager* eventmanager, GameScene* gamelayer, int type) :_eventManager(eventmanager)
, _gameLayer(gamelayer)
, _type(type)
{

}
