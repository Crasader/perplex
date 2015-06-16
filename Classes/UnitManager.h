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
class Explosion;

class UnitManager
{
public:
	UnitManager(GameScene* gameScene);
	Unit* createUnit(int unitID, int type, int x, int y, int moveType, int dir, int campType);
	Unit* createRedHeader(int unitID, int type, int x, int y, int moveType, int dir, int campType);
	Unit* findUnitFromID(int unitID);
	Building* findBuildingFromID(int buildingID);
	Tool* createTool(const char* name, int x, int y, int type);
	Building* createBuilding(int buildType, int BuildiID, int x, int y, int state, bool fliph);
	Bullet* createBullet(std::shared_ptr<WeaponRes> weaponRes, const cocos2d::Vec2& pos, const cocos2d::Vec2& move, int dir, int campType);
	void processDropItem(Unit* unit);
	Unit* createDefaultUnit(int type, int x, int y);
	Unit* spawnEnemy(int unitID, int type, int x, int y, int moveType, int dir, int campType);
	Unit* spawnEnemy(int type);
	Unit* getOrCreate(GameScene* gameScene, int unitID, int type, int dir, int campType);
	Explosion* createExplode(Unit* unit, int type, const cocos2d::Vec2& pos, std::function<void()> callback);
public:
	static cocos2d::Vector<Tool*> _tools;
	static cocos2d::Vector<Building*> _buildings;
	static cocos2d::Vector<GameEntity*> _sprites;
	static cocos2d::Vector<GameEntity*> _sortSprites;
	static cocos2d::Vector<Unit*> _allys;
	static cocos2d::Vector<Unit*> _enemies;
	static cocos2d::Vector<Bullet*> _enemyBullets;
	static cocos2d::Vector<Bullet*> _allyBullets;
	static cocos2d::Vector<Explosion*> _explodes;
	static cocos2d::Vector<GameEntity*> _explodeReals;
private:
	GameScene* _gameScene;
};
#endif // __spritemanager_h__
