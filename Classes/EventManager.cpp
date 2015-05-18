#include "EventManager.h"
#include "GameScene.h"
#include "EventUnit.h"
#include "MapManager.h"

void EventManager::doPeriodicTask()
{
	auto activeMapID = _gameLayer->getMapManager()->getActiveMapID();
	for (auto it = _eventUnits.begin(); it != _eventUnits.end();)
	{

		auto event = (*it);
		if (event->isCastoff())//事件结果
		{
			it = _eventUnits.erase(it);
		}
		else
		{
			it++;
			event->perform();
		}
	}
}

std::shared_ptr<EventUnit> EventManager::getEventUnitFromID(int id)
{
	for (auto event : _eventUnits)
	{
		if (event->getID() == id)
		{
			return event;
		}
	}
	return nullptr;
}

std::shared_ptr<EventUnit> EventManager::createEventUnit(int ID)
{
	auto eventUnit = std::make_shared<EventUnit>(EventUnit(this, _gameLayer, ID));
	_eventUnits.push_back(eventUnit);
	_eventCount++;
	return eventUnit;
}

GameScene* EventManager::getGameScene()
{
	return _gameLayer;
}

void EventManager::setCounter(int counterID, int count)
{
	_counters[counterID] = count;
}

int EventManager::getCounter(int counterID)
{
	return _counters[counterID];
}

bool EventManager::setSwitch(int switchID, bool set)
{
	return _switchs[switchID] = set;
}

bool EventManager::isPlayerReceiveKey()
{
	return _playerReceiveky;
}

void EventManager::setCamerBusy(bool yes)
{
	_camerBusy = yes;
}

bool EventManager::isCameraBusy()
{
	return _camerBusy;
}

std::vector<std::shared_ptr<EventUnit>> EventManager::getEventUnits()
{
	return _eventUnits;
}

