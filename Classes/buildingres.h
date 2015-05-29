#ifndef __buildingres_h__
#define __buildingres_h__

class BuildingRes
{
public:
	int _type;
	int _id;
	int _originX;
	int _originY;
	int _rectCount;
	int _Hp;
	int _exploreCount;
	bool _shake;
	std::vector<cocos2d::Rect> _buildingRects;
	int _stateImageID[3];
};


#endif // __buildingres_h__
