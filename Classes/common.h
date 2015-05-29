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
	//����UI����
	void startSplash(int type);
	//������Ϸ����
	void startGame();

	//������Ϸ
	void continueGame();

	//������Ϸ
	void gameOver();

	void quitGame();

	void update(float dt) override;
	bool init() override;
private:
	bool _changeScene;//�Ƿ�Ҫ�滻Scene
	int _nextSceneType;//��Ҫ�滻��Scene���� 0 --- splash  1 --- gamescene
	int _nextSceneParaA;//���ݹ����Ĳ���	
	int _nextSceneParaB;//���ݹ����Ĳ���
	SceneBase* _scene;
};

#endif // __common_h__
