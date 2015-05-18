#ifndef __MyTime_h__
#define __MyTime_h__

struct myTm
{
	int _year;
	int _month;
	int _day;
	int _hour;
	int _min;
	int _second;
};

class MyTime
{
public:
	static long getCurrentTime();
	static myTm getTime();
private:

};

#endif // __MyTime_h__
