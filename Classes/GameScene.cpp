#include "GameScene.h"
#include "cocostudio/CocoStudio.h"
#include "ui/CocosGUI.h"
#include "GameLayer.h"
#include "Configure.h"
#include "GameOverLayer.h"
#include "EventManager.h"
#include "XMap.h"
#include "tool.h"
#include "CameraExt.h"
#include "Unit.h"
#include "MapManager.h"
#include "sharedptrobject.h"
#include "AnimationManager.h"
#include "AnimResManager.h"
#include "UnitResManager.h"
#include "buildingresmanager.h"
#include "weaponresmanager.h"
#include "UnitManager.h"
#include "buildingresmanager.h"
#include "consts.h"
#include "MyTime.h"
#include "Player.h"
#include "building.h"
#include "Bullets.h"
#include "common.h"

USING_NS_CC;

using namespace cocostudio::timeline;

GameScene::GameScene()
	:_state(EGS_SmallMap)
	, _nextState(EGS_SmallMap)
	, _stateStep(0)
	, _game1stStart(true)
	, _victory(false)
	, _firstRunSmallMap(true)
	, _gameEnd(false)
	, _eventQuick(false)
	, _eventLock(false)
	, _playerReckey(false)
	, _playerInCamara(false)
	, _pauseTick(0)
	, _pauseTickCount(0)
	, _life(2)
	, _playerDie(false)
	, _playerID(0)
	, _difficulty(3)
	, _stage(0)
	, _mapSectonID(0)
	, _bullets(0)
	, _playerDelay(0)
	, _waitToRun(0)
	, _gameEndTick(0)
	, _statTick(0)
	, _sortTick(0)
	, _collisionTick(0)
	, _spaceTick(0)
	, _deleteSpriteCount(0)
	, _gameOverPerfomTick(0)
	, _gameOverStepTick(0)
	, _gameOverColorDeep(0)
	, _chargeInde(0)
	, _startTime(0)
	, _psTime(0)
	, _totalTime(0)
	, _SMSType(0)
	, _usedBumpCount(0)
	, _dieLifes(0)
	, _scoreLevel(0)
	, _addScore(0)
	, _levelEnemyCount(0)
	, _killEnemyCount(0)
	, _killoddsF(0)
	, _cameraUnit(nullptr)
	, _player(nullptr)
	, _unitResManager(nullptr)
	, _buildingResManager(nullptr)
	, _weaponResManager(nullptr)
	, _AnimResManager(nullptr)
	, _unitManager(nullptr)
	, _eventManager(nullptr)
	, _activeMap(nullptr)
	, _mapManager(nullptr)
{

}

Scene* GameScene::createScene()
{
    // 'scene' is an autorelease object
    auto scene = Scene::create();
    
    // 'layer' is an autorelease object
    auto layer = GameScene::create();
	layer->setTag(LAYER_TAG_GAMESCENE);

    // add layer as a child to scene
    scene->addChild(layer, 2);

	//add waring layer;
	//auto warningLayer = LayerColor::create(Color4B(255, 0, 0, 60));
	//warningLayer->setOpacity(0);
	//warningLayer->setTag(LAYER_TAG_WARNING);
	//scene->addChild(warningLayer, 7);

    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool GameScene::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Layer::init() )
    {
        return false;
    }

	auto keyListner = EventListenerKeyboard::create();
	keyListner->onKeyReleased = [](EventKeyboard::KeyCode keyCode, Event* event){
		log("keyListner...%d", keyCode);
		switch (keyCode)
		{
		case EventKeyboard::KeyCode::KEY_ESCAPE:
			Director::getInstance()->end();
			break;
		case EventKeyboard::KeyCode::KEY_F2:
			Director::getInstance()->isPaused() ? Director::getInstance()->resume():Director::getInstance()->pause();
			break;
		default:
			break;
		}
	};
	_eventDispatcher->addEventListenerWithSceneGraphPriority(keyListner, this);
	//
	NotificationCenter::getInstance()->destroyInstance();
	NotificationCenter::getInstance()->addObserver(this, callfuncO_selector(GameScene::ShowGameOver), "ShowGameOver", nullptr);
	//
	_state = EGS_Init;
	_stateStep = 0;
	_firstRunSmallMap = false;

	scheduleUpdate();
	return true;
}

