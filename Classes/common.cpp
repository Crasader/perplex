#include "common.h"
#include "GameScene.h"

Common::Common()
	:_changeScene(true)
	, _nextSceneType(1)
	, _nextSceneParaA(0)
	, _nextSceneParaB(0)
	, _scene(nullptr)
{

}

void Common::changeScene()
{
	_changeScene = false;
	if (_nextSceneType == 1)//�л�GameScene
	{
		if (_nextSceneParaA == 1)//������Ϸ
		{
			load();
			if (_scene != nullptr)
			{

			}
			_scene->removeFromParent();
			_scene = nullptr;
			_scene = GameScene::create();
			this->addChild(_scene);
		}
		else//����Ϸ
		{
			if (_scene == nullptr)
			{
				_scene->doEnd();
			}
			_scene->removeFromParent();
			_scene = nullptr;
			_scene = GameScene::create();
			this->addChild(_scene);
			_scene->doStart();
		}
	}
	else//�л���Splash
	{
		if (_nextSceneParaA == 1)//��Ϸ����
		{
			// 				if (_scene != nullptr)
			// 				{
			// 					_scene->doEnd();
			// 				}
			// 				_scene = nullptr;
			// 				auto splash = Splash::create(1);

		}
		else if (_nextSceneParaA == 2)//�˳���Ϸ
		{
		}
		else
		{
			if (_nextSceneParaB != 2)
			{
				/*dataReset();*/
			}
		}
	}
}

void Common::load()
{

}

void Common::startSplash(int type)
{
	_changeScene = true;
	_nextSceneParaA = 0;
	_nextSceneType = 0;
	_nextSceneParaB = type;
}

void Common::startGame()
{
	_changeScene = true;
	_nextSceneType = 1;
	_nextSceneParaA = 0;
}

void Common::continueGame()
{
	_changeScene = true;
	_nextSceneType = 1;
	_nextSceneParaA = 1;
}

void Common::gameOver()
{
	_changeScene = true;
	_nextSceneParaA = 1;
	_nextSceneParaB = 0;
	_nextSceneType = 0;
}

void Common::quitGame()
{
	_changeScene = true;
	_nextSceneType = 0;
	_nextSceneParaA = 2;
	_nextSceneParaB = 0;
}

void Common::update(float dt)
{
	Scene::update(dt);
	if (_scene == nullptr)
	{
		return;
	}
	_scene->doPeriodicTask(dt);
	if (_changeScene)
	{
		changeScene();
	}
}

bool Common::init()
{
	
	changeScene();
	scheduleUpdate();
	return true;
}
