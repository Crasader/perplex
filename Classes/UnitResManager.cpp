#include "UnitResManager.h"
#include "Ifstream.h"



UnitResManager::UnitResManager(const std::string& file)
{
	log("%s,enty, %s", __FUNCTION__, file.c_str());
	Ifstream in(file.c_str());
	if (!in.fail())
	{
		log("read....");
		int urCount;
		in >> urCount;
		_unitReses.clear();
		for (int n = 0; n < urCount; n++)
		{
			std::shared_ptr<UnitRes> pUnitRes(new UnitRes());
			int id, hp, animID;
			in >> id >> hp >> animID;
			pUnitRes->_ID = id;
			pUnitRes->_HP = hp;
			pUnitRes->_animID = animID;
			_unitReses.push_back(pUnitRes);
		
			pUnitRes->_moveAnimRanges.resize(WALK_DIR_COUNT);
			//0 ~ 5://8�����ƶ��Ķ���range��ֻ��5�������ݣ�˳��Ϊ D, DL, L, UL, U
			//TInt start;//ÿ���������ʼ֡
			//TInt end;//ÿ������Ľ���֡
			for (int i = 0; i < 5; i++)
			{
				pUnitRes->_moveAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				int x, y;
				in >> x >> y;
				pUnitRes->_moveAnimRanges[i]->x = x;
				pUnitRes->_moveAnimRanges[i]->y = y;
			}
			//��������3�����������
			for (int i = 5; i < 8; i++)
			{
				pUnitRes->_moveAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_moveAnimRanges[i] = pUnitRes->_moveAnimRanges[8 - i];
			}
			//        =========================================================================================
			//for 0 ~ 8://8�����ƶ��ٶȣ�8�������ݶ��У�˳��Ϊ D, DL, L, LU, U, UR, R, RD
			//TInt speedX;
			//TInt speedY;
			pUnitRes->_speedX.resize(WALK_DIR_COUNT);
			pUnitRes->_speedY.resize(WALK_DIR_COUNT);
			for (int i = 0; i < 8; i++)
			{
				int x, y;
				in >> x >> y;
				pUnitRes->_speedX[i] = x;
				pUnitRes->_speedY[i] = y;
			}
			int gridCount;
			in >> gridCount;
			pUnitRes->_moveGridCount = gridCount;
			if (gridCount > 0)
			{
				pUnitRes->_moveGrids.resize(gridCount);
				//for 0 ~ gridCount://ÿ��ռ�ظ������ս��λ��׼���ƫ��
				//int offsetX;
				//int offsetY;
				for (int i = 0; i < gridCount; i++)
				{
					int x, y;
					in >> x >> y;
					pUnitRes->_moveGrids[i] = std::shared_ptr<Vec2>(new Vec2());
					pUnitRes->_moveGrids[i]->x = x;
					pUnitRes->_moveGrids[i]->y = y;
				}
			}
			//==========================================================================================================
			//0 ~ 9://16����վ���Ķ���range��ֻ��9�������ݣ�˳��Ϊ D, DDL, DL, DLL, L, LLU, LU, LUU, U
			//int start;
			//int end;
			pUnitRes->_standAnimRanges.resize(STAND_DIR_COUNT);
			for (int i = 0; i < 9; i++)
			{
				int x, y;
				in >> x >> y;
				pUnitRes->_standAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_standAnimRanges[i]->x = x;
				pUnitRes->_standAnimRanges[i]->y = y;
			}		
			//�����������������
			for (int i = 9; i < 16; i++)
			{
				pUnitRes->_standAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_standAnimRanges[i] = pUnitRes->_standAnimRanges[16 - i];
			}
			//for 0 ~16://16�����������ĵ��λ�ã�16�������ݶ��У�˳��ΪD, DDL, DL, DLL, L, LLU, LU, LUU, U, UUR, UR, URR, R, RRD, RD, RDD
			//        		TInt centerX;//���ĵ���Ի�׼���ƫ����
			//        		TInt centerY;
			pUnitRes->_centerPoints.resize(STAND_DIR_COUNT);
			for (int i = 0; i < 16; i++)
			{
				pUnitRes->_centerPoints[i] = std::shared_ptr<Vec2>(new Vec2());
				int x, y;
				in >> x >> y;
				pUnitRes->_centerPoints[i]->x = x;
				pUnitRes->_centerPoints[i]->y = y;
			}
			    
			//        		for 0~ 16://16���򹥻���Χ��16�������ݶ��У�˳��ͬ��
			pUnitRes->_bodyRectCounts.resize(STAND_DIR_COUNT);
			pUnitRes->_bodyRect.resize(STAND_DIR_COUNT);
			for (int i = 0; i < 16; i++)
			{
				int rectCount;
				in >> rectCount;
				pUnitRes->_bodyRectCounts[i] = rectCount;
				if (rectCount <= 0)
				{
					continue;
				}
				pUnitRes->_bodyRect[i].resize(rectCount);
				//for 0 ~ rectCount://ÿ���������ε�����
				//���������ս��λ��׼���ƫ��
				int left, top, width, height;
				for (int j = 0; j < rectCount; j++)
				{
					in >> left >> top >> width >> height;
					pUnitRes->_bodyRect[i][j] = std::shared_ptr<Rect>(new Rect());
					pUnitRes->_bodyRect[i][j]->size.width = width;
					pUnitRes->_bodyRect[i][j]->size.height = height;
					pUnitRes->_bodyRect[i][j]->origin.x = left;
					pUnitRes->_bodyRect[i][j]->origin.y = top;
				}
			}
			//        ===================================================================================================
			//for 0 ~ 9://16���������Ķ���range��ֻ��9�������ݣ�˳��ͬ��
			//	int start;
			//	int end;
			pUnitRes->_dieAnimRanges.resize(STAND_DIR_COUNT);
			for (int i = 0; i < 9; i++)
			{
				int x, y;
				in >> x >> y;
				pUnitRes->_dieAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_dieAnimRanges[i]->x = x;
				pUnitRes->_dieAnimRanges[i]->y = y;
			}
			//������������Ķ���
			for (int i = 9; i < 16; i++) {
				pUnitRes->_dieAnimRanges[i] = pUnitRes->_dieAnimRanges[16 - 8];
			}

			//////////////////////////////////////////////////////////////////////////
			pUnitRes->_explodeCount.resize(STAND_DIR_COUNT);
			pUnitRes->_explode.resize(STAND_DIR_COUNT);
			for (int i = 0; i < 16; i++)
			{
				int exploreCount;
				in >> exploreCount;
				if (exploreCount <= 0)
				{
					continue;
				}
				pUnitRes->_explode[i].resize(exploreCount);
				//	int animID;//�ñ�ը�Ķ���ID
				//  int x;//��ըЧ�������ս��λ��׼���ƫ����
				//  int y;
				//	int start;//�����ը�����Ĳ���range
				//	int end;
				//  int beginIndex;//��ʾ�ӻ����˵�����������ʼ����ֱ��������beginIndex��ô��֮֡��ʼ���������ըЧ��
				//  for 0 ~ exploreCount://ÿ��������ը������
				for (int j = 0; j < exploreCount; j++)
				{
					pUnitRes->_explode[i][j] = std::shared_ptr<Explode>(new Explode());
					int animID, add, x, y, start, end, beginIndex;
					in >> animID >> add >> x >> y >> start >> end >> beginIndex;
					pUnitRes->_explode[i][j]->iAnimID = animID;
					pUnitRes->_explode[i][j]->iAdd = add;
					pUnitRes->_explode[i][j]->iX = x;
					pUnitRes->_explode[i][j]->iY = y;
					pUnitRes->_explode[i][j]->iStart = start;
					pUnitRes->_explode[i][j]->iEnd = end;
					pUnitRes->_explode[i][j]->iBeginIndex = beginIndex;

				}
			}
			//////////////////////////////////////////////////////////////////////////
			int weaponPosCount;
			in >> weaponPosCount;
			if (weaponPosCount > 0)
			{
				//for 0 ~ weaponPosCount://ÿ�������ҽӵ������
				pUnitRes->_weaponPos.resize(STAND_DIR_COUNT);
				for (int i = 0; i < 16; i++)
				{
					pUnitRes->_weaponPos[i].resize(weaponPosCount);
				}
				int id, weaponType, cover, x, y;
				for (int j = 0; j < weaponPosCount; j++)
				{
					in >> id >> weaponType >> cover >> x >> y;
					//              int x;//�ùҽӵ��ڸ÷����ϵ�λ�������ս��λ��׼���ƫ��
					//              int y;
					//for 0 ~ 7://�ùҽӵ���8�����ϵľ���λ�ã�8�������ݶ��У�˳��ͬ��
					for (int i = 0; i < 16; i++)
					{
						pUnitRes->_weaponPos[i][j] = std::shared_ptr<WeaponPos>(new WeaponPos());
						pUnitRes->_weaponPos[i][j]->id = id;
						pUnitRes->_weaponPos[i][j]->type = weaponType;
						pUnitRes->_weaponPos[i][j]->cover = cover;
						pUnitRes->_weaponPos[i][j]->x = x;
						pUnitRes->_weaponPos[i][j]->y = y;
					}
				
				}
			}
			//�����ҽӵ����������б�
			std::vector<int> weaponTypeList;
			std::vector<int> weaponEveryTypeCount;
			weaponTypeList.resize(UNIT_MAX_WEAPON_COUNT);
			weaponEveryTypeCount.resize(UNIT_MAX_WEAPON_COUNT);
			for (int p = 0; p < UNIT_MAX_WEAPON_COUNT; p++)
			{
				weaponTypeList[p] = -1;
				weaponEveryTypeCount[p] = 0;
			}
			bool use = false;
			pUnitRes->_weaponTypeCount = 0;
			for (int p = 0; p < weaponPosCount; p++)
			{
				for (int m = 0; m < pUnitRes->_weaponTypeCount; m++)
				{
					if (pUnitRes->_weaponPos[0][p]->type == weaponTypeList[p])
					{
						weaponEveryTypeCount[m]++;
						use = true;
						break;
					}
				}
				if (use)
				{
					continue;
				}
				weaponTypeList[pUnitRes->_weaponTypeCount] = pUnitRes->_weaponPos[0][p]->type;
				weaponEveryTypeCount[pUnitRes->_weaponTypeCount]++;
				pUnitRes->_weaponTypeCount++;
			}
			pUnitRes->_weaponTypeList.resize(pUnitRes->_weaponTypeCount);
			pUnitRes->_weaponEveryTypeCountList.resize(pUnitRes->_weaponTypeCount);
			pUnitRes->_weaponTypeIndexList.resize(pUnitRes->_weaponTypeCount);
			for (int p = 0; p < pUnitRes->_weaponTypeCount; p++)
			{
				pUnitRes->_weaponTypeList[p] = weaponTypeList[p];
				pUnitRes->_weaponEveryTypeCountList[p] = weaponEveryTypeCount[p];
			}
			//ÿ���������͵Ĺҽӵ�����ֵ��
			for (int p = 0; p < pUnitRes->_weaponTypeCount; p++)
			{
				pUnitRes->_weaponTypeIndexList[p].resize(pUnitRes->_weaponEveryTypeCountList[p]);
				for (int j = 0, m = 0; j < weaponPosCount; j++)
				{
					if (pUnitRes->_weaponPos[0][j]->type == pUnitRes->_weaponTypeList[p])
					{
						pUnitRes->_weaponTypeIndexList[p][m] = j;
						m++;
					}
				}
			}
		}
	}
	in.close();
	log("%s,exit", __FUNCTION__);
}

std::shared_ptr<UnitRes> UnitResManager::getUnitResFromID(int ID)
{
	for (auto a : _unitReses)
	{
		if (a->_ID == ID)
		{
			return a;
		}
	}
	return nullptr;
}