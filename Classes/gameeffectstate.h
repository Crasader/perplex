#ifndef __gameeffectstate_h__
#define __gameeffectstate_h__

#include "Effect.h"

class EffectGameState : public Effect
{
public:
	EffectGameState(EventManager* eventManager, GameScene* gameLayer, int gamesstate);

	bool perform();
private:
	int _gameState;
	int _start;
};

#endif // __gameeffectstate_h__
