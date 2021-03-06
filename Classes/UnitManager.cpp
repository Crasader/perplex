#include "UnitManager.h"
#include "GameScene.h"
#include "Unit.h"
#include "Bullets.h"
#include "Explosion.h"
#include "tool.h"
#include "building.h"
#include "buildingresmanager.h"
#include "building.h"
#include "dropitemdate.h"
#include "WeaponRes.h"
#include "Weapon.h"
#include "weaponresmanager.h"
#include "consts.h"
#include "GameEntity.h"
#include "Player.h"
#include "Fodder.h"
#include "Enemies.h"
#include "Explosion.h"
#include "tank.h"

cocos2d::Vector<Tool*> UnitManager::_tools;
cocos2d::Vector<Building*> UnitManager::_buildings;
cocos2d::Vector<GameEntity*> UnitManager::_sprites;
cocos2d::Vector<GameEntity*> UnitManager::_sortSprites;
cocos2d::Vector<Unit*> UnitManager::_allys;
cocos2d::Vector<Unit*> UnitManager::_enemies;
cocos2d::Vector<Bullet*> UnitManager::_enemyBullets;
cocos2d::Vector<Bullet*> UnitManager::_allyBullets;
cocos2d::Vector<Explosion*> UnitManager::_explodes;
cocos2d::Vector<GameEntity*> UnitManager::_explodeReals;

UnitManager::UnitManager(GameScene* gameScene)
:_gameScene(gameScene)
{

}

UnitManager::~UnitManager()
{
	clear();
}

Unit* UnitManager::createUnit(int unitID, int type, int x, int y, int moveType, int dir, int campType)
{
	Unit* pUnit = nullptr;
	switch (type)
	{
	case 0:
		pUnit = createRedHeader(unitID, type, x, y, moveType, dir, campType);
		_gameScene->addLevelEnemy(-1);
		break;
	case kEnemyFodder:
	case kEnemyTank:
		pUnit = spawnEnemy(unitID, type, x, y, moveType, dir, campType);
		_gameScene->addLevelEnemy(-1);
		break;
	default:
		break;
	}
	return pUnit;
}

Unit* UnitManager::createRedHeader(int unitID, int type, int x, int y, int moveType, int dir, int campType)
{
	auto unit = Player::create(_gameScene, unitID, type, dir, campType);
	assert(unit != nullptr);
	if (unit != nullptr)
	{
		unit->setPosition(Vec2(x, y));
		unit->setMoveType(moveType);
		unit->setCampType(Ally);
		unit->setLocalZOrder(kZOrderSky);
		_gameScene->setPlayer(unit);
		_gameScene->addChild(unit);

		_sprites.pushBack(unit);
		_sortSprites.pushBack(unit);
		_allys.pushBack(unit);
	}

	return unit;
}

Unit* UnitManager::findUnitFromID(int unitID)
{
	if (unitID == 0)
	{
		return _gameScene->getPlayer();
	}
	for (auto a : _allys)
	{
		if (((Unit*)a)->getID() == unitID)
		{
			return ((Unit*)a);
		}
	}
	for (auto a : _enemies)
	{
		if (((Unit*)a)->getID() == unitID)
		{
			return ((Unit*)a);
		}
	}
	return nullptr;
}

Building* UnitManager::findBuildingFromID(int buildingID)
{
	for (auto a : _buildings)
	{
		if (((Building*)a)->getBuildingID() == buildingID)
		{
			return ((Building*)a);
		}
	}
	return nullptr;
}

Tool* UnitManager::createTool(const char* name, int x, int y, int type)
{
	CCLOG("%s", __FUNCTION__);
	auto tool = Tool::create(_gameScene, name, x, y, type);
	if (tool == nullptr)
	{
		return nullptr;
	}
	tool->setLocalZOrder(kZorderTool);
	_gameScene->addUnit(tool);
	_tools.pushBack(tool);
	_sprites.pushBack(tool);
	return tool;
}

