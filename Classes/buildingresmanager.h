#ifndef __buildingresmanager_h__
#define __buildingresmanager_h__

#include "cocos2d.h"
#include "Ifstream.h"
#include "buildingres.h"
#include "explode.h"



class BuildingResManager
{
public:
	BuildingResManager(const std::string& res);
	std::shared_ptr<BuildingRes> getBuildingResFromID(int ID);
private:
	void loadRes(const std::string& res);
private:
	std::vector< std::shared_ptr<BuildingRes>> _buildingResList;
};

#endif // __buildingresmanager_h__
