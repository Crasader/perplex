#ifndef __EffectSwitch_h__
#define __EffectSwitch_h__

#include "Effect.h"

class EffectSwitch : public Effect
{
public:
	EffectSwitch(EventManager* eventManager, GameScene* gameLayer, int switchID, bool set);

	virtual bool perform();

private:
	int _switchID;
	bool _set;
};

#endif // __EffectSwitch_h__
