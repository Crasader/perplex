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

Unit* UnitManager::createUnit(int unitID, int type, int x, int y, int moveType, int dir, int campType)
{
	Unit* pUnit = nullptr;
	switch (type)
	{
	case 0:
		pUnit = createRedHeader(unitID, type, x, y, moveType, dir, campType);
		_gameScene->addLevelEnemy(-1);
		break;
	case 1:
	case 5:
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
		if (((Unit*)a)->getUnitID() == unitID)
		{
			return ((Unit*)a);
		}
	}
	for (auto a : _enemies)
	{
		if (((Unit*)a)->getUnitID() == unitID)
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

Bullet* UnitManager::createBullet(std::shared_ptr<WeaponRes> weaponRes, int x, int y, int moveX, int moveY, int dir, int campType)
{
	assert(weaponRes!= nullptr);
	Bullet* pBullet = nullptr;
	switch (weaponRes->getBulletLogicType())
	{
	case 1:
		pBullet = Bullet::create(_gameScene, weaponRes, Vec2(x, y), Vec2(moveX, moveY), Vec2::ZERO);
		break;
	case 2:
		pBullet = Bullet::create(_gameScene, weaponRes, Vec2(x, y), Vec2(moveX, moveY), Vec2::ZERO);
		break;
	case 5:
		pBullet = Bullet::create(_gameScene, weaponRes, Vec2(x, y), Vec2(moveX, moveY), Vec2::ZERO);
		break;
	default:
		pBullet = Bullet::create(_gameScene, weaponRes, Vec2(x, y), Vec2(moveX, moveY), Vec2::ZERO);
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

Explosion* UnitManager::createExplode(Unit* unit, int type, const cocos2d::Vec2& pos, std::function<void()> callback)
{
	Explosion* explosion = nullptr;
	switch (type)
	{
	default:
		explosion = Explosion::create(pos, callback);
		break;
	}
	if (explosion == nullptr)
	{
		return nullptr;
	}
	explosion->setCallBack(callback);
	explosion->setLocalZOrder(unit->getLocalZOrder());
	_gameScene->addUnit(explosion);
	_explodes.pushBack(explosion);
	return explosion;
}