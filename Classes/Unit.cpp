/****************************************************************************
 Copyright (c) 2014 Chukong Technologies Inc.

 http://github.com/chukong/EarthWarrior3D

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#include "Unit.h"
#include "SimpleAudioEngine.h"
#include "Effects.h"
#include "GameScene.h"
#include "consts.h"
#include "UnitRes.h"
#include "Weapon.h"
#include "shotlogic.h"
#include "shotlogicmanager.h"
#include "Player.h"
#include "UnitResManager.h"
#include "CameraExt.h"
#include "MapManager.h"
#include "XMap.h"
#include "UnitManager.h"
#include "Explosion.h"
#include "AIBase.h"
#include "ShadowController.h"

bool Unit::alive()
{
	return _alive;
}

Unit::Unit()
:Unit(nullptr, -1, 0, 0, 1)
{
	_dieExplode = vector<bool>();
	_unitOrder = vector<XUnitOrder>();
	_moveProb = vector<int>();
	_moveItemData = vector<Vec2>();
	_pointItemData = vector<Vec2>();
	_fireItemData = vector<Vec2>();
	_distItemData = vector<Vec2>();
	_dropToolData = vector<XDropTool>();
	_unitRecycleOrder = vector<XUnitOrder>();
	_weapons = Vector<Weapon*>();
}

Unit::Unit(GameScene* gameScene, int unitID, int type, int walkdir, int camptype)
	:GameEntity(unitID, type)
	, _alive(true)
	, _active(false)
	, _eventUnit(false)
	, _unitFireRequire(false)
	, _die(false)
	, _endableMove(true)
	, _power(10)
	, _score(0)
	, _iDieExplodCount(0)
	, _startExplodeTick(0)
	, _campType(camptype)
	, _moveType(0)
	, _maxHP(1000)
	, _BodyLevel(0)
	, _state(UniteState::INVALID)
	, _orderType(0)
	, _currentOrderIndex(0)
	, _orderDelay(0)
	, _orderCount(0)
	, _moveX(0)
	, _moveY(0)
	, _motion(0)
	, _beAttackTick(0)
	, _diaphaneity(0)
	, _dieTick(0)
	, _walkDir(walkdir)
	, _AIType(0)
	, _shotPosIndex(0)
	, _shotLogicType(0)
	, _weaponResID(0)
	, _fireTick(0)
	, _walkRect()
	, _hitRect()
	, _gameScene(gameScene)
	, _player(nullptr)
	, _unitRes(nullptr)
	, _shotLogicManager(new ShotLogicManager())
	, _curMotion(nullptr)
	, _curMotionID(0)
	, _shadowdata(nullptr)
{
	_dieExplode = vector<bool>();
	_unitOrder = vector<XUnitOrder>();
	_moveProb = vector<int>();
	_moveItemData = vector<Vec2>();
	_pointItemData = vector<Vec2>();
	_fireItemData = vector<Vec2>();
	_distItemData = vector<Vec2>();
	_dropToolData = vector<XDropTool>();
	_unitRecycleOrder = vector<XUnitOrder>();
	_weapons = Vector<Weapon*>();
}

Unit::~Unit()
{
	_weapons.clear();
	if (_shadowdata)
	{
		_shadowdata->removeFromParent();
		CC_SAFE_RELEASE_NULL(_shadowdata);
	}
}

bool Unit::init(GameScene* gameScene, int unitID, int type, int walkdir, int camptype)
{
	if (!GameEntity::init())
	{
		return false;
	}
	log("%s", __FUNCTION__);
	auto unitRes = _gameScene->getUnitResManager().getUnitResFromID(_type);
	assert(unitRes != nullptr);
	if (unitRes == nullptr)
	{
		return false;
	}
	setUnitRes(unitRes);
	_shotLogicManager = std::shared_ptr<ShotLogicManager>(new ShotLogicManager());
	_AI = std::shared_ptr<AIBase>(new AIBase(_gameScene, this));
	return true;
}

vector<int> Unit::getMoveProb() const
{
	return _moveProb;
}

void Unit::setMoveProb(std::vector<int> aMoveProb)
{

	_moveProb = aMoveProb;
}

vector<Vec2> Unit::getMoveItemData() const
{
	return _moveItemData;
}

void Unit::setMoveItemData(vector<Vec2> aMoveItemData)
{
	_moveItemData = aMoveItemData;
}

void Unit::setPointItemData(vector<Vec2> aPointItemData)
{
	_pointItemData = aPointItemData;
}

void Unit::setFireItemData(vector<Vec2> aFireItemData)
{

	_fireItemData = aFireItemData;
}

void Unit::setDistItemData(vector<Vec2> aDistItemData)
{
	_distItemData = aDistItemData;
}

void Unit::setDropToolData(vector<XDropTool> aDropToolData)
{
	_dropToolData = aDropToolData;
}

void Unit::setUnitRecycleOrder(vector<XUnitOrder> aUnitRecycleOrder)
{

	_unitRecycleOrder = aUnitRecycleOrder;
}


void Unit::setMoveRect(Rect aMoveRect)
{
	_hitRect = aMoveRect;
}

void Unit::setUnitRes(std::shared_ptr<UnitRes> aUnitRes)
{
	assert(aUnitRes != nullptr);
	_unitRes = aUnitRes;
	setMotionAndDIR(0, _walkDir);
	_maxHP = _power = _unitRes->_HP;
	//主角
	if (_unitRes && _unitRes->_ID == 0)
	{
		return;
	}
	//敌人
	if (_gameScene->getDefficulty() >= 2)
	{
		_power += (_maxHP >> 1);
	}
	else if (_gameScene->getDefficulty() < 1)
	{
		_power = (_maxHP >> 1);
	}
}

void Unit::perform(float dt)
{
	log("enter unit::perform...%s,unitid = %d", __FUNCTION__, _id);
	if (_shadowdata)
	{
		_shadowdata->updateShadow(dt);
	}	
	//处理已发射的武器
	performWeapon(dt);
	switch (_state)
	{
	case UniteState::INVALID:
		_active = isActivate();
		if (_active)
		{
			_state = UniteState::ACTIVATE;
		}
		break;
	case UniteState::ACTIVATE:
	
		if (_AI)
		{
			_AI->perform(dt);
		}
		//处理待发射的武器
		fire(dt);
		log("exit unit::perfrom...%s, %d", __FUNCTION__, _id);
		break;
	case UniteState::EXPLOSION:
		processExplosion(dt);
		break;
	case UniteState::DEATH:
		//死亡处理
		processDie(dt);
		break;
	case UniteState::CASTOFF:
		if (_castoff)
		{
			log("enter unit::perform...%s,unitid = %d, needDelete", __FUNCTION__, _id);
			castoffStage();
			if (_shadowdata)
			{
				_shadowdata->removeFromParent();
			}
			return;
		}
		break;
	case UniteState::RELIVE:
		_state = UniteState::ACTIVATE;
		setVisible(true);
		break;
	default:
		break;
	}
	if (isNeedDelete())
	{
		_state = UniteState::CASTOFF;
		_castoff = true;
	}
}


void Unit::castoffStage()
{
	//如果是事件物体，则将一直保留废弃状态，但不会被切底删除
	if (!_eventUnit)
	{
		_castoffStage++;
	}
}

void Unit::performWeapon(float dt)
{
	for (auto it = _weapons.begin(); it != _weapons.end();)
	{
		if ((*it)->getCastoffStage())
		{
			it = _weapons.erase(it);
		}
		else
		{
			(*it)->perform(dt);
			++it;
		}
	}
}

bool Unit::isNeedDelete()
{
	if (_active)
	{
		if (getPositionInCamera().x <= -32 || getPositionInCamera().x >= _gameScene->getMapManager()->getActiveMap()->getWidth() + 32)
		{
			return true;
		}
		if (getPositionInCamera().y <= (_gameScene->getCamera()->getY() - _gameScene->getSceneHeight()) ||
			getPositionInCamera().y >= (_gameScene->getCamera()->getY() + _gameScene->getSceneHeight() + 70))
		{
			return true;
		}
	}
	return false;
}

void Unit::AI(float dt)
{
	if (_power <= 0)
	{
		_power = 0;
		log("hp is zero!");
		return;
	}
	if (_orderType == 0)
	{
		analyzeOrder(dt);
	}
	else
	{
		analyzeRecycleOrder(dt);
	}
}
void Unit::analyzeOrder(vector<XUnitOrder>& orders, float dt)
{
	if (orders.empty())
	{
		_orderType = 1;
		return;
	}
	if (_orderDelay > 0)
	{
		_orderDelay -= dt;
		return;
	}
	if (_currentOrderIndex >= (int)orders.size())
	{
		_orderType = 1;
		_currentOrderIndex = 0;
		return;
	}
	_orderDelay = orders[_currentOrderIndex].Time;
	int motion = 0;
	if (orders[_currentOrderIndex].Speed != 0)
	{
		motion = 1;
	}
	_shotPosIndex = 0;
	auto wdir = 0;
	auto IntWDir = orders[_currentOrderIndex].Direct;
	//#对准Player
	if (IntWDir == WALK_DIR_COUNT)
	{
		if (_gameScene->getPlayer() != nullptr)//面向player
		{
			//find angle difference
			auto angleDeg = calculeAngle();
			setRotation(angleDeg + getRotation());
			IntWDir = calDirction(angleDeg);
		}
		else
		{
			IntWDir = 0;
		}
	}

	//#指定发射口
	else if (IntWDir > WALK_DIR_COUNT)
	{
		_shotPosIndex = IntWDir - WALK_DIR_COUNT + 1;
		IntWDir = 0;
	}

	wdir = IntWDir;
	setMotionAndDIR(motion, wdir);
	switch (IntWDir)
	{
	case D_S_DOWN:
		_moveX = 0;
		_moveY = -orders[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT_DOWN:
		_moveX = -orders[_currentOrderIndex].Speed;
		_moveY = -orders[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT:
		_moveX = -orders[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_LEFT_UP:
		_moveX = -orders[_currentOrderIndex].Speed;
		_moveY = orders[_currentOrderIndex].Speed;
		break;
	case D_S_UP:
		_moveX = 0;
		_moveY = orders[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT_UP:
		_moveX = orders[_currentOrderIndex].Speed;
		_moveY = orders[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT:
		_moveX = orders[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_RIGHT_DOWN:
		_moveX = orders[_currentOrderIndex].Speed;
		_moveY = -orders[_currentOrderIndex].Speed;
		break;
	}
	//射击
	if (orders[_currentOrderIndex].BulletType != 0
		&& orders[_currentOrderIndex].FireLogic != 0)
	{
		fireRequire(orders[_currentOrderIndex].FireLogic, orders[_currentOrderIndex].BulletType);
	}
	_currentOrderIndex++;
}
//解析指令
void Unit::analyzeOrder(float dt)
{
	analyzeOrder(_unitOrder, dt);
}

void Unit::analyzeRecycleOrder(float dt)
{
	analyzeOrder(_unitRecycleOrder, dt);
}

//改变动作
void Unit::setMotionAndDIR(int motion, int wdir)
{
	if (_walkDir != wdir || _motion != motion)
	{
		_motion = motion;
		_walkDir = wdir;
		if (_motion == 1)//走路
		{
			/*_curMotion = _motions[0];*/
		}
		else if (_motion == 0)
		{
			/*_curMotion = _motions[0];*/
			_moveY = 0;
			_moveX = 0;;
		}
		else
		{
			/*_curMotion = _motions[0];*/
			_moveY = 0;
			_moveX = 0;
		}
		auto angle = wdir * 45;
		this->setRotation(angle);
	}
}
//指令控制接口
void Unit::fireRequire(int logicType, int weaponResID)
{
	if (_gameScene->getPlayer() != nullptr && _gameScene->getPlayer()->getPower() <= 0)//player
	{
		return;
	}
	_unitFireRequire = true;
	_shotLogicType = logicType;
	_weaponResID = weaponResID;

}

