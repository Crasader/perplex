#include "AnimationLoader.h"
#include "cocostudio\CCArmatureDataManager.h"

void AnimationLoader::load()
{
	for (auto k : _loder->getAnimationResData())
	{
		ArmatureDataManager::getInstance()->addArmatureFileInfo(k.second.img, k.second.plist, k.second.config);
	}
}

AnimationLoader::AnimationLoader()
{
	_loder = std::shared_ptr<AnimationResLoader>(new AnimationResLoader("confg.csv"));
	load();
}

AnimationLoader& AnimationLoader::getInstance()
{
	static AnimationLoader loder;
	return loder;
}

Armature* AnimationLoader::createAnimation(const std::string& name)
{
	return Armature::create(name);
}
