#include "TriggerUnitToRect.h"
#include "GameScene.h"
#include "Unit.h"
#include "UnitManager.h"
#include "CameraExt.h"

bool TriggerUnitToRect::perform()
{
	if (_unit == nullptr)
	{
		return false;
	}
	auto p = _unit->getPositionInCamera();
	if (_rect.containsPoint(p))
	{
		return true;
	}
	return false;
}

TriggerUnitToRect::~TriggerUnitToRect()
{

}

TriggerUnitToRect::TriggerUnitToRect(EventManager* eventManager, GameScene* gameLayer, int unitID, cocos2d::Rect rect) :Trigger(eventManager, gameLayer, 3)
, _unitID(unitID)
, _rect(rect)
, _unit(nullptr)
{
	if (_unitID == -1)
	{
		return;
	}
	_unit = _gameLayer->getSpriteManager()->findUnitFromID(_unitID);
	if (_unit != nullptr)
	{
		_unit->setEventUnit(true);
	}
}
