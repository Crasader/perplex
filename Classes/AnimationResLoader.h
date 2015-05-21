#ifndef __AnimationResLoader_h__
#define __AnimationResLoader_h__

#include "cocos2d.h"

struct AnimConfig
{
	std::string config;
	std::string img;
	std::string plist;
};

class AnimationResLoader
{
public:
	using MAnimConfig = std::map <std::string, AnimConfig>;
public:
	std::vector< std::string> split(const  std::string& str, const  std::string& p);
	AnimationResLoader(const std::string& filePath);
	const AnimConfig& getAnimConfig(const std::string key)const;
	const MAnimConfig& getAnimationResData()const{ return _data; }
private:
	void readConfig(const std::string &str);
private:
	MAnimConfig _data;
};
#endif // __AnimationResLoader_h__
