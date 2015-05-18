#ifndef __weaponresmanager_h__
#define __weaponresmanager_h__

#include "cocos2d.h"

class WeaponRes;

class WeaponResManager
{
public:
	WeaponResManager();
	void loadRes();
private:
	std::vector<std::shared_ptr<WeaponRes>> _weaponResList;
};
#endif // __weaponresmanager_h__
