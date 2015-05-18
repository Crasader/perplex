#ifndef __WeaponRes_h__
#define __WeaponRes_h__

#include "Head.h"
using namespace std;

class WeaponRes
{
public:
	static const int WEAPONDIRECTIONCOUNT = 8;
	static const int WEAPONANIMDIRECTIONCOUNT = 5;
	static const int MAX_WEAPONRES_COUNT = 24;

	WeaponRes(int id);

	void init(int id);
	bool isFlame() { return _flame; }
	bool isFlameFollowUnit() { return _flameFollowUnit; }
	bool isLockUnit() { return _lockUnit; }

	int getFlameAnimID() { return _flameAnimID; }
	int getBulletAnimID() { return _bulletAnimID; }
	int getFireAnimID() { return _fireAnimID; }
	int getSmokeAnimID() { return _smokeAnimID; }
	int getExplodeAnimID() { return _explordeAnimID; }
	int getShotFrame() { return _shotFrame; }
	int getPower() { return _power; }
	int getBulletLogicType() { return _bulletLogicType; }
	int getFireDelay() { return _fireDelay; }
	byte getSmokeDelay() { return _smokeDelay; }
	int getSpeed() { return _speed; }
	Rect getWeaponHitRect() { return _weaponHitRect; }
	Vec2 getFlameAnimRange(int direction) { return _flameAnimRange[direction]; }
	Vec2 getBulletAnimRange(int direction) { return _bulletAnimRange[direction]; }
	Vec2 getFireAnimRanger(int direction) { return _fireAnimRange[direction]; }
	int getID() const;
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
	byte	_smokeDelay;//产生烟雾延迟
	Rect	_weaponHitRect;//子弹攻击矩形
	int		_speed;//子弹速度

	vector<Vec2>	_flameAnimRange;
	vector<Vec2>	_bulletAnimRange;
	vector<Vec2>	_fireAnimRange;
};

#endif // __WeaponRes_h__
