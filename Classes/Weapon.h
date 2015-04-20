
#ifndef __Weapon_h__
#define __Weapon_h__

#include "Unit.h"
#include "WeaponRes.h"

enum WeaponState
{
	W_FLAME = 0,
	W_BULLET,
	W_CASTOFF,
};

enum WeaponCampType
{
	W_ENEMY = 0,
	W_ALLY,
	W_NEUTRAL,
};

class Weapon
{
public:
	//weapon sound
	//TODO
	
	Weapon(){}
	~Weapon(){}

private:
	bool		_castoff;
	int			_castoffState;//被处理的状态，当大于等于1时被删除
	bool		_beCover;//是否被unit遮挡
	int			_posIndex;//unit武器的挂节点索引
	float		_x;//发射点坐标
	float		_y;
	
	float		_moveX;
	float		_moveY;
	int			_dir;//方向
	int			_state;//
	bool		_bump;
	int			_wcampType;//子弹所属阵型
	WeaponRes	_weaponRes;
	/*Unit*		_unit;*/
};
#endif // __Weapon_h__
