#ifndef __ShadowController_h__
#define __ShadowController_h__

#include "cocos2d.h"
#include <unordered_map>
#include "cocostudio\CCArmature.h"

USING_NS_CC;
using namespace std;

class GameEntity;

class IShadow
{
public:
	virtual ~IShadow(){}
	virtual void updateShadow(float dt) = 0;
private:

};

class ShadowSprite : public Node, public IShadow
{
public:
	ShadowSprite() :_offset(50, 50), _type(0), _target(nullptr), _model(nullptr){}
	CREATE_FUNC(ShadowSprite);
	void setOffset(Point p) { _offset = p; }
	Point getOffset() { return _offset; }
	void updateShadow(float dt);
	int getType();
	Node* getTarget() { return _target; }
	bool equal(const Node* rl);
	void setTarget(Node* t){ _target = t; }
	void setShadowData(cocostudio::Armature* s, Node* target);
private:
	Point _offset;
	int _type;
	Node* _target;
	Node* _model;
};

class ShadowController
{
	typedef Vector<ShadowSprite*> umap;
public:

	static void init(Node* layer);
	static void createShadow(GameEntity* target, Point offset = Point(50, -50));
	static void erase(GameEntity* target);
	static void clean();
	static void update(float dt);
	static void reset();

	static umap _shadows;
private:
	static bool _init;
	static Node* _shadowLayer;
	static Point _offset;
};

#endif // __ShadowController_h__
