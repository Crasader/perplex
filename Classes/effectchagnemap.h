#ifndef __effectchagnemap_h__
#define __effectchagnemap_h__

#include "Effect.h"

class EffectChangeMap : public Effect
{
public:
	EffectChangeMap(EventManager* eventManager, GameScene* gameLayer, int stage, int section);
	bool perform();
private:
	int _state;
	int _section;
};


#endif // __effectchagnemap_h__
