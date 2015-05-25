#ifndef __EventManager_h__
#define __EventManager_h__
#include "cocos2d.h"
#include "Ifstream.h"

class GameScene;
class EventUnit;

class EventManager
{
public:
	const int MAXSWITCH = 64;
	const int MAXCOUTER = 64;
public:
	EventManager(GameScene* gamescen, std::string eventRes);
	~EventManager() {}
	void loadEvent(std::string eventRes);

	std::shared_ptr<EventUnit> createEventUnit(int ID);

	std::vector<std::shared_ptr<EventUnit>> getEventUnits();
	bool isCameraBusy();
	void setCamerBusy(bool yes);
	bool isPlayerReceiveKey();;
	bool setSwitch(int switchID, bool set);
	bool getSwitch(int switchID){ return _switchs[switchID]; }
	int getCounter(int counterID);
	void setCounter(int counterID, int count);
	GameScene* getGameScene();
	std::shared_ptr<EventUnit> getEventUnitFromID(int id);
	void doPeriodicTask();
	void addCount(int counterID) { _counters[counterID] += 1; }
private:
	bool _camerBusy;
	bool _playerReceiveky;
	int _eventNum;
	int _eventCount;
	GameScene* _gameLayer;
	std::vector<bool> _switchs;
	std::vector<int> _counters;
	std::vector<std::shared_ptr<EventUnit>> _eventUnits;
};

#endif // __EventManager_h__
