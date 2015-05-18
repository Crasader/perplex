#include "WeaponRes.h"



WeaponRes::WeaponRes(int id)
{
	init(id);
}

void WeaponRes::init(int id)
{
	_ID = id;
	_flame = true;
	_lockUnit = false;
	_smokeDelay = 0;
	_shotFrame = 0;
	switch (id)
	{
	case 0://������ǹ
		_flameFollowUnit = true;//�����Ƿ����Weapon
		_flameAnimID = 21; //����Ķ�������
		_bulletAnimID = 16; //�ӵ��Ķ�������
		_fireAnimID = 0;
		_smokeAnimID = 0;  //β�̵Ķ�������
		_explordeAnimID = 12;//��ը�Ķ�������
		_bulletLogicType = 0;//�ӵ����߼�����//0 --- ������ǹ 1 --- ������ǹ  2 --- ɢ��  3 --- ����  4 --- ����  5 --- ������
		_power = 8; //�ӵ�������
		_fireDelay = 8;//�����ӵ����ӳ�
		_speed = 10;
		break;
	case 1://������ǹ
	case 3: //����        
		_flameFollowUnit = true;//�����Ƿ����Weapon
		_flameAnimID = 21; //����Ķ�������
		_bulletAnimID = 16; //�ӵ��Ķ�������
		_fireAnimID = 0;
		_smokeAnimID = 0;  //β�̵Ķ�������
		_explordeAnimID = 12;//��ը�Ķ�������
		_bulletLogicType = 1;//�ӵ����߼�����//0 --- ������ǹ 1 --- ������ǹ  2 --- ɢ��  3 --- ����  4 --- ����  5 --- ������
		_power = 8; //�ӵ�������
		_fireDelay = 8;//�����ӵ����ӳ�
		_speed = 10;
		break;
	case 2://ɢ��ǹ
		_flameFollowUnit = false;//�����Ƿ����Weapon
		_flameAnimID = 25; //����Ķ�������
		_bulletAnimID = 17; //�ӵ��Ķ�������
		_fireAnimID = 0;
		_smokeAnimID = 20;  //β�̵Ķ�������
		_explordeAnimID = 12;//��ը�Ķ�������
		_bulletLogicType = 2;//�ӵ����߼�����//0 --- ������ǹ 1 --- ������ǹ  2 --- ɢ��  3 --- ����  4 --- ����  5 --- ������
		_power = 14; //�ӵ�������
		_fireDelay = 8;//�����ӵ����ӳ�
		_speed = 10;
		break;
	case 4://Player�ĵ���
		_flameFollowUnit = false;//�����Ƿ����Weapon
		_flameAnimID = 25; //����Ķ�������
		_bulletAnimID = 43; //�ӵ��Ķ�������
		_fireAnimID = 43;
		_smokeAnimID = 42;  //β�̵Ķ�������
		_explordeAnimID = 12;//��ը�Ķ�������
		_bulletLogicType = 6;//�ӵ����߼�����//0 --- ������ǹ 1 --- ������ǹ  2 --- ɢ��  3 --- ����  4 --- ����  5 --- ������
		_power = 100; //�ӵ�������
		_fireDelay = 4;//�����ӵ����ӳ�
		_speed = 10;
		_smokeDelay = 2;
		break;
	case 7:
		_flame = false;
		_flameFollowUnit = false;//�����Ƿ����Weapon
		_flameAnimID = 25; //����Ķ�������
		_bulletAnimID = 43; //�ӵ��Ķ�������
		_fireAnimID = 43;
		_smokeAnimID = 42;  //β�̵Ķ�������
		_explordeAnimID = 12;//��ը�Ķ�������
		_bulletLogicType = 6;//�ӵ����߼�����//0 --- ������ǹ 1 --- ������ǹ  2 --- ɢ��  3 --- ����  4 --- ����  5 --- ������
		_power = 100; //�ӵ�������
		_fireDelay = 4;//�����ӵ����ӳ�
		_speed = 10;
		break;
	default:
		break;
	}
	//todo��������


	//todo���ù�������
}

int WeaponRes::getID() const
{
	return _ID;
}
