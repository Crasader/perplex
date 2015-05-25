#include "TriggerRandom.h"
#include "cocos2d.h"


TriggerRandom::TriggerRandom(EventManager* eventManger, GameScene* gameLayer, int min, int max, int para)
	:Trigger(eventManger, gameLayer, 2)
	, _min(min)
	, _max(max)
	, _para(para)
{

}

bool TriggerRandom::perform()
{
	auto temp = rand() % _para;
	if (temp >= _min && temp < _max)
	{
		return true;
	}
	return false;
}
