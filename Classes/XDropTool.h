#ifndef __XDropTool_h__
#define __XDropTool_h__

#include "Head.h"

class XDropTool
{
public:
	XDropTool():type(0), min(0), max(0), prob(0){}
	int type;//类型：0表示血包，1表示武器A，2表示武器B，3表示任意情况
	int min;//表示类型对应的玩家属性在某个范围内，范围取值为 min <= prop < max，如果type == 3那么这两个属性无效
	int max;
	int prob;//掉落对应道具的可能性，如果type == 3那么就是任意道具，采用百分比概率
}; 

class XUnitFactory
{
public:
	XUnitFactory() :unitType(0), left(0), top(0), width(0), height(0), unitCount(0), interval(0){};
	int unitType;//兵种类型
	int left;//出兵的地图范围，使用实际地图坐标
	int top;
	int width;
	int height;
	int unitCount;//出兵数量
	int interval;//此次出兵之后需要冷却的时间
};

class XUnitOrder
{
public:
	XUnitOrder() :Direct(0), Speed(0), Time(0), BulletType(0), FireLogic(0), Addition(0){};
	int Direct;    //方向
	int Speed;     //速度
	int Time;      //时间tick为单位
	int BulletType;//子弹类型
	int FireLogic; //发射逻辑
	int Addition;  //
};

class Explode
{
public:
	Explode() :iAnimID(0), iAdd(0), iX(0), iY(0), iStart(0), iEnd(0), iBeginIndex(0){};
   int iAnimID;//该爆炸的动画ID
   int iAdd;//附加项
   int iX;//爆炸效果相对作战单位基准点的偏移量
   int iY;
   int iStart;//这个爆炸动画的播放range
   int iEnd;
   int iBeginIndex;//表示从机器人的死亡动画开始计算直到经过了beginIndex这么多帧之后开始播放这个爆炸效果
};
#endif // __XDropTool_h__