void GameScene::increaseScore(float dt)
{
	this->score++;
	std::stringstream ss;
	std::string str;
	ss << score;
	ss >> str;
	const char *p = str.c_str();
}

void GameScene::ShowGameOver(Ref* pObj)
{
	auto gameoverlayer = GameOverLayer::create(score);
	addChild(gameoverlayer, 10);
}

void GameScene::setNextGameState(int state)
{
	_nextState = state;
}

int GameScene::getGameState()
{
	return _state;
}

void GameScene::MoveCamera(int x, int y)
{
	if (_cameraUnit != nullptr && _camera->getType() >= 1 && _camera->getType() <= 2)
	{
		if (_cameraUnit->getMaxHP() <= 0)
		{
			_cameraUnit = nullptr;
			auto beforeType = _camera->getType();
			_camera->setType(0);
		}
		else
		{
			
		}
	}
}

std::shared_ptr<MapManager> GameScene::getMapManager()
{
	return _mapManager;
}

cocos2d::Size GameScene::getSceneSize()
{
	return Director::getInstance()->getVisibleSize();
}

float GameScene::getSceneHeight()
{
	return Director::getInstance()->getVisibleSize().height;
}

float GameScene::getSceneWidth()
{
	return Director::getInstance()->getVisibleSize().width;
}

void GameScene::loadEvent(int stage, int mapSectonID)
{
	char filename[256];
	sprintf(filename, "l%d_event_mobile.dat", MAPID_EVENT[stage][mapSectonID]);
	_eventManager = nullptr;
	_eventManager = std::shared_ptr<EventManager>(new EventManager(this, filename));
}

void GameScene::MapWalkRectActive()
{
	auto size = Director::getInstance()->getVisibleSize();
	int i = 0;
	for (auto a : _activeMap->getWalkRect())
	{
		if (a.getMinY() > _camera->getY() + size.height ||
			a.getMaxX() < _camera->getY())
		{
			_activeMap->setWalkRectActive(i, false);
		}
		else
		{
			_activeMap->setWalkRectActive(i, true);
		}
		i++;
	}
}

void GameScene::spriteCollision()
{
	//累计计数器，控制部分需要作碰撞处理
	addCollisionTick();
	//#删除废弃的子弹
	deleteCastoffBullet();
	//处理碰撞
	performCollision();
}

void GameScene::addCollisionTick()
{
	if (_collisionTick >= 2)
	{
		_collisionTick = 0;
	}
	else
	{
		_collisionTick++;
	}
}

void GameScene::performCollision()
{
	allyToEnemy();
	//处理player与道具碰撞处理
	playerCollissionTool();
	////爆炸碰撞处理
	explodeCollision();
	//子弹碰撞处理
	bulletCollision();
}

void GameScene::deleteCastoffBullet()
{
	deleteCastoffallyBullet();
	deleteCastoffEnemyBullet();
}

void GameScene::deleteCastoffEnemyBullet()
{
	for (auto iter = UnitManager::_enemyBullets.begin(); iter != UnitManager::_enemyBullets.end();)
	{
		auto b = *iter;
		if (b->getCastoffStage() >= 1)
		{
			iter = UnitManager::_enemyBullets.erase(iter);
		}
		else
		{
			++iter;
		}
	}
}

void GameScene::deleteCastoffallyBullet()
{
	for (auto iter = UnitManager::_allyBullets.begin(); iter != UnitManager::_allyBullets.end();)
	{
		auto b = *iter;
		if (b->getCastoffStage() >= 1)
		{
			iter = UnitManager::_allyBullets.erase(iter);
		}
		else
		{
			++iter;
		}
	}
}

void GameScene::playerCollissionTool()
{
	if (_collisionTick == 0)
	{
		for (auto a : UnitManager::_tools)
		{

			a->beTouch(_player);
		}
	}
}

void GameScene::explodeCollision()
{
	
}

