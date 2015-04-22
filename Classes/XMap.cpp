#include "XMap.h"
#include "Ifstream.h"
#include "GameGlobe.h"
#include "GameControllers.h"
#include "Unit.h"

void XMap::AnalyzeRectDataL()
{
	char f[256];
	sprintf(f, "l%d_rect_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iWalkRectCount;
		if (iWalkRectCount > 0)
		{
			ibWalkRectActive = new bool[iWalkRectCount];
			iMapWalkRect = new Rect[iWalkRectCount];
			int l, t, w, h;
			for (int n = 0; n < iWalkRectCount; n++)
			{
				dis >> l >> t >> w >> h;
				ibWalkRectActive[n] = false;
				iMapWalkRect[n].setRect(l, t, w, h);
			}
		}
	}
	dis.close();
}

void XMap::AnalyzeBuildingDataL()
{
	//解析建筑数据
	char f[256];
	sprintf(f, "l%d_building_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iBuildingCount;
		if (iBuildingCount > 0)
		{
			iMapBuilding = new SMapBuilding[iBuildingCount];
			for (int i = 0; i < iBuildingCount; i++)
			{
				int brID, id, x, y, state, bflip, factoryCount;
				dis >> brID >> id >> x >> y >> state >> bflip >> factoryCount;
				iMapBuilding[i].brID = brID;
				iMapBuilding[i].id = id;
				iMapBuilding[i].x = x;
				iMapBuilding[i].y = y;
				iMapBuilding[i].state = state;
				iMapBuilding[i].bflip = bflip == 0 ? false : true;
				iMapBuilding[i].bcasern = false;
				iMapBuilding[i].tool = -1;//当等于-1时，不产生道具
				iMapBuilding[i].add = 0;
				iMapBuilding[i].iUnitFactoryCount = factoryCount;
				if (factoryCount > 0)
				{
					iMapBuilding[i].iUnitFactoryData = new XUnitFactory[factoryCount];
					for (int k = 0; k < factoryCount; ++k)
					{
						int unitType, left, top, width, height, unitCount, interval;
						dis >> unitType >> left >> top >> width >> height >> unitCount >> interval;
					
						iMapBuilding[i].iUnitFactoryData[k].unitType = unitType;
						iMapBuilding[i].iUnitFactoryData[k].left = left;
						iMapBuilding[i].iUnitFactoryData[k].top = top;
						iMapBuilding[i].iUnitFactoryData[k].width = width;
						iMapBuilding[i].iUnitFactoryData[k].height = height;
						iMapBuilding[i].iUnitFactoryData[k].unitCount = unitCount;
						iMapBuilding[i].iUnitFactoryData[k].interval = interval;
					}
				}
	
				dis >> iMapBuilding[i].iDropToolCount;
				if (iMapBuilding[i].iDropToolCount > 0)
				{
					iMapBuilding[i].iDropToolData = new XDropTool[iMapBuilding[i].iDropToolCount];
					for (int k = 0; k < iMapBuilding[i].iDropToolCount; ++k)
					{
						int type, min, max, prob;
						dis >> type >> min >> max >> prob;
						iMapBuilding[i].iDropToolData[k].type = type;
						iMapBuilding[i].iDropToolData[k].min = min;
						iMapBuilding[i].iDropToolData[k].max = max;
						iMapBuilding[i].iDropToolData[k].prob = prob;
					}
				}
			}
		}

	}
	dis.close();

	//生成建筑物
	
}

void XMap::AnalyzeInfDataL()
{
	char f[256];
	sprintf(f, "l%d_mapinfo_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iWidth >> iHeight >> iBackColor;
		/*	iWidth *= CC_CONTENT_SCALE_FACTOR();
			iHeight *= CC_CONTENT_SCALE_FACTOR();*/
		byte r, g, b;
		r = (iBackColor & 0xff0000) >> 16;
		g = (iBackColor & 0xff00) >> 8;
		b = (iBackColor & 0xff);
	}
	dis.close();
}

