
#ifndef __CameraExt_h__
#define __CameraExt_h__

#include "cocos2d.h"
#include "CAnimationData.h"
#include "GameScene.h"


USING_NS_CC;

struct TCameraOrder
{
	int _type;
	int _other;
	float _x;
	float _y;

};

enum CameraType
{
	ECameraNormal = 0,
	ECameraCmdMoving = 1,
	ECameraCmdMoveEnd = 2,
	ECameraReturnPlayer = 3,
	ECameraMovecenter = 4,
};

enum CameraMovingType
{
	ECameraMovingX = 0,
	ECameraMoveFailX = 1,
	ECameraMoveEndX = 2,
	ECameraMovingY = 3,
	ECameraMoveFailY = 4,
	ECameraMoveEndY = 5,
};

enum CameraOrder
{
	ECAMERAORDER_DEFAUL = 0,
	ECAMERAORDER_MOVE = 1,
	ECAMERAORDER_FOLLOW = 2,
	ECAMERAORDER_LOCK = 3,
	ECAMERAORDER_FADEIN = 4,
	ECAMERAORDER_FADEOUT = 5,
	ECAMERAORDER_MOVETOPOINT = 6,
	ECAMERAORDER_JUMPTOPOINT = 7,
	ECAMERAORDER_OPEN = 8,
};

static const int KCameraAnchorSpeedX = 20;
static const int KCameraAnchorSpeedY = 20;
static const int MAX_OPERATION_COUNT = 8;
static const float FOLLOW_TYPE_X[] = { -120.0f, -120.0f };
static const float FOLLOW_TYPE_Y[] = { 88.0f, 88.0f };
static const float CAMERA_OFFSETX = 120.0f;
static const float CAMERA_OFFSETY = 180.0f;
static const int SHAKEX[] = { -2, 0, 2, 0, -3, 0, 4, 0, -4, 0, 4, 0, -4, 0, 4, 0 };
static const int SHAKEY[] = { 2, 0, -2, 0, 3, 0, -4, 0, -4, 0, 4, 0, -4, 0, 4, 0 };

class GameScene;
class MapManager;

class CameraExt
{
public:
	CameraExt(GameScene* gameScene, std::shared_ptr<MapManager> mapManager);
	Rect getCameraVisibleSize();
	void setLockUnit(bool lock);
	bool isClose();;
	bool isOperationOk();
	int getShake();
	void setShake(int tick);
	float getX() const;
	void setX(float aX);
	float getY() const;
	void setY(float aY);
	float getDesX() const;
	void setDesX(float aDesX);
	float getDesY() const;
	void setDesY(float aDesY);
	float getMoveX() const;
	void setMoveX(float aMoveX);
	float getMoveY() const;
	void setMoveY(float aMoveY);
	int getType() const;
	void setType(int aType);
	int getOrderCount();
	void doPeriodicTask(float dt);
	void startMoveToPoint(int type, int x, int y, int speed);
	void startShake(int shakeTick);
	void startFollowUnit(bool center);
	void startFollowNPC();
	void startLockNPC();
	void setMoveRect(cocos2d::Rect rect);
	cocos2d::Rect getMoveRect();
	bool addOperationg(int type, int x, int y, int other);
	void setMoveRect();
	float getSpeedX() const;
	void setSpeedX(float aSpeedX);
	Node* getTarget() const;
	void setTarget(Node* aTarget);
	Size getMapSize() const;
	void setMapSize(Size aMapSize);
	void revise(float &x, float &y);
	Size getCameraSize() const;
	void setCameraSize(Size aCameraSize);
	void setPlayer(Node* player);
	Rect GetMapVisibleBound();
	Vec2 GetCameraCenter();
	Vec2 GetCameraOriginToGL();
	void initCamera();
	void close();
	void open();
protected:
	float _speedX;
	float _speedY;
	Size _mapSize;
	Size _cameraSize;
	Node* _target;
	Node* _Player;
	

	//////////////////////////////////////////////////////////////////////////
	int _type; //运动类型
	//0 --- 向上运动
	//1 --- 追踪Unit
	//2 --- 锁定Unit
	float _x;
	float _y;
	float _desX;
	float _desY;
	float _lastDesX;
	float _lastDesY;

	float _moveX;
	float _moveY;
	int _shakeTick;
	int _fadeTick;
	int _followType; //跟踪Unit的方式  0:局中  1:自由
	int _recycle;

	bool _lockUnit;
	bool _close;
	bool _operationOk;

	cocos2d::Rect _moveRect; //摄像机可移动区域
	int _operationCount;
	std::vector<TCameraOrder> _operations;
	GameScene* _gameScene;
	std::shared_ptr<MapManager> _mapManager;

};

#endif // __CameraExt_h__
