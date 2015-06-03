#include "tool.h"
#include "GameScene.h"
#include "Unit.h"
#include "AnimationLoader.h"

#define APPEAR "appear"
#define GENERAL "genera"
#define DISAPPEAR "disappear"

bool Tool::init(GameScene* gamescene, const std::string& anim, int x, int y, int type)
{
	_anim = AnimationLoader::getInstance().createAnimation(anim);
	if (_anim == nullptr)
	{
		return false;
	}
	_action = _anim->getAnimation();
	addChild(_anim);
	appearAnim();
	return true;
}

void Tool::appearAnim()
{
	if (_action)
	{
		_action->play(APPEAR);
		setEvent();
	}
}

void Tool::activeAnim()
{
	if (_action)
	{
		_action->play(GENERAL);
	}
}

void Tool::disappearAnim()
{
	if (_action)
	{
		_action->play(DISAPPEAR);
		setEvent();
	}
}

void Tool::setEvent()
{
	auto event = [&](Armature *armature, MovementEventType movementType, const std::string& movementID)
	{
		if (movementType == MovementEventType::COMPLETE || movementType == MovementEventType::LOOP_COMPLETE)
		{
			if (movementID == APPEAR)
			{
				_state = 2;
				activeAnim();
			}
			else if (movementID == GENERAL)
			{

			}
			else if (movementID == DISAPPEAR)
			{
				removeFromParent();
			}
		}
	};
	_action->setMovementEventCallFunc(event);
}

Tool* Tool::create(GameScene* gamescene, const std::string& anim, int x, int y, int type)
{
	auto tool = new Tool(gamescene, anim, x, y, type);
	if (tool && tool->init(gamescene, anim, x, y, type))
	{
		tool->autorelease();
		return tool;
	}
	CC_SAFE_DELETE(tool);
	return nullptr;
}

void Tool::beTouch(Unit* unit)
{
	if (_state != 2 || unit == nullptr)
	{
		return;
	}

	auto rectTool = _anim->getBoundingBox();
	rectTool.origin += getPosition();
	if (unit->beAttack(rectTool, 0) != 0)
	{
		_state = 3;
		removeFromParent();
	}
}

void Tool::perform( float dt )
{
	if (_castoff)
	{
		_castoffStage++;
		return;
	}
	if (_state == 2)
	{
		_existTick++;
		if (_existTick > TOOL_DELAY_TICK)
		{
			_state = 3;
			disappearAnim();
		}
	}
}

Tool::Tool(GameScene* gamescene, const std::string& anim, int x, int y, int type)
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
