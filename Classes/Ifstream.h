#ifndef __FNIfstream_h__
#define __FNIfstream_h__

#include <string>
#include <cstdio>
#include "cocos2d.h"

using std::string;
using std::FILE;

class Ifstream;

class FNInputStreamProtocol
{
public:
	virtual int inputStream(Ifstream& dis, int aFormat) =  0;
};

class Ifstream
{
public:
	Ifstream(const char *_Filename);
	virtual ~Ifstream(void);
	void set_high_endian(bool hi_endian);

	Ifstream& operator>>(Ifstream& (*_Pfn)(Ifstream&));
	Ifstream& operator>>(int& val);
	Ifstream& operator>>(short& val);
	Ifstream& operator>>(unsigned char & val);
	Ifstream& operator>>(signed char & val);
	Ifstream& operator>>(std::string & val);

	bool close();

	int seek(long offset, int origin);

	int read(char* buffer, int size);

	bool eof();

	int tell();

	bool fail();

private:
	bool high_endian;
	FILE *m_fp;
	cocos2d::Data m_data;
	int m_pos;
};

inline Ifstream& highendian(Ifstream& ifs)
{
	ifs.set_high_endian(true);
	return ifs;
}

inline Ifstream& lowendian(Ifstream& ifs)
{
	ifs.set_high_endian(false);
	return ifs;
}

inline int isLittleEndian() {
	union {
		int vi;
		char vc;
	}u;

	u.vi = 1;
	return (u.vc == 1);
}
#endif // __FNIfstream_h__

