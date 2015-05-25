#include "XMap.h"
#include "Ifstream.h"
#include "GameGlobe.h"
#include "GameControllers.h"
#include "Unit.h"
#include "GameScene.h"

void XMap::AnalyzeRectDataL()
{
	char f[256];
	sprintf(f, "l%d_rect_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		auto iWalkRectCount = 0;
		dis >> iWalkRectCount;
		if (iWalkRectCount > 0)
		{
			ibWalkRectActive.clear();
			iMapWalkRect.clear();
			ibWalkRectActive.resize(iWalkRectCount);
			iMapWalkRect.resize(iWalkRectCount);
			int l, t, w, h;
			for (int n = 0; n < iWalkRectCount; n++)
			{
				dis >> l >> t >> w >> h;
				l *= CC_CONTENT_SCALE_FACTOR();
				t *= CC_CONTENT_SCALE_FACTOR();
				w *= CC_CONTENT_SCALE_FACTOR();
				h *= CC_CONTENT_SCALE_FACTOR();
				ibWalkRectActive[n] = false;
				iMapWalkRect[n].setRect(l, t, w, h);
			}
		}
	}
	dis.close();
}

void XMap::AnalyzeBuildingDataL()
{
	//������������
	char f[256];
	sprintf(f, "l%d_building_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		auto iBuildingCount = 0;
		dis >> iBuildingCount;
		iMapBuilding.clear();
		iMapBuilding.resize(iBuildingCount);
		if (iBuildingCount > 0)
		{
			for (int i = 0; i < iBuildingCount; i++)
			{
				int brID, id, x, y, state, bflip, factoryCount;
				dis >> brID >> id >> x >> y >> state >> bflip >> factoryCount;
				iMapBuilding[i].brID = brID;
				iMapBuilding[i].id = id;
				iMapBuilding[i].x = x * CC_CONTENT_SCALE_FACTOR();
				iMapBuilding[i].y = y * CC_CONTENT_SCALE_FACTOR();
				iMapBuilding[i].state = state;
				iMapBuilding[i].bflip = bflip == 0 ? false : true;
				iMapBuilding[i].bcasern = false;
				iMapBuilding[i].tool = -1;//������-1ʱ������������
				iMapBuilding[i].add = 0;
		
				if (factoryCount > 0)
				{
					iMapBuilding[i].iUnitFactoryData.clear();
					iMapBuilding[i].iUnitFactoryData.resize(factoryCount);
					for (int k = 0; k < factoryCount; ++k)
					{
						int unitType, left, top, width, height, unitCount, interval;
						dis >> unitType >> left >> top >> width >> height >> unitCount >> interval;
					
						iMapBuilding[i].iUnitFactoryData[k].unitType = unitType;
						iMapBuilding[i].iUnitFactoryData[k].left = left * CC_CONTENT_SCALE_FACTOR();
						iMapBuilding[i].iUnitFactoryData[k].top = top * CC_CONTENT_SCALE_FACTOR();
						iMapBuilding[i].iUnitFactoryData[k].width = width * CC_CONTENT_SCALE_FACTOR();
						iMapBuilding[i].iUnitFactoryData[k].height = height * CC_CONTENT_SCALE_FACTOR();
						iMapBuilding[i].iUnitFactoryData[k].unitCount = unitCount;
						iMapBuilding[i].iUnitFactoryData[k].interval = interval;
					}
				}
				auto iDropToolCount = 0;
				dis >> iDropToolCount;
				if (iDropToolCount > 0)
				{
					iMapBuilding[i].iDropToolData.clear();
					iMapBuilding[i].iDropToolData.resize(iDropToolCount);
					for (int k = 0; k < iDropToolCount; ++k)
					{
						int type, min, max, prob;
						dis >> type >> min >> max >> prob;
						iMapBuilding[i].iDropToolData[k].type = type;
						iMapBuilding[i].iDropToolData[k].min = min;
						iMapBuilding[i].iDropToolData[k].max = max;
						iMapBuilding[i].iDropToolData[k].prob = prob;
					}
				}
			}
		}

	}
	dis.close();

	//���ɽ�����
	
}

void XMap::AnalyzeInfDataL()
{
	char f[256];
	sprintf(f, "l%d_mapinfo_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		dis >> iWidth >> iHeight >> iBackColor;
		iWidth *= CC_CONTENT_SCALE_FACTOR();
		iHeight *= CC_CONTENT_SCALE_FACTOR();
		byte r, g, b;
		r = (iBackColor & 0xff0000) >> 16;
		g = (iBackColor & 0xff00) >> 8;
		b = (iBackColor & 0xff);
	}
	dis.close();
}

