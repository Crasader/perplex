#include "weaponresmanager.h"
#include "WeaponRes.h"


WeaponResManager::WeaponResManager()
{
	loadRes();
}

void WeaponResManager::loadRes()
{
	for (int i = 0; i < WeaponRes::MAX_WEAPONRES_COUNT; i++)
	{
		std::shared_ptr<WeaponRes> pWeaponRes = std::shared_ptr<WeaponRes>(new WeaponRes(i));
		_weaponResList.push_back(pWeaponRes);
	}
}
