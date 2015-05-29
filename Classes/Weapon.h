
#ifndef __Weapon_h__
#define __Weapon_h__

#include "cocos2d.h"

class Unit;
class Bullet;
class BulletExplosion;
class WeaponRes;
class GameScene;

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

class Weapon : public cocos2d::Node
{
public:
	//weapon sound
	//TODO
	
	Weapon(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, float moveX, float moveY, int dir, int posIndex, int weaponCampType);
	virtual ~Weapon();
	static Weapon* create(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, float moveX, float moveY, int dir, int posIndex, int weaponCampType);
	bool  init(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, float moveX, float moveY, int dir, int posIndex, int weaponCampType);
	void perform(float dt);
	bool isCastoff() { return _castoff; }
	void setCastoff() { _castoff = true; }
	int getCastoffStage() { return _castoffState; }
	std::shared_ptr<WeaponRes> getWeaponRes() { return _weaponRes; }
	int getPosIndex() { return _posIndex; }
private:
	bool		_castoff;
	int			_castoffState;//�������״̬�������ڵ���1ʱ��ɾ��
	bool		_beCover;//�Ƿ�unit�ڵ�
	int			_posIndex;//unit�����Ĺҽڵ�����
	float		_x;//���������
	float		_y;
	
	float		_moveX;
	float		_moveY;
	int			_dir;//����
	int			_state;//
	bool		_bump;
	int			_wcampType;//�ӵ���������
	std::shared_ptr<WeaponRes>	_weaponRes;
	Unit*		_unit;
	Bullet*		_bullet;
	GameScene*  _gameLayer;
};
#endif // __Weapon_h__
