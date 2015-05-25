#include "EffectUnitAppear.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"


bool EffectUnitAppear::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	if (_show)
	{
		_unit->setPosition(_x, _y);
		_unit->setVisible(_show);
	}
	else
	{
		_unit->setVisible(_show);
	}
	return true;
}

EffectUnitAppear::EffectUnitAppear(EventManager* eventManager, GameScene* gameLayer, int unitID, int x, int y, bool show)
:Effect(eventManager, gameLayer, 4)
, _unitID(unitID)
, _x(x)
, _y(y)
, _show(show)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
