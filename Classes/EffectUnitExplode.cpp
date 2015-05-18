#include "EffectUnitExplode.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"


bool EffectUnitExplode::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	_unit->beAttack(cocos2d::Rect(_unit->getPositionX() - 64, _unit->getPositionY() - 64, 64, 64), _unit->getPower() + 10);
	return true;
}

EffectUnitExplode::EffectUnitExplode(EventManager* eventManager, GameScene* gameLayer, int unitID)
:Effect(eventManager, gameLayer, 7)
, _unitID(unitID)
, _unit(nullptr)
{
	_unit = gameLayer->getSpriteManager()->findUnitFromID(unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
