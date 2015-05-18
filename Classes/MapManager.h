#ifndef __MapManager_h__
#define __MapManager_h__

#include "Head.h"
#include "GameGlobe.h"
#include "mapconst.h"

class GameScene;
class XMap;

class MapManager
{
public:
	enum MapLayer
	{
		EIdle = 0,
		ELoadMapInf, //��ͼ����
		ELoadMapFloor,//�ذ��
		ELoadMapTop,//����
		ELoadMapBuilding,//����
		ELoadMapUnit,//�����ɫ
		ELoadMapWalkRect,//���߷�Χ
		ELoadMapImg,//ͼ��
	};

	MapManager(GameScene* scene);
	~MapManager();
	bool requestMap(int aMapID, bool aLocal);
	bool isMapOk(int aMapID);
	bool analyzeDataL();
	bool loadMapImg(int aMapID);
	void analyzeMap();
	bool createFloor();
	Node* getFloor() { return _floor; }
	void createUnits();
	int getXMapW();
	int getXMapH();
	Size getMapSize(){ return Size(getXMapW(), getXMapH()); }
	bool isLoadingMap() { return iLoadingMap; }
	int getActiveMapID() { return iActiveMapID; }
	std::shared_ptr<XMap> getActiveMap() { return iActiveMap; }
private:
	 GameScene*           iGameScene;
	 bool                 iLoadingMap;    //�Ƿ����������ͼ
	 bool                 iLocalMap;
	 bool                 iGetMap;
	 int                  iRequestMapID;
	 int                  iActiveMapID;   //��ǰ��Ծ�ĵ�ͼID
	 int 				  iLastCameraY;	//�ϴ��������Y����
	 int				  iImgCounts;
	 int				  iLoadState;     //�������
	 std::shared_ptr<XMap>               iPrepareLoadMap;//׼������ĵ�ͼ��
	 std::shared_ptr<XMap>                iActiveMap;     //��ǰ��Ծ�ĵ�ͼ
	 Texture2D** 		  iMapImgs;		//��ͼͼ��
	 std::string          iFileName;      //��ͼ����
	 Node*				 _floor;//�ذ�
};

#endif // __MapManager_h__
