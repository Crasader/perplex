#ifndef __IState_h__
#define __IState_h__

class IState
{
public:
	virtual void execute() = 0;
};


#endif // __IState_h__


class IdleState : public IState
{
public:


	virtual void execute()
	{
		
	}

};