void GameScene::allyToEnemy()
{
	//处理敌军与盟军相撞
	for (auto b : UnitManager::_allys)
	{
		if (b->isCastoff())
		{
			continue;
		}
		auto hitRect = b->getHitRect();
		hitRect.origin.x = getCamera()->getX() + b->getPositionX();
		hitRect.origin.y = getCamera()->getY() + b->getPositionY();
		auto power = b->getPower();
		//盟军子弹于敌人碰撞
		for (auto e : UnitManager::_enemies)
		{
			auto unit = e;
			if (unit && (!unit->isActive() || unit->isCastoff()))
			{
				continue;
			}
			//收到攻击，且子弹类型
			if (unit->beAttack(hitRect, power) != 0)
			{
				b->setPower(power);
				/*unit->setCastoff();*/
			}
		}
	}
}

void GameScene::bulletCollision()
{
	//处理盟军子弹
	for (auto b : UnitManager::_allyBullets)
	{
		if (b->isBump())
		{
			continue;
		}
		auto hitRect = b->getHitRect();
		auto power = b->getPower();
		//盟军子弹于敌人碰撞
		for (auto e : UnitManager::_enemies)
		{
			auto unit = e;
			if (unit && (!unit->isActive() || unit->isCastoff()))
			{
				continue;
			}
			//收到攻击，且子弹类型
			if (unit->beAttack(hitRect, power) != 0)
			{
				b->setBump();
				b->setCastoff();
			}
		}
		//盟军子弹与建筑碰撞
		if (b->isBump())
		{
			continue;
		}
		for (auto building : UnitManager::_buildings)
		{
			if (building->isActive())
			{
				if (building->beAttack(hitRect, power) != 0)
				{
					b->setBump();
					b->setCastoff();
					break;
				}
			}
		}
	}
	//处理敌人子弹
	for (auto eb : UnitManager::_enemyBullets)
	{
		if (eb->isBump())
		{
			continue;
		}
		auto hitRect = eb->getHitRect();
		auto power = eb->getPower();
		//敌人子弹与盟军相碰
		for (auto a : UnitManager::_allys)
		{
			if (a->isCastoff())
			{
				continue;
			}
			eb->setLocalZOrder(a->getLocalZOrder());
			if (a->beAttack(hitRect, power) != 0)
			{
				eb->setBump();
				eb->removeFromParent();
				break;
			}
		}
		//敌人子弹与建筑碰撞
		if (eb->isBump())
		{
			continue;
		}
		if (_collisionTick == 1)
		{
			continue;
		}
		for (auto building : UnitManager::_buildings)
		{
			if (building->isActive() && 
				building->beAttack(hitRect, power) != 0)
			{
				eb->setBump();
				eb->setCastoff();
				break;
			}
		}
	}
}

