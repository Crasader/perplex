#include "TriggerBuildingHP.h"
#include "building.h"
#include "GameScene.h"
#include "UnitManager.h"

bool TriggerBuildingHP::perform()
{
	if (_building)
	{
		return false;
	}
	if (_building->getBuildingHP() >= _minHP && _building->getBuildingHP() <= _maxHP)
	{
		return true;
	}
	return false;
}

TriggerBuildingHP::TriggerBuildingHP(EventManager* eventManager, GameScene* gameLayer, int buildingID, int minHP, int maxHP) :Trigger(eventManager, gameLayer, 5)
, _buidingID(buildingID)
, _minHP(minHP)
, _maxHP(maxHP)
, _building(nullptr)
{
	_building = _gameLayer->getSpriteManager()->findBuildingFromID(_buidingID);
}
