#include "effectunitproperty.h"
#include "GameScene.h"
#include "Unit.h"
#include "UnitManager.h"

bool EffectUnitProperty::perform()
{
	if (_unit == nullptr)
		return false;

	int temPro;
	temPro = _unit->getPower();

	switch (_operate) {
	case 5:
		temPro = _count;
		break;

	case 6:
		temPro += _count;
		break;

	case 7:
		temPro -= _count;
		break;
	}

	_unit->setPower(temPro);

	return true;
}

EffectUnitProperty::EffectUnitProperty(EventManager* eventManager, GameScene* gameLayer, int unitID, int property, int operate, int count) :Effect(eventManager, gameLayer, 9)
, _unitID(unitID)
, _property(property)
, _operate(operate)
, _count(count)
, _unit(nullptr)
{
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit)
	{
		_unit->setEventUnit(true);
	}
}

