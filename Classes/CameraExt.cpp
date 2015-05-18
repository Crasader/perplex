#include "CameraExt.h"

static CameraExt* g_camera = nullptr;

CameraExt::CameraExt()
{

}

CameraExt::CameraExt(GameScene* gameScene, std::shared_ptr<MapManager> mapManager)
:_gameScene(gameScene)
, _mapManager(mapManager)
, _x(0)
, _y(0)
, _desX(0)
, _desY(0)
, _moveX(0)
, _moveY(0)
, _lastDesX(0)
, _lastDesY(0)
, _shakeTick(0)
, _followType(0)
, _lockUnit(false)
, _close(false)
, _operationOk(false)
, _moveRect(Rect::ZERO)
, _operationCount(0)
{
	_operations.resize(MAX_OPERATION_COUNT);
	for (int i = 0; i < MAX_OPERATION_COUNT; i++)
	{
		_operations[i]._type = 0;
		_operations[i]._x = 0;
		_operations[i]._y = 0;
		_operations[i]._other = 0;
	}
}

CameraExt::~CameraExt()
{
	
}

void CameraExt::setLockUnit(bool lock)
{
	_lockUnit = lock;
}

bool CameraExt::init()
{
	_speedX = 0;
	_speedY = 5;
	_target = nullptr;


	auto _visiableSize = Director::getInstance()->getVisibleSize();
	auto origin = Director::getInstance()->getVisibleOrigin();
	_cameraLeft = origin.x;
	_cameraTop = origin.y;
	_cameraRight = _cameraLeft + _visiableSize.width;
	_cameraBottom = _cameraTop + _visiableSize.height;

	_cameraSize = _visiableSize;
	setCameraPostion((_mapSize.width - _cameraSize.width) / 2, 0);
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

Rect CameraExt::getCameraVisibleSize()
{
	return Rect(_cameraLeft, _cameraTop, _cameraSize.width, _cameraSize.height);
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
	startCamera();
}

void CameraExt::StartCmdCamera(float speedX, float speedY)
{
	auto l = (_mapSize.width - _cameraSize.width) / 2;
	auto t = _mapSize.height;

	StartCmdCamera(l, t, speedX, speedY);
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
	else if (_cameraState == ECameraMovecenter)
	{
		moveCameraCenter();
		_cameraState = ECameraNormal;
		stopCamera();
	}

	/*setCameraPostion(_cameraLeft, _cameraTop);
	_cameraLeft += _speedX * dt;
	_cameraTop += _speedY  * dt;
	setCameraPostion(_cameraLeft, _cameraTop);*/
}

Size CameraExt::getMapSize() const
{
	return _mapSize;
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

void CameraExt::setMapSize(Size aMapSize)
{
	_mapSize = aMapSize;
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
	setCameraPostion((_mapSize.width - _cameraSize.width) / 2, 0);
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
	/*stopCamera();
	CC_SAFE_RELEASE(_schedulerHandler);*/
}

float CameraExt::getCameraLeft() const
{
	return _cameraLeft;
}

void CameraExt::setCameraLeft(float aCameraLeft)
{
	_cameraLeft = aCameraLeft;
}

float CameraExt::getCameraTop() const
{
	return _cameraTop;
}

void CameraExt::setCameraTop(float aCameraTop)
{
	_cameraTop = aCameraTop;
}

float CameraExt::getCameraRight() const
{
	return _cameraRight;
}

void CameraExt::setCameraRight(float aCameraRight)
{
	_cameraRight = aCameraRight;
}

float CameraExt::getCameraBottom() const
{
	return _cameraBottom;
}

void CameraExt::setCameraBottom(float aCameraBottom)
{
	_cameraBottom = aCameraBottom;
}

Size CameraExt::getCameraSize() const
{
	return _cameraSize;
}

void CameraExt::setCameraSize(Size aCameraSize)
{
	_cameraSize = aCameraSize;
}

void CameraExt::stopCamera()
{
// 	if (_cameraUsed)
// 	{
// 		_cameraUsed = false;
// 		_schedulerHandler->unschedule(schedule_selector(CameraExt::update), g_camera);
// 	}
}

void CameraExt::startCamera()
{
	/*if (!_cameraUsed)
	{
		_cameraUsed = true;
		_schedulerHandler->schedule(schedule_selector(CameraExt::update), g_camera, 0, false);
	}*/
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

void CameraExt::initCamera()
{
	switch (_operations[0]._type)
	{
	case ECAMERAORDER_NULL:
		_operationOk = true;
		break;
	case ECAMERAORDER_MOVE:
		_operationOk = false;
		_moveX = _operations[0]._x;
		_moveY = _operations[0]._y;
		break;
	case ECAMERAORDER_FOLLOW:
		startFollowNPC();
		break;
	case ECAMERAORDER_LOCK:
		startLockNPC();
		break;
	case ECAMERAORDER_MOVETOPOINT:
		startMoveToPoint(5, _operations[0]._x, _operations[0]._y, _operations[0]._type);
		break;
	case ECAMERAORDER_JUMPTOPOINT://切换到某点
		startMoveToPoint(6, _operations[0]._x, _operations[0]._y, _operations[0]._type);
		break;

	case ECAMERAORDER_OPEN:     //打开摄像机
		_close = false;
		startMoveToPoint(6, _operations[0]._x, _operations[0]._y, _operations[0]._type);
		break;
	default:
		break;
	}
}

bool CameraExt::isClose()
{
	return _close;
}

bool CameraExt::isOperationOk()
{
	return _operationOk;
}

int CameraExt::getShake()
{
	return _shakeTick;
}

void CameraExt::setShake(int tick)
{
	_shakeTick = tick;
}

float CameraExt::getX() const
{
	return _x;
}

void CameraExt::setX(float aX)
{
	_x = aX;
}

float CameraExt::getY() const
{
	return _y;
}

void CameraExt::setY(float aY)
{
	_y = aY;
}

float CameraExt::getDesX() const
{
	return _desX;
}

void CameraExt::setDesX(float aDesX)
{
	_desX = aDesX;
}

float CameraExt::getDesY() const
{
	return _desY;
}

void CameraExt::setDesY(float aDesY)
{
	_desY = aDesY;
}

float CameraExt::getMoveX() const
{
	return _moveX;
}

void CameraExt::setMoveX(float aMoveX)
{
	_moveX = aMoveX;
}

float CameraExt::getMoveY() const
{
	return _moveY;
}

void CameraExt::setMoveY(float aMoveY)
{
	_moveY = aMoveY;
}

int CameraExt::getType() const
{
	return _type;
}

void CameraExt::setType(int aType)
{
	_type = aType;
}

void CameraExt::close()
{
	_close = true;
}

void CameraExt::open()
{
	_close = false;
}

int CameraExt::getOrderCount()
{
	return _operationCount;
}

void CameraExt::doPeriodicTask()
{
	auto tempX = _x;
	auto tempY = _y;
	auto xOk = false;
	auto yOk = false;
	switch (_type)
	{
	case 0:
		_x += _moveX;
		_y += _moveY;
		break;
	case 1:
		if (_x <= _desX + FOLLOW_TYPE_X[_followType] &&
			_gameScene->getSceneWidth() >= _moveRect.getMaxX() ||
			(_x >= _desX + FOLLOW_TYPE_X[_followType] && _x <= _moveRect.getMinX()))
		{
			_moveX = 0;
			xOk = true;
		}
		else if (abs(_desX + FOLLOW_TYPE_X[_followType] - _x) > 8)
		{
			_moveX = (_desX + FOLLOW_TYPE_X[_followType] - _x) / 2;
			if (_moveX > 0)
			{
				_moveX++;
			}
			else if (_moveX < 0)
			{
				_moveX--;
			}
			else
			{
				xOk = true;
			}
		}
		else
		{
			_moveX = (_desX + FOLLOW_TYPE_X[_followType] - _x);
			xOk = true;
		}
		if (_y <= _desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight() &&
			_y + _gameScene->getSceneHeight() >= _moveRect.getMaxY() ||
			(_y >= _desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight() &&
			_y <= _moveRect.getMinY()))
		{
			_moveY = 0;
			yOk = true;
		}
		else if (abs(_desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight() - _y) > 8)
		{
			_moveY = (_desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight()) / 2;
			if (_moveY > 0)
			{
				_moveY++;
			}
			else if (_moveY < 0)
			{
				_moveY--;
			}
			else
			{
				yOk = true;
			}
		}
		else
		{
			_moveY = 0;
			yOk = true;
		}
		if (xOk && yOk)
		{
			_operationOk = true;
			_type = 2;//追踪Unit成功，将摄像机运动方式改变成锁定Unit
		}
		_x += _moveX;
		_y += _moveY;
		break;
	case 2://锁定Unit
		if (_followType == 1)
		{
			_x = _desX - CAMERA_OFFSETX;
			if (_y >= _desY - CAMERA_OFFSETY)
			{
				_y = _desY - CAMERA_OFFSETY;
			}
		}
		else
		{
			_x = _desX - CAMERA_OFFSETX;
			if (_y >= _desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight())
			{
				_y = _desY + FOLLOW_TYPE_Y[_followType] - _gameScene->getSceneHeight();
				_lastDesX = _desX;
				_lastDesY = _desY;
			}
		}
		break;
	case 5://移动到某点
		if (_operationOk)
		{
			break;
		}
		if (_x < _desX)
		{
			if (_x > _desX - 10)
			{
				_x = _desX;
			}
			else
			{
				_x += 10;
			}
		}
		else if (_x > _desX)
		{
			if (_x < _desX + 10)
			{
				_x = _desX;
			}
			else
			{
				_x -= 10;
			}
		}
		if (_y < _desY)
		{
			if (_y > _desY - 10)
			{
				_y = _desY;
			}
			else
			{
				_y += 10;
			}
		}
		else if (_y > _desY)
		{
			if (_y < _desY + 10)
			{
				_y = _desY;
			}
			else
			{
				_y -= 10;
			}
		}
		if (_x >= _desY && _y >= _desY)
		{
			_operationOk = true;
		}
		break;
	case  6://切换到某点
		_operationOk = true;
		_x = _desX;
		_y = _desY;
		_gameScene->cameraOperationOK(6);
		break;
	case 7:
		if (_shakeTick > 0)
		{
			_x += SHAKEX[_shakeTick];
			_y += SHAKEY[_shakeTick];
			if (_shakeTick == 0)
			{
				_operationOk = true;
			}
		}
		break;
	case 8://设置镜头矩形范围
		_operationOk = true;
		break;
	default:
		break;
	}
	//判断已经设置的镜头矩形范围
	setMoveRect();
	return;
}

void CameraExt::startMoveToPoint(int type, int x, int y, int speed)
{
	_type = type;
	_desX = x - _gameScene->getSceneWidth() / 2;
	_desY = y - _gameScene->getSceneHeight() / 2;
	_operationOk = false;
	if (_desX < _moveRect.getMinX())
	{
		_desX = _moveRect.getMinX();
	}
	else if (_desX > _moveRect.getMaxX() - _gameScene->getSceneWidth())
	{
		_desX = _moveRect.getMaxX() - _gameScene->getSceneWidth();
	}
	if (_desY < _moveRect.getMinY())
	{
		_desY = _moveRect.getMinY();
	}
	else if (_desY > _moveRect.getMaxY() - _gameScene->getSceneHeight())
	{
		_desY = _moveRect.getMaxY() - _gameScene->getSceneHeight();
	}
}

void CameraExt::startShake(int shakeTick)
{
	_type = 7;
	_shakeTick = shakeTick;
	_operationOk = false;
}

bool CameraExt::addOperationg(int type, int x, int y, int other)
{
	if (type <= 0)
	{
		return false;
	}
	if (_operationCount >= MAX_OPERATION_COUNT - 1)
	{
		return false;
	}
	_operations[_operationCount]._type = type;
	_operations[_operationCount]._x = x;
	_operations[_operationCount]._y = y;
	_operations[_operationCount]._other = other;
	if (_operationCount == 1)
	{
		initCamera();
	}
	return true;
}

void CameraExt::startFollowUnit(bool center)
{
	_type = 1;
	_followType = center ? 1 : 0;
	_operationOk = false;
}

void CameraExt::startFollowNPC()
{
	_operationOk = false;
	_desX = _operations[0]._x;
	_desY = _operations[0]._y;
}

void CameraExt::startLockNPC()
{
	_operationOk = false;
}

void CameraExt::setMoveRect()
{
	if (_moveRect.size.width < _gameScene->getSceneWidth() &&
		_moveRect.size.height >= _gameScene->getSceneHeight())
	{
		_x = (_moveRect.getMinX() + _moveRect.getMaxX() - _gameScene->getSceneWidth()) / 2;
		if (_x <= 0)
		{
			_x = 0;
		}
		else if (_x >= _gameScene->getMapWidth() - _gameScene->getSceneWidth())
		{
			_x = _gameScene->getMapWidth() - _gameScene->getSceneWidth();
		}
		if (_y < _moveRect.getMinY())
		{
			_y = _moveRect.getMinY();
		}
		else if (_y > _gameScene->getMapHeight() - _gameScene->getSceneHeight())//#地图高度
		{
			_y = _gameScene->getMapHeight() - _gameScene->getSceneHeight();
		}
		return;
	}
	else if (_moveRect.size.height < _gameScene->getSceneHeight() &&
		_moveRect.size.width >= _gameScene->getSceneWidth())
	{
		if (_x < _moveRect.getMinX())
		{
			_x = _moveRect.getMinX();
		}
		else if (_x > _gameScene->getMapWidth() - _gameScene->getSceneWidth())
		{
			_x = _gameScene->getMapWidth() - _gameScene->getSceneWidth();
		}
		if (_y < 0)
		{
			_y = 0;
		}
		else if (_y > _gameScene->getMapHeight() - _gameScene->getSceneHeight())
		{
			_y = _y > _gameScene->getMapHeight() - _gameScene->getSceneHeight();
		}
		return;
	}
	else if (_moveRect.size.width < _gameScene->getSceneWidth() &&
		_moveRect.size.height < _gameScene->getSceneHeight())
	{
		_x = (_moveRect.getMinX() + _moveRect.getMaxX() - _gameScene->getSceneWidth()) / 2;
		_y = (_moveRect.getMinY() + _moveRect.getMaxY() - _gameScene->getSceneHeight()) / 2;
		if (_x <= 0)
		{
			_x = 0;
		}
		else if (_x >= _gameScene->getMapWidth() - _gameScene->getSceneWidth())
		{
			_x = _gameScene->getMapWidth() - _gameScene->getSceneWidth();
		}
		if (_y < 0)
		{
			_y = 0;
		}
		else if (_y >= _gameScene->getSceneHeight() - _gameScene->getSceneWidth())
		{
			_y = _gameScene->getSceneHeight() - _gameScene->getSceneWidth();
		}
		return;
	}
	if (_x < _moveRect.getMinX())
	{
		_x = _moveRect.getMinX();
	}
	else if (_x > _moveRect.getMaxX() - _gameScene->getSceneWidth())
	{
		_x = _moveRect.getMaxX() - _gameScene->getSceneWidth();
	}
	if (_y < _moveRect.getMinY())
	{
		_y = _moveRect.getMinY();
	}
	else if (_y > _moveRect.getMaxY() - _gameScene->getSceneHeight())
	{
		_y = _moveRect.getMaxY() - _gameScene->getSceneHeight();
	}
}

void CameraExt::setMoveRect(cocos2d::Rect rect)
{
	_type = 8;
	_moveRect = rect;
	_operationOk = false;
}

cocos2d::Rect CameraExt::getMoveRect()
{
	return _moveRect;
}

float CameraExt::getSpeedX() const
{
	return _speedX;
}

void CameraExt::setSpeedX(float aSpeedX)
{
	_speedX = aSpeedX;
}

Node* CameraExt::getTarget() const
{
	return _target;
}

void CameraExt::setTarget(Node* aTarget)
{
	_target = aTarget;
	_cameraState = ECameraMovecenter;
	moveCameraCenter();
	startCamera();
}

