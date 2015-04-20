#include "LoadMap.h"



LoadMapRes::LoadMapRes()
{

}

LoadMapRes::~LoadMapRes()
{
	_enemyInfos.clear();
	CC_SAFE_RELEASE(_map);
	cleamImage();
}

void LoadMapRes::loadMapLayer(const char* aName, int aType)
{
	CCLOG("loadMapLayer....");
	char fn[32];
	sprintf(fn, "map/%s_map.dat", aName);
	Ifstream is(fn);
	
	memset(&_mapInfo, 0, sizeof(_mapInfo));
	if (!is.fail())
	{
		CCLOG("loadMapLayer.... read map");
		int realLeft, realWidth, realTop, realHeight;
		unsigned char r, g, b;
		is >> realLeft;
		is >> realWidth;
		is >> realTop;
		is >> realHeight;
		is >>  r >> g >> b;
		CCLOG("loadMapLayer.... read map:>>> l:%d,w:%d,t:%d,h:%d", realLeft, realWidth, realTop, realHeight);

		_mapInfo.realLeft = realLeft / CC_CONTENT_SCALE_FACTOR();
		_mapInfo.realWidth = realWidth / CC_CONTENT_SCALE_FACTOR();
		_mapInfo.realTop = realTop / CC_CONTENT_SCALE_FACTOR();
		_mapInfo.realHeight = realHeight / CC_CONTENT_SCALE_FACTOR();
		
		auto layer = LayerColor::create(Color4B(r, g, b, 255));
		layer->setContentSize(Size(_mapInfo.realWidth, _mapInfo.realHeight));
		layer->setPosition(Point::ZERO);
		_map->addChild(layer);

		short imgCount = 0;
		is >> imgCount;
		short maxImgId;
		is >> maxImgId;

		int iType = aType;
		if (aType <= 1)
		{
			iType = 3;
		}

		short imgId = 0;
		if (iType == 1)
		{
			is >> imgId;
			sprintf(fn, "map/%s.png", aName);
			//to code
			auto bg = Sprite::create(fn);
			bg->setAnchorPoint(Point(0, 0));
			_map->addChild(bg);
		}
		else
		{
			_imgCount = maxImgId + 1;
			_imgs = new Texture2D*[_imgCount];
			short *imgIds = new short[imgCount];
			CCLOG("loadMapLayer.... read Image:>>>");
			//读取图片
			for (int i = 0; i < imgCount; i++)
			{
				is >> imgIds[i];
				imgId = imgIds[i];
				sprintf(fn, "map/%d.png", imgId);
				auto img = Director::getInstance()->getTextureCache()->addImage(fn);
				/*img->retain();*/
				if (!img)
				{
					sprintf(fn, "map/%d.jpg", imgId);
					img = Director::getInstance()->getTextureCache()->addImage(fn);
				}
				_imgs[imgId] = img;
				CCLOG("loadMapLayer.... Image name:s%>>>", fn);
			}
			CC_SAFE_DELETE_ARRAY(imgIds);

			//读取地图Sprite
			is >> _SpriteCount;
			Vector<Sprite*> sprites;
			for (int i = 0; i < _SpriteCount; i++)
			{
				short id, x, y, flip;
	
				is >> id >> x >> y >> flip;

				// to code;
				float fx = x / CC_CONTENT_SCALE_FACTOR();
				float fy = y / CC_CONTENT_SCALE_FACTOR();


				Rect textureRect;
				auto texture = _imgs[id];
				/*Size size(texture->getRect().size.width, texture->getRect().size.height);
				auto spRect = Rect(fx, fy, size.width, size.height);
				auto intRect = intersectsRect(Rect(0, 0, _mapInfo.realWidth, _mapInfo.realHeight), spRect);
				auto offsetx = intRect.origin.x - spRect.origin.x;
				auto offsety = intRect.origin.y - spRect.origin.y;
				if (flip)
				{
					if (offsetx == 0 && intRect.size.width < size.width)
					{
						offsetx = size.width - intRect.size.width;
					}
					else
					{
						offsetx = 0;
					}
				}
				auto originRect = texture->getRect();
				textureRect.setRect(originRect.origin.x + offsetx, originRect.origin.y + offsety, intRect.size.width, intRect.size.height);
				texture->setRect(textureRect);*/
				auto sp = Sprite::createWithTexture(texture);
				sp->setPosition(fx + sp->getContentSize().width / 2, -fy - sp->getContentSize().height / 2 + _mapInfo.realHeight);
				if (flip == 1)
				{
					sp->setFlippedX(true);
				}
				_map->addChild(sp);
				/*CC_SAFE_RELEASE(texture);*/
			}
			
		}
	}
	is.close();
	CCLOG("loadMapLayer.... finished>>>");
}