void GameScene::gameInitPerform()
{
	switch (_stateStep)
	{
	case 0:
		if (_AnimResManager == nullptr)
		{
			/*_AnimResManager = new AnimResManager("");*/
		}
		break;
	case 1:
		if (_unitResManager == nullptr)
		{
			_unitResManager = std::shared_ptr<UnitResManager>(new UnitResManager("unitres_mobile.dat"));
		}
		if (_buildingResManager == nullptr)
		{
			_buildingResManager = std::shared_ptr<BuildingResManager>(new BuildingResManager("buildingres_mobile.dat"));
		}

		break;
	case 2:
		if (_weaponResManager == nullptr)
		{
			_weaponResManager = std::shared_ptr<WeaponResManager>(new WeaponResManager());
		}
		_levelEnemyCount = 0;
		_unitManager = nullptr;
		_unitManager = std::shared_ptr<UnitManager>(new UnitManager(this));
		break;
	case 4:
		_mapManager = nullptr;
		_mapManager = std::shared_ptr<MapManager>(new MapManager(this));
		_camera = nullptr;
		_camera = std::shared_ptr<CameraExt>(new CameraExt(this, _mapManager));
		break;
	case 5:
		//载入地图数据，第一步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		//载入地图数据，第二步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 6:
		//载入地图数据，第三步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		//载入地图数据，第四步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 7:
		//载入地图数据，第五步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		//载入地图数据，第六步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 8:
		//载入地图数据，第七步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 9:
		//载入地图数据，第八步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 10:
		//载入地图数据，第九步
		changeMap(MAPID_EVENT[_stage][_mapSectonID]);
		break;
	case 11:
		if (!changeMap(MAPID_EVENT[_stage][_mapSectonID]))
		{
			return;
		}
		break;
	case 12:
		//todo设置无敌


		break;
	case 13:
		loadEvent(_stage, _mapSectonID);
		break;
	case 18:
		_gameEnd = false;
		_camera->setMoveRect(cocos2d::Rect(0, 0, _mapManager->getActiveMap()->getWidth(), _mapManager->getActiveMap()->getHeight()));
		_camera->close();
		_psTime = 0;
		_totalTime = 0;
		_usedBumpCount = 0;
		_dieLifes = 0;
		_scoreLevel = 0;
		_addScore = 0;
		_killEnemyCount = 0;
		_killoddsF = 0;
		if (_player != nullptr)
		{
			
		}
		//记录开始时间
		_totalStartSecond = MyTime::getCurrentTime();
		_state = EGS_GamePlaying;
		_stateStep = 0;
		break;
	default:
		break;
	}
	log("%s,%d,%d", __FUNCTION__, _state, _stateStep);
	_stateStep++;
}

void GameScene::sortSprites()
{
	if (_sortTick > 2)
	{
		_sortTick = 0;
	}
	else
	{
		_sortTick++;
		if (_sortTick == 1)
		{
			for (auto a : UnitManager::_enemies)
			{
// 				if (a->getPower() > 0)
// 				{
// 
// 				}
			}
		}
	}
}

bool GameScene::changeMap(int mapID)
{
	bool changeOk = _mapManager->requestMap(mapID, true);
	if (changeOk)
	{
		_activeMap = _mapManager->getActiveMap();
	}
	return changeOk;
}
//删除废弃的指针
void GameScene::deleteCastoffPoint()
{
	for (auto iter = UnitManager::_buildings.begin();  iter != UnitManager::_buildings.end();)
	{
		auto building = *iter;
		if (building->isCastoff())
		{
			(*iter)->removeFromParent();
			iter = UnitManager::_buildings.erase(iter);
		}
		else
		{
			++iter;
		}
	}

	for (auto iter = UnitManager::_enemies.begin(); iter != UnitManager::_enemies.end();)
	{
		auto e = *iter;
		if (e->isCastoff())
		{
			(*iter)->removeFromParent();
			iter = UnitManager::_enemies.erase(iter);
		}
		else
		{
			++iter;
		}
	}

	for (auto iter = UnitManager::_allys.begin(); iter != UnitManager::_allys.end();)
	{
		auto e = *iter;
		if (e->isCastoff())
		{
			(*iter)->removeFromParent();
			iter = UnitManager::_allys.erase(iter);
		}
		else
		{
			++iter;
		}
	}

	for (auto iter = UnitManager::_tools.begin(); iter != UnitManager::_tools.end();)
	{
		auto e = *iter;
		if (e->isCastoff())
		{
			(*iter)->removeFromParent();
			iter = UnitManager::_tools.erase(iter);
		}
		else
		{
			++iter;
		}
	}
}

void GameScene::gamePlayingPerform(float dt)
{
	log("enter GameScene::gamePlayingPerform....");

	//#处理事件
	_eventManager->doPeriodicTask();
	if (_eventLock)
	{
		return;
	}
	//如果摄像机没有打开，则退出
// 	if (_camera->isClose())
// 	{
// 		return;
// 	}
	//地图可走范围处理
	MapWalkRectActive();
	//精灵运动
	performUnit(dt);
	//物体碰撞处理
	spriteCollision();
	//删除索引表的废弃索引值
	deleteCastoffPoint();
	//将Sprite排序
	//sortSprites();
	//摄像机
	moveCamera(dt);
	//player复活
	if (_player != nullptr && _player->getPower() <= 0 && _state == EGS_GamePlaying && _life > 0)
	{
		_playerDelay ++;
		if (_playerDelay > RELIFE_DEADLINE)
		{
			if (_difficulty == 3 && _life > 1)
			{
				_life--;
				playerRelife();
				return;
			}

		}
	}
	if (_player != nullptr && _player->getPower() <= 0)
	{
		if (_life <= 0)
		{
			gameOver();
		}
	}
}


