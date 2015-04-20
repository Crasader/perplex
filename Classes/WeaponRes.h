#ifndef __WeaponRes_h__
#define __WeaponRes_h__

#include "Head.h"
using namespace std;

class WeaponRes
{
private:
	int		_ID;
	bool	_flame;//是否有火舌
	bool	_flameFollowUnit;//火舌是否跟踪unit
	bool	_lockUnit;//是否锁住unit的方向

	int		_flameAnimID;//火舌的动画类型
	int		_bulletAnimID;//子弹的动画类型
	int		_fireAnimID;//子弹喷洒火的动画类型
	int		_smokeAnimID;//尾烟的动画类型
	int		_explordeAnimID;//爆炸动画类型

	int		_shotFrame;//火舌在第几帧发射子弹
	int		_power;//子弹威力
	int		_bulletLogicType;//子弹的逻辑类型
	int		_fireDelay;//发射的延迟
	int		_smokeDelay;//产生烟雾延迟
	Rect	_weaponHitRect;//子弹攻击矩形
	int		_speed;//子弹速度

	vector<Vec2>	_flameAnimRange;
	vector<Vec2>	_bulletAnimRange;
	vector<Vec2>	_fireAnimRange;
};

#endif // __WeaponRes_h__
