#ifndef __UnitRes_h__
#define __UnitRes_h__

#include "Head.h"
#include "XDropTool.h"
#include "GameGlobe.h"

class WeaponPos
{
public:
	int id;
	short type; //武器类型
	short cover;//遮挡关系，0表示遮挡UNIT，1表示被UNIT遮挡
	int x;//该挂接点在该方向上的位置相对作战单位基准点的偏移
	int y;
};

class UnitRes
{
public:
	static const int WALK_DIR_COUNT = 8;
	static const int STAND_DIR_COUNT = 16;
public:
	UnitRes();
public:
	int			_ID; //每一种Unit对应一个UnitRes, iID是UnitRes的唯一标识
	int			_HP;
	int			_animID;
	int			_moveGridCount;//占地格个数
	int			_weaponPosCount;//挂接点数目，各个方向都相同
	int			_weaponTypeCount;//挂接点武器类型的数目
	int			_weaponEveryTypeCount;//每种武器类型的挂接点数目
	std::vector<int>			_bodyRectCounts; // 每个方向攻击矩形的个数
	std::vector<int>			_explodeCount;//死亡爆炸效果的个数
	std::vector<int>			_speedX;//8方向移动速度，8方向数据都有，顺序为 D, DL, L, LU, U, UR, R, RD
	std::vector<int>			_speedY;//8方向移动速度，8方向数据都有，顺序为 D, DL, L, LU, U, UR, R, RD
	std::vector<std::shared_ptr<Vec2>>		_moveGrids;//每个方向都相同
	std::vector<std::vector<std::shared_ptr<Rect>>>		_bodyRect;//16方向攻击范围，16方向数据都有，顺序同上
	std::vector<std::vector<std::shared_ptr<Explode>>>	_explode;//每个死亡爆炸的数据
	std::vector<std::vector<std::shared_ptr<WeaponPos>>> _weaponPos;//每个方向的挂接点数据
	std::vector<int>		_weaponTypeList;//挂接点武器类型列表
	std::vector<std::vector<int>>		_weaponTypeIndexList; //每种武器类型的挂接点的索引值表
	std::vector<int>		_weaponEveryTypeCountList;//每种武器类型的挂接点数目
	std::vector<std::shared_ptr<Vec2>>		_moveAnimRanges;//8方向移动速度，8方向数据都有，顺序为 D, DL, L, LU, U, UR, R, RD
	std::vector<std::shared_ptr<Vec2>>		_dieAnimRanges;//16方向死亡的动画range，只有9方向数据，顺序同上
	std::vector<std::shared_ptr<Vec2>>		_standAnimRanges;//16方向站立的动画range，只有9方向数据，顺序为 D, DDL, DL, DLL, L, LLU, LU, LUU, U
	std::vector<std::shared_ptr<Vec2>>		_centerPoints;//16方向身体中心点的位置，16方向数据都有，顺序为D, DDL, DL, DLL, L, LLU, LU, LUU, U, UUR, UR, URR, R, RRD, RD, RDD
};

#endif // __UnitRes_h__
