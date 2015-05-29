#include "building.h"
#include "buildingres.h"
#include "GameScene.h"
#include "tool.h"
#include "XDropTool.h"
#include "Unit.h"
#include "UnitManager.h"
#include "CameraExt.h"

int Building::beAttack(const cocos2d::Rect &rect, int hp)
{
	if (_HP <= 0)
	{
		return 0;
	}
	if (_buildingRes->_buildingRects.empty())
	{
		return 0;
	}
	for (auto a : _buildingRes->_buildingRects)
	{
		cocos2d::Rect tempRect(a);
		if (_flipH)
		{
			int l, t, r, b;
			l = a.getMinX();
			t = a.getMinY();
			r = a.getMaxX();
			b = a.getMaxY();
			tempRect.setRect(-r, t, -l, b);
		}
		tempRect.origin.x += _middleX;
		tempRect.origin.y += _middleY;
		if (tempRect.intersectsRect(rect))
		{
			if (!_enableHarm)
			{
				return 1;
			}
			_HP -= hp;
			if (_HP <= 0)
			{
				_enableHarm = false;
				_dieTick = 1;
				_HP = 0;
				_state = 2;
				if (_dieExplode.empty())
				{
					_dieExplode.resize(_buildingRes->_exploreCount);
				}
// 				for (auto &a : _dieExplode)
// 				{
// 					a = false;
// 				}
				if (_flipH)
				{

				}
				//�ӷ�
				if (_buildingRes->_type == 4 || _buildingRes->_type == 5)
				{
					_gameScene->addScore(200);
				}
				else
				{
					_gameScene->addScore(100);
				}
			}
			return 1;
		}
	}
	return 0;
}

void Building::processTool()
{
	auto player = _gameScene->getPlayer();
	if (player == nullptr)
	{
		return;
	}
	//�������
	for (auto tool : _dropToolDatas)
	{
		//XDropTool*  		
		//int type;//���ͣ�0��ʾѪ����1��ʾ����, 2��ʾ�������
		//int min;//��ʾ���Ͷ�Ӧ�����������ĳ����Χ�ڣ���ΧȡֵΪ min <= prop < max�����type == 3��ô������������Ч
		//int max;
		//int prob;//�����Ӧ���ߵĿ����ԣ����type == 3��ô����������ߣ����ðٷֱȸ���
		auto prob = rand() % 100;
		if ((tool.prob + prob) >= 100)
		{
			switch (tool.type)
			{
			case 0:
				_gameScene->getSpriteManager()->createTool(55, 50, 55, getPositionX(), getPositionY() + 8, 0);
				break;
			case 1:
				_gameScene->getSpriteManager()->createTool(55, 50, 55, getPositionX(), getPositionY() + 8, 1);
				break;
			case 2:
				_gameScene->getSpriteManager()->createTool(55, 50, 55, getPositionX(), getPositionY() + 8, 2);
				break;
			case 3:
				_gameScene->getSpriteManager()->createTool(55, 50, 55, getPositionX(), getPositionY() + 8, 3);
				break;
			case 4:
				_gameScene->getSpriteManager()->createTool(55, 50, 55, getPositionX(), getPositionY() + 8, 4);
				break;
			default:
				break;
			}
		}
	}
}

void Building::processDie()
{
	if (_dieTick < 1)
	{
		return;
	}
	auto explodeOK = true;
// 	for (auto &a : _dieExplode)
// 	{
// 		_dieTick++;
// 		if (!a)
// 		{
// 			a = true;
// 		}
// 
// 		//#������ը
// 		if (!a)
// 		{
// 			explodeOK = false;
// 		}
// 	}
	//��ը���
	if (explodeOK)
	{

		processTool();
	}
}

