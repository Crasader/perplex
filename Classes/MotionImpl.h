#ifndef __IState_h__
#define __IState_h__

class MotionImpl
{
public:
	virtual void execute() = 0;
};


#endif // __IState_h__


class IdleState : public MotionImpl
{
public:


	virtual void execute()
	{
		
	}

};
