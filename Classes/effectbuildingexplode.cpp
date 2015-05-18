#include "effectbuildingexplode.h"
#include "GameScene.h"
#include "building.h"
#include "UnitManager.h"


bool EffectBuildingExplode::perform()
{
	if (_building == nullptr)
	{
		return false;
	}
	_building->beAttack(cocos2d::Rect(_building->getPositionX()-100, _building->getPositionY() - 100, 200, 200),
		_building->getBuildingHP() + 100);
	return true;
}

EffectBuildingExplode::EffectBuildingExplode(EventManager* eventManager, GameScene* gameLayer, int buildingID) 
:Effect(eventManager, gameLayer, 10)
, _buildingID(buildingID)
, _building(nullptr)
{
	_building = _gameLayer->getSpriteManager()->findBuildingFromID(_buildingID);
}
