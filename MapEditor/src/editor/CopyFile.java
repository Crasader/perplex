/*******************************************************************************
 * Copyright (C)   Inc.All Rights Reserved.
 * FileName: CopyFile.java 
 * Description:
 * History:  
 * 版本号 作者 日期 简要介绍相关操作 
 * 1.0 cole  2015年5月26日 下午12:55:36
 *******************************************************************************/
package editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class CopyFile {
	public void copyFolder(File srcFolder, File destFolder)
	{
		String[] filterFile = null;
		File[] files = srcFolder.listFiles();
		for(File file : files)
		{
			if (file.isFile()) {
				String pathname = destFolder + File.separator + file.getName();
				if (filterFile == null) {
					createFolderAndFile(file, pathname);
				}
				else 
				{
					for(String suff : filterFile)
					{
						if(pathname.endsWith(suff))
						{
							createFolderAndFile(file, pathname);
						}
					}
				}
			}
			else 
			{
				copyFolder(file, destFolder);
			}
		}
	}

	/**
	 * @param file
	 * @param pathname
	 */
	private void createFolderAndFile(File file, String pathname) {
		File dest = new File(pathname);
		File destPar = dest.getParentFile();
		dest.mkdirs();
		if (!dest.exists()) {
			try {
				dest.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			CopyFile(file, dest);
		}
	}
	
	private void CopyFile(File src, File dest){
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(src);
			outputStream = new FileOutputStream(dest);
		} catch (Exception e) {
			// TODO: handle exception
		} finally
		{
			try {
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
