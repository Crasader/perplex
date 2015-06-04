#include "FodderIdle.h"
#include "Fodder.h"
#include "cocos2d.h"

USING_NS_CC;

using namespace cocostudio;

void FodderIdle::execute()
{
	_armature->getAnimation()->play("001");
}

FodderIdle::FodderIdle(Fodder* fodder)
{
	CCAssert(fodder != nullptr, "fodder is null");
	_armature = dynamic_cast<Armature*>(fodder->getModel());
}

FodderRun::FodderRun(Fodder* fodder)
	:FodderIdle(fodder)
{

}

void FodderRun::execute()
{
	_armature->getAnimation()->play("run");
}
