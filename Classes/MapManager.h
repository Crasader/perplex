#ifndef __MapManager_h__
#define __MapManager_h__

#include "Head.h"
#include "GameGlobe.h"

class GameScene;
class XMap;

class MapManager : public Ref
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
	bool RequestMap(int aMapID, bool aLocal);
	bool IsMapOk(int aMapID);
	bool AnalyzeDataL();
	bool LoadMapImg(int aMapID);
	void AnalyzeMap();
	bool createFloor();
	Node* getFloor() { return _floor; }
	void createUnits();
	int getXMapW();
	int getXMapH();
	Size getMapSize(){ return Size(getXMapW(), getXMapH()); }
private:
	 GameScene*           iGameScene;
	 bool                 iLoadingMap;    //是否正在载入地图
	 bool                 iLocalMap;
	 bool                 iGetMap;
	 int                  iRequestMapID;
	 int                  iActiveMapID;   //当前活跃的地图ID
	 int 				  iLastCameraY;	//上次摄像机的Y坐标
	 int 				  iScreenPartCount; //屏幕内容纳的缓冲区个数
	 int				  iImgCounts;
	 int				  iLoadState;     //载入进度
	 XMap*                iPrepareLoadMap;//准备载入的地图大
	 XMap*                iActiveMap;     //当前活跃的地图
	 Texture2D** 		  iMapImgs;		//地图图像
	 std::string          iFileName;      //地图包名
	 Node*				 _floor;//地板
};

#endif // __MapManager_h__