void Unit::processDie(float dt)
{
	_dieTick++;
	if (_dieTick == 1)//播放音效
	{
	}

	if (_dieTick >= 320)
	{
		_state = UniteState::CASTOFF;
		_castoff = true;
		_die = true;
	}
}
//处理道具产生
void Unit::processTool()
{
	CCLOG("%s", __FUNCTION__);
	auto player = _gameScene->getPlayer();
	if (player == nullptr)
	{
		return;
	}
	auto prob = 0;
	log("processTool entry....,%d", __LINE__);
	for (auto dropTool : _dropToolData)
	{
		//XDropTool*        
		//int type;//类型：0表示血包，1表示武器, 2表示任意情况
		//int min;//表示类型对应的玩家属性在某个范围内，范围取值为 min <= prop < max，如果type == 3那么这两个属性无效
		//int max;
		//int prob;//掉落对应道具的可能性，如果type == 3那么就是任意道具，采用百分比概率
		log("processTool itertor....,%d, type %d", __LINE__, dropTool.type);
		prob = CCRANDOM_0_1() * 100;
		prob += dropTool.prob;
		if (prob >= 100)
		{
		switch (dropTool.type)
		{
		case 0:
		_gameScene->getUnitManager().createTool("blood", getPositionInCamera().x, getPositionInCamera().y, 0);
		break;
		default:
		_gameScene->getUnitManager().createTool("blood", getPositionInCamera().x, getPositionInCamera().y, 0);
		break;
		}
		}
	}
}

