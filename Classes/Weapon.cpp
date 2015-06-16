#include "Weapon.h"
#include "Unit.h"
#include "WeaponRes.h"
#include "Bullets.h"
#include "GameScene.h"
#include "UnitManager.h"
#include "consts.h"

USING_NS_CC;

Weapon::Weapon(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, const cocos2d::Vec2& pos, const cocos2d::Vec2& vec, int dir, int posIndex, int weaponCampType) :_castoff(false)
, _castoffState(0)
, _beCover(false)
, _posIndex(posIndex)
, _unit(unit)
, _dir(dir)
, _state(W_FLAME)
, _bullet(nullptr)
, _wcampType(weaponCampType)
, _gameLayer(gameLayer)
, _weaponRes(weaponRes)
{

}

void Weapon::perform( float dt )
{
	switch (_state)
	{
	case W_BULLET:
		//当子弹被废弃时表明子弹与其它物体相碰或者飞出屏幕
		if (_bullet->isCastoff())
		{
			//将武器主体删除(不包括武器组件)
			_castoff = true;
			_state = W_CASTOFF;
		}
		//当子弹与物体相碰, 产生爆炸和弹坑
		if (_bullet->isBump() && !_bump)
		{
			_bump = true;
			//#todo生成爆炸
			
		}
		break;
	case W_CASTOFF:
		_castoffState++;
		break;
	}
}

bool Weapon::init(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, const cocos2d::Vec2& pos, const cocos2d::Vec2& vec, int dir, int posIndex, int weaponCampType)
{
	_state = W_BULLET;
	_bullet = _gameLayer->getSpriteManager()->createBullet(weaponRes, pos, vec, dir, weaponCampType);;
	_bullet->setType(_weaponRes->getID());
	CC_SAFE_RETAIN(_bullet);
	return true;
}

Weapon* Weapon::create(GameScene* gameLayer, std::shared_ptr<WeaponRes> weaponRes, Unit* unit, const cocos2d::Vec2& pos, const cocos2d::Vec2& vec, int dir, int posIndex, int weaponCampType)
{
	auto weapon = new Weapon(gameLayer, weaponRes, unit, pos, vec, dir, posIndex, weaponCampType);
	if (weapon && weapon->init(gameLayer, weaponRes, unit, pos, vec, dir, posIndex, weaponCampType))
	{
		weapon->autorelease();
		return weapon;
	}
	CC_SAFE_DELETE(weapon);
	return nullptr;
}

Weapon::~Weapon()
{
	CC_SAFE_RELEASE(_bullet);
}