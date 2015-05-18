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
		case 0://��Ļ����
			//#�������
			_active = true;
			break;
		case 1://�Ϸ���
			_active = true;
			break;
		case 2://����
			_active = true;
			break;
		case 3://�·���
			_active = true;
			break;
		case 4://������
			_active = true;
			break;
		default://��Ļ���ߣ��Ϸ�)
			break;
		}
	}
	else if (isNeedDelete())
	{
		_castoff = true;
		return;
	}
	//�����ѷ��������
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
		//������¼����壬��һֱ��������״̬�������ᱻ�е�ɾ��
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
	//�˹�����
	AI();
	//��������
	processDie();

	//�ƶ�
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
//����ָ��
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
	//#��׼Player
	if (IntWDir == WALK_DIR_COUNT)
	{
		if (_player != nullptr)//����player
		{

		}
		else
		{
			IntWDir = 0;
		}
	}

	//#ָ�������
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
	//���
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
	//#��׼Player
	if (IntWDir == WALK_DIR_COUNT)
	{
		if (nullptr != nullptr)//����player
		{

		}
		else
		{
			IntWDir = 0;
		}
	}

	//#ָ�������
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
	//���
	if (_unitRecycleOrder[_currentOrderIndex].BulletType != 0
		&& _unitRecycleOrder[_currentOrderIndex].FireLogic != 0)
	{
		fireRequire(_unitRecycleOrder[_currentOrderIndex].FireLogic, _unitRecycleOrder[_currentOrderIndex].BulletType);
	}
	_currentOrderIndex++;
}

//�ı䶯��
void Unit::setMotionAndDIR(int motion, int wdir)
{
	if (_walkDir != wdir || _motion != motion)
	{
		_motion = motion;
		_walkDir = wdir;
		if (_motion == 1)//��·
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

//ָ����ƽӿ�
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
			//���ű�ը
		}
		if (!explode)
		{
			explodeOK = false;
		}
	}
	if (explodeOK)
	{
		_dieTick++;
		if (_dieTick == 1)//������Ч
		{
		}
		else if (_dieTick == 8)
		{
			//������߲���
			processTool();
		}
	}
	if (true)//����������
	{
		if (_dieTick >= 320)
		{
			_die = true;
		}
	}
}
//������߲���
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
		//int type;//���ͣ�0��ʾѪ����1��ʾ����, 2��ʾ�������
		//int min;//��ʾ���Ͷ�Ӧ�����������ĳ����Χ�ڣ���ΧȡֵΪ min <= prop < max�����type == 3��ô������������Ч
		//int max;
		//int prob;//�����Ӧ���ߵĿ����ԣ����type == 3��ô����������ߣ����ðٷֱȸ���
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
	_moveType = 2;//���ð��չ̶�·������
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
				//#��ըЧ��
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

