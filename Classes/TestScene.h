#ifndef __TestScene_h__
#define __TestScene_h__

#include "cocos2d.h"

USING_NS_CC;

class TestScene : public Layer
{
public:
	static Scene* createScene();
	CREATE_FUNC(TestScene);

	bool init() override;
private:

};
#endif // __TestScene_h__
