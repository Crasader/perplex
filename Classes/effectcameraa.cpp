#include "effectcameraa.h"



bool EffectCameraA::perform()
{
	return true;
}

EffectCameraA::EffectCameraA(EventManager* eventManager, GameScene* gameLayer, int effectType)
:Effect(eventManager, gameLayer, 11)
, _effectType(effectType)
{

}
