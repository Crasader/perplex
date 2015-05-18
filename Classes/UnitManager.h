#ifndef __spritemanager_h__
#define __spritemanager_h__

#include "cocos2d.h"

class Tool;
class Building;
class Explode;
class ExplodeReal;
class Bullet;
class Unit;
class WeaponRes;
class GameScene;
class GameEntity;

class UnitManager
{
public:
	UnitManager(GameScene* gameScene);
	Unit* createUnit(int unitID, int type, int x, int y, int moveType, int dir, int campType);
	Unit* createRedHeader(int unitID, int type, int x, int y, int moveType, int dir, int campType);
	Unit* findUnitFromID(int unitID);
	Building* findBuildingFromID(int buildingID);
	Tool* createTool(int appearAnimaID, int generalAnimID, int disappearAnimID, int x, int y, int type);
	Building* createBuilding(int buildType, int BuildiID, int x, int y, int state, bool fliph);
	Bullet* createBullet(std::shared_ptr<WeaponRes> weaponRes, int x, int y, int moveX, int moveY, int dir, int campType);
	void processDropItem(Unit* unit);
	Unit* createDefaultUnit(int type, int x, int y);
public:
	static std::vector<GameEntity*> _tools;
	static std::vector<GameEntity*> _buildings;
	static std::vector<GameEntity*> _sprites;
	static std::vector<GameEntity*> _sortSprites;
	static std::vector<GameEntity*> _allys;
	static std::vector<GameEntity*> _enemies;
	static std::vector<GameEntity*> _enemyBullets;
	static std::vector<GameEntity*> _allyBullets;
	static std::vector<Explode*> _explodes;
	static std::vector<GameEntity*> _explodeReals;
private:
	GameScene* _gameScene;
};
#endif // __spritemanager_h__
