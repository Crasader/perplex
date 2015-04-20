#include "MapManager.h"
#include "XMap.h"
#include "GameControllers.h"
#include "cocos2d.h"
#include "Unit.h"

void MapManager::AnalyzeMap()
{
	if (iLoadState == MapLayer::EIdle)
	{
		do
		{
			AnalyzeDataL();
			iLoadState++;
		} while (iLoadState <= MapLayer::ELoadMapImg);

		CC_SAFE_RELEASE(iActiveMap);
		iActiveMap = iPrepareLoadMap;
		iGetMap = true;
		iPrepareLoadMap = nullptr;
	}
	else
	{
		CC_SAFE_RELEASE(iActiveMap);
		iActiveMap = iPrepareLoadMap;
		iGetMap = true;
		iPrepareLoadMap = nullptr;
	}
}

bool MapManager::LoadMapImg(int aMapID)
{
	bool result = true;
	int count = MAP_IMAGE_COUNT[MAPID_TO_STAGE[aMapID]];
	int index = MAP_IMAGE_START_INDEX[MAPID_TO_STAGE[aMapID]];
	if (iMapImgs == nullptr)
	{
		iMapImgs = new Texture2D*[count];
	}
	iImgCounts = count;
	count += index;
	int loadImgCount = 0;
	for (int i = index; i < count; i++)
	{
		char f[256];
		sprintf(f, "map/%d.jpg", i);
		 auto texture = Director::getInstance()->getTextureCache()->addImage(f);
		 iMapImgs[i - index] = texture;
		loadImgCount++;
		if (loadImgCount >= 15)
		{
			result = false;
			break;
		}
	}
	return result;
}

bool MapManager::AnalyzeDataL()
{
	bool result = true;
	switch (iLoadState)
	{
	case MapManager::MapLayer::EIdle:
		break;
	case MapManager::MapLayer::ELoadMapInf:
		iPrepareLoadMap->AnalyzeInfDataL();
		break;
	case MapManager::MapLayer::ELoadMapFloor:
		iPrepareLoadMap->AnalyzeFloorDataL();
		break;
	case MapManager::MapLayer::ELoadMapTop:
		iPrepareLoadMap->AnalyzeTopDataL();
		break;
	case MapManager::MapLayer::ELoadMapBuilding:
		iPrepareLoadMap->AnalyzeBuildingDataL();
		break;
	case MapManager::MapLayer::ELoadMapUnit:
		iPrepareLoadMap->AnalyzeUnitDataL();
		break;
	case MapManager::MapLayer::ELoadMapWalkRect:
		iPrepareLoadMap->AnalyzeRectDataL();
		break;
	case MapManager::MapLayer::ELoadMapImg:
		/*result = LoadMapImg(iRequestMapID);*/
		break;
	default:
		break;
	}
	return result;
}

bool MapManager::IsMapOk(int aMapID)
{
	return iActiveMap != nullptr && iActiveMap->getID() == aMapID;
}

bool MapManager::RequestMap(int aMapID, bool aLocal)
{
	if (IsMapOk(aMapID))
	{
		iRequestMapID = -1;
		iLoadState = MapLayer::EIdle;
		iActiveMapID = aMapID;
		return true;
	}

	if (iRequestMapID == aMapID)
	{
		AnalyzeMap();
		return false;
	}
	
	CC_SAFE_DELETE(iActiveMap);
	iLocalMap = aLocal;
	iGetMap = false;
	CC_SAFE_DELETE(iPrepareLoadMap);
	iPrepareLoadMap = new XMap(aMapID);
	iLoadingMap = true;
	iLoadState = MapLayer::EIdle;
	iRequestMapID = aMapID;

	return false;
}

MapManager::~MapManager()
{
	CC_SAFE_DELETE(iPrepareLoadMap);
	CC_SAFE_DELETE(iActiveMap);
	for (int i = 0; i < iImgCounts; i++)
	{
		CC_SAFE_RELEASE(iMapImgs[i]);
	}
	CC_SAFE_DELETE_ARRAY(iMapImgs);
	CC_SAFE_RELEASE(_floor);
}

