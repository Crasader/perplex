#ifndef __gamestate_h__
#define __gamestate_h__

enum GameState
{
	EGS_GamePlaying = 0,
	EGS_GamePaused = 1,
	EGS_GameOver = 2,
	EGS_SelectMap = 3,
	EGS_SmallMap = 4, // help or about
	EGS_LoadMap = 5,
	EGS_Cover = 6,
	EGS_Dialog = 7,
	EGS_CG = 8,
	EGS_NoData = 9,
	EGS_Init = 10,
	EGS_Stat = 11, //过关界面
	EGS_Charge = 12, //关卡付费
	EGS_RelifeCharge = 13, //复活付费
	EGS_END = 14,
	EGS_WAIT = 15,
	EGS_SHOP = 16,
	EGS_VICTORY = 17,
	EGS_WeaponCharge = 18, //复活付费
};

#endif // __gamestate_h__
