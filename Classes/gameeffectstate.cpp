#include "gameeffectstate.h"
#include "GameScene.h"



bool EffectGameState::perform()
{
	switch (_gameState)
	{
	case 0:
		if (!_start)
		{
			_gameLayer->startGameState();
			_start = true;
			return false;
		}
		break;
	case 1://通关
		_gameLayer->startGameEnd();

		break;
	case 2://游戏结束
		if (!_start)
		{
			_gameLayer->startGameOver();
			_start = true;
			return false;
		}
		break;
	default:
		break;
	}
	return true;
}

EffectGameState::EffectGameState(EventManager* eventManager, GameScene* gameLayer, int gamesstate) :Effect(eventManager, gameLayer, 28)
, _gameState(gamesstate)
, _start(false)
{

}
