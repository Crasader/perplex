#include "TriggerCounter.h"
#include "EventManager.h"



bool TriggerCounter::perform()
{
	switch (_operate)
	{
	case 0:
		if (_eventManager->getCounter(_counterID) < _count)
		{
			return true;
		}
		break;
	case 1:
		if (_eventManager->getCounter(_counterID) <= _count)
		{
			return true;
		}
		break;
	case 2:
		if (_eventManager->getCounter(_counterID) == _count)
		{
			return true;
		}
		break;
	case 3:
		if (_eventManager->getCounter(_counterID) >= _count)
		{
			return true;
		}
		break;
	case 4:
		if (_eventManager->getCounter(_counterID) > _count)
		{
			return true;
		}
		break;
	default:
		break;
	}
	_eventManager->addCount(_counterID);
	return false;
}

TriggerCounter::TriggerCounter(EventManager* eventManager, GameScene* gameLayer, int counterID, int operate, int count) :Trigger(eventManager, gameLayer, 1)
, _counterID(counterID)
, _operate(operate)
, _count(count)
{

}
