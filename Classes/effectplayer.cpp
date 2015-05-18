#include "effectplayer.h"
#include "GameScene.h"



bool EffectPlayer::perform()
{
	if (_gameLayer->getPlayer() == nullptr)
	{
		return false;
	}
	switch (_effectType)
	{
	case 0:

		break;
	default:
		break;
	}
	return true;
}

EffectPlayer::EffectPlayer(EventManager* eventManager, GameScene* gameLayer, int effectType, bool set)
:Effect(eventManager, gameLayer, 10)
, _effectType(effectType)
, _set(set)
{

}