void XMap::AnalyzeFloorDataL()
{
	char f[256];
	sprintf(f, "l%d_floor_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		auto iFloorCount = 0;
		dis >> iFloorCount;
		iMapFloor.clear();
		iMapFloor.resize(iFloorCount);
		for (int i = 0; i < iFloorCount; i++)
		{
			int id;
			dis >> id;
			iMapFloor[i].iImageID = id;
			if (iMapFloor[i].iImageID >= 500)
			{
				iMapFloor[i].iImageID = iMapFloor[i].iImageID - (500 - 134);
			}
			else if (iMapFloor[i].iImageID >= 400)
			{
				iMapFloor[i].iImageID = iMapFloor[i].iImageID - (400 - 329);
			}
			int temp;
			dis >> temp;
			dis >> iMapFloor[i].iX;
			dis >> iMapFloor[i].iY;
			iMapFloor[i].iX *= CC_CONTENT_SCALE_FACTOR();
			iMapFloor[i].iY *= CC_CONTENT_SCALE_FACTOR();
			int flip;
			dis >> flip;
			iMapFloor[i].iFlipH = flip > 0 ? true : false;
		}
	}
	dis.close();
}

void XMap::AnalyzeTopDataL()
{
	char f[256];
	sprintf(f, "l%d_tree_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		auto iTopCount = 0;
		dis >> iTopCount;
		iMapTop.clear();
		iMapTop.resize(iTopCount);
		if (iTopCount > 0)
		{
			for (int i = 0; i < iTopCount; ++i)
			{
				int imageID, temp, ix, iy, ifliph;
				dis >> imageID >> temp >> ix >> iy >> ifliph;
		
				iMapTop[i].iImageID = imageID;
				iMapTop[i].iX = ix * CC_CONTENT_SCALE_FACTOR();
				iMapTop[i].iY = iy * CC_CONTENT_SCALE_FACTOR();
				iMapTop[i].iFlipH = ifliph > 0 ? true : false;
			}
		}
	}
	dis.close();
}

