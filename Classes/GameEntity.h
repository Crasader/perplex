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

class GameEntity : public Node
{
public:
	GameEntity();
	GameEntity(int id, int type, const Rect& moveRect = Rect::ZERO);
	virtual ~GameEntity();;
    CREATE_FUNC(GameEntity);
    Node *getModel();
    void setID(int id){_id = id;};
    int getID(){return _id;};
    void forward(const cocos2d::Vec2& dist, float angle);
	void setMoveSize(cocos2d::Rect rect) { _hitRect = rect; }
	const Rect getMoveRect();
	bool isCastoff() const { return _castoff; }
	void setCastoff() { _castoff = true; }
	int getCastoffStage() const { return _castoffStage; }
	int getType() const { return _type; }
	void setType(int val) { _type = val; }
	void setCastoffStage(int aCastoffStage) { _castoffStage = aCastoffStage; }
public:
	virtual void perform(float dt);
protected:
	bool _castoff;
	int _castoffStage;
	int _id;
	int _type;
	Node *_Model;
	Rect _hitRect;
};
#endif /* defined(__Moon3d__GameEntity__) */
