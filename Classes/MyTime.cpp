#include "MyTime.h"
#include "cocos2d.h"

USING_NS_CC;

myTm MyTime::getTime()
{
	struct tm *_tm;
	time_t timep;
#if (CC_TARGET_PLATFORM == CC_PLATFORM_WIN32)
	time(&timep);
#else
	struct timeval now;
	gettimeofday(&now, nullptr);
	timep = now.tv_sec;
#endif

	_tm = localtime(&timep);
	return myTm{ _tm->tm_year + 1900, _tm->tm_mon + 1, _tm->tm_mday, _tm->tm_hour, _tm->tm_min, _tm->tm_sec};
}

long MyTime::getCurrentTime()
{
	struct timeval tv;
	gettimeofday(&tv, nullptr);
	return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}
