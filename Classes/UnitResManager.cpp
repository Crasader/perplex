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
			//0 ~ 5://8方向移动的动画range，只有5方向数据，顺序为 D, DL, L, UL, U
			//TInt start;//每个方向的起始帧
			//TInt end;//每个方向的结束帧
			for (int i = 0; i < 5; i++)
			{
				pUnitRes->_moveAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				int x, y;
				in >> x >> y;
				pUnitRes->_moveAnimRanges[i]->x = x;
				pUnitRes->_moveAnimRanges[i]->y = y;
			}
			//补充其它3个方向的数据
			for (int i = 5; i < 8; i++)
			{
				pUnitRes->_moveAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_moveAnimRanges[i] = pUnitRes->_moveAnimRanges[8 - i];
			}
			//        =========================================================================================
			//for 0 ~ 8://8方向移动速度，8方向数据都有，顺序为 D, DL, L, LU, U, UR, R, RD
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
				//for 0 ~ gridCount://每个占地格相对作战单位基准点的偏移
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
			//0 ~ 9://16方向站立的动画range，只有9方向数据，顺序为 D, DDL, DL, DLL, L, LLU, LU, LUU, U
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
			//补充其它方向的数据
			for (int i = 9; i < 16; i++)
			{
				pUnitRes->_standAnimRanges[i] = std::shared_ptr<Vec2>(new Vec2());
				pUnitRes->_standAnimRanges[i] = pUnitRes->_standAnimRanges[16 - i];
			}
			//for 0 ~16://16方向身体中心点的位置，16方向数据都有，顺序为D, DDL, DL, DLL, L, LLU, LU, LUU, U, UUR, UR, URR, R, RRD, RD, RDD
			//        		TInt centerX;//中心点相对基准点的偏移量
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
			    
			//        		for 0~ 16://16方向攻击范围，16方向数据都有，顺序同上
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
				//for 0 ~ rectCount://每个攻击矩形的数据
				//矩形相对作战单位基准点的偏移
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
			//for 0 ~ 9://16方向死亡的动画range，只有9方向数据，顺序同上
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
			//补充其它方向的动画
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
				//	int animID;//该爆炸的动画ID
				//  int x;//爆炸效果相对作战单位基准点的偏移量
				//  int y;
				//	int start;//这个爆炸动画的播放range
				//	int end;
				//  int beginIndex;//表示从机器人的死亡动画开始计算直到经过了beginIndex这么多帧之后开始播放这个爆炸效果
				//  for 0 ~ exploreCount://每个死亡爆炸的数据
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
				//for 0 ~ weaponPosCount://每个武器挂接点的数据
				pUnitRes->_weaponPos.resize(STAND_DIR_COUNT);
				for (int i = 0; i < 16; i++)
				{
					pUnitRes->_weaponPos[i].resize(weaponPosCount);
				}
				int id, weaponType, cover, x, y;
				for (int j = 0; j < weaponPosCount; j++)
				{
					in >> id >> weaponType >> cover >> x >> y;
					//              int x;//该挂接点在该方向上的位置相对作战单位基准点的偏移
					//              int y;
					//for 0 ~ 7://该挂接点在8方向上的具体位置，8方向数据都有，顺序同上
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
			//创建挂接点武器类型列表
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
			//每种武器类型的挂接点索引值表
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