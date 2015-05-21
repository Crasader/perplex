#include "EventUnit.h"
#include "EventManager.h"
#include "Effect.h"
#include "Trigger.h"
#include "TriggerSwitch.h"
#include "TriggerCounter.h"
#include "TriggerUnitToRect.h"
#include "TriggerUnitProperty.h"
#include "TriggerBuildingHP.h"
#include "EffectSwitch.h"
#include "effectcounter.h"
#include "effectdelay.h"
#include "effectdialog.h"
#include "EffectUnitAppear.h"
#include "EffectUnitRoad.h"
#include "effectunitattack.h"
#include "EffectUnitExplode.h"
#include "effectunitmovetype.h"
#include "effectunitproperty.h"
#include "effectplayer.h"
#include "effectbuildingexplode.h"
#include "effectcameraa.h"
#include "effectchagnemap.h"
#include "gameeffectstate.h"
#include "effectcalculagraph.h"
#include "GameScene.h"



EventUnit::EventUnit(EventManager* manager, GameScene* scene, int id)
:_eventManager(manager)
, _gamelayer(scene)
, _ID(id)
, _castoff(false)
, _triggerOK(false)
, _activeEffect(0)
, _effectCount(0)
, _triggerCount(0)
{

}

void EventUnit::perform()
{
	if (_triggerOK)
	{
		for (auto effect : _effects)
		{
			if (effect->getType() == 3)//Ö´ÐÐ¶Ô»°
			{
				
			}
			if (!effect->perform())
			{
				break;
			}
			_activeEffect++;
		}
		if (_activeEffect >= (int)_effects.size())
		{
			_castoff = true;
			return;
		}
	}
	else
	{
		for (auto trigger : _triggers)
		{
			if (!trigger->perform())
			{
				return;
			}
		}
		_triggerOK = true;
	}
}

void EventUnit::createTrigger(int id)
{
	auto trigger = std::shared_ptr<Trigger>(new Trigger(_eventManager, _gamelayer, id));
	_triggers.push_back(trigger);
}

void EventUnit::createTriggerSwitch(int switchID, bool set)
{
	auto trigger = std::shared_ptr<TriggerSwitch>(new TriggerSwitch(_eventManager, _gamelayer, switchID, set));
	_triggers.push_back(trigger);
}

void EventUnit::createTriggerCounter(int counterID, int operate, int count)
{
	auto trigger = std::shared_ptr<TriggerCounter>(new TriggerCounter(_eventManager, _gamelayer, counterID, operate, count));
	_triggers.push_back(trigger);
}

void EventUnit::createTriggerRandom(int min, int max, int para)
{
	
}

void EventUnit::createTriggerUnitToRect(int unitID, cocos2d::Rect rect)
{
	auto trigger = std::shared_ptr<TriggerUnitToRect>(new TriggerUnitToRect(_eventManager, _gamelayer, unitID, rect));
	_triggers.push_back(trigger);
	
}

void EventUnit::createTriggerUnitPoperty(int unitID, int property, int min, int max)
{
	auto trigger = std::shared_ptr<TriggerUnitProperty>(new TriggerUnitProperty(_eventManager, _gamelayer, unitID, property, min, max));
	_triggers.push_back(trigger);
}

void EventUnit::createTriggerBuildingHP(int buildingID, int minHP, int maxHP)
{
	auto trigger = std::shared_ptr<TriggerBuildingHP>(new TriggerBuildingHP(_eventManager, _gamelayer, buildingID, minHP, maxHP));
	_triggers.push_back(trigger);
}
//////////////////////////////////////////////////////////////////////////
///Create Effect-----------------------------------------------
//////////////////////////////////////////////////////////////////////////
void EventUnit::createEffectSwitch(int switchID, bool set)
{
	auto effect = std::shared_ptr<EffectSwitch>(new EffectSwitch(_eventManager, _gamelayer, switchID, set));
	_effects.push_back(effect);
}

