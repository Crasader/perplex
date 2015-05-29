#ifndef __common_h__
#define __common_h__
#include "cocos2d.h"
#include "basescene.h"

USING_NS_CC;

class Common : public Scene
{
public:
	Common();
	CREATE_FUNC(Common);
	void changeScene();

	void load();
	//激活UI场景
	void startSplash(int type);
	//激活游戏场景
	void startGame();

	//继续游戏
	void continueGame();

	//结束游戏
	void gameOver();

	void quitGame();

	void update(float dt) override;
	bool init() override;
private:
	bool _changeScene;//是否要替换Scene
	int _nextSceneType;//需要替换的Scene类型 0 --- splash  1 --- gamescene
	int _nextSceneParaA;//传递过来的参数	
	int _nextSceneParaB;//传递过来的参数
	SceneBase* _scene;
};

#endif // __common_h__
