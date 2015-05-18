#ifndef __UnitResManager_h__
#define __UnitResManager_h__

#include "cocos2d.h"
#include "UnitRes.h"

class UnitRes;
class Ifstream;

static const int UNIT_MAX_WEAPON_COUNT = 12;

class UnitResManager
{
public:
	UnitResManager(const std::string& file);

	std::shared_ptr<UnitRes> getUnitResFromID(int ID);
private:
	std::vector<std::shared_ptr<UnitRes>> _unitReses;
};
#endif // __UnitResManager_h__
