#include "effectchagnemap.h"
#include "GameScene.h"



bool EffectChangeMap::perform()
{
	_gameLayer->changeMapByEvent(_state, _section);
	return true;
}

EffectChangeMap::EffectChangeMap(EventManager* eventManager, GameScene* gameLayer, int stage, int section) :Effect(eventManager, gameLayer, 17)
, _state(stage)
, _section(section)
{

}
