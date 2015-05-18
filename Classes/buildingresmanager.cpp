#include "buildingresmanager.h"



BuildingResManager::BuildingResManager(const std::string& res)
{
	loadRes(res);
}

void BuildingResManager::loadRes(const std::string& res)
{
	Ifstream fin(res.c_str());
	if (!fin.fail())
	{
		int count;
		fin >> count;
		if (count > 0)
		{
			_buildingResList.resize(count);
			for (int n = 0; n = count; n++)
			{
				auto pBuildingRes = std::make_shared<BuildingRes>(BuildingRes());
				int type, ID, shake, x, y, rectCount;
				int left, top, width, height;
				int stateImageID, hp, exploreCount;
				int animaID, aAdd, ax, ay, start, end, beginIndex;
				int interval;

				fin >> type >> ID >> shake >> x >> y >> rectCount;
				pBuildingRes->_rectCount = rectCount;
				if (rectCount > 0)
				{
					//			for 0 ~ rectCount://ÿ���������ε�����
					//				int left;//������Խ���ͼƬ�����ĵ��ƫ����
					//				int top;
					//				int width;
					//				int height;
					//			end for
					pBuildingRes->_buildingRects.resize(rectCount);
					for (int i = 0; i < rectCount; i++)
					{
						fin >> left >> top >> width >> height;
						pBuildingRes->_buildingRects[i] = cocos2d::Rect(left, top, width, height);
					}
				}
				fin >> stateImageID;
				fin >> stateImageID;
				fin >> stateImageID;
				fin >> hp;
				pBuildingRes->_Hp = hp;
				fin >> exploreCount;
				if (exploreCount > 0)
				{
					//for 0 ~ exploreCount://ÿ��������ը������
					//int animID;//�ñ�ը�Ķ���ID
					//int x;//��ըЧ����Ըý���ͼƬ�����ĵ�ƫ����
					//int y;
					//int start;//�����ը�����Ĳ���range
					//int end;
					//int beginIndex;//��ʾ�ӽ���������ʱ�̿�ʼ����ֱ��������beginIndex��ô�����֮��ʼ���������ըЧ��
					//end for

					for (int i = 0; i < exploreCount; i++)
					{
						fin >> animaID >> aAdd >> ax >> ay >> start >> end >> beginIndex;
					}
				}
				fin >> interval;
			}
		}
	}
	fin.close();
}

std::shared_ptr<BuildingRes> BuildingResManager::getBuildingResFromID(int ID)
{
	for (auto a : _buildingResList)
	{
		if (a->_id == ID)
		{
			return a;
		}
	}
	return _buildingResList[0];
}
