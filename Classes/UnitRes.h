#ifndef __UnitRes_h__
#define __UnitRes_h__

#include "Head.h"
#include "XDropTool.h"
#include "GameGlobe.h"

class WeaponPos
{
public:
	int id;
	short type; //��������
	short cover;//�ڵ���ϵ��0��ʾ�ڵ�UNIT��1��ʾ��UNIT�ڵ�
	int x;//�ùҽӵ��ڸ÷����ϵ�λ�������ս��λ��׼���ƫ��
	int y;
};

class UnitRes
{
public:
	static const int WALK_DIR_COUNT = 8;
	static const int STAND_DIR_COUNT = 16;
public:
	UnitRes();
public:
	int			_ID; //ÿһ��Unit��Ӧһ��UnitRes, iID��UnitRes��Ψһ��ʶ
	int			_HP;
	int			_animID;
	int			_moveGridCount;//ռ�ظ����
	int			_weaponPosCount;//�ҽӵ���Ŀ������������ͬ
	int			_weaponTypeCount;//�ҽӵ��������͵���Ŀ
	int			_weaponEveryTypeCount;//ÿ���������͵Ĺҽӵ���Ŀ
	std::vector<int>			_bodyRectCounts; // ÿ�����򹥻����εĸ���
	std::vector<int>			_explodeCount;//������ըЧ���ĸ���
	std::vector<int>			_speedX;//8�����ƶ��ٶȣ�8�������ݶ��У�˳��Ϊ D, DL, L, LU, U, UR, R, RD
	std::vector<int>			_speedY;//8�����ƶ��ٶȣ�8�������ݶ��У�˳��Ϊ D, DL, L, LU, U, UR, R, RD
	std::vector<std::shared_ptr<Vec2>>		_moveGrids;//ÿ��������ͬ
	std::vector<std::vector<std::shared_ptr<Rect>>>		_bodyRect;//16���򹥻���Χ��16�������ݶ��У�˳��ͬ��
	std::vector<std::vector<std::shared_ptr<Explode>>>	_explode;//ÿ��������ը������
	std::vector<std::vector<std::shared_ptr<WeaponPos>>> _weaponPos;//ÿ������Ĺҽӵ�����
	std::vector<int>		_weaponTypeList;//�ҽӵ����������б�
	std::vector<std::vector<int>>		_weaponTypeIndexList; //ÿ���������͵Ĺҽӵ������ֵ��
	std::vector<int>		_weaponEveryTypeCountList;//ÿ���������͵Ĺҽӵ���Ŀ
	std::vector<std::shared_ptr<Vec2>>		_moveAnimRanges;//8�����ƶ��ٶȣ�8�������ݶ��У�˳��Ϊ D, DL, L, LU, U, UR, R, RD
	std::vector<std::shared_ptr<Vec2>>		_dieAnimRanges;//16���������Ķ���range��ֻ��9�������ݣ�˳��ͬ��
	std::vector<std::shared_ptr<Vec2>>		_standAnimRanges;//16����վ���Ķ���range��ֻ��9�������ݣ�˳��Ϊ D, DDL, DL, DLL, L, LLU, LU, LUU, U
	std::vector<std::shared_ptr<Vec2>>		_centerPoints;//16�����������ĵ��λ�ã�16�������ݶ��У�˳��ΪD, DDL, DL, DLL, L, LLU, LU, LUU, U, UUR, UR, URR, R, RRD, RD, RDD
};

#endif // __UnitRes_h__