void Unit::unitMoveTo(const cocos2d::Vec2& pos)
{
	_endableMove = enableMove(pos.x, pos.y);
	if (_endableMove)
	{
		setPosition(pos);
	}
}

bool Unit::enableMove(int newX, int newY)
{
	bool enable = false;
	auto tempRect(_walkRect);
	auto l = tempRect.getMinX();
	auto t = tempRect.getMinY();
	auto r = tempRect.getMaxX();
	auto b = tempRect.getMaxY();
	enable = _gameScene->getUnitCollisionToMap(tempRect);
	if (enable)
	{
		enable = _gameScene->getUnitCollision(tempRect);
	}
	return enable;
}

void Unit::setEventUnit(bool eventUnit)
{
	_eventUnit = eventUnit;
}

int Unit::getPower()
{
	return _power;
}


void Unit::setHurt(int power)
{
	_power -= power;
}

void Unit::setNewRoad(std::vector<cocos2d::Vec2> _roads)
{
	_pointItemData = _roads;
	_moveType = 2;//设置按照固定路线行走
	_active = true;
	if (_AI)
	{
		_AI->resetRaod();
	}
}

int Unit::beAttack(const cocos2d::Rect rect, int power)
{
	if (_power <= 0)
	{
		return 0;
	}
	auto m = dynamic_cast<Armature*>(_Model);
	if (m)
	{
		auto tempRect = m->getBoundingBox();
		auto pos = getPositionInCamera();
		tempRect.origin.x += pos.x;
		tempRect.origin.y += pos.y;
		if (tempRect.intersectsRect(rect))
		{
			_beAttackTick = 1;
			_power -= power;
			if (_type == 0)
			{
				_gameScene->setPlayerPower(_power);
			}
			if (_power <= 0)
			{
				_power = 0;
				_state = UniteState::EXPLOSION;
				setMotionAndDIR(2, D_S_DOWN);
				_dieExplode.clear();
				_dieExplode.push_back(false);
				if (_campType == Enemy){
					//加分
					_gameScene->addScore(UNIT_DIE_SCORE[_id]);
					//加杀敌数量
					_gameScene->addKillEnemy(1);
				}
			}
			return 1;
		}
	}
	return 0;
}

