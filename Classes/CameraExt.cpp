#include "CameraExt.h"

static CameraExt* g_camera = nullptr;

CameraExt::CameraExt()
{

}

CameraExt::~CameraExt()
{
	destroy();
}

bool CameraExt::init()
{
	_speedX = 0;
	_speedY = 5;
	_target = nullptr;


	auto _visiableSize = Director::getInstance()->getVisibleSize();
	_cameraLeft = 0;
	_cameraTop = 0;
	_cameraRight = _cameraLeft + _visiableSize.width;
	_cameraBottom = _cameraTop + _visiableSize.height;

	_cameraSize = _visiableSize;
	setCameraPostion(0, 0);
	SetCameraReady();
	_schedulerHandler = Director::getInstance()->getScheduler();
	_schedulerHandler->retain();
	startCamera();
	return true;
}

void CameraExt::SetCameraReady()
{
	ResetCameraAnchor();
	_cameraState = ECameraNormal;
	_cameraHLocked = false;
	_cameraVLocked = false;
	_cameraUsed = false;
	/*CameraAnchorPlayer(10000, 10000);*/
	//StartCmdCamera(_mapSize.width, _mapSize.height, KCameraAnchorSpeedX, KCameraAnchorSpeedY);
}

void CameraExt::ResetCameraAnchor()
{
	_KCameraAnchorX = _cameraSize.width / 2;
	_KCameraAnchorY = _cameraSize.height / 2;
}

void CameraExt::CameraReturnPlayer(float speedx, float speedy)
{
	_speedX = speedx;
	_speedY = speedy;
	_cameraState = ECameraReturnPlayer;
}

Point CameraExt::CameraAnchorPlayer(float speedx, float speedy)
{
	return MoveCamera(_mapSize.width, _mapSize.height, speedx, speedy);
}

Point CameraExt::SetCameraPos(float left, float top)
{
	auto l = 0;
	auto r = _mapSize.width;
	auto t = 0;
	auto b = _mapSize.height;

	if (_cameraHLocked)
	{
		l = _cameraLockL;
		r = _cameraLockR;
	}

	if (_cameraVLocked)
	{
		t = _cameraLockT;
		b = _cameraLockB;
	}

	_cameraLeft = left;
	_cameraTop = top;

	auto rx = ECameraMoveEndX;
	auto ry = ECameraMoveEndY;

	if (_cameraLeft < l)
	{
		_cameraLeft = l;
		rx = ECameraMoveFailX;
	}

	if (_cameraLeft + _cameraSize.width > r)
	{
		_cameraLeft = r - _cameraSize.width;
		rx = ECameraMoveFailX;
	}

	if (_cameraTop < t)
	{
		_cameraTop = t;
		ry = ECameraMoveFailY;
	}

	if (_cameraTop + _cameraSize.height > b)
	{
		_cameraTop = b - _cameraSize.height;
		ry = ECameraMoveFailY;
	}

	_cameraRight = _cameraLeft + _cameraSize.width;
	_cameraBottom = _cameraTop + _cameraSize.height;
	return Point(rx, ry);
}

Point CameraExt::MoveCamera(float left, float top, float speedX, float speedY)
{
	auto cl = _cameraLeft;
	auto ct = _cameraTop;

	if (cl < left)
	{
		cl = cl + speedX;
		if (cl > left)
		{
			cl = left;
		}
	}
	else if (cl > left)
	{
		cl = cl - speedX;
		if (cl < left)
		{
			cl = left;
		}
	}

	if (ct < top)
	{
		ct += speedY;
		if (ct > top)
		{
			ct = top;
		}
	}
	else if (ct > top)
	{
		ct -= speedY;
		if (ct > top)
		{
			ct = top;
		}
	}

	auto r = SetCameraPos(cl, ct);

	if (_cameraLeft == left)
	{
		r.x = ECameraMoveEndX;
	}
	else if (r.x == ECameraMoveEndX)
	{
		r.x = ECameraMovingX;
	}

	if (_cameraTop == top)
	{
		r.y = ECameraMoveEndY;
	}
	else if (r.y == ECameraMoveEndY)
	{
		r.y = ECameraMovingY;
	}
	return r;
}

void CameraExt::LockCameraH(float left, float right)
{
	_cameraHLocked = true;
	_cameraLockL = MAX(0, left);
	_cameraLockR = MAX(_mapSize.width, right);

	if (_cameraLeft < _cameraLockL)
	{
		_cameraLeft = _cameraLockL;
	}

	if (_cameraLeft + _cameraSize.width > _cameraLockR)
	{
		_cameraLeft = _cameraLockR - _cameraSize.width;
	}

	_cameraRight = _cameraLeft + _cameraSize.width;
}

void CameraExt::LockCameraV(float top, float bottom)
{
	_cameraVLocked = true;
	_cameraLockT = MAX(0, top);
	_cameraLockB = MIN(_mapSize.height, bottom);

	if (_cameraTop < _cameraLockT)
	{
		_cameraTop = _cameraLockT;
	}

	if (_cameraTop + _cameraSize.height > _cameraLockB)
	{
		_cameraTop = _cameraLockB - _cameraSize.height;
	}

	_cameraBottom = _cameraTop + _cameraRight;
}

