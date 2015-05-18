#ifndef __Trigger_h__
#define __Trigger_h__

#include "cocos2d.h"

class EventManager;
class GameScene;

class Trigger
{
public:
	Trigger(EventManager* eventManger, GameScene* gameLayer, int type);
	virtual ~Trigger();

	virtual bool perform();
protected:
	EventManager* _eventManager;
	GameScene* _gameLayer;
	int _type;
};

#endif // __Trigger_h__
