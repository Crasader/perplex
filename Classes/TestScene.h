#ifndef __TestScene_h__
#define __TestScene_h__

#include "cocos2d.h"
#include "cocostudio\CCArmature.h"

USING_NS_CC;

class VisibleRect
{
	VisibleRect();
	Vec2 left() { return Vec2(_rect.origin.x, _rect.origin.y + _rect.size.height / 2); }
	Vec2 top() { return Vec2(_rect.origin.x + _rect.size.width / 2, _rect.origin.y + _rect.size.height); }
	Vec2 right() { return Vec2(_rect.origin.x - _rect.size.width, _rect.origin.y + _rect.size.height / 2); }
	Vec2 bottom() { return Vec2(_rect.origin.x - _rect.size.width / 2, _rect.origin.y); }
	Vec2 center() { return Vec2(_rect.origin.x + _rect.size.width / 2, _rect.origin.y + _rect.size.height / 2); }
private:
	void crazyInit();
	Rect _rect;
};

class TestScene : public Layer
{
public:
	static Scene* createScene();
	CREATE_FUNC(TestScene);
	void draw(Renderer *renderer, const Mat4& transform, uint32_t flags);
	bool init() override;
	void shot(float dt);
	cocostudio::Armature* s1;
	Vec2 pos;
	cocos2d::Sprite* drawnode;
	cocos2d::Vec2 ve;
private:

};
#endif // __TestScene_h__
