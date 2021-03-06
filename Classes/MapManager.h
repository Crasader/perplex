#ifndef __MapManager_h__
#define __MapManager_h__

#include "Head.h"
#include "GameGlobe.h"
#include "mapconst.h"

class GameScene;
class XMap;

class MapManager
{
public:
	enum MapLayer
	{
		EIdle = 0,
		ELoadMapInf, //地图描述
		ELoadMapFloor,//地板层
		ELoadMapTop,//顶层
		ELoadMapBuilding,//建筑
		ELoadMapUnit,//人物角色
		ELoadMapWalkRect,//可走范围
		ELoadMapImg,//图像
	};

	MapManager(GameScene* scene);
	~MapManager();
	bool requestMap(int aMapID, bool aLocal);
	bool isMapOk(int aMapID);
	bool analyzeDataL();
	bool loadMapImg(int aMapID);
	void analyzeMap();
	bool createFloor();
	Node* getFloor() { return _floor; }
	void createUnits();
	void createBuilding();
	int getXMapW();
	int getXMapH();
	Size getMapSize(){ return Size(getXMapW(), getXMapH()); }
	bool isLoadingMap() { return iLoadingMap; }
	int getActiveMapID() { return iActiveMapID; }
	std::shared_ptr<XMap> getActiveMap();
	void performMap();
	bool isEqualF(float a, float b);
	void moveMap(float offsetY, float moveUp);
	int getVersion();
private:
	 GameScene*           _gameScene;
	 bool                 iLoadingMap;    //是否正在载入地图
	 bool                 iLocalMap;
	 bool                 iGetMap;
	 int                  iRequestMapID;
	 int                  iActiveMapID;   //当前活跃的地图ID
	 int				  iImgCounts;
	 int				  iLoadState;     //载入进度
	 int				  _version;
	 float 				  iLastCameraY;	//上次摄像机的Y坐标
	 std::shared_ptr<XMap>               iPrepareLoadMap;//准备载入的地图大
	 std::shared_ptr<XMap>                iActiveMap;     //当前活跃的地图
	 Texture2D** 		  iMapImgs;		//地图图像
	 std::string          iFileName;      //地图包名
	 Node*				 _floor;//地板
};
#endif // __MapManager_h__
