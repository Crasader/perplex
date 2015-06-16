#ifndef __tank_h__
#define __tank_h__

#include "Unit.h"

class GameScene;
class cocostudio::Bone;

class Tank : public Unit
{
public:
	Tank(GameScene* gameScene, int unitID, int type, int dir, int campType);
	virtual ~Tank();
	bool init(GameScene* gameScene, int unitID, int type, int dir, int campType);
	static Tank* create(GameScene* gameScene, int unitID, int type, int dir, int campType);
	void followGoald(float dt);
	Vec2 getTurrentVector();
	void setTurrentRotation(float angle);
	void setRotation(float rotation) override;
protected:
	cocostudio::Bone* _turrent;
	float rad;
	cocostudio::Bone* _shot1;
	cocostudio::Bone* _shot2;
};
#endif // __tank_h__
