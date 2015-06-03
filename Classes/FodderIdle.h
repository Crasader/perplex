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

protected:
	cocostudio::Armature* _armature;
};

class FodderRun : public FodderIdle
{
public:
	FodderRun(Fodder* fodder);
	void execute();
private:

};

#endif // __FodderIdle_h__
