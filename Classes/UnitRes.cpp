#include "UnitRes.h"

UnitRes::UnitRes()
:_ID(0) //每一种Unit对应一个UnitRes, iID是UnitRes的唯一标识
,_HP(0)
,_animID(0)
,_moveGridCount(0)//占地格个数
,_weaponPosCount(0)//挂接点数目，各个方向都相同
,_weaponTypeCount(0)//挂接点武器类型的数目
,_weaponEveryTypeCount(0)//每种武器类型的挂接点数目
{

}
