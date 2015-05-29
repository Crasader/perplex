#ifndef __TestScene_h__
#define __TestScene_h__

#include "cocos2d.h"
#include "cocostudio\CCArmature.h"

USING_NS_CC;

class TestScene : public Layer
{
public:
	static Scene* createScene();
	CREATE_FUNC(TestScene);
	void draw(Renderer *renderer, const Mat4& transform, uint32_t flags);
	bool init() override;
	cocostudio::Armature* s1;
private:

};
#endif // __TestScene_h__
