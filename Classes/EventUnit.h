#ifndef __EventUnit_h__
#define __EventUnit_h__

#include "cocos2d.h"

class GameScene;
class EventManager;
class Trigger;
class Effect;

class EventUnit
{
public:
	EventUnit(EventManager* manager, GameScene* scene, int id);

	int getID() const { return _ID; }
	void setCastoff(bool off) { _castoff = off; }
	bool isCastoff(){ return _castoff; }
	void perform();
	void createTrigger(int id);
	void createTriggerSwitch(int switchID, bool set);
	void createTriggerCounter(int counterID, int operate, int count);
	void createTriggerRandom(int min, int max, int para);
	void createTriggerUnitToRect(int unitID, cocos2d::Rect rect);
	void createTriggerUnitPoperty(int unitID, int property, int min, int max);
	void createTriggerBuildingHP(int buildingID, int minHP, int maxHP);
	void createEffectSwitch(int switchID, bool set);
	void createEffectCount(int counter, int operate, int count);
	void createEffectDelay(int second);
	void createEffectDialog(int faceID, int position, int sentenceID);
	void createEffectUnitAppear(int UnitID, int x, int y, bool show);
	void createEffectUnitRoad(int unitID, std::vector<cocos2d::Vec2> roads);
	void createEffectUnitAttack(int unitID, int weaponType, int times);
	void createEeffectUnitExplode(int unitID);
	void createEeffectUnitMoveType(int unitID, int moveType);
	void createEeffectUnitProperty(int unitID, int property, int operate, int count);
	void createEffectPlayer(int effectType, bool set);
	void createEffectdBuildingExplode(int buildingID);
	void createEffectCamerA(int effectType, int x, int y, int color);
	void createEffectCameraB(int effectType, int unitID, bool set);
	void createEffectCameraC(cocos2d::Rect rect);
	void createEffectCG(int cgID);
	void createEffectChangeMap(int level, int section);
	void createEffectGameState(int gameState);
	void createEffectCalculagragh();
private:
	GameScene* _gamelayer;
	int _ID;
	int _triggerCount;
	int _effectCount;
	int _activeEffect;
	bool _castoff;
	bool _triggerOK;
	std::vector<std::shared_ptr<Trigger>> _triggers;
	std::vector<std::shared_ptr<Effect>> _effects;
	EventManager* _eventManager;
};
#endif // __EventUnit_h__
