#include "effectcalculagraph.h"
#include "GameScene.h"



bool EffectCalculaGraph::perform()
{
	if (_start)
	{
		return true;
	}
	_start = true;
	return true;
}

EffectCalculaGraph::EffectCalculaGraph(EventManager* eventManager, GameScene* gameLayer)
:Effect(eventManager, gameLayer, 30)
{
	_start = false;
}
