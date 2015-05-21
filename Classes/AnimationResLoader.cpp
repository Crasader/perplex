#include "AnimationResLoader.h"

USING_NS_CC;
using namespace std;


AnimationResLoader::AnimationResLoader(const std::string& filePath)
	:_data()
{
	auto size = 0L;
	auto config = cocos2d::FileUtils::getInstance()->getStringFromFile(filePath);
	readConfig(config);
}

const AnimConfig& AnimationResLoader::getAnimConfig(const std::string key) const
{
	return _data.at(key);
}

void AnimationResLoader::readConfig(const std::string &str)
{
	auto line = split(str, "\n");
	for (auto l : line)
	{
		auto cs = split(l, ",");
		if (!cs.empty())
		{
			AnimConfig config;
			string key{ "" };
			for (auto c : cs)
			{
				auto index = c.find_last_of(".");
				auto suffix = c.substr(index);
				std::transform(suffix.begin(), suffix.end(), suffix.begin(), ::tolower);
				if (suffix == ".csb")
				{
					config.config = c;
					key = c.substr(0, index);
				}
				else if (suffix == ".plist")
				{
					config.plist = c;
				}
				else if (suffix == ".png")
				{
					config.img = c;
				}
			}
			_data.insert(pair<string, AnimConfig>(key, config));
		}
	}
}

vector<string>  AnimationResLoader::split(const string& str, const string& p)
{
	vector<string> ret;
	if (p.empty()) return ret;
	auto start = 0;
	auto index = str.find_first_of(p, 0);
	while (index != str.npos)
	{
		if (start != index)
		{
			ret.push_back(str.substr(start, index - start));
		}
		start = index + 1;
		index = str.find_first_of(p, start);
	}
	if (!str.substr(start).empty()) ret.push_back(str.substr(start));
	return ret;
}
