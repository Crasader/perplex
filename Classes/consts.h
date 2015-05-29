/****************************************************************************
 Copyright (c) 2014 Chukong Technologies Inc.

 http://github.com/chukong/EarthWarrior3D

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#ifndef Moon3d_consts_h
#define Moon3d_consts_h

#define PLAYER_LIMIT_LEFT 0
#define PLAYER_LIMIT_RIGHT 480
#define PLAYER_LIMIT_TOP 1113
#define PLAYER_LIMIT_BOT 0

const static Rect BOUND_RECT = Rect(0, PLAYER_LIMIT_BOT, PLAYER_LIMIT_RIGHT, PLAYER_LIMIT_TOP - PLAYER_LIMIT_BOT + 180);
const static Rect ENEMY_BOUND_RECT = Rect(0, PLAYER_LIMIT_BOT, PLAYER_LIMIT_RIGHT, 800);
#endif

static const int MAX_STAGE_COUNT = 5;
static const int MAX_MAP_SECTION = 2;
static const int MAPID_EVENT[MAX_STAGE_COUNT][MAX_MAP_SECTION] = {
	{ 0,1 },
	{ 2,3 },
	{ 4,5 },
	{ 6,7 },
	{ 8,9 },
};

static const int UNIT_DIE_SCORE[] = { 
	80,5000,400,200,
	3000,4000,6000,300,
	1200,500,200,200,
	200,1000,10,100,
	0,200,150,150,
	0,0,0,0 
};

enum entityTypes
{
    kPlayer,

    kEnemyFodder,
    kEnemyFodderL,
    kEnemyBigDude,
    kEnemyBoss,
	kEnemyTank,
	kEnemyTurret,

    kPlayerBullet,
    kPlayerMissiles,
    kEnemyBullet,
    
    kEnemy,
};

enum zOrder
{
	kZOrderMap,
	kZorderBuilding,
	kZOrderLandShadow,
	kZOrderLand,
	kZOrderSkyShadow,
	kZOrderSky,
	kZOrderBullet,
	kZorderExplosion,
	kZorderUI,
};

enum shadowType
{
	kShadow = -1,
	kShadowWater,
	kShadowLand,
	kShadowSky,
};

enum CampType
{
	Enemy,
	Ally,
	Neutral
};
