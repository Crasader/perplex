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

bool Unit::hurt(float damage)
{
    _HP -= damage;
    if(_HP <= 0)
    {
        die();
        return true;
    }
    return false;
}
void Unit::die()
{
    CocosDenshion::SimpleAudioEngine::getInstance()->playEffect("explodeEffect.mp3");
	/* auto helloworld = (HelloWorld*)Director::getInstance()->getRunningScene()->getChildByTag(100);
	 int score = helloworld->getScore();
	 helloworld->setScore(score+=_score);
	 std::stringstream ss;
	 std::string str;
	 ss<<score;
	 ss>>str;
	 const char *p = str.c_str();
	 helloworld->getScoreLabel()->setString(p);
	 _alive = false;
	 auto scale = ScaleTo::create(0.1, 1.2);
	 auto scaleBack = ScaleTo::create(0.1, 1);
	 auto label =helloworld->getScoreLabel();
	 label->runAction(Sequence::create(scale, scaleBack,NULL));*/
    //removeFromParent();
}

void Unit::move(float y, float dt)
{
    //setPosition(getPosition().x+getPosition().y+y);
    forward(y);
}

void Unit::reset()
{
    _alive = true;
}
bool Unit::alive()
{
    return _alive;
}

Unit::Unit() 
:GameEntity()
,_alive(0)
, _HP(0)
, _score(0)
{

}

Unit::Unit(bool alive, float hp, int score)
:GameEntity()

{

}

Unit::Unit(int type, int shadowType, int radius, bool alive, float hp, int score)
:GameEntity(type, shadowType, score)
, _alive(alive)
, _HP(hp)
, _score(score)
{

}

vector<int> Unit::getMoveProb() const
{
	return _moveProb;
}

void Unit::setMoveProb(std::vector<int> aMoveProb)
{

	_moveProb.clear();
	_moveProb = aMoveProb;
}

vector<Vec2> Unit::getMoveItemData() const
{
	return _moveItemData;
}

void Unit::setMoveItemData(vector<Vec2> aMoveItemData)
{
	_moveItemData.clear();
	_moveItemData = aMoveItemData;
}

void Unit::setPointItemData(vector<Vec2> aPointItemData)
{
	_pointItemData.clear();
	_pointItemData = aPointItemData;
}

void Unit::setFireItemData(vector<Vec2> aFireItemData)
{

	_fireItemData.clear();
	_fireItemData = aFireItemData;
}

void Unit::setDistItemData(vector<Vec2> aDistItemData)
{
	_distItemData.clear();
	_distItemData = aDistItemData;
}

void Unit::setDropToolData(vector<XDropTool> aDropToolData)
{
	_dropToolData.clear();
	_dropToolData = aDropToolData;
}

void Unit::setUnitRecycleOrder(vector<XUnitOrder> aUnitRecycleOrder)
{

	_unitRecycleOrder.clear();
	_unitRecycleOrder = aUnitRecycleOrder;
}


void Unit::perfrom()
{
	if (!_active)
	{
		switch (_AIType)
		{
		case 0://屏幕顶线
			//#如果超出
			_active = true;
			break;
		case 1://上方线
			_active = true;
			break;
		case 2://中线
			_active = true;
			break;
		case 3://下方线
			_active = true;
			break;
		case 4://底下线
			_active = true;
			break;
		default://屏幕外线（上方)
			break;
		}
	}
	else if (isNeedDelete())
	{
		_castoff = true;
		return;
	}
	//处理已发射的武器
	for (auto it = _weapons.begin(); it != _weapons.end();)
	{
		if ((*it)->getCastoffStage())
		{
			it = _weapons.erase(it);
		}
		else
		{
			++it;
			(*it)->perform();
		}
	}
	if (_castoff)
	{
		//如果是事件物体，则将一直保留废弃状态，但不会被切底删除
		if (!_eventUnit)
		{
			_castoffStage++;
		}
		return;
	}
	if (_beAttackTick > 0)
	{
		_beAttackTick--;
	}
	if (_die)
	{
		_diaphaneity--;
		if (_diaphaneity < 0)
		{
			_diaphaneity = 0;
			_castoff = true;
		}
		return;
	}
	//人工智能
	AI();
	//死亡处理
	processDie();

	//移动
	unitMove();
}

bool Unit::isNeedDelete()
{
	if (_active)
	{
		if (getPositionX() <= -32)
		{
			return true;
		}
		return true;
	}
	return false;
}

