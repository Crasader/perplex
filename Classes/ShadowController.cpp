#include "ShadowController.h"
#include "GameEntity.h"
#include "GameLayer.h"
#include "consts.h"

Vector<ShadowSprite*> ShadowController::_shadows;
bool  ShadowController::_init = false;
Node*  ShadowController::_shadowLayer = nullptr;

void ShadowController::init(Node* layer)
{
	if (layer)
	{
		reset();
		_init = true;
		_shadowLayer = layer;
	}
}

void ShadowController::createShadow(GameEntity* target, Point offset)
{
// 	CC_ASSERT(_shadowLayer);
// 	CC_ASSERT(target);
// 	auto shadow = ShadowSprite::create(target);
// 	shadow->setOffset(offset);
// 	_shadows.pushBack(shadow);
// 	_shadowLayer->addChild(shadow, shadow->getType());
}

void ShadowController::erase(GameEntity* target)
{
	CC_ASSERT(target);
	for (size_t i = _shadows.size() - 1; i > 0; --i)
	{
		auto object = _shadows.at(i);
		if (object->equal(target))
		{
			object->removeFromParent();
			_shadows.eraseObject(object);
		}
	}
}

void ShadowController::update(float dt)
{
	if (!_init || !_shadowLayer || GameLayer::isDie)
	{
		return;
	}
	for(int i = _shadows.size() - 1; i >= 0; --i)
	{
		_shadows.at(i)->updateShadow(dt);
	
	}
}

void ShadowController::reset()
{
	_init = false;
	_shadowLayer = nullptr;
	_shadows.clear();
}

void ShadowController::clean()
{
	for (size_t i = _shadows.size() - 1; i > 0; --i)
	{
		auto object = _shadows.at(i);
		object->removeFromParent();
		_shadows.eraseObject(object);
	}
}

void ShadowSprite::updateShadow(float dt)
{
	if (_target == nullptr)
	{
		return;
	}
	this->setVisible(true);
	this->setPosition(_target->getPosition() + _offset);
	this->setRotation(_target->getRotation());
}

int ShadowSprite::getType()
{
	return _type;
}

bool ShadowSprite::equal(const Node* rl)
{
	return _target == rl;
}

void ShadowSprite::setShadowData(cocostudio::Armature* s, Node* target)
{
	CC_ASSERT(s != nullptr && target != nullptr);
	_model = s;
	s->setColor(Color3B::BLACK);
	s->setOpacity(127);
	s->setScale(0.75);
	_target = target;
	addChild(_model);
	updateShadow(0);
}