void EventManager::loadEvent(std::string eventRes)
{
	Ifstream fs(eventRes.c_str());
	if (!fs.fail())
	{
		int eventCount = 0;
		fs >> eventCount;
		for (int i = 0; i < eventCount; i++)
		{
			auto event = createEventUnit(i);
			int triggerCount = 0;
			fs >> triggerCount;
			for (int j = 0; j < triggerCount; j++)
			{
				int typeTriger = -1;
				fs >> typeTriger;
				switch (typeTriger)
				{
				case 0://开关满足条件
				{
					int switchid = 0;
					int set = 0;
					fs >> switchid >> set;
					event->createTriggerSwitch(switchid, set == 0 ? false : true);
					break;
				}
				case 1://计数器满足条件
				{
					int tcounterID, toperate, tcount = 0;
					fs >> tcounterID >> toperate >> tcount;
					event->createTriggerCounter(tcounterID, toperate, tcount);
					break;
				}
				case 2://随机数某个范围内
				{
					int rmin, rmax, rpara = 0;
					fs >> rmin >> rmax >> rpara;
					event->createTriggerRandom(rmin, rmax, rpara);
					break;
				}
				case 3://unit位于某个矩形范围内
				{
					int trunitID, trl, trt, trw, trh;
					fs >> trunitID >> trl >> trt >> trw >> trh;
					event->createTriggerUnitToRect(trunitID, cocos2d::Rect(trl, trt, trw, trh));
					break;
				}
				case 4://unit的hp在某个范围内
				{
					int paunitID, ppro, pmin, pmax;
					fs >> paunitID >> ppro >> pmin >> pmax;
					event->createTriggerUnitPoperty(paunitID, ppro, pmin, pmax);
					break;
				}
				case 5://建筑HP在某个范围内
				{
					int aBuildingID = 0;
					int aMinHP = 0;
					int aMaxHP = 0;
					fs >> aBuildingID >> aMinHP >> aMaxHP;
					event->createTriggerBuildingHP(aBuildingID, aMinHP, aMaxHP);
					break;
				}
				default:
					break;
				}
			}
			std::vector <cocos2d::Vec2> roads;
			int unitID, count, operationCount;
			fs >> operationCount;
			for (int i = 0; i < operationCount; i++)
			{
				int typeEffect;
				fs >> typeEffect;
				switch (typeEffect)
				{
				case 0://设置开关量
				{
					int switchindex, set;
					fs >> switchindex >> set;
					event->createEffectSwitch(switchindex, set == 0 ? false : true);
					break;
				}
				case 1:
				{
					int counterID;
					int operate;
					int count;
					fs >> counterID >> operate >> count;
					event->createEffectCount(counterID, operate, count);
					break;
				}
				case 2://对话框
				{
					int aFaceImageID, aPosition, aSentenceID;
					fs >> aFaceImageID >> aPosition >> aSentenceID;
					event->createEffectDialog(aFaceImageID, aPosition, aSentenceID);
					break;
				}
				case 3:
				{
					int second;
					fs >> second;
					event->createEffectDelay(second);
					break;
				}
				case 4://Unit出现
				{
					int aUnitID, aX, aY;
					fs >> aUnitID >> aX >> aY;
					event->createEffectUnitAppear(aUnitID, aX, aY, true);
					break;
				}
				case 5://unit消失
				{
					int aUnitID;
					fs >> aUnitID;
					event->createEffectUnitAppear(aUnitID, -255, -255, false);
					break;
				}
				case 6://Unit按照固定路线移动
				{

					fs >> unitID >> count;
					for (auto i = 0; i < count; i++)
					{
						int x, y;
						fs >> x >> y;
						roads.push_back(cocos2d::Vec2(x, y));
					}
					event->createEffectUnitRoad(unitID, roads);
					break;
				}
				case 7: //Unit使用某种武器攻击
				{
					int aUnitID, aWeaponType, aTimes;
					fs >> aUnitID >> aWeaponType >> aTimes;
					event->createEffectUnitAttack(aUnitID, aWeaponType, aTimes);
					break;
				}
				case 8: //Unit爆炸
				{
					int unitID;
					fs >> unitID;
					event->createEeffectUnitExplode(unitID);
					break;
				}
				case 9: //Unit改变AI
				{
					int aUnitID, aMoveType;
					fs >> aUnitID >> aMoveType;
					event->createEeffectUnitMoveType(aUnitID, aMoveType);
					break;
				}
				case 10: //Unit改变HP
				{
					int aUnitID, aProperty, aOperate, aCount;
					fs >> aUnitID >> aProperty >> aOperate >> aCount;
					event->createEeffectUnitProperty(aUnitID, aProperty, aOperate, aCount);
					break;
				}
				case 11: //Player是否接收按键
				{
					int set;
					fs >> set;
					event->createEffectPlayer(0, set != 0);
					break;
				}
				case 12: //是否限制Player走出屏幕
				{
					int outCamera;
					fs >> outCamera;
					event->createEffectPlayer(1, outCamera != 0);
					break;
				}
				case 13:
				{
					int aBuildingID;
					fs >> aBuildingID;
					event->createEffectdBuildingExplode(aBuildingID);
					break;
				}
				case 14://
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(0, aX, aY, aColor);
					break;
				}
				case 15: //镜头淡出
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(1, aX, aY, aColor);
					break;
				}
				case 16: //移动到某点
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(2, aX, aY, aColor);
					break;
				}
				case 17: //切换到某点
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(3, aX, aY, aColor);
					break;
				}
				case 18: //跟踪某个Unit(自由/居中)
				{
					int type, id, set;
					fs >> type >> id >> set;
					event->createEffectCameraB(type, id, set != 0);
					break;
				}
				case 19: //震屏(是/否出现白屏)
				{
					int set;
					fs >> set;
					event->createEffectCameraB(1, 0, set != 0);
					break;
				}
				case 20: //设置Camera在指定的矩形内移动
				{
					int l, t, w, h;
					fs >> l >> t >> w >> h;
					event->createEffectCameraC(cocos2d::Rect(l, t, w, h));
					break;
				}
				case 21: //打开/关闭剧情效果(开/关) -----. 打开计时器
				{
					int temp;
					fs >> temp;
					event->createEffectCalculagragh();
					break;
				}
				case 22://播放系统音乐
				{
					int temp;
					fs >> temp;
					break;
				}
				case 23://播放动画
				{
					int aCGID;
					fs >> aCGID;
					event->createEffectCG(aCGID);
					break;
				}
				case 24: //切换地图
				{
					int level, section;
					fs >> level >> section;
					event->createEffectChangeMap(level, section);
					break;
				}
				case 25:
				{
					int gameSate;
					fs >> gameSate;
					event->createEffectGameState(gameSate);
					break;
				}
				default:
					break;
				}
			}

		}
	}
	fs.close();
}

EventManager::EventManager(GameScene* gamescen, std::string eventRes) :_gameLayer(gamescen)
{
	_camerBusy = false;
	_playerReceiveky = false;
	_switchs = std::vector<bool>(MAXSWITCH, false);
	_counters = std::vector<int>(MAXCOUTER, 0);
	_eventCount = 0;
	loadEvent(eventRes);
}
