#include "EffectUnitRoad.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"


bool EffectUnitRoad::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	_unit->setNewRoad(_roads);
	return true;
}

EffectUnitRoad::EffectUnitRoad(EventManager* eventManager, GameScene* gameLayer, int unitId, std::vector<cocos2d::Vec2> roads) 
:Effect(eventManager, gameLayer, 4)
, _unitID(unitId)
, _roads(roads)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