void XMap::AnalyzeUnitDataL()
{
	char f[256];
	sprintf(f, "l%d_unit_mobile.dat", iID);
	Ifstream dis(f);
	if (!dis.fail())
	{
		auto iUnitCount = 0;
		dis >> iUnitCount;
		if (iUnitCount <= 0)
		{
			dis.close();
			return;
		}
		iMapUnit.clear();
		iMapUnit.resize(iUnitCount);
		for (int i = 0; i < iUnitCount; i++)
		{
			int urID, id, x, y, dir, aitype, camptype, movetype;
			dis >> urID;
			dis >> id;
			dis >> x;
			dis >> y;
			dis >> dir;
			dis >> aitype;
			dis >> camptype;
			dis >> movetype;
			x *= CC_CONTENT_SCALE_FACTOR();
			y *= CC_CONTENT_SCALE_FACTOR();
			iMapUnit[i].urID = urID;
			iMapUnit[i].id = id;
			iMapUnit[i].x = x;
			iMapUnit[i].y = y;
			iMapUnit[i].dir = dir;
			iMapUnit[i].AIType = aitype;
			iMapUnit[i].CampType = camptype;
			iMapUnit[i].MoveType = movetype;

			int moveDataLength = 0;
			dis >> moveDataLength;

			//����·��
			if (iMapUnit[i].MoveType == STAND_MOVE)
			{
				//�ƶ��������
				iMapUnit[i].iMoveProb.clear();
				iMapUnit[i].iMoveProb.resize(8);
				for (int k = 0; k < 8; k++)
				{
					iMapUnit[i].iMoveProb[k] = 10;
				}

				iMapUnit[i].iDistItemCount = 0;
				iMapUnit[i].iMoveItemCount = 4;
				iMapUnit[i].iMoveItemData.clear();
				iMapUnit[i].iMoveItemData.resize(iMapUnit[i].iMoveItemCount);
				for (int k = 0; k < iMapUnit[i].iMoveItemCount; k++)
				{
					//iMapUnit[i]->iMoveItemData[k] = new Vec2();
					iMapUnit[i].iMoveItemData[k].x = 25 * CC_CONTENT_SCALE_FACTOR();
					iMapUnit[i].iMoveItemData[k].y = 20 * k  * CC_CONTENT_SCALE_FACTOR();
				}
			}

			//����ƶ�
			if (iMapUnit[i].MoveType == RANDOM_MOVE || iMapUnit[i].MoveType == ORDER_MOVE)
			{
				//�ƶ��������
				iMapUnit[i].iMoveProb.clear();
				iMapUnit[i].iMoveProb.resize(8);
				for (int k = 0; k < 8; k++)
				{
					dis >> iMapUnit[i].iMoveProb[k];
				}

				int tmpCount1, tmpCount2;
				dis >> tmpCount1;
				dis >> tmpCount2;
				iMapUnit[i].iDistItemCount = tmpCount1;
				//����Ŀ����룬��Ŀ�귽���ƶ�����
				if (iMapUnit[i].iDistItemCount > 0)
				{
					iMapUnit[i].iDistItemData.clear();
					iMapUnit[i].iDistItemData.resize(tmpCount1);
					for (int k = 0; k < tmpCount1; k++)
					{
						int tempx, tempy;
						dis >> tempx;
						dis >> tempy;
					
						iMapUnit[i].iDistItemData[k].x = tempx;
						iMapUnit[i].iDistItemData[k].y = tempy;
					}
				}
				//�ƶ�ʱ�����
				iMapUnit[i].iMoveItemCount = tmpCount2;
				if (tmpCount2 > 0)
				{
					iMapUnit[i].iMoveItemData.clear();
					iMapUnit[i].iMoveItemData.resize(tmpCount2);
					for (int k = 0; k < tmpCount2; k++)
					{
						int tempx, tempy;
						dis >> tempx;
						dis >> tempy;
					
						iMapUnit[i].iMoveItemData[k].x = tempx;
						iMapUnit[i].iMoveItemData[k].y = tempy;
					}
				}
			}
			//�߹̶�·��
			else if (iMapUnit[i].MoveType == PATH_MOVE)
			{
				iMapUnit[i].iMoveItemCount = moveDataLength;
				if (iMapUnit[i].iMoveItemCount > 0)
				{
					iMapUnit[i].iMoveItemData.clear();
					iMapUnit[i].iMoveItemData.resize(moveDataLength);
					for (int k = 0; k < moveDataLength; k++)
					{
						int tempx, tempy;
						dis >> tempx >> tempy;
						
						iMapUnit[i].iMoveItemData[k].x = tempx;
						iMapUnit[i].iMoveItemData[k].y = tempy;
					}
				}
			}

			//��������
			int tmpCount3;
			dis >> tmpCount3;
			iMapUnit[i].iFireItemCount = tmpCount3;
			if (tmpCount3 > 0)
			{
				iMapUnit[i].iFireItemData.clear();
				iMapUnit[i].iFireItemData.resize(tmpCount3);
				for (int k = 0; k < tmpCount3; k++)
				{
					int tempx, tempy;
					dis >> tempx;
					dis >> tempy;
					
					iMapUnit[i].iFireItemData[k].x = tempx;
					iMapUnit[i].iFireItemData[k].y = tempy;
				}
			}
			//�������
			int toolCount;
			dis >> toolCount;
			iMapUnit[i].iDropToolCount = toolCount;
			if (toolCount > 0)
			{
				iMapUnit[i].iDropToolData.clear();
				iMapUnit[i].iDropToolData.resize(toolCount);
				for (int k = 0; k < toolCount; k++)
				{
					int type, min, max, prob;
					dis >> type;
					dis >> min;
					dis >> max;
					dis >> prob;
					iMapUnit[i].iDropToolData[k].type = type;
					iMapUnit[i].iDropToolData[k].min = min;
					iMapUnit[i].iDropToolData[k].max = max;
					iMapUnit[i].iDropToolData[k].prob = prob;
				}
			}

			//ָ��
			int orderCount;
			dis >> orderCount;
			iMapUnit[i].iOrderCount = orderCount;
			if (orderCount > 0)
			{
				iMapUnit[i].iOrderData.clear();
				iMapUnit[i].iOrderData.resize(orderCount);
				for (int k = 0; k < orderCount; k++)
				{
					int direct, speed, time, bulletypte, firelogic;
					dis >> direct;
					dis >> speed;
					dis >> time;
					dis >> bulletypte;
					dis >> firelogic;
					
					iMapUnit[i].iOrderData[k].Direct = direct;
					iMapUnit[i].iOrderData[k].Speed = speed;
					iMapUnit[i].iOrderData[k].Time = time;
					iMapUnit[i].iOrderData[k].BulletType = bulletypte;
					iMapUnit[i].iOrderData[k].FireLogic = firelogic;
					iMapUnit[i].iOrderData[k].Addition = 0;
				}
			}

			//ѭ��ָ��
			int recycleCount;
			dis >> recycleCount;
			iMapUnit[i].iRecycleOrderCount = recycleCount;
			if (recycleCount > 0)
			{
				iMapUnit[i].iRecycleOrderData.clear();
				iMapUnit[i].iRecycleOrderData.resize(recycleCount);
				for (int j = 0; j < recycleCount; j++)
				{
					int direct, speed, time, bulletypte, firelogic;
					dis >> direct;
					dis >> speed;
					dis >> time;
					dis >> bulletypte;
					dis >> firelogic;
				
					iMapUnit[i].iRecycleOrderData[j].Direct = direct;
					iMapUnit[i].iRecycleOrderData[j].Speed = speed;
					iMapUnit[i].iRecycleOrderData[j].Time = time;
					iMapUnit[i].iRecycleOrderData[j].BulletType = bulletypte;
					iMapUnit[i].iRecycleOrderData[j].FireLogic = firelogic;
					iMapUnit[i].iRecycleOrderData[j].Addition = 0;
				}
			}
		}
	}
	dis.close();
}

