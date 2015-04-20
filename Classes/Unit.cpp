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
#include "HelloWorldScene.h"

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
	//EffectManager::createBigExplosion(getPosition());
	/*EffectManager::createBulletTwoExplosion(getPosition());*/
	//EffectManager::createBulletTwoExplosion(getPosition() + Point(20, 20));
	//EffectManager::createBulletTwoExplosion(getPosition() + Point(-20, -20));
    auto helloworld = (HelloWorld*)Director::getInstance()->getRunningScene()->getChildByTag(100);
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
    label->runAction(Sequence::create(scale, scaleBack,NULL));
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

void Unit::setMoveProb(int* aMoveProb, int len)
{
	if (aMoveProb == nullptr)
	{
		return;
	}
	_moveProb.clear();
	for (int i = 0; i < len; i++)
	{
		_moveProb.push_back(aMoveProb[i]);
	}
}

vector<Vec2> Unit::getMoveItemData() const
{
	return _moveItemData;
}

void Unit::setMoveItemData(Vec2* aMoveItemData, int len)
{
	if (aMoveItemData == nullptr)
	{
		return;
	}
	_moveItemData.clear();
	for (int i = 0; i < len; i++)
	{
		_moveItemData.push_back(aMoveItemData[i]);
	}
}

void Unit::setPointItemData(Vec2* aPointItemData, int len)
{
	if (aPointItemData == nullptr)
	{
		return;
	}
	_pointItemData.clear();
	for (int i = 0; i < len; i++)
	{
		_pointItemData.push_back(aPointItemData[i]);
	}
}

void Unit::setFireItemData(Vec2* aFireItemData, int len)
{
	if (aFireItemData == nullptr)
	{
		return;
	}
	_fireItemData.clear();
	for (int i = 0; i < len; i++)
	{
		_fireItemData.push_back(aFireItemData[i]);
	}
}

void Unit::setDistItemData(Vec2* aDistItemData, int len)
{
	if (aDistItemData == nullptr)
	{
		return;
	}
	_distItemData.clear();
	for (int i = 0; i < len; i++)
	{
		_distItemData.push_back(aDistItemData[i]);
	}
}

void Unit::setDropToolData(XDropTool* aDropToolData, int len)
{
	if (aDropToolData == nullptr)
	{
		return;
	}
	_dropToolData.clear();
	for (int i = 0; i < len; i++)
	{
		_dropToolData.push_back(aDropToolData[i]);
	}
}

void Unit::setUnitRecycleOrder(XUnitOrder* aUnitRecycleOrder, int len)
{
	if (aUnitRecycleOrder == nullptr)
	{
		return;
	}
	_unitRecycleOrder.clear();
	for (int i = 0; i < len; i++)
	{
		_unitRecycleOrder.push_back(aUnitRecycleOrder[i]);
	}
}
