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
	bool	_flame;//�Ƿ��л���
	bool	_flameFollowUnit;//�����Ƿ����unit
	bool	_lockUnit;//�Ƿ���סunit�ķ���

	int		_flameAnimID;//����Ķ�������
	int		_bulletAnimID;//�ӵ��Ķ�������
	int		_fireAnimID;//�ӵ�������Ķ�������
	int		_smokeAnimID;//β�̵Ķ�������
	int		_explordeAnimID;//��ը��������

	int		_shotFrame;//�����ڵڼ�֡�����ӵ�
	int		_power;//�ӵ�����
	int		_bulletLogicType;//�ӵ����߼�����
	int		_fireDelay;//������ӳ�
	byte	_smokeDelay;//���������ӳ�
	Rect	_weaponHitRect;//�ӵ���������
	int		_speed;//�ӵ��ٶ�

	vector<Vec2>	_flameAnimRange;
	vector<Vec2>	_bulletAnimRange;
	vector<Vec2>	_fireAnimRange;
};

#endif // __WeaponRes_h__