void XMap::AnalyzeFloorDataL()
{
	char f[256];
	sprintf(f, "l%d_floor_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iFloorCount;
		iMapFloor = new SMapFloor[iFloorCount];
		for (int i = 0; i < iFloorCount; i++)
		{
			int id;
			dis >> id;
			iMapFloor[i].iImageID = id;
			if (iMapFloor[i].iImageID >= 500)
			{
				iMapFloor[i].iImageID = iMapFloor[i].iImageID - (500 - 134);
			}
			else if (iMapFloor[i].iImageID >= 400)
			{
				iMapFloor[i].iImageID = iMapFloor[i].iImageID - (400 - 329);
			}
			int temp;
			dis >> temp;
			dis >> iMapFloor[i].iX;
			dis >> iMapFloor[i].iY;
			int flip;
			dis >> flip;
			iMapFloor[i].iFlipH = flip > 0 ? true : false;
		}
	}
	dis.close();
}

void XMap::AnalyzeTopDataL()
{
	char f[256];
	sprintf(f, "l%d_tree_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iTopCount;
		if (iTopCount > 0)
		{
			iMapTop = new SMapFloor[iTopCount];
			for (int i = 0; i < iFloorCount; ++i)
			{
				int imageID, temp, ix, iy, ifliph;
				dis >> imageID >> temp >> ix >> iy >> ifliph;
		
				iMapTop[i].iImageID = imageID;
				iMapTop[i].iX = ix;
				iMapTop[i].iY = iy;
				iMapTop[i].iFlipH = ifliph > 0 ? true : false;
			}
		}
	}
	dis.close();
}

