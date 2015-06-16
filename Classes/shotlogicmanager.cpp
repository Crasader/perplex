#include "shotlogicmanager.h"
#include "shotlogic.h"
#include "Unit.h"

ShotLogicManager::ShotLogicManager()
	:_shotLogics()
{
	
}

void ShotLogicManager::createShotLogic(Unit* parent, cocos2d::Node* shot, int type, int bulletID, int posIndex)
{
	std::shared_ptr<ShotLogic> shotLogic;
	switch (type)
	{
	case 1:
		shotLogic = std::shared_ptr<ShotLogicD>(new ShotLogicD(parent, shot, 0, posIndex));
		break;
	case 2:
		shotLogic = std::shared_ptr<ShotLogicB>(new ShotLogicB(parent, shot, 0, posIndex, 1));
		break;
	case 3:
		shotLogic = std::shared_ptr<ShotLogicB>(new ShotLogicB(parent, shot, 0, posIndex, 2));
		break;
	case 4:
		shotLogic = std::shared_ptr<ShotLogicB>(new ShotLogicB(parent, shot, 0, posIndex, 8));
		break;
	case 5:
		shotLogic = std::shared_ptr<ShotLogicE>(new ShotLogicE(parent, shot, 0, posIndex, false));
		break;
	case 6:
		shotLogic = std::shared_ptr<ShotLogicF>(new ShotLogicF(parent, shot, 0, posIndex, 1, false));
		break;
	case 7:
		shotLogic = std::shared_ptr<ShotLogicF>(new ShotLogicF(parent, shot, 0, posIndex, 2, false));
		break;
	case 8:
		shotLogic = std::shared_ptr<ShotLogicH>(new ShotLogicH(parent, shot, 0, posIndex));
		break;
	case 9:
		shotLogic = std::shared_ptr<ShotLogicI>(new ShotLogicI(parent, shot, 0, posIndex));
		break;
	case 10:
		shotLogic = std::shared_ptr<ShotLogicA>(new ShotLogicA(parent, shot, 0, posIndex));
		break;
	case 11://单发(指定方向)
		shotLogic = std::shared_ptr<ShotLogicC>(new ShotLogicC(parent, shot, 0, posIndex));
		break;
	case 12://双发(指定方向)
		shotLogic = std::shared_ptr<ShotLogicG>(new ShotLogicG(parent, shot, 0, posIndex));
		break;
	case 13:
		shotLogic = std::shared_ptr<ShotLogicE>(new ShotLogicE(parent, shot, 0, posIndex, true));
		break;
	case 14:
		shotLogic = std::shared_ptr<ShotLogicF>(new ShotLogicF(parent, shot, 0, posIndex, 1, true));
		break;
	case 15:
		shotLogic = std::shared_ptr<ShotLogicF>(new ShotLogicF(parent, shot, 0, posIndex, 2, true));
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
	if (unit == nullptr)
	{
		return;
	}

	auto shotLogic = std::shared_ptr<ShotLogicD>(new ShotLogicD(unit, unit, weaponRes, 0));
	_shotLogics.push_back(shotLogic);
}
