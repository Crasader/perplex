#ifndef __FodderIdle_h__
#define __FodderIdle_h__

#include "MotionImpl.h"
#include "cocostudio/CCArmature.h"


class Fodder;

class FodderIdle : public MotionImpl
{
public:
	FodderIdle(Fodder* fodder);

	virtual void execute();

private:
	cocostudio::Armature* _armature;
};

#endif // __FodderIdle_h__
