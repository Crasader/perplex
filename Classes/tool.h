#ifndef __tool_h__
#define __tool_h__

#include "GameEntity.h"

class GameScene;
class Unit;

class Tool : public GameEntity
{
public:
	static const int TOOL_DELAY_TICK = 150;
	static const int TOOL_RAY_TICK = 100;
public:
	Tool(GameScene* gamescene, int appearAnimID, int generalAnimID, int disappearID, int x, int y, int type);
	virtual ~Tool();
	void perform();

	void beTouch(Unit* unit);
	static Tool* create(GameScene* gamescene, int appearAnimID, int generalAnimID, int disappearID, int x, int y, int type);

	bool init();

protected:
	short _rayTick;//��˸���
	int _type;
	int _state;
	int _power;
	int _moveX;
	int _moveY;
	int _existTick;//����ʱ��
	GameScene* _gameScene;
};
#endif // __tool_h__