void XMap::AnalyzeUnitDataL()
{
	char f[256];
	sprintf(f, "l%d_unit_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iUnitCount;
		if (iUnitCount <= 0)
		{
			dis.close();
			return;
		}

		iMapUnit = new SMapUnit[iUnitCount];
		for (int i = 0; i < iUnitCount; i++)
		{
			int urID, id, x, y, dir, aitype, camptype, movetype;
			dis >> urID;
			dis >> id;
			dis >> x;
			dis >> y;
			dis >> dir;
			dis >> aitype;
			dis >> camptype;
			dis >> movetype;
		
			iMapUnit[i].urID = urID;
			iMapUnit[i].id = id;
			iMapUnit[i].x = x;
			iMapUnit[i].y = y;
			iMapUnit[i].dir = dir;
			iMapUnit[i].AIType = aitype;
			iMapUnit[i].CampType = camptype;
			iMapUnit[i].MoveType = movetype;

			int moveDataLength = 0;
			dis >> moveDataLength;

			//站岗
			if (iMapUnit[i].MoveType == STAND_MOVE)
			{
				//移动方向概率
				iMapUnit[i].iMoveProb = new int[8];
				for (int k = 0; k < 8; k++)
				{
					iMapUnit[i].iMoveProb[k] = 10;
				}

				iMapUnit[i].iDistItemCount = 0;
				iMapUnit[i].iMoveItemCount = 4;
				iMapUnit[i].iMoveItemData = new Vec2[iMapUnit[i].iMoveItemCount];
				for (int k = 0; k < iMapUnit[i].iMoveItemCount; k++)
				{
					//iMapUnit[i]->iMoveItemData[k] = new Vec2();
					iMapUnit[i].iMoveItemData[k].x = 25;
					iMapUnit[i].iMoveItemData[k].y = 20 * k ;
				}
			}

			//随机移动
			if (iMapUnit[i].MoveType == RANDOM_MOVE || iMapUnit[i].MoveType == ORDER_MOVE)
			{
				//移动方向概率
				iMapUnit[i].iMoveProb = new int[8];
				for (int k = 0; k < 8; k++)
				{
					dis >> iMapUnit[i].iMoveProb[k];
				}

				int tmpCount1, tmpCount2;
				dis >> tmpCount1;
				dis >> tmpCount2;
				iMapUnit[i].iDistItemCount = tmpCount1;
				//根据目标距离，向目标方向移动概率
				if (iMapUnit[i].iDistItemCount > 0)
				{
					iMapUnit[i].iDistItemData = new Vec2[tmpCount1];
					for (int k = 0; k < tmpCount1; k++)
					{
						int x, y;
						dis >> x;
						dis >> y;
					
						iMapUnit[i].iDistItemData[k].x = x;
						iMapUnit[i].iDistItemData[k].y = y;
					}
				}
				//移动时间概率
				iMapUnit[i].iMoveItemCount = tmpCount2;
				if (tmpCount2 > 0)
				{
					iMapUnit[i].iMoveItemData = new Vec2[tmpCount2];
					for (int k = 0; k < tmpCount2; k++)
					{
						int x, y;
						dis >> x;
						dis >> y;
					
						iMapUnit[i].iMoveItemData[k].x = x;
						iMapUnit[i].iMoveItemData[k].y = y;
					}
				}
			}
			//走固定路线
			else if (iMapUnit[i].MoveType == PATH_MOVE)
			{
				iMapUnit[i].iMoveItemCount = moveDataLength;
				if (iMapUnit[i].iMoveItemCount > 0)
				{
					iMapUnit[i].iMoveItemData = new Vec2[moveDataLength];
					for (int k = 0; k < moveDataLength; k++)
					{
						int x, y;
						dis >> x >> y;
						
						iMapUnit[i].iMoveItemData[k].x = x;
						iMapUnit[i].iMoveItemData[k].y = y;
					}
				}
			}

			//武器概率
			int tmpCount3;
			dis >> tmpCount3;
			iMapUnit[i].iFireItemCount = tmpCount3;
			if (tmpCount3 > 0)
			{
				iMapUnit[i].iFireItemData = new Vec2[tmpCount3];
				for (int k = 0; k < tmpCount3; k++)
				{
					int x, y;
					dis >> x;
					dis >> y;
					
					iMapUnit[i].iFireItemData[k].x = x;
					iMapUnit[i].iFireItemData[k].y = y;
				}
			}
			//掉落道具
			int toolCount;
			dis >> toolCount;
			iMapUnit[i].iDropToolCount = toolCount;
			if (toolCount > 0)
			{
				iMapUnit[i].iDropToolData = new XDropTool[toolCount];
				for (int k = 0; k < toolCount; k++)
				{
					int type, min, max, prob;
					dis >> type;
					dis >> min;
					dis >> max;
					dis >> prob;
					iMapUnit[i].iDropToolData[k].type = type;
					iMapUnit[i].iDropToolData[k].min = min;
					iMapUnit[i].iDropToolData[k].max = max;
					iMapUnit[i].iDropToolData[k].prob = prob;
				}
			}

			//指令
			int orderCount;
			dis >> orderCount;
			iMapUnit[i].iOrderCount = orderCount;
			if (orderCount > 0)
			{
				iMapUnit[i].iOrderData = new XUnitOrder[orderCount];
				for (int k = 0; k < orderCount; k++)
				{
					int direct, speed, time, bulletypte, firelogic, addition;
					dis >> direct;
					dis >> speed;
					dis >> time;
					dis >> bulletypte;
					dis >> firelogic;
					dis >> addition;
					iMapUnit[i].iOrderData[k].Direct = direct;
					iMapUnit[i].iOrderData[k].Speed = speed;
					iMapUnit[i].iOrderData[k].Time = time;
					iMapUnit[i].iOrderData[k].BulletType = bulletypte;
					iMapUnit[i].iOrderData[k].FireLogic = firelogic;
					iMapUnit[i].iOrderData[k].Addition = addition;
				}
			}
		}
		
	}
	dis.close();
}

void XMap::init(int aID)
{
	iID = aID;                   //地图ID，全球唯一
	iWidth = 0;
	iHeight = 0;
	iBackColor = 0;
	Release();
	iCameraX = 0;
	iCameraY = 0;
}

XMap::~XMap()
{
	Release();
}

XMap::XMap(int aID)
:iID(0)                   // 地图ID，全球唯一
,iWidth(0)                // 宽像素点
,iHeight(0)               // 高像素点
, iBackColor(0) 		// 背景色
//地图摄像机
, iCameraX(0) 				//摄像机X坐标
, iCameraY(0) 				//摄像机Y坐标
, iFloorCount(0)           //地板数
, iTopCount(0)
, iUnitCount(0)            //Unit数
, iBuildingCount(0)        //建筑数
, iWalkRectCount(0)        //不可行走矩形的个数
, iMapTop(nullptr)
, iMapFloor(nullptr) 			//地板数据
, iMapUnit(nullptr)              //Unit数据
, iMapBuilding(nullptr)          //建筑数据
, ibWalkRectActive(nullptr) 		//不可行走区域是否活跃
, iMapWalkRect(nullptr)           //不可行走矩形
{

}

