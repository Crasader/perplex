#include "effectcounter.h"
#include "EventManager.h"



bool EffectCounter::perform()
{
	auto tem = _eventManager->getCounter(_counterID);
	switch (_operate)
	{
	case 5:
		_eventManager->setCounter(_counterID, _count);
		break;
	case 6:
		_eventManager->setCounter(_counterID, _count + tem);
		break;
	case 7:
		_eventManager->setCounter(_counterID, tem - _count);
		break;
	default:
		break;
	}
	return true;
}

EffectCounter::EffectCounter(EventManager* eventManager, GameScene* gameLayer, int counterID, int operate, int count) :Effect(eventManager, gameLayer, 1)
, _counterID(counterID)
, _operate(operate)
, _count(count)
{

}
