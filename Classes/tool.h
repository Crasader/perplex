#ifndef __tool_h__
#define __tool_h__

#include "GameEntity.h"


class GameScene;
class Unit;

class Tool : public GameEntity
{
public:
	static const int TOOL_DELAY_TICK = 60 * 10;
	static const int TOOL_RAY_TICK = 60 * 1;
public:
	Tool(GameScene* gamescene, const std::string& anim, int x, int y, int type);
	void perform(float dt);
	void beTouch(Unit* unit);
	static Tool* create(GameScene* gamescene, const std::string& anim, int x, int y, int type);
	bool init(GameScene* gamescene, const std::string& anim, int x, int y, int type);

private:
	void appearAnim();
	void activeAnim();
	void disappearAnim();
	void setEvent();
protected:
	float _rayTick;//ÉÁË¸¼ä¸ô
	int _type;//0 --- ÅçÇ¹  1 --- MG  2 --- µ¼µ¯  3 --- À×  4 --- Ñª
	int _state;
	int _power;
	int _moveX;
	int _moveY;
	float _existTick;//Éú´æÊ±¼ä
	GameScene* _gameScene;
	cocostudio::Armature* _anim;
	cocostudio::ArmatureAnimation* _action;

};
#endif // __tool_h__