void CameraExt::LockCurrentCamera()
{
	LockCurrentCameraH();
	LockCurrentCameraV();
}

void CameraExt::LockCurrentCameraH()
{
	LockCameraH(_cameraLeft, _cameraLeft + _cameraSize.width);
}

void CameraExt::LockCurrentCameraV()
{
	LockCameraV(_cameraTop, _cameraTop + _cameraSize.height);
}

void CameraExt::UnlockCameraWhenDead(Node* e)
{
	_target = e;
}

void CameraExt::UnlockCamera()
{
	UnlockCameraH();
	UnlockCameraV();
}

void CameraExt::UnlockCameraH()
{
	_cameraHLocked = false;
}

void CameraExt::UnlockCameraV()
{
	_cameraVLocked = false;
}

void CameraExt::StartCmdCamera(float left, float top, float speedX, float speedY)
{
	_cameraCmdLeft = left;
	_cameraCmdTop = top;
	_speedX = speedX;
	_speedY = speedY;
	_cameraState = ECameraCmdMoving;
}

void CameraExt::update(float dt)
{
	/*if (_target)
	{
	if (cameraHLocked)
	{
	UnlockCamera();
	_target = nullptr;
	}
	}*/

	/*	if (cameraState == ECameraNormal)
		{
		CameraAnchorPlayer(KCameraAnchorSpeedX*dt, KCameraAnchorSpeedY*dt);
		}
		else if (cameraState = ECameraReturnPlayer)
		{
		auto r = CameraAnchorPlayer(_speedX*dt, _speedY*dt);
		if (r.x != ECameraMovingX && r.y != ECameraMovingY)
		{
		cameraState = ECameraNormal;
		}
		}
		else*/ if (_cameraState == ECameraCmdMoving)
	{
		auto r = MoveCamera(_cameraCmdLeft, _cameraCmdTop, _speedX*dt, _speedY*dt);
		if (_target)
		{
			CC_ASSERT(_target != nullptr);
			CC_ASSERT(_Player);
			_target->setPosition(-_cameraLeft, -_cameraTop);
			_Player->setPositionY(_Player->getPositionY() + _speedY*dt);
		}
		if (r.x != ECameraMovingX && r.y != ECameraMovingY)
		{
			_cameraState = ECameraCmdMoveEnd;
		}
	}
	else if (_cameraState == ECameraCmdMoveEnd)
	{
	}

	/*setCameraPostion(_cameraLeft, _cameraTop);
	_cameraLeft += _speedX * dt;
	_cameraTop += _speedY  * dt;
	setCameraPostion(_cameraLeft, _cameraTop);*/
}

void CameraExt::setCameraPostion(float x, float y)
{
	revise(x, y);
	_cameraLeft = x;
	_cameraTop = y;

	_cameraRight = _cameraLeft + _cameraSize.width;
	_cameraBottom = _cameraTop + _cameraSize.height;

	if (_target)
	{
		_target->setPosition(-_cameraLeft, -_cameraTop);
	}
}

void CameraExt::revise(float &x, float &y)
{
	if (x <= 0)
	{
		x = 0;
	}
	else if (x + _cameraSize.width >= _mapSize.width)
	{
		x = _mapSize.width - _cameraSize.width;
	}

	if (y <= 0)
	{
		y = 0;
	}
	else if (y + _cameraSize.height >= _mapSize.height)
	{
		y = _mapSize.height - _cameraSize.height;
	}
}

void CameraExt::moveCameraCenter()
{
	setCameraPostion(_cameraSize.width / 2, 0);
}

CameraExt* CameraExt::getInstance()
{
	if (g_camera == nullptr)
	{
		g_camera = new CameraExt();
		g_camera->init();
	}
	return g_camera;
}

void CameraExt::destroy()
{
	stopCamera();
	CC_SAFE_RELEASE(_schedulerHandler);
	CC_SAFE_RELEASE(g_camera);
}

void CameraExt::stopCamera()
{
	if (_cameraUsed)
	{
		_cameraUsed = false;
		_schedulerHandler->unschedule(schedule_selector(CameraExt::update), g_camera);
	}
}

void CameraExt::startCamera()
{
	if (!_cameraUsed)
	{
		_cameraUsed = true;
		_schedulerHandler->schedule(schedule_selector(CameraExt::update), g_camera, 0, false);
	}
}

void CameraExt::setPlayer(Node* player)
{
	_Player = player;
}

Rect CameraExt::GetMapVisibleBound()
{
	return Rect(_cameraLeft, _cameraBottom - _cameraSize.height, _cameraSize.width, _cameraSize.height);
}

void CameraExt::ResetCamera()
{
	init();
}

Vec2 CameraExt::GetCameraCenter()
{
	return Vec2(_cameraSize / 2) + Vec2(GetMapVisibleBound().origin);
}

Vec2 CameraExt::GetCameraOriginToGL()
{
	return Vec2(_cameraLeft, _cameraBottom - _cameraSize.height);
}