void Unit::AI()
{
	if (_HP <= 0)
	{
		return;
	}
	if (_orderType == 0)
	{
		analyzeOrder();
	}
	else
	{
		analyzeRecycleOrder();
	}
}
//解析指令
void Unit::analyzeOrder()
{
	if (_orderDelay > 0)
	{
		_orderDelay--;
		return;
	}
	if (_currentOrderIndex >= _orderCount)
	{
		_orderType = 1;
		_currentOrderIndex = 0;
		return;
	}
	_orderDelay = _unitOrder[_currentOrderIndex].Time;
	int motion = 0;
	if (_unitOrder[_currentOrderIndex].Speed != 0)
	{
		motion = 1;
	}
	_shotPosIndex = 0;
	auto wdir = 0;
	auto IntWDir = _unitOrder[_currentOrderIndex].Direct;
	//#对准Player
	if (IntWDir == WALK_DIR_COUNT)
	{
		if (_player != nullptr)//面向player
		{

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
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT_DOWN:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_LEFT_UP:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_UP:
		_moveX = 0;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT_UP:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_RIGHT_DOWN:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	}
	//射击
	if (_unitOrder[_currentOrderIndex].BulletType != 0
		&& _unitOrder[_currentOrderIndex].FireLogic != 0)
	{
		fireRequire(_unitOrder[_currentOrderIndex].FireLogic, _unitOrder[_currentOrderIndex].BulletType);
	}
	_currentOrderIndex++;
}

void Unit::analyzeRecycleOrder()
{
	if (_unitRecycleOrder.empty())
	{
		return;
	}
	if (_orderDelay > 0)
	{
		_orderDelay--;
		return;
	}
	if (_currentOrderIndex > (int)_unitRecycleOrder.size())
	{
		_currentOrderIndex = 0;
	}
	_orderDelay = _unitRecycleOrder[_currentOrderIndex].Time;
	auto motion = 0;
	if (_unitRecycleOrder[_currentOrderIndex].Speed != 0)
	{
		motion = 1;
	}
	_shotPosIndex = 0;
	auto wdir = 0;
	auto IntWDir = _unitOrder[_currentOrderIndex].Direct;
	//#对准Player
	if (IntWDir == WALK_DIR_COUNT)
	{
		if (nullptr != nullptr)//面向player
		{

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
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT_DOWN:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_LEFT:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_LEFT_UP:
		_moveX = -_unitOrder[_currentOrderIndex].Speed;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_UP:
		_moveX = 0;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT_UP:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = _unitOrder[_currentOrderIndex].Speed;
		break;
	case D_S_RIGHT:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = 0;
		break;
	case D_S_RIGHT_DOWN:
		_moveX = _unitOrder[_currentOrderIndex].Speed;
		_moveY = -_unitOrder[_currentOrderIndex].Speed;
		break;
	}
	//射击
	if (_unitRecycleOrder[_currentOrderIndex].BulletType != 0
		&& _unitRecycleOrder[_currentOrderIndex].FireLogic != 0)
	{
		fireRequire(_unitRecycleOrder[_currentOrderIndex].FireLogic, _unitRecycleOrder[_currentOrderIndex].BulletType);
	}
	_currentOrderIndex++;
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
			
		}
		else if (_motion == 0)
		{

		}
		else
		{

		}
	}
}

//指令控制接口
void Unit::fireRequire(int logicType, int weaponResID)
{
	if (_gameScene->getPlayer() && _gameScene->getPlayer()->getPower() > 0)//player
	{
		_unitFireRequire = true;
		_shotLogicType = logicType;
		_weaponResID = weaponResID;
	}
}

void Unit::processDie()
{
	if (_motion != 2)
	{
		return;
	}
	auto explodeOK = true;
	for (auto explode : _dieExplode)
	{
		if (!explode)
		{
			explode = true;
			//播放爆炸
		}
		if (!explode)
		{
			explodeOK = false;
		}
	}
	if (explodeOK)
	{
		_dieTick++;
		if (_dieTick == 1)//播放音效
		{
		}
		else if (_dieTick == 8)
		{
			//处理道具产生
			processTool();
		}
	}
	if (true)//动画播放完
	{
		if (_dieTick >= 320)
		{
			_die = true;
		}
	}
}
//处理道具产生
void Unit::processTool()
{
	if (_player == nullptr)
	{
		return;
	}
	auto prob = 0;
	for (auto dropTool : _dropToolData)
	{
		//XDropTool*        
		//int type;//类型：0表示血包，1表示武器, 2表示任意情况
		//int min;//表示类型对应的玩家属性在某个范围内，范围取值为 min <= prop < max，如果type == 3那么这两个属性无效
		//int max;
		//int prob;//掉落对应道具的可能性，如果type == 3那么就是任意道具，采用百分比概率
		prob = rand_0_1() * 99;
		if ((dropTool.prob + prob) >= 100)
		{
			switch (dropTool.type)
			{
			case 0:

				break;
			default:
				break;
			}
		}
	}
}

void Unit::unitMove()
{
	auto newX = getPositionX() + _moveX;
	auto newY = getPositionY() + _moveY;

	_endableMove = enableMove(newX, newY);
	if (_endableMove)
	{
		setPositionX(newX);
		setPositionY(newY);
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
	//#
	if (enable)
	{

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

void Unit::setNewRoad(std::vector<cocos2d::Vec2> _roads)
{
	_pointItemData = _roads;
	_moveType = 2;//设置按照固定路线行走
	_active = true;
}

int Unit::beAttack(const cocos2d::Rect rect, int power)
{
	if (_power <= 0)
	{
		return 0;
	}
	for (auto a : _unitRes->_bodyRect[0])
	{
		auto tempRect = cocos2d::Rect(a->origin.x + getPositionX(), a->origin.y + getPositionY(), a->size.width, a->size.height);
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
				setMotionAndDIR(2, D_S_DOWN);
				_startExplodeTick = 0;
				//#爆炸效果
				if (_campType == Enemy)
				{
					_gameScene->addScore(UNIT_DIE_SCORE[_type]);
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

