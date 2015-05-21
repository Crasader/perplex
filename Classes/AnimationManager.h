#ifndef __AnimationManager_h__
#define __AnimationManager_h__

#include "CAnimationData.h"
#include "AnimationLoader.h"

class AnimationManage
{
public:
	AnimationManage(const char* name);

private:
	bool LoadRes(const char* name);
	AnimRes getAnimRes(int resID);

private:
	std::vector<AnimRes> _AnimaReses;
};

#endif // __AnimationManager_h__
