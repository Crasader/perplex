#ifndef __TriggerCounter_h__
#define __TriggerCounter_h__

#include "Trigger.h"

class TriggerCounter : public Trigger
{
public:
	TriggerCounter(EventManager* eventManager, GameScene* gameLayer, int counterID, int operate, int count);
	virtual bool perform();

private:
	int _counterID;
	int _operate;
	int _count;
};
#endif // __TriggerCounter_h__
