#ifndef __TriggerSwitch_h__
#define __TriggerSwitch_h__

#include "Trigger.h"

class TriggerSwitch : public Trigger
{
public:
	TriggerSwitch(EventManager* eventManager, GameScene* gameLayer, int switchID, bool set);

	virtual bool perform();

private:
	int _switchID;
	bool _set;
};

#endif // __TriggerSwitch_h__
