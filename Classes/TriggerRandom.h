#ifndef __TriggerRandom_h__
#define __TriggerRandom_h__

#include "Trigger.h"

class TriggerRandom : public Trigger
{
public:
	TriggerRandom(EventManager* eventManger, GameScene* gameLayer, int min, int max, int para);
	bool perform();
private:
	int _min;
	int _max;
	int _para;
};

#endif // __TriggerRandom_h__
