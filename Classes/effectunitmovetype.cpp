#include "effectunitmovetype.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"


bool EffectUnitMoveType::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	_unit->setAIType(_AIType);
	return true;
}

EffectUnitMoveType::EffectUnitMoveType(EventManager* eventManager, GameScene* gameLayer, int unitID, int aitype) :Effect(eventManager, gameLayer, 8)
, _unitID(unitID)
, _AIType(aitype)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	CC_SAFE_RETAIN(_unit);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