void XMap::init(int aID)
{
	iID = aID;                   //��ͼID��ȫ��Ψһ
	iWidth = 0;
	iHeight = 0;
	iBackColor = 0;
	Release();
	iCameraX = 0;
	iCameraY = 0;
}

XMap::~XMap()
{
	Release();
	log("%s", __FUNCTION__);
}

XMap::XMap(int aID)
:iID(aID)                   // ��ͼID��ȫ��Ψһ
,iWidth(0)                // �����ص�
,iHeight(0)               // �����ص�
, iBackColor(0) 		// ����ɫ
//��ͼ�����
, iCameraX(0) 				//�����X����
, iCameraY(0) 				//�����Y����
, iMapBuilding()
, iMapFloor()
, iMapUnit()
, iMapTop()
{
	log("%s", __FUNCTION__);
}

XMap::XMap()
:XMap(0)
{
}

void XMap::Release()
{


}

SMapUnit::SMapUnit()
:urID(0)//��ʾ�õ�λʹ�õ���һ����ս��λ��Դ
,id(0)//����ս��λ��ID��
,x(0)//����ս��λ�����ꣻ
,y(0)
,dir(0)//��ʼ����
,AIType(0)//AI����
,CampType(0)//��Ӫ���� 0 --- ����  1 --- �˾�  2 --- Player
,MoveType(0)//�ƶ���ʽ
//���ƶ���ʽΪ����ƶ�ʱ��ʾ �ƶ�ʱ��͸���
//���ƶ���ʽΪ��·���ƶ�ʱ��ʾ ����
,iMoveItemCount(0)
,iDistItemCount(0)//Ŀ�����
,iFireItemCount(0) //�������
,iDropToolCount(0)//�������
,iOrderCount(0)   //ָ����
,iRecycleOrderCount(0) //ѭ��ָ����
{

}

SMapUnit::~SMapUnit()
{
	release();
}

void SMapUnit::release()
{
	urID = 0;//��ʾ�õ�λʹ�õ���һ����ս��λ��Դ
	id = 0;//����ս��λ��ID��
	x = 0;//����ս��λ�����ꣻ
	y = 0;
	dir = 0;//��ʼ����
	AIType = 0;//AI����
	CampType = 0;//��Ӫ���� 0 --- ����  1 --- �˾�  2 --- Player
	MoveType = 0;//�ƶ���ʽ
	//���ƶ���ʽΪ����ƶ�ʱ��ʾ �ƶ�ʱ��͸���
	//���ƶ���ʽΪ��·���ƶ�ʱ��ʾ ����
	iMoveItemCount = 0;
	iDistItemCount = 0;//Ŀ�����
	iFireItemCount = 0; //�������
	iDropToolCount = 0;//�������
	iOrderCount = 0;   //ָ����
	iRecycleOrderCount = 0; //ѭ��ָ����
}

SMapBuilding::SMapBuilding()
:bflip(0)  //0��ʾ��ˮƽ��ת��1��ʾˮƽ��ת
,bcasern(0)//�Ƿ��Ӫ
,brID(0)   //��ʾ�ý���ʹ�õ��Ǹ���Դ
,id(0)     //�ý�����ID��
,x(0)      //�ý����Ļ�׼������ꣻע�⣺���������ĵ�
,y(0)      //
,state(0)  //��ʼ״̬��0��ʾ��ã�1��ʾ��٣�2��ʾȫ��
,tool(0)   //��ը����ֵĹ���
,add(0)    //������Ϣ
{

}

SMapBuilding::~SMapBuilding()
{
	release();
}

void SMapBuilding::release()
{
	bflip = false;
	bcasern = 0;
	brID = 0;
	id = 0;
	x = 0;
	y = 0;
	state = 0;
	tool = 0;
	add = 0;
}
