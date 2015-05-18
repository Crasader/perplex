#ifndef __dropitemdate_h__
#define __dropitemdate_h__

static const int DROPITEMCOUNT[] = {6,7,11,11};
static const int DROPITEMDATA[][11][4] = {
	{	{ 1,0,5,40 },
		{ 1,6,13,25 },
		{ 1,14,20,10 },
		{ 1,21,30,5 },
		{ 0,0,100,30 },
		{ 0,101,200,15 }
	},
	{
		{ 2,0,15,40 },
		{ 2,15,30,25 },
		{ 2,31,50,10 },
		{ 2,51,99,5 },
		{ 0,0,100,40 },
		{ 0,101,200,25 },
		{ 0,201,300,10 }
	},
	{
		{ 1,0,5,100 },
		{ 2,0,15,100 },
		{ 0,0,100,100 },
		{ 1,6,13,25 },
		{ 1,14,20,10 },
		{ 1,21,30,5 },
		{ 2,16,30,25 },
		{ 2,31,50,10 },
		{ 2,51,99,5 },
		{ 0,101,200,25 },
		{ 0,201,300,10 }
	},
	{
		{ 1,0,5,100 },
		{ 2,0,15,100 },
		{ 0,0,100,100 },
		{ 1,6,13,50 },
		{ 1,14,20,15 },
		{ 1,21,30,5 },
		{ 2,16,30,50 },
		{ 2,31,50,15 },
		{ 2,51,99,5 },
		{ 0,101,200,50 },
		{ 0,201,300,10 }
	}
};

#endif // __dropitemdate_h__