#ifndef __shotlogicmanager_h__
#define __shotlogicmanager_h__


#include <vector>
#include <memory>

class ShotLogic;
class Unit;

class ShotLogicManager
{
public:
	ShotLogicManager(Unit* unit, int type, int bulletID, int posIndex);
	std::vector<std::shared_ptr<ShotLogic>> getShotLogics() const;
	void createDefaultLogic(Unit* unit, int weaponRes);
private:
	std::vector<std::shared_ptr<ShotLogic>> _shotLogics;
};

#endif // __shotlogicmanager_h__
