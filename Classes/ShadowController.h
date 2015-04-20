#ifndef __ShadowController_h__
#define __ShadowController_h__

#include "cocos2d.h"
#include <unordered_map>

USING_NS_CC;
using namespace std;

class GameEntity;

class ShadowSprite : public Sprite
{
public:
	virtual ~ShadowSprite();
	static ShadowSprite* create(GameEntity* target);
	virtual bool init(GameEntity* target);
	static ShadowSprite* createWithTexture(Texture2D *texture, const Rect& rect, bool rotated = false);
	virtual bool initWithTexture(Texture2D *texture, const Rect& rect, bool rotated);

	void setOffset(Point p) { _offset = p; }
	Point getOffset() { return _offset; }
	void updateShadow(float dt);
	int getType();
	GameEntity* getTarget() { return _target; }
	bool equal(const GameEntity* rl);
private:
	GameEntity* _target;
	Point _offset;
	int _type;
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
