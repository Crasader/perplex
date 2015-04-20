#ifndef __AgentSate_h__
#define __AgentSate_h__

class AgentState
{
public:
	virtual void seeEnemy() = 0;
	virtual void noEnemy() = 0;
	virtual void lowHealth() = 0;
};

class WanderState : public AgentState
{
public:
	virtual void seeEnemy();
	virtual void noEnemy();
	virtual void lowHealth();
};

class FSM : public AgentState
{
public:
	FSM();
	~FSM();
	virtual void seeEnemy();
	virtual void noEnemy();
	virtual void lowHealth();
private:
	WanderState* _wanderState;
};

class GlobalState_LP
{
public:
	virtual void active() = 0;
	virtual void onExit() = 0;
	virtual void update() = 0;
	virtual void onGameEvent() = 0;
	virtual void isClosed() = 0;
private:

};

class GlobalStateCombat_LP : public GlobalState_LP
{
public:
	GlobalStateCombat_LP();
	~GlobalStateCombat_LP();
	virtual void active();
	virtual void onExit();
	virtual void update();
	virtual void onGameEvent();
	virtual void isClosed();
private:

};

class ActionState_LP
{
public:
	virtual void active() = 0;
	virtual void onExit() = 0;
	virtual void upDate() = 0;
	virtual void onGameEvent() = 0;
private:

};

class GameEventHandler_LP
{
public:
	virtual onGameEvent(){}
private:

};

#endif // __AgentSate_h__