MapManager::MapManager(GameScene* scene)
:iGameScene(nullptr)
,iLoadingMap(0)    //是否正在载入地图
,iLocalMap(0)
,iGetMap(0)
,iRequestMapID(-1)
,iActiveMapID(-1)   //当前活跃的地图ID
,iLastCameraY(0)	//上次摄像机的Y坐标
,iScreenPartCount(0) //屏幕内容纳的缓冲区个数
,iImgCounts(0)
,iLoadState(0)     //载入进度
,iPrepareLoadMap(nullptr)//准备载入的地图大
,iActiveMap(nullptr)     //当前活跃的地图
,iMapImgs(nullptr)		//地图图像
,iFileName("")    //地图包名
, _floor(nullptr)
{
	iGameScene = scene;
	//CC_SAFE_RETAIN(iGameScene);
}

bool MapManager::createFloor()
{
	if (iActiveMap == nullptr)
	{
		return false;
	}
	CC_SAFE_RELEASE(_floor);
	_floor = Node::create();
	auto count = iActiveMap->getFloorCount();
	for (int i = 0; i < count; i++)
	{
		SMapFloor mapFloor = iActiveMap->getMapFloor()[i];
		char f[256];
		sprintf(f, "map/%d.jpg", mapFloor.iImageID);
		auto img = Director::getInstance()->getTextureCache()->addImage(f);
		auto sprite = Sprite::createWithTexture(img);
		sprite->setAnchorPoint(Point::ZERO);
		auto x = mapFloor.iX;
		auto y = mapFloor.iY;
		sprite->setPosition(x, y);
		if (mapFloor.iFlipH)
		{
			sprite->setFlippedX(true);
		}
		_floor->addChild(sprite);
	}
	return true;
}

int MapManager::getXMapW() { return iActiveMap->getWidth(); }
int MapManager::getXMapH() { return iActiveMap->getHeight(); }

void MapManager::createUnits()
{
	assert(iActiveMap);
	auto iUnitCount = iActiveMap->getUnitCount();
	auto iMapUnit = iActiveMap->getSMapUnit();
	//生成Unit
	int unitWDir;
	int campType;
	for (int n = 0; n < iUnitCount; n++)
	{
		unitWDir = (iMapUnit[n].dir >> 1);
		campType = iMapUnit[n].CampType;
		//TODO
		auto pUnit = EnemyController::spawnEnemy(iMapUnit[n].urID);
		if (pUnit == nullptr)
		{
			continue;
		}
		pUnit->setUnitID(iMapUnit[n].id);
		pUnit->setPosition(iMapUnit[n].x, iMapUnit[n].y);
		pUnit->setMoveType(iMapUnit[n].MoveType);
		pUnit->setWalkDir(unitWDir);
		pUnit->setCampType(campType);
		pUnit->setMoveProb(iMapUnit[n].iMoveProb, 8);
		pUnit->setAnchorPoint(Vec2(1,0));

		if (iMapUnit[n].MoveType == PATH_MOVE)
		{
			pUnit->setPointItemData(iMapUnit[n].iMoveItemData, iMapUnit[n].iMoveItemCount);
		}
		else
		{
			pUnit->setMoveItemData(iMapUnit[n].iMoveItemData, iMapUnit[n].iMoveItemCount);
		}
		pUnit->setFireItemData(iMapUnit[n].iFireItemData, iMapUnit[n].iFireItemCount);
		pUnit->setDistItemData(iMapUnit[n].iDistItemData, iMapUnit[n].iDistItemCount);
		pUnit->setDropToolData(iMapUnit[n].iDropToolData, iMapUnit[n].iDropToolCount);
		pUnit->setUnitRecycleOrder(iMapUnit[n].iRecycleOrderData, iMapUnit[n].iRecycleOrderCount);
		pUnit->setAIType(iMapUnit[n].AIType);
	}
	CC_SAFE_DELETE_ARRAY(iMapUnit);
	iUnitCount = 0;
}
