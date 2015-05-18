#ifndef __effectplayer_h__
#define __effectplayer_h__

#include "Effect.h"

class EffectPlayer : public Effect
{
public:
	EffectPlayer(EventManager* eventManager, GameScene* gameLayer, int effectType, bool set);

	virtual bool perform();

private:
	int _effectType;
	bool _set;
};

#endif // __effectplayer_h__
