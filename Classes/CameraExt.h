
#ifndef __CameraExt_h__
#define __CameraExt_h__

#include "cocos2d.h"
#include "CAnimationData.h"

USING_NS_CC;

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

const int KCameraAnchorSpeedX = 20;
const int KCameraAnchorSpeedY = 20;

class CameraExt : public cocos2d::Ref
{
public:
	CameraExt();
	~CameraExt();

	float getSpeedX() const { return _speedX; }
	void setSpeedX(float aSpeedX) { _speedX = aSpeedX; }

	Node* getTarget() const { return _target; }
	void setTarget(Node* aTarget);

	void setCameraPostion(float x, float y);
	void moveCameraCenter();

	bool init();
	
	static CameraExt* getInstance();

	void destroy();
	void update(float dt);
	Size getMapSize() const { return _mapSize; }
	void setMapSize(Size aMapSize) { _mapSize = aMapSize; }
	void revise(float &x, float &y);
	Point SetCameraPos(float left, float top);
	Point MoveCamera(float left, float top, float speedX, float speedY);
public:
	void LockCameraH(float left, float right);
	void LockCameraV(float top, float bottom);
	void LockCurrentCamera();
	void UnlockCameraWhenDead(Node* e);
	void LockCurrentCameraH();
	void LockCurrentCameraV();
	void UnlockCamera();
	void UnlockCameraH();
	void UnlockCameraV();
	void SetCameraReady();
	void ResetCameraAnchor();
	void StartCmdCamera(float left, float top, float speedX, float speedY);
	void StartCmdCamera(float speedX = 0, float speedY = 20);
	void CameraReturnPlayer(float speedx, float speedy);
	Point CameraAnchorPlayer(float speedx, float speedy);
	Rect getCameraVisibleSize(){ return Rect(_cameraLeft, _cameraTop, _cameraSize.width, _cameraSize.height); }
	float getCameraLeft() const { return _cameraLeft; }
	void setCameraLeft(float aCameraLeft) { _cameraLeft = aCameraLeft; }
	float getCameraTop() const { return _cameraTop; }
	void setCameraTop(float aCameraTop) { _cameraTop = aCameraTop; }
	float getCameraRight() const { return _cameraRight; }
	void setCameraRight(float aCameraRight) { _cameraRight = aCameraRight; }
	float getCameraBottom() const { return _cameraBottom; }
	void setCameraBottom(float aCameraBottom) { _cameraBottom = aCameraBottom; }
	Size getCameraSize() const { return _cameraSize; }
	void setCameraSize(Size aCameraSize) { _cameraSize = aCameraSize; }
	void stopCamera();
	void startCamera();
	void setPlayer(Node* player);
	Rect GetMapVisibleBound();
	void ResetCamera();
	Vec2 GetCameraCenter();
	Vec2 GetCameraOriginToGL();
	void setRecycle(bool recycle);

protected:
	float _speedX;
	float _speedY;

	float _cameraLeft;
	float _cameraTop;
	float _cameraRight;
	float _cameraBottom;
	float _cameraLockL;
	float _cameraLockT;
	float _cameraLockR;
	float _cameraLockB;

	float _KCameraAnchorX;
	float _KCameraAnchorY;

	bool _cameraHLocked;
	bool _cameraVLocked;

	float _cameraCmdLeft;
	float _cameraCmdTop;
	float _cameraCmdSpeedX;
	float _cameraCmdSpeedY;

	int _cameraState;
	bool _cameraUsed;
	bool _recycle;

	Size _mapSize;
	Size _cameraSize;
	Node* _target;
	Node* _Player;
	Scheduler* _schedulerHandler;
};

#endif // __CameraExt_h__
