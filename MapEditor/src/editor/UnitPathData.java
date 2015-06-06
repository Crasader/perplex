/*******************************************************************************
 * Copyright (C)   Inc.All Rights Reserved.
 * FileName: UnitPathData.java 
 * Description:
 * History:  
 * 版本号 作者 日期 简要介绍相关操作 
 * 1.0 cole  2015年6月5日 下午2:48:30
 *******************************************************************************/
package editor;

import com.sun.org.apache.regexp.internal.REUtil;

public class UnitPathData {
	public IntPair[] path;
	public int[] animaID;
	public int[] speed;
	public int[] orientation;
	public int[] delay;
	/**
	 * return path : return the property path.
	 */
	public IntPair[] getPath() {
		return path;
	}
	/**
	 * param path : set the property path.
	 */
	public void setPath(IntPair[] path) {
		this.path = path;
	}
	/**
	 * return animaID : return the property animaID.
	 */
	public int[] getAnimaID() {
		return animaID;
	}
	/**
	 * param animaID : set the property animaID.
	 */
	public void setAnimaID(int[] animaID) {
		this.animaID = animaID;
	}
	/**
	 * return speed : return the property speed.
	 */
	public int[] getSpeed() {
		return speed;
	}
	/**
	 * param speed : set the property speed.
	 */
	public void setSpeed(int[] speed) {
		this.speed = speed;
	}
	/**
	 * return orientation : return the property orientation.
	 */
	public int[] getOrientation() {
		return orientation;
	}
	/**
	 * param orientation : set the property orientation.
	 */
	public void setOrientation(int[] orientation) {
		this.orientation = orientation;
	}
	/**
	 * return delay : return the property delay.
	 */
	public int[] getDelay() {
		return delay;
	}
	/**
	 * param delay : set the property delay.
	 */
	public void setDelay(int[] delay) {
		this.delay = delay;
	}
	
	public UnitPathData() {
		this.path = null;
		this.animaID = null;
		this.speed = null;
		this.orientation = null;
		this.delay = null;
	}
	
	/**
	 * construct
	 * @param path
	 * @param animaID
	 * @param speed
	 * @param orientation
	 * @param delay
	 */
	public UnitPathData(IntPair[] path, int[] animaID, int[] speed,
			int[] orientation, int[] delay) {
		this.path = path;
		this.animaID = animaID;
		this.speed = speed;
		this.orientation = orientation;
		this.delay = delay;
	}
	
	public UnitPathData(UnitPathData data) {
		copy(data);
	}
	
	public void copy(UnitPathData data)
	{
		if(data == null) return;
		path = XUtil.copyArray(data.path);
		animaID = XUtil.copyArray(data.animaID);
		speed = XUtil.copyArray(data.speed);
		orientation = XUtil.copyArray(data.orientation);
		delay = XUtil.copyArray(data.delay);
	}
	
	public UnitPathData getCopy()
	{
		UnitPathData result = new UnitPathData(this);
		return result;
	}
}