void GameScene::performUnit(float dt)
{
	for (auto i = 0; i < UnitManager::_sprites.size(); i++)
	{
		auto iter = UnitManager::_sprites.at(i);
		log("Sprite size:%d", UnitManager::_sprites.size());
		if (iter->getCastoffStage() > 1)
		{
			iter->removeFromParent();
			log("delete sprite...");
			UnitManager::_sprites.eraseObject(iter, true);
			continue;
		}
		else
		{
			iter->perform(dt);
		}
	}
}

void GameScene::changeMapByEvent(int stage, int mapFileID)
{
	_totalScore += _scoreLevel;
	if (_stage != stage)
	{
		_stage = stage;
		_mapSectonID = mapFileID;
		_playerPower = 0;
		_bullets = 0;
	}
	else
	{
		CCASSERT(_mapSectonID != mapFileID, "map id is same");
		_state = EGS_LoadMap;
		_stateStep = 0;
		_mapSectonID = mapFileID;
		/*save();*/

	}
}

Unit* GameScene::getPlayer()
{
	return _player;
}

void GameScene::addLevelEnemy(int count)
{
	_levelEnemyCount += count;
}

void GameScene::addScore(int param1)
{
	_addScore += param1;
}

std::shared_ptr<CameraExt> GameScene::getCamera()
{
	return _camera;
}

std::shared_ptr<BuildingResManager> GameScene::getBuildingResManager()
{
	return _buildingResManager;
}

void GameScene::setPlayerPower(int power)
{
	_playerPower = power;
}

void GameScene::addKillEnemy(int count)
{
	_killEnemyCount += count;
}

void GameScene::setGameState(int state)
{
	_state = state;
}

void GameScene::cameraOperationOK(int type)
{
	switch (type)
	{
	case 6://切换到某点
		refreshMap(_camera->getY(), true);
		break;
	default:
		break;
	}
}

void GameScene::refreshMap(float offY, bool move)
{
//#todo
}

void GameScene::startGameState()
{
	_state = EGS_Stat;
	_stateStep = 0;
}

void GameScene::startGameEnd()
{
}

void GameScene::startGameOver()
{
}

int GameScene::getMapWidth()
{
	return _mapManager->getXMapW();
}

int GameScene::getMapHeight()
{
	return _mapManager->getXMapH();
}

void GameScene::doPeroidicTick(float dt)
{
	switch (_state)
	{
	case EGS_GamePlaying:
		gamePlayingPerform(dt);
		break;
	case EGS_LoadMap:
		gameLoadMapPerform();
		break;
	case EGS_Init:
		gameInitPerform();
		break;
	case EGS_Stat:
		switch (_stateStep)
		{
		case 0:
			_totalVicorySecond = MyTime::getCurrentTime();
			_totalUsedSecond = (_totalVicorySecond - _totalStartSecond) / 1000;
			_totalHour = _totalUsedSecond / 3600;
			_totalMinute = _totalUsedSecond / 60 - _totalHour * 60;
			_totalSecond = _totalUsedSecond - _totalUsedSecond * 3600 - _totalMinute * 60;
			_stateStep = 1;
			break;
		case 1:
			setGameState(EGS_GamePlaying);
			break;
		default:
			break;
		}
		break;
	default:
		break;
	}
}

