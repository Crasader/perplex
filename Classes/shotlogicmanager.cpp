#include "shotlogicmanager.h"
#include "shotlogic.h"
#include "Unit.h"


ShotLogicManager::ShotLogicManager(Unit* unit, int type, int bulletID, int posIndex)
{
	std::shared_ptr<ShotLogic> shotLogic;
	switch (type)
	{
	case 1:
		shotLogic = std::make_shared<ShotLogicD>(ShotLogicD(unit, 0, posIndex));
		break;
	default:
		break;
	}
	_shotLogics.push_back(shotLogic);
}

std::vector<std::shared_ptr<ShotLogic>> ShotLogicManager::getShotLogics() const
{
	return _shotLogics;
}

void ShotLogicManager::createDefaultLogic(Unit* unit, int weaponRes)
{
	auto shotLogic = std::make_shared<ShotLogicD>(ShotLogicD(unit, weaponRes, 0));
	_shotLogics.push_back(shotLogic);
}
