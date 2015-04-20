#ifndef __CAnimationData_h__
#define __CAnimationData_h__

#include "Head.h"

struct CImageData
{
	char flag;
	short _ImageID;
	int	_x;
	int _y;
	int _w;
	int _h;
};

struct CFrameData
{
	std::vector<CImageData>		_Images;
	int						_Interval;
};

class AnimRes
{
public:
	AnimRes(int id);
	~AnimRes();
	
	std::vector<CFrameData> getFrames() const { return _Frames; }
	int getID() const { return _ID; }
	void setFrame(CFrameData f);
private:
	bool init();
private:
	int							_ID;
	int							_OriginX;
	int							_OriginY;
	int							_FrameCount;
	std::vector<CFrameData>         _Frames;
};

class CAnimationData
{
public:
	CAnimationData(AnimRes* res);
	~CAnimationData();

private:
	AnimRes* _AnimRes;
};
#endif // __CAnimation_h__
