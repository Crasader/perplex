#ifndef __MoveMode_h__
#define __MoveMode_h__

class MoveMode
{
public:
	virtual void horizontal(float x, float dt){}
	virtual void vertical(float y, float dt){}
	virtual void moveAtAngle(Vec2 pos, float angle, float dt){};
	virtual void moveAtBezier(float dt){};
};
#endif // __MoveMode_h__