int Unit::getShotDir()
{
	return _walkDir;
}

int Unit::calDirction(float angle)
{
	angle += 23;
	if (angle < 0)
	{
		angle += 360;
	}

	return angle / 45;
}

float Unit::calculeAngle()
{
	auto angleRad = (getPosition() - _gameScene->getPlayer()->getPosition()).getAngle();
	auto angleDeg = CC_RADIANS_TO_DEGREES(-1 * angleRad);
	return angleDeg;
}

void Unit::setUnitOrder(vector<XUnitOrder> aOrder)
{
	_unitOrder = aOrder;
}

bool Unit::isSceneTop()
{
	return (getPositionY() <= _gameScene->getCamera()->getY() + _gameScene->getSceneHeight() + 24);
}

bool Unit::isSceneButton()
{
	return (getPositionY() <= _gameScene->getCamera()->getY() - 12);
}

bool Unit::isSceneCenter()
{
	return (getPositionY() <= _gameScene->getCamera()->getY() + _gameScene->getSceneHeight() / 2);
}

bool Unit::isSceneUnder()
{
	return (getPositionY() <= _gameScene->getCamera()->getY() + _gameScene->getSceneHeight() / 4);
}

bool Unit::isSceneAbove()
{
	return ((getPositionY() <= _gameScene->getCamera()->getY() + 3 * _gameScene->getSceneHeight() / 4));
}

bool Unit::isSceneTopAbove()
{
	return (getPositionY() <= _gameScene->getCamera()->getY() + 3 * _gameScene->getSceneHeight() / 2);
}

bool Unit::isActivate()
{
	auto active = false;
	switch (_AIType)
	{
	case 0://屏幕顶线
		//#如果超出
		if (isSceneTop())
		{
			active = true;
		}
		break;
	case 1://上方线
		if (isSceneAbove())
		{
			active = true;
		}
		break;
	case 2://中线
		if (isSceneCenter())
		{
			active = true;
		}
		break;
	case 3://下方线
		if (isSceneUnder())
		{
			active = true;
		}
		break;
	case 4://底下线
		if (isSceneButton())
		{
			active = true;
		}
		break;
	default://屏幕外线（上方)
		if (isSceneTopAbove())
		{
			active = true;
		}
		break;
	}
	return active;
}