void GameScene::gameLoadMapPerform()
{
	switch (_stateStep)
	{
	case 1:
		_levelEnemyCount = 0;
		_cameraUnit = nullptr;
		_unitManager = nullptr;
		_unitManager = std::shared_ptr<UnitManager>(new UnitManager(this));
		break;
	case 2:
		if (!changeMap(MAPID_EVENT[_stage][_mapSectonID]))
		{
			return;
		}
		break;
	case 3:
		loadEvent(_stage, _mapSectonID);
		break;
	case 4:
		_gameEnd = false;
		_camera->setMoveRect(cocos2d::Rect(0, 0, _mapManager->getActiveMap()->getWidth(), _mapManager->getActiveMap()->getHeight()));
		_camera->close();
		if (_player != nullptr)
		{
			unitFollowCamer(_player, true);
		}
		break;
	case 5:
		_state = EGS_GamePlaying;
		_stateStep = 0;
		//#设置boss
		break;
	default:
		break;
	}
	_stateStep++;
}

void GameScene::unitFollowCamer(Unit* _player, bool center)
{
	
}

void GameScene::update(float delta)
{
	doPeroidicTick(delta);
}

UnitManager& GameScene::getUnitManager() const
{
	return *_unitManager;
}

void GameScene::setPlayer(Player* unit)
{
	_player = unit;
}

UnitResManager& GameScene::getUnitResManager() const
{
	return *_unitResManager;
}

int GameScene::getDefficulty()
{
	return _difficulty;
}

bool GameScene::getUnitCollisionToMap(cocos2d::Rect tempRect)
{
	for (auto i = 0; i < (int)_activeMap->getWalkRect().size(); i++)
	{
		if (!_activeMap->isWalkRectActive(i))
		{
			continue;
		}
		if (_activeMap->getWalkRect()[i].intersectsRect(tempRect))
		{
			return false;
		}
	}
	return true;
}
//unit身体碰撞处理
bool GameScene::getUnitCollision(cocos2d::Rect tempRect)
{
	for (auto iter = UnitManager::_buildings.begin(); iter != UnitManager::_buildings.end(); ++iter)
	{
		auto building = *iter;
		if (building->isActive())
		{
			continue;
		}
		if (building->getWalkRect().size.width > 0 && tempRect.intersectsRect(building->getWalkRect()))
		{
			return false;
		}
	}
	return true;
}

void GameScene::moveCamera( float dt )
{
	if (_cameraUnit != nullptr && _camera->getType() >= 1 && _camera->getType() <= 2)
	{
		if (_cameraUnit->getPower() <= 0)
		{
			_cameraUnit = nullptr;
			_cameraBeforeType = _camera->getType();
			_camera->setType(0);
		}
		else
		{
			_camera->setDesX(_cameraUnit->getPositionX());
			_camera->setDesY(_cameraUnit->getPositionY());
		}
	}
	_camera->doPeriodicTask(dt);

	if (_cameraUnit && _cameraUnit->getType() == 2)//跟踪player
	{
		auto tempX = _cameraUnit->getPositionX();
		auto tempY = _cameraUnit->getPositionY();
		if (tempX < _camera->getX() + 8)
		{
			tempX = _camera->getX() + 8;
		}
		else if (tempX > _camera->getX() + getSceneHeight() - 8)
		{
			tempX = _camera->getX() + getSceneHeight() - 8;
		}
		if (tempY < _camera->getY() + 16)
		{
			tempY = _camera->getY() + 16;
		}
		else if (tempY > _camera->getY() + getSceneHeight() - 20)
		{
			tempY = _camera->getY() + getSceneHeight() - 20;
		}
		/*_camera->SetCameraPos(tempX, tempY);*/
	}
	_mapManager->performMap();
}

cocos2d::Size GameScene::getMapSize()
{
	_mapManager->getActiveMap();
	return Size::ZERO;
}

GameScene* GameScene::create()
{
	auto ret = new GameScene();
	if (ret && ret->init())
	{
		ret->autorelease();
		return ret;
	}
	CC_SAFE_DELETE(ret);
	return ret;
}

void GameScene::addUnit(Node* node)
{
	if (_mapManager->getFloor()) _mapManager->getFloor()->addChild(node);
}

void GameScene::gameOver()
{
	CC_SAFE_RELEASE_NULL(_player);
	_state = EGS_GameOver;
	_stateStep = 0;
}

void GameScene::playerRelife()
{
	_playerDie = false;
	_player->setPower(1000);
	_playerPower = _player->getPower();
	_scoreLevel += 1;
	_player->relive();
}


