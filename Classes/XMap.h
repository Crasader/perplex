#ifndef __XMap_h__
#define __XMap_h__

#include "Head.h"
#include "XDropTool.h"
#include "mapconst.h"

class SMapFloor
{
public:
	SMapFloor() :iImageID(0), iFlipH(0), iX(0), iY(0){};
	int iImageID;
	bool iFlipH;
	int iX;
	int iY;
};

class SMapUnit
{
public:
	SMapUnit();
	~SMapUnit();
	void release();
	short urID;//表示该单位使用的哪一种作战单位资源
	short id;//该作战单位的ID；
	short x;//该作战单位的坐标；
	short y;
	short dir;//初始方向
	short AIType;//AI类型
	short CampType;//阵营类型 0 --- 敌人  1 --- 盟军  2 --- Player
	short MoveType;//移动方式
	//当移动方式为随机移动时表示 移动时间和概率
	//当移动方式为按路径移动时表示 坐标
	short           iMoveItemCount;
	short           iDistItemCount;//目标距离
	short           iFireItemCount; //开火概率
	short           iDropToolCount;//掉落道具
	short           iOrderCount;   //指令数
	short           iRecycleOrderCount; //循环指令数
	std::vector<int>			iMoveProb;  //8方向随机移动的概率
	std::vector<Vec2>			iMoveItemData;
	std::vector<Vec2>			iFireItemData;
	std::vector<Vec2>			iDistItemData;
	std::vector<XDropTool>      iDropToolData;
	std::vector<XUnitOrder>    iOrderData;
	std::vector<XUnitOrder>    iRecycleOrderData;
};

class SMapBuilding{
public:
	SMapBuilding();
	~SMapBuilding();
	void release();
	bool    bflip;  //0表示无水平翻转；1表示水平翻转
	bool    bcasern;//是否兵营
	short   brID;   //表示该建筑使用的那个资源
	short   id;     //该建筑的ID；
	short   x;      //该建筑的基准点的坐标；注意：不是正中心点
	short   y;      //
	short   state;  //初始状态。0表示完好；1表示半毁；2表示全毁
	short   tool;   //爆炸后出现的工具
	short   add;    //附加信息
	std::vector<XUnitFactory> iUnitFactoryData;
	std::vector<XDropTool> iDropToolData;
};



class XMap
{
private:

public:
	XMap();
	XMap(int aID);
	~XMap();
	void init(int aID);
	void AnalyzeInfDataL();
	void AnalyzeFloorDataL();
	void AnalyzeUnitDataL();
	void AnalyzeBuildingDataL();
	void AnalyzeRectDataL();
	void Release();
	bool LoadMapImg();
	void DoGameLoad();
	void AnalyzeTopDataL();
	int getID() const { return iID; }
	void setID(int aID) { iID = aID; }
	int getTopCount() const { return iMapTop.size(); }
	std::vector<SMapFloor> getMapTop() const { return iMapTop; }
	int getFloorCount() const { return iMapFloor.size(); }
	std::vector<SMapFloor> getMapFloor() const { return iMapFloor; }
	int getWidth() const { return iWidth; }
	int getHeight() const { return iHeight; }
	int getUnitCount() { return iMapUnit.size(); }
	std::vector<SMapUnit> getSMapUnit() const { return iMapUnit; }
	std::vector<cocos2d::Rect> getWalkRect() {
		return iMapWalkRect;
	}
	void setWalkRectActive(int index, bool active) { ibWalkRectActive[index] = active; }
	bool isWalkRectActive(int index) { return ibWalkRectActive[index]; }
	
	int getBuildingCount() const { return iMapBuilding.size(); }
	std::vector<SMapBuilding> getMapBuilding() { return iMapBuilding; }
private:
	int             	iID;                    // 地图ID，全球唯一v
	int					iActivist;
	int             	iWidth;                 // 宽像素点
	int             	iHeight;                // 高像素点
	int 				iBackColor;				// 背景色
	//地图摄像机
	int					iCameraX;				//摄像机X坐标
	int 				iCameraY;				//摄像机Y坐标

	std::vector<SMapFloor>    	iMapTop;                //顶层地板数据
	std::vector<SMapFloor>    	iMapFloor;				//地板数据
	std::vector<SMapUnit>		iMapUnit;               //Unit数据
	std::vector<SMapBuilding>   iMapBuilding;           //建筑数据
	std::vector<bool>          	ibWalkRectActive;		//不可行走区域是否活跃
	std::vector<Rect>	        iMapWalkRect;           //不可行走矩形
};

#endif // __XMap_h__
