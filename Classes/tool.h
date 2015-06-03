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
	float _rayTick;//��˸���
	int _type;//0 --- ��ǹ  1 --- MG  2 --- ����  3 --- ��  4 --- Ѫ
	int _state;
	int _power;
	int _moveX;
	int _moveY;
	float _existTick;//����ʱ��
	GameScene* _gameScene;
	cocostudio::Armature* _anim;
	cocostudio::ArmatureAnimation* _action;

};
#endif // __tool_h__
