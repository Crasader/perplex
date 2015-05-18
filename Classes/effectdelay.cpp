#include "effectdelay.h"


bool EffectDelay::perform()
{
	_startTick++;
	if (_startTick > _second)
	{
		return true;
	}
	return false;
}

EffectDelay::EffectDelay(EventManager* eventManager, GameScene* gameLayer, int second)
:Effect(eventManager, gameLayer, 2)
, _second(second * 28)
, _startTick(0)
{

}
