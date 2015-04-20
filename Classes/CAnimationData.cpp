#include "CAnimationData.h"




AnimRes::AnimRes(int id)
:_ID(id)
{
	init();
}

AnimRes::~AnimRes()
{

}

bool AnimRes::init()
{
	_Frames.clear();
	return true;
}

void AnimRes::setFrame(CFrameData f)
{
	_Frames.push_back(f);
}

CAnimationData::CAnimationData(AnimRes* res)
:_AnimRes(res)
{

}

CAnimationData::~CAnimationData()
{

}
