#ifndef __Fodder_h__
#define __Fodder_h__

#include "Unit.h"

class Fodder : public Unit
{
public:
	Fodder(GameScene* gameScene, int unitID, int type, int dir, int campType); 
	static Fodder* create(GameScene* gameScene, int unitID, int type, int dir, int campType);
	bool init(GameScene* gameScene, int unitID, int type, int dir, int campType);
	Fodder();
	~Fodder();
protected:
};

#endif // __Fodder_h__
