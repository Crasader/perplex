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

#ifndef __Moon3d__GameEntity__
#define __Moon3d__GameEntity__

#include "cocos2d.h"
#include "cocostudio/CocoStudio.h"

USING_NS_CC;

using namespace cocostudio;

class MotionImpl;

class GameEntity : public Node
{
public:
	GameEntity();
	GameEntity(int type, int shadowType, int radius, const Vec2& oriention = Vec2::ZERO, const Rect& moveRect = Rect::ZERO);
	virtual ~GameEntity();;
    CREATE_FUNC(GameEntity);
    Node *getModel();
    void remove();
    Vec2 getOrientation();
    void setType(int type){_type = type;};
    int getType(){return _type;};
    float getRadius(){return _radius;};
    void forward(float dist);
    void forward(float dist, float angle);
	void setMoveSize(const Size& size) { _moveRect.size = size; }
	const Rect getMoveRect();
	int getShadowType() const { return _shadowType; }
	void setShadowType(int aShadowType) { _shadowType = aShadowType; }
	bool isCastoff() const { return _castoff; }
	void setCastoff() { _castoff = true; }
	int getCastoffStage() const { return _castoffStage; }
	void setCastoffStage(int aCastoffStage) { _castoffStage = aCastoffStage; }
public:
    static void UseOutlineEffect(Sprite3D* sprite, float width, Color3B color);
	virtual void perform(float dt);
protected:
	bool _castoff;
	int _castoffStage;
	int _type;
	int _shadowType;
	float _radius;
    Node *_Model;
	std::shared_ptr<MotionImpl> _curState;
    Vec2 _orientation;
	Rect _moveRect;
};


#endif /* defined(__Moon3d__GameEntity__) */