void Building::processUnitFactory( float dt )
{
	if (_state >= 2 ||
		_unitFactoryDatas.empty() ||
		_unitFactoryIndex >= (int)_unitFactoryDatas.size())
	{
		return;
	}
	if (!_factory &&  _gameScene->getPlayer())
	{
		cocos2d::Rect lookRect(getPositionX() - 150, getPositionY() - 150, 300, 300);
		auto p = _gameScene->getPlayer()->getPosition();
		p.y += _gameScene->getCamera()->getY();
		if (_gameScene->getPlayer() != nullptr && lookRect.containsPoint(p))
		{
			_factory = true;
			_unitFactoryTick = 0;
		}
		return;
	}
/*	_unitFactoryTick += dt;*/
// 	if (_unitFactoryTick <= _unitFactoryDatas[_unitFactoryIndex].interval)
// 	{
// 		return;
// 	}

	if (_unitFactoryTick == 0)
	{
		_unitFactoryTick = 1;
		auto createUnit = [&](){
			for (auto i = 0; i < _unitFactoryDatas[_unitFactoryIndex].unitCount; i++)
			{
				auto type = _unitFactoryDatas[_unitFactoryIndex].unitType;
				auto x = _unitFactoryDatas[_unitFactoryIndex].left + rand() / _unitFactoryDatas[_unitFactoryIndex].width;
				auto y = _unitFactoryDatas[_unitFactoryIndex].top + rand() / _unitFactoryDatas[_unitFactoryIndex].height;
				_gameScene->getSpriteManager()->createDefaultUnit(type, x, y);
				_gameScene->addLevelEnemy(1);
			}
			_unitFactoryTick = 0;
			_unitFactoryIndex++;
		};
		runAction(Sequence::create(DelayTime::create(_unitFactoryDatas[_unitFactoryIndex].interval), CallFunc::create(createUnit), nullptr));
	}
// 	
// 
// 	_unitFactoryTick = 0;
// 	_unitFactoryIndex++;
}

void Building::perform(float dt)
{
	if (_castoff)
	{
		_castoffStage++;
		return;
	}
	if (!_active)
	{
		log("%f, %f", getPositionY(), _gameScene->getCamera()->getY() + _gameScene->getSceneHeight());
		if (getPositionY() <= _gameScene->getCamera()->getY() + _gameScene->getSceneHeight())
		{
			_active = true;
		}
		return;
	}
	else
	{
		if (getPositionY() < _gameScene->getCamera()->getY() - _gameScene->getSceneHeight())
		{
			_castoff = true;
			_active = false;
		}
	}
	processUnitFactory(dt);
	processDie();
}

Building::Building(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph)
	:GameEntity()
,_gameScene(gameScene)
, _buildingRes(buildingRes)
, _buildingID(buildingID)
, _factory(false)
, _unitFactoryTick(0)
, _unitFactoryIndex(0)
, _middleX(0)
, _middleY(0)
, _state(state)
, _dieTick(0)
, _flipH(fliph)
, _enableHarm(true)
, _active(false)
{
	_HP = buildingRes->_Hp;
	_walkRect = cocos2d::Rect(0, 0, 0, 0);
}

Building* Building::create(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph)
{
	auto ret = new Building(gameScene, buildingRes, buildingID, state, fliph);
	if (ret && ret->init(gameScene, buildingRes, buildingID, state, fliph))
	{
		ret->autorelease();
		return ret;
	}
	CC_SAFE_DELETE(ret);
	return nullptr;
}

bool Building::init(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph)
{
	auto s = cocos2d::String::createWithFormat("%d.png", _buildingRes->_stateImageID[state]);
	_Model = Sprite::create(s->getCString());
	if (_Model)
	{
		addChild(_Model);
		return true;
	}
	return false;
}

int Building::getBuildingID()
{
	return _buildingID;
}

void Building::setUnitFactory(VUF uf)
{
	_unitFactoryDatas = uf;
}

void Building::setDropTool(VDT dt)
{
	_dropToolDatas = dt;
}

bool Building::isCastoff()
{
	return _castoff;
}
