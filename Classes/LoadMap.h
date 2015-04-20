#ifndef __LoadMap_h__
#define __LoadMap_h__

#include "Head.h"
#include "Ifstream.h"
#include "vector"
#include "memory"

using namespace std;

#define  MAX_TEXT_LEN 256

enum TrigType
{
	TRIGGER_TOP = 1,
	TRIGGER_MIDDLE,
	TRIGGER_BOTTOM,
};

typedef struct
{
	float realLeft;
	float realWidth;
	float realTop;
	float realHeight;
	int color;
	bool colorInit;
	char name[MAX_TEXT_LEN];
} MapInfo;

typedef struct
{
	 int id;
	 float x;
	 float y;
	 int movingX;
	 int movingY;
	 int imageId;
	 int triggerType;
	 int triggerY;
	 bool selected;
	 bool moving;
	 unsigned char flip;
	 char name[MAX_TEXT_LEN];
	 char scriptName[MAX_TEXT_LEN];
	 char prop[MAX_TEXT_LEN];
}EnemyInfo;

class LoadMapRes : public Ref
{
public:
	LoadMapRes();
	~LoadMapRes();
	bool init();
	CREATE_FUNC(LoadMapRes);

public:
	void loadMap(const char* name);
	void readString(Ifstream &is, string &name);
	void cleamImage();
	Node* createEnemy(EnemyInfo& e);
	string getImageName(const char* str);
	void screenShoot();
	const MapInfo& getMapInfo() const { return _mapInfo; }
	void setMapInfo(MapInfo& aMapInfo) { _mapInfo = aMapInfo; }
	Node* getMap() const { return _map; }
private:
	void loadMapLayer(const char* aName, int aType = 1);


	void loadEnemyLayer(const char* aName, int aType = 0);
	void revisePositon(float &x, float &y, const Size& size);
	Rect intersectsRect(const Rect& rectA, const Rect& rectB);
private:
	MapInfo _mapInfo;
	vector<EnemyInfo> _enemyInfos;
	Node* _map;
	Texture2D** _imgs;

	short _imgCount;
	short _SpriteCount;
};

#endif // __LoadMap_h__
