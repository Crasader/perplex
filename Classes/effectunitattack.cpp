#include "effectunitattack.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"


bool EffectUnitAttack::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	_unit->fireRequire(_times, _weaponType);
	return true;
}

EffectUnitAttack::EffectUnitAttack(EventManager* eventmanager, GameScene* gamelayer, int unitID, int weaponType, int times)
:Effect(eventmanager, gamelayer, 6)
, _unitID(unitID)
, _weaponType(weaponType)
, _times(times)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
