#include "Ifstream.h"
#include "cocos2d.h"


Ifstream::Ifstream(const char *_Filename)
:high_endian(true)
, m_pos(0)
{
	m_fp = nullptr;
	auto utils = cocos2d::FileUtils::getInstance();
	/*if (utils->isFileExist(_Filename)) {
		const std::string path = utils->fullPathForFilename(_Filename);
		m_fp = fopen(path.c_str(), "rb");
		}*/
	m_data = utils->getDataFromFile(string(_Filename));
}


Ifstream::~Ifstream(void)
{
	close();
}

bool Ifstream::fail()
{
	/*return !m_fp;*/
	return m_data.isNull();
}

int Ifstream::tell()
{
	if (m_fp){
		return ftell(m_fp);
	}
	return 0;
}

bool Ifstream::eof()
{
	return false;
}

int Ifstream::read(char* buffer, int size)
{
	/*if (m_fp) {
		return fread(buffer, 1, size, m_fp);
	}*/
	if (m_pos + size <= m_data.getSize())
	{
		memcpy(buffer, m_data.getBytes() + m_pos, size);
		m_pos += size;
		return size;
	}
	return 0;
}

int Ifstream::seek(long offset, int origin)
{
	if (m_fp) {
		return fseek(m_fp, offset, origin);
	}
	return 0;
}

bool Ifstream::close()
{
	/*if (m_fp) {
		fclose(m_fp);
		m_fp = 0;
		return true;
	}
	return false;*/
	m_data.clear();
	m_pos = 0;
	return true;
}

Ifstream& Ifstream::operator>>(std::string & val)
{
	signed short utflen;
	(*this) >> utflen;
	utflen = utflen & 0xffff;
	char *bytearr = new char[utflen];
	read(bytearr, utflen);
	val.assign(bytearr, utflen);
	CC_SAFE_DELETE_ARRAY(bytearr);
	return *this;
}

Ifstream& Ifstream::operator>>(signed char & val)
{
	(*this) >> *((unsigned char*)&val);
	return *this;
}

Ifstream& Ifstream::operator>>(unsigned char & val)
{
	if (read((char*)&val, sizeof(val))>0)
	{

	}
	else
	{
		throw("null!");
	}
	return *this;
}

Ifstream& Ifstream::operator>>(short& val)
{
	if (read((char*)&val, sizeof(val)) >0) {
		if ((isLittleEndian() && high_endian) // CPU小端,按大端读数据
			|| (!isLittleEndian() && high_endian == false))// CPU大端,按小端读数据
		{
			val = (val & 0xff00) >> 8 | (val & 0xff) << 8;
		}
	}
	else {
		//throw...
	}
	return *this;
}

Ifstream& Ifstream::operator>>(int& val)
{
	if (read((char*)&val, sizeof(val)) >0) {
		if ((isLittleEndian() && high_endian) // CPU小端,按大端读数据
			|| (!isLittleEndian() && high_endian == false))// CPU大端,按小端读数据
		{
			val = (val & 0xff) << 24 | (val & 0xff00) << 8 | (val & 0xff0000) >> 8 | (val & 0xff000000) >> 24;
		}
	}
	else {
		//throw...
	}
	return *this;
}

void Ifstream::set_high_endian(bool hi_endian)
{
	hi_endian = hi_endian;
}


