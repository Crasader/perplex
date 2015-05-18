#ifndef __effectdialog_h__
#define __effectdialog_h__

#include "Effect.h"
#include "gamestate.h"

class EffectDialog : public Effect
{
public:
	EffectDialog(EventManager* eventManager, GameScene* gameLayer, int faceImageID, int position, int secentenceID);

	virtual bool perform();
	virtual void quickPerform();;
private:
	bool _showDialog;
	int _faceImageID;
	int _position;
	int _scentenceID;
};


#endif // __effectdialog_h__
