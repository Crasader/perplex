#ifndef __mapconst_h__
#define __mapconst_h__


static const int MAP_FILES_COUNT = 4;
static const int MAX_ACTIVE_MAP_COUNT = 4;

static const int MAP_DATA_START_INDEX = 1;
static const int MAP_DATA_FILE_COUNT = 5;

//地图图像开始索引值
static const int MAP_IMAGE_START_INDEX[] = {
	100, 300, 200, 300, 100 };
//地图图像的数目
static const int MAP_IMAGE_COUNT[] = {
	34, 29, 32, 29, 34 };
//地图ID对应的关卡
static const int MAPID_TO_STAGE[] = {
	0, 0, 1, 1, 2, 2, 3, 3, 4, 4 };
//地图缓冲区的高度
static const int PART_HEIGHT = 24;

#endif // __mapconst_h__
