#include "shotlogicmanager.h"
#include "shotlogic.h"
#include "Unit.h"


ShotLogicManager::ShotLogicManager()
	:_shotLogics()
{
	
}

void ShotLogicManager::createShotLogic(Unit* unit, int type, int bulletID, int posIndex)
{
	std::shared_ptr<ShotLogic> shotLogic;
	switch (type)
	{
	case 1:
		shotLogic = std::shared_ptr<ShotLogicD>(new ShotLogicD(unit, 0, posIndex));
		break;
	default:
		break;
	}
	_shotLogics.push_back(shotLogic);
}

std::vector<std::shared_ptr<ShotLogic>>& ShotLogicManager::getShotLogics()
{
	return _shotLogics;
}

void ShotLogicManager::createDefaultLogic(Unit* unit, int weaponRes)
{
	auto shotLogic = std::shared_ptr<ShotLogicD>(new ShotLogicD(unit, weaponRes, 0));
	_shotLogics.push_back(shotLogic);
}
