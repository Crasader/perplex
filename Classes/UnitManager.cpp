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

std::vector<GameEntity*> UnitManager::_tools;
std::vector<GameEntity*> UnitManager::_buildings;
std::vector<GameEntity*> UnitManager::_sprites;
std::vector<GameEntity*> UnitManager::_sortSprites;
std::vector<GameEntity*> UnitManager::_allys;
std::vector<GameEntity*> UnitManager::_enemies;
std::vector<GameEntity*> UnitManager::_enemyBullets;
std::vector<GameEntity*> UnitManager::_allyBullets;
std::vector<Explode*> UnitManager::_explodes;
std::vector<GameEntity*> UnitManager::_explodeReals;

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
		break;
	default:
		break;
	}
	return pUnit;
}

Unit* UnitManager::createRedHeader(int unitID, int type, int x, int y, int moveType, int dir, int campType)
{
	Unit* unit = nullptr;
	unit->setPosition(x, y);
	unit->setMoveType(moveType);
	unit->setCampType(campType);
	/*_gameScene->setPlayer(unit);*/

	_sprites.push_back(unit);
	_sortSprites.push_back(unit);
	_allys.push_back(unit);

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

Tool* UnitManager::createTool(int appearAnimaID, int generalAnimID, int disappearAnimID, int x, int y, int type)
{
	auto tool = Tool::create(_gameScene, appearAnimaID, generalAnimID, disappearAnimID, x, y, type);
	_tools.push_back(tool);
	_sprites.push_back(tool);
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
	//循环指令
	std::vector<XUnitOrder> recycleOrderData;
	XUnitOrder order;
	order.Direct = 8;
	order.Speed = 0;
	order.Time = 10;
	order.BulletType = 3;
	order.FireLogic = 2;
	order.Addition = 0;
	recycleOrderData.push_back(order);

	order.Direct = 8;
	order.Speed = 0;
	order.Time = 20;
	order.BulletType = 0;
	order.FireLogic = 0;
	order.Addition = 0;
	recycleOrderData.push_back(order);

	unit->setUnitRecycleOrder(recycleOrderData);
	
	return unit;
}

Building* UnitManager::createBuilding(int buildType, int BuildiID, int x, int y, int state, bool fliph)
{
	auto pBuildingRes = _gameScene->getBuildingResManager()->getBuildingResFromID(buildType);
	auto pBuilding = Building::create(_gameScene, pBuildingRes, BuildiID, state, fliph);
	pBuilding->setPosition(x, y);
	_buildings.push_back(pBuilding);
	_sprites.push_back(pBuilding);
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
	_sprites.push_back(pBullet);
	if (campType != Enemy)
	{
		_allyBullets.push_back(pBullet);
	}
	else
	{
		_enemyBullets.push_back(pBullet);
	}
	return pBullet;
}

