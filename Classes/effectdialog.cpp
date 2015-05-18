#include "effectdialog.h"
#include "GameScene.h"



void EffectDialog::quickPerform()
{

}

bool EffectDialog::perform()
{
	if (!_showDialog)
	{
		_gameLayer->setGameState(EGS_Dialog);
		if (_faceImageID == 0)
		{

		}
		else if (_faceImageID > 0)
		{
			_faceImageID += 3;
		}
// 		_gameLayer->getDialogManager().createTalkDialog(_faceImageID, _position _scentenceID);
// 		_showDialog = true;
// 		_gameLayer->setEventLock(true);
		return false;
	}
	return true;
}

EffectDialog::EffectDialog( EventManager* eventManager, GameScene* gameLayer, int faceImageID, int position, int secentenceID ) 
:Effect(eventManager, gameLayer, 3)
, _showDialog(false)
, _faceImageID(faceImageID)
, _position(position)
, _scentenceID(secentenceID)
{

}
