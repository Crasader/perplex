#ifndef __shotlogicmanager_h__
#define __shotlogicmanager_h__


#include <vector>
#include <memory>
#include "cocos2d.h"

class ShotLogic;
class Unit;

class ShotLogicManager
{
public:
	ShotLogicManager();
	void createShotLogic(Unit* parent, cocos2d::Node* shot, int type, int bulletID, int posIndex);
	std::vector<std::shared_ptr<ShotLogic>>& getShotLogics();
	void createDefaultLogic(Unit* unit, int weaponRes);
private:
	std::vector<std::shared_ptr<ShotLogic>> _shotLogics;
};

#endif // __shotlogicmanager_h__
