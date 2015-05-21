#include "MapManager.h"
#include "XMap.h"
#include "GameControllers.h"
#include "cocos2d.h"
#include "Unit.h"
#include "GameScene.h"
#include "UnitManager.h"
#include "building.h"

void MapManager::analyzeMap()
{
	if (iLoadState == MapLayer::EIdle)
	{
		iLoadState++;
	}
	else if (iLoadState <= ELoadMapImg)
	{
		if (analyzeDataL())
		{
			iLoadState++;
		}
	}
	else
	{
		iActiveMap = iPrepareLoadMap;
		iActiveMapID = iRequestMapID;
		iGetMap = true;
		iPrepareLoadMap = nullptr;
	}
}

bool MapManager::loadMapImg(int aMapID)
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

bool MapManager::analyzeDataL()
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
		createFloor();
		break;
	case MapManager::MapLayer::ELoadMapTop:
		iPrepareLoadMap->AnalyzeTopDataL();
		break;
	case MapManager::MapLayer::ELoadMapBuilding:
		iPrepareLoadMap->AnalyzeBuildingDataL();
		createBuilding();
		break;
	case MapManager::MapLayer::ELoadMapUnit:
		iPrepareLoadMap->AnalyzeUnitDataL();
		createUnits();
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

bool MapManager::isMapOk(int aMapID)
{
	return iActiveMap != nullptr && iActiveMap->getID() == aMapID;
}

bool MapManager::requestMap(int aMapID, bool aLocal)
{
	if (isMapOk(aMapID))
	{
		iRequestMapID = -1;
		iLoadState = MapLayer::EIdle;
		iActiveMapID = aMapID;
		return true;
	}
	if (iRequestMapID == aMapID)
	{
		analyzeMap();
		return false;
	}

	iLocalMap = aLocal;
	iGetMap = false;
	iActiveMap = nullptr;
	iPrepareLoadMap = std::shared_ptr<XMap>(new XMap(aMapID));
	iLoadingMap = true;
	iLoadState = MapLayer::EIdle;
	iRequestMapID = aMapID;
	return false;
}

MapManager::~MapManager()
{
	for (int i = 0; i < iImgCounts; i++)
	{
		CC_SAFE_RELEASE(iMapImgs[i]);
	}
	CC_SAFE_DELETE_ARRAY(iMapImgs);
	CC_SAFE_RELEASE(_floor);
}

MapManager::MapManager(GameScene* scene)
:iGameScene(nullptr)
,iLoadingMap(0)    //�Ƿ����������ͼ
,iLocalMap(0)
,iGetMap(0)
,iRequestMapID(-1)
,iActiveMapID(-1)   //��ǰ��Ծ�ĵ�ͼID
,iLastCameraY(-1000)	//�ϴ��������Y����
,iImgCounts(0)
,iLoadState(0)     //�������
,iPrepareLoadMap(nullptr)//׼������ĵ�ͼ��
,iActiveMap(nullptr)     //��ǰ��Ծ�ĵ�ͼ
,iMapImgs(nullptr)		//��ͼͼ��
,iFileName("")    //��ͼ����
, _floor(nullptr)
{
	iGameScene = scene;
}

bool MapManager::createFloor()
{
	if (iPrepareLoadMap == nullptr)
	{
		return false;
	}
	CC_SAFE_RELEASE(_floor);
	_floor = Node::create();
	auto count = iPrepareLoadMap->getFloorCount();
	for (int i = 0; i < count; i++)
	{
		SMapFloor mapFloor = iPrepareLoadMap->getMapFloor()[i];
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
		_floor->retain();
	}
	iGameScene->addChild(_floor);
	return true;
}

int MapManager::getXMapW() { return iActiveMap->getWidth(); }
int MapManager::getXMapH() { return iActiveMap->getHeight(); }

void MapManager::createUnits()
{
	auto iUnitCount = iPrepareLoadMap->getUnitCount();
	auto iMapUnit = iPrepareLoadMap->getSMapUnit();
	//����Unit
	int unitWDir;
	int campType;
	for (int n = 0; n < iUnitCount; n++)
	{
		unitWDir = (iMapUnit[n].dir >> 1);
		campType = iMapUnit[n].CampType;
		//TODO
		auto pUnit = iGameScene->getUnitManager().createUnit(
			iMapUnit[n].id,
			iMapUnit[n].urID,
			iMapUnit[n].x,
			iMapUnit[n].y,
			iMapUnit[n].MoveType,
			unitWDir,
			campType);
		if (pUnit == nullptr) continue;
		pUnit->setMoveProb(iMapUnit[n].iMoveProb);
		pUnit->setAnchorPoint(Vec2(0.5f, 0.5f));

		if (iMapUnit[n].MoveType == PATH_MOVE)
		{
			pUnit->setPointItemData(iMapUnit[n].iMoveItemData);
		}
		else
		{
			pUnit->setMoveItemData(iMapUnit[n].iMoveItemData);
		}
		pUnit->setFireItemData(iMapUnit[n].iFireItemData);
		pUnit->setDistItemData(iMapUnit[n].iDistItemData);
		pUnit->setDropToolData(iMapUnit[n].iDropToolData);
		pUnit->setUnitRecycleOrder(iMapUnit[n].iRecycleOrderData);
		pUnit->setAIType(iMapUnit[n].AIType);
	}
	iUnitCount = 0;
}


void MapManager::createBuilding()
{
	Building *pBuilding = nullptr;
	auto count = iPrepareLoadMap->getBuildingCount();
	auto mapBuilding = iPrepareLoadMap->getMapBuilding();
	for (auto n = 0; n < count; n++)
	{
		pBuilding = iGameScene->getUnitManager().createBuilding(
			mapBuilding[n].brID,
			mapBuilding[n].id,
			mapBuilding[n].x,
			mapBuilding[n].y,
			mapBuilding[n].state,
			mapBuilding[n].bflip
			);
		pBuilding->setUnitFactory(mapBuilding[n].iUnitFactoryData);
		pBuilding->setDropTool(mapBuilding[n].iDropToolData);
	}
}