void LoadMapRes::loadEnemyLayer(const char* aName, int aType)
{
	CCLOG("loadEnemyLayer.... start...>>>");
	char fn[32];
	sprintf(fn, "map/%s_enemy.dat", aName);
	Ifstream is(fn);
	if (!is.fail())
	{
		int spriteSize;
		is >> spriteSize;
		_enemyInfos.clear();
		for (int i = 0; i < spriteSize; i++)
		{
			EnemyInfo e;
			memset(&e, 0, sizeof(EnemyInfo));
			string scriptName, prop;
			int x, y;
			is >> e.id >> x >> y;

			is >> e.imageId >> e.flip;
			readString(is, scriptName);
			is >> e.triggerType >> e.triggerY;

			e.x = x / CC_CONTENT_SCALE_FACTOR();
			e.y = y / CC_CONTENT_SCALE_FACTOR();

			memcpy(e.scriptName, scriptName.c_str(), strlen(scriptName.c_str()));
			_enemyInfos.push_back(e);
			CCLOG("%s,%s", e.name, e.scriptName);

			createEnemy(e);
		}

	}
	is.close();
	CCLOG("loadEnemyLayer.... end...>>>");
}

Node* LoadMapRes::createEnemy(EnemyInfo& e)
{

	auto fn = "airplane0_1.png";/*getImageName(e.name);*/

	auto s = Sprite::create(fn);
	auto size = s->getContentSize();
	revisePositon(e.x, e.y, size);
	s->setPosition(e.x, e.y);
	_map->addChild(s);
	return nullptr;
}

void LoadMapRes::revisePositon(float &x, float &y, const Size& size)
{
	x = x + size.width / 2;
	y = -y  - size.height / 2 + _mapInfo.realHeight;
}

bool LoadMapRes::init()
{
	_imgCount = 0;
	_SpriteCount = 0;
	_map = Node::create();
	_map->retain();
	return true;
}

void LoadMapRes::cleamImage()
{
	/*for (int i = 0; i < _imgCount; i++)
	{
	CC_SAFE_RELEASE(_imgs[i]);
	}*/
	CC_SAFE_DELETE_ARRAY(_imgs);
	_imgCount = 0;
}

void LoadMapRes::readString(Ifstream &is, string &name)
{
	signed int utflen;
	is >> utflen;
	utflen = utflen & 0xffffffff;
	char *bytearr = new char[utflen];
	is.read(bytearr, utflen);
	name.assign(bytearr, utflen);
	CC_SAFE_DELETE_ARRAY(bytearr);
}

string LoadMapRes::getImageName(const char* str)
{
	string name(str);
	auto index = name.find_last_of("_");
	name.erase(index, name.length());

	char fn[125];
	sprintf(fn, "map/%s.png", name.c_str());
	return string(fn);
}

void LoadMapRes::loadMap(const char* name)
{
	loadMapLayer(name);
	loadEnemyLayer(name);
}

void LoadMapRes::screenShoot()
{
	auto renderTexture = RenderTexture::create(_mapInfo.realWidth, _mapInfo.realHeight, Texture2D::PixelFormat::RGBA8888);

	Scene* curScene = Director::getInstance()->getRunningScene();
	auto ancPos = _map->getAnchorPoint();

	//渲染纹理开始捕捉
	renderTexture->begin();
	_map->setAnchorPoint(Point::ZERO);

	//绘制当前场景
	curScene->visit();

	//end
	renderTexture->end();

	renderTexture->saveToFile("map/screenshoot.png", Image::Format::PNG);
}

Rect LoadMapRes::intersectsRect(const Rect& rectA, const Rect& rectB)
{
	Point ori;//左下角顶点
	Point topM;//右下角顶点
	float width = 0.f;
	float height = 0.f;

	if (rectA.intersectsRect(rectB))
	{
		ori.x = max(rectA.getMinX(), rectB.getMinX());
		ori.y = max(rectA.getMinY(), rectB.getMinY());

		topM.x = min(rectA.getMaxX(), rectB.getMaxX());
		topM.y = min(rectA.getMaxY(), rectB.getMaxY());

		width = topM.x - ori.x;
		height = topM.y - ori.y;

		return Rect(ori.x, ori.y, width, height);
	}
	return Rect(0, 0, -1.f, -1.f);
}