#include "RotateWithAction.h"




RotateWithAction::RotateWithAction()
{

}

RotateWithAction::~RotateWithAction()
{
	CC_SAFE_RELEASE(pInnerAction);
}

bool RotateWithAction::isDone(void) const
{
	return pInnerAction->isDone();
}

void RotateWithAction::step(float dt)
{
	auto prePos = pInnerTarget->getPosition();
	pInnerAction->step(dt);
	auto curPos = pInnerTarget->getPosition();
	if (_elapsed >= _delay)
	{
		_elapsed = 0.f;
		auto  radian = (curPos - prePos).getAngle();
		auto degree = CC_RADIANS_TO_DEGREES(-radian) + 270.0f;

		pInnerTarget->setRotation(degree);
	}
	else
	{
		_elapsed += dt;
	}
}

void RotateWithAction::startWithTarget(Node *target)
{
	pInnerTarget = target;
	Action::startWithTarget(target);
	pInnerAction->startWithTarget(target);
}

ActionInterval* RotateWithAction::reverse() const
{
	return nullptr;
}

ActionInterval * RotateWithAction::clone() const
{
	auto action = new RotateWithAction();
	if (action && action->initWithAction(pInnerAction))
	{
		action->autorelease();
		return action;
	}
	CC_SAFE_DELETE(action);
	return nullptr;
}

bool RotateWithAction::initWithAction(ActionInterval* pAction, float delay)
{
	pAction->retain();
	pInnerAction = pAction;
	_delay = delay;
	_elapsed = 0;
	return true;
}

RotateWithAction* RotateWithAction::create(ActionInterval* pAction, float delay)
{
	auto action = new RotateWithAction();
	if (action && action->initWithAction(pAction, delay))
	{
		action->autorelease();
		return action;
	}
	CC_SAFE_DELETE(action);
	return nullptr;
}

void RotateWithAction::setInnerAction(ActionInterval* pAction)
{
	if (pInnerAction != pAction)
	{
		CC_SAFE_RELEASE(pInnerAction);
		pInnerAction = pAction;
		CC_SAFE_RETAIN(pInnerAction);
	}
}
