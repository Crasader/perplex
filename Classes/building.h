#ifndef __building_h__
#define __building_h__

#include "GameEntity.h"

class GameScene;
class BuildingRes;
class XUnitFactory;
class XDropTool;

class Building : public GameEntity
{
public:
	Building(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	static Building* create(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	bool  init(GameScene* gameScene, std::shared_ptr<BuildingRes> buildingRes, int buildingID, int state, bool fliph);
	
	void perform();
	void processUnitFactory();
	void processDie();
	void processTool();
	int beAttack(const cocos2d::Rect &rect, int hp);
	int getBuildingHP() { return _HP; }
	int getBuildingID();
private:
	//����
	bool _factory;
	bool _flipH;
	bool _enableHarm;//�Ƿ��ܹ����˺�
	bool _active;
	byte _unitFactoryTick;
	byte _dieTick;
	int _buildingID;
	int _unitFactoryIndex;
	int _middleX;
	int _middleY;
	int _HP;//������Ѫ����������԰�ٵĻ���ô��ٵı�׼����hp/2
	int _state;//������״̬ 0 --- ��� 1 --- ��ٻ�
	std::shared_ptr<BuildingRes> _buildingRes;
	GameScene* _gameScene;
	std::vector<std::shared_ptr<XUnitFactory>> _unitFactoryDatas;
	std::vector<std::shared_ptr<XDropTool>> _dropToolDatas;
	std::vector<bool> _dieExplode;
	cocos2d::Rect _walkRect;
	cocos2d::Node _model;
};
#endif // __building_h__