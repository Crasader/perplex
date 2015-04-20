#include "UnitRes.h"

UnitRes::UnitRes()
:_ID(0) //每一种Unit对应一个UnitRes, iID是UnitRes的唯一标识
,_HP(0)
,_animID(0)
,_moveGridCount(0)//占地格个数
,_weaponPosCount(0)//挂接点数目，各个方向都相同
,_weaponTypeCount(0)//挂接点武器类型的数目
,_weaponEveryTypeCount(0)//每种武器类型的挂接点数目
,_moveGrids(nullptr)//每个方向都相同
,_weaponTypeList(nullptr)//挂接点武器类型列表
,_weaponTypeIndexList(nullptr) //每种武器类型的挂接点的索引值表
{
	memset(_speedX, 0, WALK_DIR_COUNT);
	memset(_speedY, 0, WALK_DIR_COUNT);
	memset(_bodyRectCounts, 0, WALK_DIR_COUNT);
	memset(_explodeCount, 0, WALK_DIR_COUNT);
}

UnitRes::~UnitRes()
{
	CC_SAFE_DELETE_ARRAY(_moveGrids);
	CC_SAFE_DELETE_ARRAY(_weaponTypeList);
	CC_SAFE_DELETE_ARRAY(_weaponTypeIndexList);
	for (int i = 0; i < STAND_DIR_COUNT; i++)
	{
		/*CC_SAFE_DELETE(_bodyRect[i]);*/
	}
	for (int i = 0; i < STAND_DIR_COUNT; i++)
	{
		/*CC_SAFE_DELETE(_explode[i]);*/
	}
	for (int i = 0; i < STAND_DIR_COUNT; i++)
	{
		//CC_SAFE_DELETE(_weaponPos[i]);
	}
}
