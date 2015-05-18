#include "tool.h"
#include "GameScene.h"
#include "Unit.h"


bool Tool::init()
{
	return true;
}

Tool* Tool::create(GameScene* gamescene, int appearAnimID, int generalAnimID, int disappearID, int x, int y, int type)
{
	auto tool = new Tool(gamescene, appearAnimID, generalAnimID, disappearID, x, y, type);
	if (tool && tool->init())
	{
		tool->autorelease();
		return tool;
	}
	CC_SAFE_DELETE(tool);
	return nullptr;
}

void Tool::beTouch(Unit* unit)
{
	if (_state != 2)
	{
		return;
	}
}

void Tool::perform()
{
	if (_castoff)
	{
		_castoffStage++;
		return;
	}
	if (_state == 0)
	{
		_state = 1;
		//
	}
	else
	{

	}
}

Tool::~Tool()
{

}

Tool::Tool(GameScene* gamescene, int appearAnimID, int generalAnimID, int disappearID, int x, int y, int type) 
: _type(type)
, _state(0)
, _power(100)
, _moveX(0)
, _moveY(0)
, _existTick(-1)
, _rayTick(0)
{
	_gameScene = gamescene;
	setPosition(x, y);
}
