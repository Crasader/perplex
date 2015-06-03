#ifndef __AnimationLoader_h__
#define __AnimationLoader_h__

#include "AnimationResLoader.h"
#include "cocostudio\CCArmature.h"

using namespace cocostudio;


class AnimationLoader
{
public:
	static AnimationLoader& AnimationLoader::getInstance();
	cocostudio::Armature* createAnimation(const std::string& name);
	
private:
	void load();
	AnimationLoader();

private:
	std::shared_ptr<AnimationResLoader> _loder;
};

#endif // __AnimationLoader_h__