void Unit::fire(float dt)
{
	
	if (_power <= 0)
	{
		return;
	}
	if (_unitFireRequire)
	{
		CCLOG("fire......");
		_unitFireRequire = false;
	/*	auto rad = CC_DEGREES_TO_RADIANS(-getRotation() + 90);
		Vec2 vec = Vec2(cosf(rad), sinf(rad)) * -1;
		auto pos = getShotPos(1).rotateByAngle(Vec2::ZERO, -CC_DEGREES_TO_RADIANS(getRotation() - 90));*/
		/*pos += getPosition();*/
		_shotLogicManager->createShotLogic(this, this,  _shotLogicType, _weaponResID, 0);
	}
	auto list = _shotLogicManager->getShotLogics();
	for (size_t i = 0; i < list.size(); i++)
	{
		auto s = list[i];
		if (s == nullptr) continue;
		if (s->isEnd())
		{
			continue;
		}
		else
		{
			s->perform(dt);
		}
	}
	auto b = remove_if(list.begin(), list.end(), [](shared_ptr<ShotLogic> s){return s == nullptr ? true : s->isEnd(); });
	for (auto iter = b; b != list.end(); ++iter)
	{
		list.erase(b);
	}
}


void Unit::drawDebug(const cocos2d::Rect& rect)
{
#if COCOS2D_DEBUG
	auto bound = DrawNode::create();
	bound->drawRect(rect.origin, Vec2(rect.getMaxX(), rect.getMaxY()), Color4F::RED);
	addChild(bound);
#endif
}


void Unit::processExplosion(float dt)
{
	if (_motion != 2)
	{
		return;
	}
	if (!_dieExplode[0])
	{
		setVisible(false);
		_state = UniteState::DEATH;
		_dieExplode[0] = true;
		_gameScene->getUnitManager().createExplode(this, -1, this->getPositionInCamera(), CC_CALLBACK_0(Unit::processTool, this));
		if (_shadowdata)
		{
			_shadowdata->removeFromParent();
			CC_SAFE_RELEASE_NULL(_shadowdata);
		}
	}
}

void Unit::setMoveSpeed(std::vector<int> speed)
{
	_moveSpeed = speed;
}


void Unit::setMoveDir(std::vector<int> dir)
{
	_moveDir = dir;
}


void Unit::setMoveDelay(std::vector<int> delay)
{
	_moveDelay = delay;
}


cocos2d::Vec2 Unit::getShotPositionInWorld(int index, float angle, const cocos2d::Vec2& pos /*= Vec2::ZERO*/)
{
	auto shotPos = getShotPos(index);
	auto cur = shotPos.rotateByAngle(Vec2::ZERO, -CC_DEGREES_TO_RADIANS(angle - 90));
	return cur + pos;
}

void Unit::changeMotion(int motion)
{
	if (!_unitRes)
	{
		return;
	}

	auto m = _unitRes->_animIDs.at(motion);
	if (m.empty())
	{
		// log
		return;
	}

	auto armature = dynamic_cast<Armature*>(_Model);
	if (armature != nullptr)
	{
		auto a = armature->getAnimation();
		if (a)
		{
			a->play(m);
			_curMotionID = motion;
		}
	}
}

void Unit::setMotionData(std::vector<int> ids)
{
	_motionIDs = ids;
}

std::vector<int> Unit::getMotionData()
{
	return _motionIDs;
}

float Unit::getSpeed()
{
	return 200.0f;
}

cocos2d::Vec2 Unit::getShotPos(int posIndex)
{
	if (_unitRes->_weaponPos.empty() || posIndex >= (int)_unitRes->_weaponPos.size())
	{
		return Vec2::ZERO;
	}
	auto wepRes = _unitRes->_weaponPos[0][posIndex];
	return Vec2(wepRes->x, wepRes->y);
}

Rect Unit::getHitRect() const
{
	auto a = dynamic_cast<Armature*>(_Model);
	if (a)
	{
		return a->getBoundingBox();
	}
	return Rect::ZERO;
}


// void Unit::setPosition(float x, float y)
// {
// 	setPositionX(x);
// 	setPositionY(y);
// 	if (_AI != nullptr)
// 	{
// 		_AI->setPatrolX(x);
// 		_AI->setPatrolY(y);
// 	}
// }


void Unit::relive()
{
	_dieTick = 0;
	_state = UniteState::RELIVE;
}

// 
// void Unit::setPosition(const Vec2& pos)
// {
// 	setPosition(pos.x, pos.y);
// }

void Unit::setPatrol(const Vec2& pos)
{
	if (_AI != nullptr){ _AI->setPatrolX(pos.x); _AI->setPatrolY(pos.y); }
}

GameScene* Unit::getGameScene()
{
	return _gameScene;
}

