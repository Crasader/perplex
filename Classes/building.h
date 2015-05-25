#ifndef __building_h__
#define __building_h__

#include "GameEntity.h"

class GameScene;
class BuildingRes;
class XUnitFactory;
class XDropTool;

class Building : public GameEntity
{
public:
	using VUF = std::vector<XUnitFactory> ;
	using VDT = std::vector<XDropTool> ;
	using VEP = std::vector < bool > ;
public:
	Building(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	static Building* create(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	bool  init(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	
	void perform();
	void processUnitFactory();
	void processDie();
	void processTool();
	int beAttack(const cocos2d::Rect &rect, int hp);
	int getBuildingHP() { return _HP; }
	int getBuildingID();
	void setUnitFactory(VUF uf);
	void setDropTool(VDT dt);
	bool isActive(){ return _active; }
	cocos2d::Rect getWalkRect() const { return _walkRect; }
	bool isCastoff();
private:
	//出兵
	bool _factory;
	bool _flipH;
	bool _enableHarm;//是否能够被伤害
	bool _active;
	byte _unitFactoryTick;
	byte _dieTick;
	int _buildingID;
	int _unitFactoryIndex;
	int _middleX;
	int _middleY;
	int _HP;//建筑的血量；如果可以半毁的话那么半毁的标准就是hp/2
	int _state;//建筑的状态 0 --- 完好 1 --- 半毁坏
	std::shared_ptr<BuildingRes> _buildingRes;
	GameScene* _gameScene;
	VUF _unitFactoryDatas;
	VDT _dropToolDatas;
	VEP _dieExplode;
	cocos2d::Rect _walkRect;
	cocos2d::Node _model;
};
#endif // __building_h__
