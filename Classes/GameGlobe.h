#ifndef __GameGlobe_h__
#define __GameGlobe_h__

 static const int SCREEN_W = 240;
 static const int SCREEN_H = 320;

 static const int MAIN_COLOR = 0x00a0ff;

 static const int CONFIRMX = 10;
 static const int CONFIRMY = SCREEN_H - 20;
 static const int RETURNX = SCREEN_W;
 static const int RETURNY = SCREEN_H - 20;

 static const int TICKS_PER_SECOND = 64;
 static const int PERIODIC_INTERVAL = 15625;
//方向
 static const int STAND_DIR_COUNT = 16;
 static const int WALK_DIR_COUNT = 8;

//运动方式
 static const int STAND_MOVE = 0;
 static const int RANDOM_MOVE = 1;
 static const int PATH_MOVE = 2;
 static const int ORDER_MOVE = 3;

//最大武器数
//	 static const int UNIT_MAX_WEAPON_COUNT = 8;

//最大关数
 static const int	MAX_STAGE_NUM = 5;

 static const int D_W_DOWN = 0;
 static const int D_W_LEFT_DOWN = 1;
 static const int D_W_LEFT = 2;
 static const int D_W_LEFT_UP = 3;
 static const int D_W_UP = 4;
 static const int D_W_RIGHT_UP = 5;
 static const int D_W_RIGHT = 6;
 static const int D_W_RIGHT_DOWN = 7;

#endif // __GameGlobe_h__