XMap::XMap()
:XMap(0)
{
}

void XMap::Release()
{

	CC_SAFE_DELETE_ARRAY(iMapTop);
	CC_SAFE_DELETE_ARRAY(iMapFloor);
	CC_SAFE_DELETE_ARRAY(iMapUnit);
	CC_SAFE_DELETE_ARRAY(iMapBuilding);
	CC_SAFE_DELETE_ARRAY(iMapWalkRect);
}

SMapUnit::SMapUnit()
:urID(0)//表示该单位使用的哪一种作战单位资源
,id(0)//该作战单位的ID；
,x(0)//该作战单位的坐标；
,y(0)
,dir(0)//初始方向
,AIType(0)//AI类型
,CampType(0)//阵营类型 0 --- 敌人  1 --- 盟军  2 --- Player
,MoveType(0)//移动方式
//当移动方式为随机移动时表示 移动时间和概率
//当移动方式为按路径移动时表示 坐标
,iMoveItemCount(0)
,iDistItemCount(0)//目标距离
,iFireItemCount(0) //开火概率
,iDropToolCount(0)//掉落道具
,iOrderCount(0)   //指令数
,iRecycleOrderCount(0) //循环指令数
, iMoveProb(nullptr)  //8方向随机移动的概率
, iMoveItemData(nullptr)
, iFireItemData(nullptr)
, iDistItemData(nullptr)
, iDropToolData(nullptr)
, iOrderData(nullptr)
,iRecycleOrderData(nullptr)
{

}

SMapUnit::~SMapUnit()
{
	release();
}

void SMapUnit::release()
{
	urID = 0;//表示该单位使用的哪一种作战单位资源
	id = 0;//该作战单位的ID；
	x = 0;//该作战单位的坐标；
	y = 0;
	dir = 0;//初始方向
	AIType = 0;//AI类型
	CampType = 0;//阵营类型 0 --- 敌人  1 --- 盟军  2 --- Player
	MoveType = 0;//移动方式
	//当移动方式为随机移动时表示 移动时间和概率
	//当移动方式为按路径移动时表示 坐标
	iMoveItemCount = 0;
	iDistItemCount = 0;//目标距离
	iFireItemCount = 0; //开火概率
	iDropToolCount = 0;//掉落道具
	iOrderCount = 0;   //指令数
	iRecycleOrderCount = 0; //循环指令数
	CC_SAFE_DELETE_ARRAY(iMoveProb);
	CC_SAFE_DELETE_ARRAY(iMoveItemData);
	CC_SAFE_DELETE_ARRAY(iFireItemData);
	CC_SAFE_DELETE_ARRAY(iDistItemData);
	CC_SAFE_DELETE_ARRAY(iDropToolData);
	CC_SAFE_DELETE_ARRAY(iOrderData);
	CC_SAFE_DELETE_ARRAY(iRecycleOrderData);
}

SMapBuilding::SMapBuilding()
:bflip(0)  //0表示无水平翻转；1表示水平翻转
,bcasern(0)//是否兵营
,brID(0)   //表示该建筑使用的那个资源
,id(0)     //该建筑的ID；
,x(0)      //该建筑的基准点的坐标；注意：不是正中心点
,y(0)      //
,state(0)  //初始状态。0表示完好；1表示半毁；2表示全毁
,tool(0)   //爆炸后出现的工具
,add(0)    //附加信息
,iUnitFactoryCount(0)//出兵次数
,iDropToolCount(0)//掉落道具情况数
,iUnitFactoryData(nullptr)
,iDropToolData(nullptr)
{

}

SMapBuilding::~SMapBuilding()
{
	release();
}

void SMapBuilding::release()
{
	bflip = false;
	bcasern = 0;
	brID = 0;
	id = 0;
	x = 0;
	y = 0;
	state = 0;
	tool = 0;
	add = 0;
	iUnitFactoryCount = 0;
	iDropToolCount = 0;
	CC_SAFE_DELETE_ARRAY(iUnitFactoryData);
	CC_SAFE_DELETE_ARRAY(iDropToolData);
}
