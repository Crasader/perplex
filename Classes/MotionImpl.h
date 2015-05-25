#ifndef __IState_h__
#define __IState_h__

class MotionImpl
{
public:
	virtual void execute() = 0;
};


#endif // __IState_h__


class Idle : public MotionImpl
{
public:
	virtual void execute(){};
};

class Walk : public MotionImpl
{
public:
	virtual void execute() {};
};

class Die : public MotionImpl
{
public:
	virtual void execute() {};
};
