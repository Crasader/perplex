#ifndef __basescene_h__
#define __basescene_h__

#include "cocos2d.h"

USING_NS_CC;

class SceneBase : public Layer
{
public:
	CREATE_FUNC(SceneBase);
	virtual void doPeriodicTask(float dt){};
	virtual void doStart(){};
	virtual void doLogic(){};
	virtual void doEnd(){};
protected:

};

#endif // __basescene_h__
