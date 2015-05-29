#ifndef __weaponresmanager_h__
#define __weaponresmanager_h__

#include "cocos2d.h"

class WeaponRes;

class WeaponResManager
{
public:
	WeaponResManager();
	void loadRes();
	const std::vector<std::shared_ptr<WeaponRes>>& getWeaponResList() const { return _weaponResList; }
	std::shared_ptr<WeaponRes> getWeapResFromID(int id);
private:
	std::vector<std::shared_ptr<WeaponRes>> _weaponResList;
};
#endif // __weaponresmanager_h__