void UnitManager::processDropItem(Unit* unit)
{
	auto dropItemCount = 0;
	std::vector<XDropTool> pDropItemData;
	if (unit->getType() == 0)//紫色机器人
	{
		dropItemCount = DROPITEMCOUNT[0];
		pDropItemData.resize(dropItemCount);
		for (int k = 0; k < dropItemCount; k++)
		{
			pDropItemData[k].type = DROPITEMDATA[0][k][0];
			pDropItemData[k].min = DROPITEMDATA[0][k][1];
			pDropItemData[k].max = DROPITEMDATA[0][k][2];
			pDropItemData[k].prob = DROPITEMDATA[0][k][3];
		}
	}
	else if (unit->getType() == 1)
	{
		dropItemCount = DROPITEMCOUNT[1];
		pDropItemData.resize(dropItemCount);
		for (int k = 0; k < dropItemCount; k++)
		{
			pDropItemData[k].type = DROPITEMDATA[1][k][0];
			pDropItemData[k].min = DROPITEMDATA[1][k][1];
			pDropItemData[k].max = DROPITEMDATA[1][k][2];
			pDropItemData[k].prob = DROPITEMDATA[1][k][3];
		}
	}
	else if (unit->getType() == 9 || unit->getType() == 10)
	{
		dropItemCount = DROPITEMCOUNT[2];
		pDropItemData.resize(dropItemCount);
		for (int k = 0; k < dropItemCount; k++)
		{
			pDropItemData[k].type = DROPITEMDATA[2][k][0];
			pDropItemData[k].min = DROPITEMDATA[2][k][1];
			pDropItemData[k].max = DROPITEMDATA[2][k][2];
			pDropItemData[k].prob = DROPITEMDATA[2][k][3];
		}
	}
	unit->setDropToolData(pDropItemData);
}

Unit* UnitManager::createDefaultUnit(int type, int x, int y)
{
	//在游戏里产生的Unit是没有事件ID的,所以就设置成-1
	auto unit = createUnit(-1, type, x, y, 1, D_S_DOWN, Enemy);
	if (!unit)
	{
		return nullptr;
	}
	//循环指令
	std::vector<XUnitOrder> recycleOrderData;
	XUnitOrder order;
	order.Direct = 8;
	order.Speed = 10;
	order.Time = 10;
	order.BulletType = 3;
	order.FireLogic = 2;
	order.Addition = 0;
	recycleOrderData.push_back(order);

	order.Direct = 8;
	order.Speed = 10;
	order.Time = 20;
	order.BulletType = 0;
	order.FireLogic = 0;
	order.Addition = 0;
	recycleOrderData.push_back(order);

	unit->setUnitRecycleOrder(recycleOrderData);
	return unit;
}

Unit* UnitManager::spawnEnemy(int unitID, int type, int x, int y, int moveType, int dir, int campType)
{
	Unit* pUnit = getOrCreate(_gameScene, unitID, type, dir, campType);
	if (pUnit)
	{
		pUnit->setPosition(Vec2(x, y));
		pUnit->setPatrol(Vec2(x, y));
		pUnit->setMoveType(moveType);
		pUnit->setLocalZOrder(kZOrderSky);
		_sprites.pushBack(pUnit);
		_sortSprites.pushBack(pUnit);
		if (campType == Ally)
		{
			_allys.pushBack(pUnit);
		}
		else
		{
			_enemies.pushBack(pUnit);
		}
		_gameScene->addUnit(pUnit);
	}
	return pUnit;
}

Unit* UnitManager::getOrCreate(GameScene* gameScene, int unitID, int type, int dir, int campType)
{
	Unit *enemy = nullptr;
	switch (type)
	{
	case kEnemyFodder:
		enemy = Fodder::create(gameScene, unitID, type, dir, campType);
		CC_SAFE_RETAIN(enemy);
	case kEnemyFodderL:
		
		break;
	case kEnemyBigDude:
	
		break;
	case kEnemyBoss:
		
		break;
	case kEnemyTank:
		enemy = Tank::create(gameScene, unitID, type, dir, campType);
		CC_SAFE_RETAIN(enemy);
		break;
	case kEnemyTurret:
		
		break;
	}
	return enemy;
}

