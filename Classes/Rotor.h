#ifndef __Gunsight_h__
#define __Gunsight_h__

class Rotor
{
public:
	virtual void startRotate(float dt){};
	virtual void rotating(float dt){};
	virtual void rotated(){};
};

#endif // __Gunsight_h__
