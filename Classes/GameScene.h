#ifndef __GAME_SCENE_H__
#define __GAME_SCENE_H__

#include "cocos2d.h"
#include "gamestate.h"

class Unit;
class UnitResManager;
class BuildingResManager;
class WeaponResManager;
class AnimResManager;
class UnitManager;
class EventManager;
class XMap;
class MapManager;
class Victory;
class CameraExt;
class Player;

class GameScene : public cocos2d::Layer
{
public:
	GameScene();
    // there's no 'id' in cpp, so we recommend returning the class instance pointer
    static cocos2d::Scene* createScene();

    // Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
    virtual bool init();

    // implement the "static create()" method manually
    CREATE_FUNC(GameScene);

	void update(float delta) override;

	int getSceneWidth();
	int getSceneHeight();
	cocos2d::Size getSceneSize();
	std::shared_ptr<MapManager> getMapManager();
	std::shared_ptr<UnitManager> getSpriteManager() { return _unitManager; }
	void MoveCamera(int x, int y);

	int getMapWidth();
	int getMapHeight();
	int getGameState();
	void setNextGameState(int state);

	CC_SYNTHESIZE(cocos2d::ProgressTimer*, hpView, HPView);
	CC_SYNTHESIZE(int, score, Score);
	CC_SYNTHESIZE(cocos2d::LabelAtlas*, scoreLabel, ScoreLabel);
	void increaseScore(float dt);
	void ShowGameOver(Ref* pObj);
	void gameInitPerform();
	bool changeMap(int mapID);
	void loadEvent(int stage, int mapSectonID);
	void deleteCastoffPoint();
	void gamePlayingPerform(float dt);
	void MapWalkRectActive();
	void changeMapByEvent(int stage, int mapFileID);
	void spriteCollision();
	void explodeCollision();
	void bulletCollision();
	void sortSprites();
	Unit* getPlayer();
	void addLevelEnemy(int count);
	void addScore(int param1);
	std::shared_ptr<CameraExt> getCamera();
	std::shared_ptr<BuildingResManager> getBuildingResManager();
	void setPlayerPower(int _power);
	void addKillEnemy(int count);
	void setGameState(int state);
	void cameraOperationOK(int type);
	void refreshMap(float offY, bool move);
	void startGameState();
	void startGameEnd();
	void startGameOver();
	void doPeroidicTick(float dt);
	void gameLoadMapPerform();
	void unitFollowCamer(Unit* _player, bool center);
	UnitManager& getUnitManager() const;
	void setPlayer(Player* unit);
	UnitResManager& getUnitResManager() const;
private:
	bool _game1stStart;
	bool _victory;
	bool _firstRunSmallMap;
	bool _gameEnd;//通关
	bool _eventQuick;//快速运行事件
	bool _eventLock;//当运行事件时，不运行游戏逻辑
	bool _playerReckey;
	bool _playerInCamara;
	bool _playerDie;
	byte _pauseTick;
	byte _gameEndTick;//通关延迟
	byte _gameOverPerfomTick;
	byte _gameOverStepTick;
	byte _gameOverColorDeep;
	byte _startTime;
	byte _psTime;
	byte _totalTime;

	int _state;
	int _nextState;
	int _stateStep;
	int _pauseTickCount;
	int _waitToRun;//切换焦点延迟
	int _statTick;
	int _sortTick;
	int _collisionTick;
	int _spaceTick;

	int _deleteSpriteCount;
	int _chargeInde;
	int _SMSType;

	int _usedBumpCount;
	int _dieLifes;
	int _scoreLevel;
	int _totalScore;
	int _addScore;
	int _levelEnemyCount;
	int _killEnemyCount;
	int _killoddsF;//杀敌率

	int _playerDelay;
	int _playerID;
	int _life;
	int _playerPower;
	int _playerWeaponType;
	int _playerBumCount;
	int _difficulty;
	int _stage;
	int _mapSectonID;
	int _bullets;

	long _totalStartSecond;
	long _totalVicorySecond;
	long _totalUsedSecond;
	long _totalHour;
	long _totalMinute;
	long _totalSecond;
	long _totalPauseTime;

	Player* _cameraUnit;
	Player* _player;
	std::shared_ptr<UnitResManager> _unitResManager;
	std::shared_ptr<BuildingResManager> _buildingResManager;
	std::shared_ptr<WeaponResManager> _weaponResManager;
	std::shared_ptr<AnimResManager> _AnimResManager;
	std::shared_ptr<UnitManager> _unitManager;
	std::shared_ptr<EventManager> _eventManager;
	std::shared_ptr<XMap> _activeMap;
	std::shared_ptr<MapManager> _mapManager;
	std::shared_ptr<CameraExt> _camera;
	//Victory* _victory;
};

#endif // __GAME_SCENE_H__
