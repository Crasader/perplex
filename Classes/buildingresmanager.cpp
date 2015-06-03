#include "buildingresmanager.h"
#include "cocos2d.h"


USING_NS_CC;

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
			for (int n = 0; n < count; n++)
			{
				auto pBuildingRes = std::shared_ptr<BuildingRes>(new BuildingRes());
				int type, ID, x, y, rectCount;
				int left, top, width, height;
				int hp, exploreCount;
				int animaID, aAdd, ax, ay, start, end, beginIndex;
				int interval;
				unsigned char shake;
				fin >> type >> ID >> shake >> x >> y >> rectCount;
				pBuildingRes->_type = type;
				pBuildingRes->_id = ID;
				pBuildingRes->_shake = shake != 0 ? true : false;
				pBuildingRes->_originX = x;
				pBuildingRes->_originY = y;
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
						pBuildingRes->_buildingRects[i] = cocos2d::Rect(left *  CC_CONTENT_SCALE_FACTOR(), top *  CC_CONTENT_SCALE_FACTOR(), width *  CC_CONTENT_SCALE_FACTOR(), height *  CC_CONTENT_SCALE_FACTOR());
					}
				}
				fin >> pBuildingRes->_stateImageID[0];
				fin >> pBuildingRes->_stateImageID[1];
				fin >> pBuildingRes->_stateImageID[2];
				fin >> hp;
				pBuildingRes->_Hp = hp;
				_buildingResList[n] = pBuildingRes;
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
