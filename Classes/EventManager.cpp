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
		if (event->isCastoff())//�¼����
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
				case 0://������������
				{
					int switchid = 0;
					int set = 0;
					fs >> switchid >> set;
					event->createTriggerSwitch(switchid, set == 0 ? false : true);
					break;
				}
				case 1://��������������
				{
					int tcounterID, toperate, tcount = 0;
					fs >> tcounterID >> toperate >> tcount;
					event->createTriggerCounter(tcounterID, toperate, tcount);
					break;
				}
				case 2://�����ĳ����Χ��
				{
					int rmin, rmax, rpara = 0;
					fs >> rmin >> rmax >> rpara;
					event->createTriggerRandom(rmin, rmax, rpara);
					break;
				}
				case 3://unitλ��ĳ�����η�Χ��
				{
					int trunitID, trl, trt, trw, trh;
					fs >> trunitID >> trl >> trt >> trw >> trh;
					event->createTriggerUnitToRect(trunitID, cocos2d::Rect(trl, trt, trw, trh));
					break;
				}
				case 4://unit��hp��ĳ����Χ��
				{
					int paunitID, ppro, pmin, pmax;
					fs >> paunitID >> ppro >> pmin >> pmax;
					event->createTriggerUnitPoperty(paunitID, ppro, pmin, pmax);
					break;
				}
				case 5://����HP��ĳ����Χ��
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
				case 0://���ÿ�����
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
				case 2://�Ի���
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
				case 4://Unit����
				{
					int aUnitID, aX, aY;
					fs >> aUnitID >> aX >> aY;
					event->createEffectUnitAppear(aUnitID, aX, aY, true);
					break;
				}
				case 5://unit��ʧ
				{
					int aUnitID;
					fs >> aUnitID;
					event->createEffectUnitAppear(aUnitID, -255, -255, false);
					break;
				}
				case 6://Unit���չ̶�·���ƶ�
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
				case 7: //Unitʹ��ĳ����������
				{
					int aUnitID, aWeaponType, aTimes;
					fs >> aUnitID >> aWeaponType >> aTimes;
					event->createEffectUnitAttack(aUnitID, aWeaponType, aTimes);
					break;
				}
				case 8: //Unit��ը
				{
					int unitID;
					fs >> unitID;
					event->createEeffectUnitExplode(unitID);
					break;
				}
				case 9: //Unit�ı�AI
				{
					int aUnitID, aMoveType;
					fs >> aUnitID >> aMoveType;
					event->createEeffectUnitMoveType(aUnitID, aMoveType);
					break;
				}
				case 10: //Unit�ı�HP
				{
					int aUnitID, aProperty, aOperate, aCount;
					fs >> aUnitID >> aProperty >> aOperate >> aCount;
					event->createEeffectUnitProperty(aUnitID, aProperty, aOperate, aCount);
					break;
				}
				case 11: //Player�Ƿ���հ���
				{
					int set;
					fs >> set;
					event->createEffectPlayer(0, set != 0);
					break;
				}
				case 12: //�Ƿ�����Player�߳���Ļ
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
				case 15: //��ͷ����
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(1, aX, aY, aColor);
					break;
				}
				case 16: //�ƶ���ĳ��
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(2, aX, aY, aColor);
					break;
				}
				case 17: //�л���ĳ��
				{
					int aX, aY, aColor;
					fs >> aX >> aY >> aColor;
					event->createEffectCamerA(3, aX, aY, aColor);
					break;
				}
				case 18: //����ĳ��Unit(����/����)
				{
					int type, id, set;
					fs >> type >> id >> set;
					event->createEffectCameraB(type, id, set != 0);
					break;
				}
				case 19: //����(��/����ְ���)
				{
					int set;
					fs >> set;
					event->createEffectCameraB(1, 0, set != 0);
					break;
				}
				case 20: //����Camera��ָ���ľ������ƶ�
				{
					int l, t, w, h;
					fs >> l >> t >> w >> h;
					event->createEffectCameraC(cocos2d::Rect(l, t, w, h));
					break;
				}
				case 21: //��/�رվ���Ч��(��/��) -----. �򿪼�ʱ��
				{
					int temp;
					fs >> temp;
					event->createEffectCalculagragh();
					break;
				}
				case 22://����ϵͳ����
				{
					int temp;
					fs >> temp;
					break;
				}
				case 23://���Ŷ���
				{
					int aCGID;
					fs >> aCGID;
					event->createEffectCG(aCGID);
					break;
				}
				case 24: //�л���ͼ
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
