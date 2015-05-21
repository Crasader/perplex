#include "AnimationManager.h"
#include "Ifstream.h"
#include <memory>


AnimationManage::AnimationManage(const char* name)
{
	/*LoadRes(name);*/
}

bool AnimationManage::LoadRes(const char* name)
{
	char f[256];
	sprintf(f, "anim/%d.anim", 0);
	Ifstream stream(f);
	if (!stream.fail())
	{
		short animID, frameCount;
		stream >> animID >> frameCount;
		AnimRes res(0);
		for (auto j = 0; j < frameCount; j++)
		{
			CFrameData frame;
			unsigned char flag, imageLen;
			short interval, imageID, w, h;
			int x, y;
			stream >> interval >> imageLen;
			frame._Interval = interval;
			for (auto i = 0; i < imageLen; i++)
			{
				CImageData image;
				stream >> flag >> imageID >> x >> y >> w >> h;
				image.flag = flag;
				image._ImageID = imageID;
				image._x = x;
				image._y = y;
				image._w = w;
				image._h = h;
				frame._Images.push_back(image);
			}
			res.setFrame(frame);
		}
		_AnimaReses.push_back(res);
	}
	stream.close();
	return true;
}

AnimRes AnimationManage::getAnimRes(int resID)
{
	for (auto animares : _AnimaReses)
	{
		if (animares.getID() == resID)
		{
			return animares;
		}
	}
	return AnimRes(0);
}
