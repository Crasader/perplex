#include "TriggerUnitProperty.h"
#include "GameScene.h"
#include "Unit.h"
#include "UnitManager.h"

bool TriggerUnitProperty::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	switch (_property)
	{
	case 0:
		if (_unit->getPower() >= _min && _unit->getPower() <= _max)
		{
			return true;
		}
		break;
	case 1:

		break;
	default:
		break;
	}
	return false;
}

TriggerUnitProperty::TriggerUnitProperty(EventManager* eventManager, GameScene* gameLayer, int unitID, int property, int min, int max)
:Trigger(eventManager, gameLayer, 4)
, _unitID(unitID)
, _property(property)
, _min(min)
, _max(max)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}
