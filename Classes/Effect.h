#ifndef __Effect_h__
#define __Effect_h__

class EventManager;
class GameScene;

class Effect
{
public:
	Effect(EventManager* eventmanager, GameScene* gamelayer, int type);
	virtual ~Effect() {};
	int getType() const;
	virtual bool perform();
protected:
	int _type;
	EventManager* _eventManager;
	GameScene* _gameLayer;
};

#endif // __Effect_h__
