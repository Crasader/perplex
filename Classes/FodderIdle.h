#ifndef __FodderIdle_h__
#define __FodderIdle_h__

#include "IState.h"
#include "cocostudio/CCArmature.h"


class Fodder;

class FodderIdle : public IState
{
public:
	FodderIdle(Fodder* fodder);

	virtual void execute();

private:
	cocostudio::Armature* _armature;
};

#endif // __FodderIdle_h__
