#ifndef __XMap_h__
#define __XMap_h__

#include "Head.h"
#include "XDropTool.h"
#include "mapconst.h"

class SMapFloor
{
public:
	SMapFloor() :iImageID(0), iFlipH(0), iX(0), iY(0){};
	int iImageID;
	bool iFlipH;
	int iX;
	int iY;
};

class SMapUnit
{
public:
	SMapUnit();
	~SMapUnit();
	void release();
	short urID;//��ʾ�õ�λʹ�õ���һ����ս��λ��Դ
	short id;//����ս��λ��ID��
	short x;//����ս��λ�����ꣻ
	short y;
	short dir;//��ʼ����
	short AIType;//AI����
	short CampType;//��Ӫ���� 0 --- ����  1 --- �˾�  2 --- Player
	short MoveType;//�ƶ���ʽ
	//���ƶ���ʽΪ����ƶ�ʱ��ʾ �ƶ�ʱ��͸���
	//���ƶ���ʽΪ��·���ƶ�ʱ��ʾ ����
	short           iMoveItemCount;
	short           iDistItemCount;//Ŀ�����
	short           iFireItemCount; //�������
	short           iDropToolCount;//�������
	short           iOrderCount;   //ָ����
	short           iRecycleOrderCount; //ѭ��ָ����
	std::vector<int>			iMoveProb;  //8��������ƶ��ĸ���
	std::vector<Vec2>			iMoveItemData;
	std::vector<Vec2>			iFireItemData;
	std::vector<Vec2>			iDistItemData;
	std::vector<XDropTool>      iDropToolData;
	std::vector<XUnitOrder>    iOrderData;
	std::vector<XUnitOrder>    iRecycleOrderData;
};

class SMapBuilding{
public:
	SMapBuilding();
	~SMapBuilding();
	void release();
	bool    bflip;  //0��ʾ��ˮƽ��ת��1��ʾˮƽ��ת
	bool    bcasern;//�Ƿ��Ӫ
	short   brID;   //��ʾ�ý���ʹ�õ��Ǹ���Դ
	short   id;     //�ý�����ID��
	short   x;      //�ý����Ļ�׼������ꣻע�⣺���������ĵ�
	short   y;      //
	short   state;  //��ʼ״̬��0��ʾ��ã�1��ʾ��٣�2��ʾȫ��
	short   tool;   //��ը����ֵĹ���
	short   add;    //������Ϣ
	std::vector<XUnitFactory> iUnitFactoryData;
	std::vector<XDropTool> iDropToolData;
};



class XMap
{
private:

public:
	XMap();
	XMap(int aID);
	~XMap();
	void init(int aID);
	void AnalyzeInfDataL();
	void AnalyzeFloorDataL();
	void AnalyzeUnitDataL();
	void AnalyzeBuildingDataL();
	void AnalyzeRectDataL();
	void Release();
	bool LoadMapImg();
	void DoGameLoad();
	void AnalyzeTopDataL();
	int getID() const { return iID; }
	void setID(int aID) { iID = aID; }
	int getTopCount() const { return iMapTop.size(); }
	std::vector<SMapFloor> getMapTop() const { return iMapTop; }
	int getFloorCount() const { return iMapFloor.size(); }
	std::vector<SMapFloor> getMapFloor() const { return iMapFloor; }
	int getWidth() const { return iWidth; }
	int getHeight() const { return iHeight; }
	int getUnitCount() { return iMapUnit.size(); }
	std::vector<SMapUnit> getSMapUnit() const { return iMapUnit; }
	std::vector<cocos2d::Rect> getWalkRect() {
		return iMapWalkRect;
	}
	void setWalkRectActive(int index, bool active) { ibWalkRectActive[index] = active; }
	bool isWalkRectActive(int index) { return ibWalkRectActive[index]; }
	
	int getBuildingCount() const { return iMapBuilding.size(); }
	std::vector<SMapBuilding> getMapBuilding() { return iMapBuilding; }
private:
	int             	iID;                    // ��ͼID��ȫ��Ψһv
	int					iActivist;
	int             	iWidth;                 // �����ص�
	int             	iHeight;                // �����ص�
	int 				iBackColor;				// ����ɫ
	//��ͼ�����
	int					iCameraX;				//�����X����
	int 				iCameraY;				//�����Y����

	std::vector<SMapFloor>    	iMapTop;                //����ذ�����
	std::vector<SMapFloor>    	iMapFloor;				//�ذ�����
	std::vector<SMapUnit>		iMapUnit;               //Unit����
	std::vector<SMapBuilding>   iMapBuilding;           //��������
	std::vector<bool>          	ibWalkRectActive;		//�������������Ƿ��Ծ
	std::vector<Rect>	        iMapWalkRect;           //�������߾���
};

#endif // __XMap_h__
