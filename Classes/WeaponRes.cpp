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
	case 0://不抖机枪
		_flameFollowUnit = true;//火舌是否跟踪Weapon
		_flameAnimID = 21; //火舌的动画类型
		_bulletAnimID = 16; //子弹的动画类型
		_fireAnimID = 0;
		_smokeAnimID = 0;  //尾烟的动画类型
		_explordeAnimID = 12;//爆炸的动画类型
		_bulletLogicType = 0;//子弹的逻辑类型//0 --- 不抖机枪 1 --- 抖动机枪  2 --- 散弹  3 --- 导弹  4 --- 划刀  5 --- 轰天炮
		_power = 8; //子弹的威力
		_fireDelay = 8;//发射子弹的延迟
		_speed = 10;
		break;
	case 1://抖动机枪
	case 3: //火炮        
		_flameFollowUnit = true;//火舌是否跟踪Weapon
		_flameAnimID = 21; //火舌的动画类型
		_bulletAnimID = 16; //子弹的动画类型
		_fireAnimID = 0;
		_smokeAnimID = 0;  //尾烟的动画类型
		_explordeAnimID = 12;//爆炸的动画类型
		_bulletLogicType = 1;//子弹的逻辑类型//0 --- 不抖机枪 1 --- 抖动机枪  2 --- 散弹  3 --- 导弹  4 --- 划刀  5 --- 轰天炮
		_power = 8; //子弹的威力
		_fireDelay = 8;//发射子弹的延迟
		_speed = 10;
		break;
	case 2://散弹枪
		_flameFollowUnit = false;//火舌是否跟踪Weapon
		_flameAnimID = 25; //火舌的动画类型
		_bulletAnimID = 17; //子弹的动画类型
		_fireAnimID = 0;
		_smokeAnimID = 20;  //尾烟的动画类型
		_explordeAnimID = 12;//爆炸的动画类型
		_bulletLogicType = 2;//子弹的逻辑类型//0 --- 不抖机枪 1 --- 抖动机枪  2 --- 散弹  3 --- 导弹  4 --- 划刀  5 --- 轰天炮
		_power = 14; //子弹的威力
		_fireDelay = 8;//发射子弹的延迟
		_speed = 10;
		break;
	case 4://Player的导弹
		_flameFollowUnit = false;//火舌是否跟踪Weapon
		_flameAnimID = 25; //火舌的动画类型
		_bulletAnimID = 43; //子弹的动画类型
		_fireAnimID = 43;
		_smokeAnimID = 42;  //尾烟的动画类型
		_explordeAnimID = 12;//爆炸的动画类型
		_bulletLogicType = 6;//子弹的逻辑类型//0 --- 不抖机枪 1 --- 抖动机枪  2 --- 散弹  3 --- 导弹  4 --- 划刀  5 --- 轰天炮
		_power = 100; //子弹的威力
		_fireDelay = 4;//发射子弹的延迟
		_speed = 10;
		_smokeDelay = 2;
		break;
	case 7:
		_flame = false;
		_flameFollowUnit = false;//火舌是否跟踪Weapon
		_flameAnimID = 25; //火舌的动画类型
		_bulletAnimID = 43; //子弹的动画类型
		_fireAnimID = 43;
		_smokeAnimID = 42;  //尾烟的动画类型
		_explordeAnimID = 12;//爆炸的动画类型
		_bulletLogicType = 6;//子弹的逻辑类型//0 --- 不抖机枪 1 --- 抖动机枪  2 --- 散弹  3 --- 导弹  4 --- 划刀  5 --- 轰天炮
		_power = 100; //子弹的威力
		_fireDelay = 4;//发射子弹的延迟
		_speed = 10;
		break;
	default:
		break;
	}
	//todo设置坐标


	//todo设置攻击矩形
}

int WeaponRes::getID() const
{
	return _ID;
}
