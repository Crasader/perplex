#include "FodderIdle.h"
#include "Fodder.h"
#include "cocos2d.h"

USING_NS_CC;

using namespace cocostudio;

void FodderIdle::execute()
{
	_armature->getAnimation()->play("idle");
}

FodderIdle::FodderIdle(Fodder* fodder)
{
	CCAssert(fodder != nullptr, "fodder is null");
	_armature = dynamic_cast<Armature*>(fodder->getModel());
}