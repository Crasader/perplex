#include "CameraExt.h"

CameraExt::CameraExt(GameScene* gameScene, std::shared_ptr<MapManager> mapManager)
:_speedX(0)
, _speedY(10)
,_gameScene(gameScene)
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
, _operationOk(true)
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


Rect CameraExt::getCameraVisibleSize()
{
	return Rect();
}

Size CameraExt::getMapSize() const
{
	return _moveRect.size;
}

void CameraExt::setMapSize(Size aMapSize)
{
	_mapSize = aMapSize;
}

Size CameraExt::getCameraSize() const
{
	return _moveRect.size;
}

void CameraExt::setCameraSize(Size aCameraSize)
{
	_cameraSize = aCameraSize;
}

void CameraExt::setPlayer(Node* player)
{
	_Player = player;
}

Rect CameraExt::GetMapVisibleBound()
{
	return Rect(0, 0 - _cameraSize.height, _cameraSize.width, _cameraSize.height);
}

Vec2 CameraExt::GetCameraCenter()
{
	return Vec2(_moveRect.size / 2) + Vec2(GetMapVisibleBound().origin);
}

Vec2 CameraExt::GetCameraOriginToGL()
{
	return _moveRect.origin;
}

void CameraExt::initCamera()
{
	switch (_operations[0]._type)
	{
	case ECAMERAORDER_DEFAUL:
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
		startMoveToPoint(5, _operations[0]._x, _operations[0]._y, _operations[0]._other);
		break;
	case ECAMERAORDER_JUMPTOPOINT://切换到某点
		startMoveToPoint(6, _operations[0]._x, _operations[0]._y, _operations[0]._other);
		break;

	case ECAMERAORDER_OPEN:     //打开摄像机
		_close = false;
		startMoveToPoint(0, (_gameScene->getSceneWidth() - _gameScene->getMapWidth()) / 2, _gameScene->getMapHeight(), _operations[0]._other);
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

void CameraExt::doPeriodicTask( float dt )
{
	auto tempX = _x;
	auto tempY = _y;
	auto xOk = false;
	auto yOk = false;
	_type = 0;
	switch (_type)
	{
	case 0:
		_x += _speedX * dt;
		_y += _speedY * dt;
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
	_speedX = _speedY = speed;
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
		if (_x <= 0.0f)
		{
			_x = 0.0f;
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
		if (_y < 0.0f)
		{
			_y = 0.0f;
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
		if (_x <= 0.0f)
		{
			_x = 0.0f;
		}
		else if (_x >= _gameScene->getMapWidth() - _gameScene->getSceneWidth())
		{
			_x = _gameScene->getMapWidth() - _gameScene->getSceneWidth();
		}
		if (_y < 0.0f)
		{
			_y = 0.0f;
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
