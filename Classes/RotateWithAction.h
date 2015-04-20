#ifndef __RotateWithAction_h__
#define __RotateWithAction_h__

#include "Head.h"

class RotateWithAction : public ActionInterval
{
public:
	RotateWithAction();
	~RotateWithAction();

	virtual bool isDone(void) const override;

	virtual void step(float dt) override;

	virtual void startWithTarget(Node *target) override;

	virtual ActionInterval* reverse() const override;

	virtual ActionInterval * clone() const override;

	bool initWithAction(ActionInterval* pAction, float delay = 0.0f);
	static RotateWithAction* create(ActionInterval* pAction, float delay = 0.0f);

protected:
	void setInnerAction(ActionInterval* pAction);
	Node* pInnerTarget;
	ActionInterval* pInnerAction;
	float _delay;
};
#endif // __RotateWithAction_h__