void EventUnit::createEffectCount(int counter, int operate, int count)
{
	auto effect = std::shared_ptr<EffectCounter>(new EffectCounter(_eventManager, _gamelayer, counter, operate, count));
	_effects.push_back(effect);
}

void EventUnit::createEffectDelay(int second)
{
	auto effect = std::shared_ptr<EffectDelay>(new EffectDelay(_eventManager, _gamelayer, second));
	_effects.push_back(effect);
}

void EventUnit::createEffectDialog(int faceID, int position, int sentenceID)
{
	auto effect = std::shared_ptr<EffectDialog>(new EffectDialog(_eventManager, _gamelayer, faceID, position, sentenceID));
	_effects.push_back(effect);
}

void EventUnit::createEffectUnitAppear(int UnitID, int x, int y, bool show)
{
	auto effect = std::shared_ptr<EffectUnitAppear>(new EffectUnitAppear(_eventManager, _gamelayer, UnitID, x, y, false));
	_effects.push_back(effect);
}

void EventUnit::createEffectUnitRoad(int unitID, std::vector<cocos2d::Vec2> roads)
{
	auto effect = std::shared_ptr<EffectUnitRoad>(new EffectUnitRoad(_eventManager, _gamelayer, unitID, roads));
	_effects.push_back(effect);
}

void EventUnit::createEffectUnitAttack(int unitID, int weaponType, int times)
{
	auto effect = std::shared_ptr<EffectUnitAttack>(new EffectUnitAttack(_eventManager, _gamelayer, unitID, weaponType, times));
	_effects.push_back(effect);
}

void EventUnit::createEeffectUnitExplode(int unitID)
{
	auto effect = std::shared_ptr<EffectUnitExplode>(new EffectUnitExplode(_eventManager, _gamelayer, unitID));
	_effects.push_back(effect);
}

void EventUnit::createEeffectUnitMoveType(int unitID, int moveType)
{
	auto effect = std::shared_ptr<EffectUnitMoveType>(new EffectUnitMoveType(_eventManager, _gamelayer, unitID, moveType));
	_effects.push_back(effect);
}

void EventUnit::createEeffectUnitProperty(int unitID, int property, int operate, int count)
{
	auto effect = std::shared_ptr<EffectUnitProperty>(new EffectUnitProperty(_eventManager, _gamelayer, unitID, property, operate, count));
	_effects.push_back(effect);
}

void EventUnit::createEffectPlayer(int effectType, bool set)
{
	auto effect = std::shared_ptr<EffectPlayer>(new EffectPlayer(_eventManager, _gamelayer, effectType, set));
	_effects.push_back(effect);
}

void EventUnit::createEffectdBuildingExplode(int buildingID)
{
	auto effect = std::shared_ptr<EffectBuildingExplode>(new EffectBuildingExplode(_eventManager, _gamelayer, buildingID));
	_effects.push_back(effect);
}

void EventUnit::createEffectCamerA(int effectType, int x, int y, int color)
{
	auto effect = std::shared_ptr<EffectCameraA>(new EffectCameraA(_eventManager, _gamelayer, effectType));
	_effects.push_back(effect);
}

void EventUnit::createEffectCameraB(int effectType, int unitID, bool set)
{

}

void EventUnit::createEffectCameraC(cocos2d::Rect rect)
{

}

void EventUnit::createEffectCG(int cgID)
{

}

void EventUnit::createEffectChangeMap(int level, int section)
{
	auto effect = std::shared_ptr<EffectChangeMap>(new EffectChangeMap(_eventManager, _gamelayer, level, section));
	_effects.push_back(effect);
}

void EventUnit::createEffectGameState(int gameState)
{
	auto effect = std::shared_ptr<EffectGameState>(new EffectGameState(_eventManager, _gamelayer, gameState));
	_effects.push_back(effect);
}

void EventUnit::createEffectCalculagragh()
{
	auto effect = std::shared_ptr<EffectCalculaGraph>(new EffectCalculaGraph(_eventManager, _gamelayer));
	_effects.push_back(effect);
}
