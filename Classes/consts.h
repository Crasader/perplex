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

enum entityTypes
{
    kEnemyFodder,
    kEnemyFodderL,
    kEnemyBigDude,
    kEnemyBoss,

    kPlayerBullet,
    kPlayerMissiles,
    kEnemyBullet,
    
    kPlayer,
    kEnemy,
    
	kEnemyTank,
	kEnemyTurret,

};

enum zOrder
{
	kZOrderMap = 1,
	kZOrderLandShadow = 20,
	kZOrderLand = 21,
	kZOrderSkyShadow = 30,
	kZOrderSky = 31,
	kZOrderBullet = 22,
	kZorderExplosion = 40,
};

enum shadowType
{
	kShadow = -1,
	kShadowWater,
	kShadowLand,
	kShadowSky,
};