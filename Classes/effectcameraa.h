#ifndef __effectcameraa_h__
#define __effectcameraa_h__

#include "Effect.h"

class EffectCameraA : public Effect
{
public:
	EffectCameraA(EventManager* eventManager, GameScene* gameLayer, int effectType);

	virtual bool perform();

private:
	int _effectType;
};


#endif // __effectcameraa_h__
