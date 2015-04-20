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
	CC_ASSERT(_shadowLayer);
	CC_ASSERT(target);
	auto shadow = ShadowSprite::create(target);
	shadow->setOffset(offset);
	_shadows.pushBack(shadow);
	_shadowLayer->addChild(shadow, shadow->getType());
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


bool ShadowSprite::initWithTexture(Texture2D *texture, const Rect& rect, bool rotated)
{
	CC_ASSERT(_target);
	if (Sprite::initWithTexture(texture, rect, rotated))
	{
		_offset = Point(20, -20);
		return true;
	}
	return false;
}

ShadowSprite* ShadowSprite::createWithTexture(Texture2D *texture, const Rect& rect, bool rotated /*= false*/)
{
	auto ret = new ShadowSprite();
	if (ret && ret->initWithTexture(texture, rect, rotated))
	{
		ret->autorelease();
		return ret;
	}
	else
	{
		CC_SAFE_DELETE(ret);
		return nullptr;
	}
}

ShadowSprite* ShadowSprite::create(GameEntity* target)
{
	auto ret = new ShadowSprite();
	if (ret && ret->init(target))
	{
		ret->autorelease();
		return ret;
	}
	else
	{
		CC_SAFE_DELETE(ret);
		return nullptr;
	}
}

void ShadowSprite::updateShadow(float dt)
{
	this->setVisible(true);
	this->setPosition(_target->getPosition() + _offset);
	this->setRotation(_target->getRotation());
}

bool ShadowSprite::init(GameEntity* target)
{
	CC_ASSERT(target);
	CC_SAFE_RETAIN(target);
	_target = target;
	auto s = dynamic_cast<Sprite*>(_target->getModel());
	if (initWithTexture(s->getTexture(), s->getTextureRect(), s->isTextureRectRotated()))
	{
			this->setColor(Color3B::BLACK);
			this->setOpacity(160);
			this->setVisible(false);
			this->setPosition(target->getPosition() + _offset);
			if (_target->getShadowType() == kShadowLand)
			{
				_type = kZOrderLandShadow;
			}
			else if (_target->getShadowType() == kShadowWater)
			{
			}
			else if (_target->getShadowType() == kShadowSky)
			{
				_type = kZOrderSkyShadow;
				this->setScale(0.5f);
			}
		return true;
	}
	return false;
}

ShadowSprite::~ShadowSprite()
{
	CC_SAFE_RELEASE(_target);
}

int ShadowSprite::getType()
{
	return _type;
}

bool ShadowSprite::equal(const GameEntity* rl)
{
	return _target == rl;
}