Building* UnitManager::createBuilding(int buildType, int BuildiID, int x, int y, int state, bool fliph)
{
	auto pBuildingRes = _gameScene->getBuildingResManager()->getBuildingResFromID(buildType);
	auto pBuilding = Building::create(_gameScene, pBuildingRes, BuildiID, state, fliph);
	if (pBuilding)
	{
		pBuilding->setPosition(x, y);
		pBuilding->setLocalZOrder(kZorderBuilding);
		_buildings.pushBack(pBuilding);
		_sprites.pushBack(pBuilding);
		_gameScene->addUnit(pBuilding);
	}
	return pBuilding;
}

Bullet* UnitManager::createBullet(std::shared_ptr<WeaponRes> weaponRes, const cocos2d::Vec2& pos, const cocos2d::Vec2& move, int dir, int campType)
{
	assert(weaponRes!= nullptr);
	Bullet* pBullet = nullptr;
	switch (weaponRes->getBulletLogicType())
	{
	case 1:
		pBullet = Bullet::create(_gameScene, weaponRes, pos, move, dir);
		break;
	case 2:
		pBullet = Bullet::create(_gameScene, weaponRes, pos, move, dir);
		break;
	case 5:
		pBullet = Bullet::create(_gameScene, weaponRes, pos, move, dir);
		break;
	default:
		pBullet = Bullet::create(_gameScene, weaponRes, pos, move, dir);
		break;
	}
	if (pBullet == nullptr) return nullptr;
	pBullet->setLocalZOrder(kZOrderBullet);
	_gameScene->addUnit(pBullet);
	_sprites.pushBack(pBullet);
	if (campType == Ally)
	{
		_allyBullets.pushBack(pBullet);
	}
	else
	{
		_enemyBullets.pushBack(pBullet);
	}
	return pBullet;
}

Explosion* UnitManager::createExplode(cocos2d::Node* unit, int type, const cocos2d::Vec2& pos, std::function<void()> callback)
{
	Explosion* explosion = nullptr;
	switch (type)
	{
	case 0:
		explosion = BulletExlosion::create(pos, callback);
		break;
	default:
		explosion = Explosion::create(pos, callback);
		break;
	}
	if (explosion == nullptr)
	{
		return nullptr;
	}
	explosion->setCallBack(callback);
	explosion->setLocalZOrder(kZorderExplosion);
	_gameScene->addUnit(explosion);
	return explosion;
}

void UnitManager::clear()
{
	for (auto i = 0; i < _tools.size(); ++i)
	{
		auto o = _tools.at(i);
		o->removeFromParent();
		_tools.eraseObject(o);
		--i;
	}
	for (auto i = 0; i < _buildings.size(); ++i)
	{
		auto o = _buildings.at(i);
		o->removeFromParent();
		_buildings.eraseObject(o);
		--i;
	}
	for (auto i = 0; i < _sprites.size(); ++i)
	{
		auto o = _sprites.at(i);
		o->removeFromParent();
		_sprites.eraseObject(o);
		--i;
	}
	for (auto i = 0; i < _sortSprites.size(); ++i)
	{
		auto o = _sortSprites.at(i);
		o->removeFromParent();
		_sortSprites.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _allys.size(); ++i)
	{
		auto o = _allys.at(i);
		o->removeFromParent();
		_allys.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _enemies.size(); ++i)
	{
		auto o = _enemies.at(i);
		o->removeFromParent();
		_enemies.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _enemyBullets.size(); ++i)
	{
		auto o = _enemyBullets.at(i);
		o->removeFromParent();
		_enemyBullets.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _allyBullets.size(); ++i)
	{
		auto o = _allyBullets.at(i);
		o->removeFromParent();
		_allyBullets.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _explodes.size(); ++i)
	{
		auto o = _explodes.at(i);
		o->removeFromParent();
		_explodes.eraseObject(o);
		--i;
	}

	for (auto i = 0; i < _explodeReals.size(); ++i)
	{
		auto o = _explodeReals.at(i);
		o->removeFromParent();
		_explodeReals.eraseObject(o);
		--i;
	}
	
}
