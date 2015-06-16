/*******************************************************************************
 * Copyright (C)   Inc.All Rights Reserved.
 * FileName: UnitPathData.java 
 * Description:
 * History:  
 * 版本号 作者 日期 简要介绍相关操作 
 * 1.0 cole  2015年6月5日 下午2:48:30
 *******************************************************************************/
package editor;

import java.util.ArrayList;

import jdk.internal.util.xml.impl.Input;

public class UnitPathData {
	private ArrayList<IntPair> path = new ArrayList<>();
	private ArrayList<Integer> animaID = new ArrayList<>();
	private ArrayList<Integer> speed = new ArrayList<>();
	private ArrayList<Integer> orientation = new ArrayList<>();
	private ArrayList<Integer> delay = new ArrayList<>();
	private IntPair origin = new IntPair();
	
	public IntPair getOrigin() {
		return origin.getCopy();
	}
	public void clear()
	{
		path.clear();
		animaID.clear();
		speed.clear();
		orientation.clear();
		delay.clear();
	}
	public void addOrigin(IntPair p)
	{
		if (path.size() == 0) {
			path.add(new IntPair(p.x, p.y));
			animaID.add(0);
			speed.add(100);
			orientation.add(0);
			delay.add(0);
		}
	}
	
	public void removeOrigin() {
		if (path.size() == 1) {
			path.clear();
			animaID.clear();
			speed.clear();
			orientation.clear();
			delay.clear();
		}
	}
	/**
	 * return path : return the property path.
	 */
	public IntPair[] getPath() {
		if (path.size() == 0) {
			return null;
		}
		IntPair[] result = new IntPair[path.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (IntPair)path.get(i);
		}
		return result;
	}
	/**
	 * param path : set the property path.
	 */
	public void setPath(IntPair[] path) {
		if (path == null) {
			return;
		}
		for (int i = 0; i < path.length; i++) {
			this.path.add(path[i]);
		}
	}
	/**
	 * return animaID : return the property animaID.
	 */
	public int[] getAnimaID() {
		if (animaID.size() == 0) {
			return null;
		}
		Integer[] result = new Integer[animaID.size()];
		animaID.toArray(result);
		return XUtil.IntegerToint(result);
	}
	/**
	 * param animaID : set the property animaID.
	 */
	public void setAnimaID(int[] animaID) {
		if (animaID == null) {
			return;
		}
		for (int i = 0; i < animaID.length; i++) {
			this.animaID.add(animaID[i]);
		}
	}
	/**
	 * return speed : return the property speed.
	 */
	public int[] getSpeed() {
		if (speed.size() == 0) {
			return null;
		}
		Integer[] result = new Integer[speed.size()];
		speed.toArray(result);
		return XUtil.IntegerToint(result);
	}
	/**
	 * param speed : set the property speed.
	 */
	public void setSpeed(int[] speed) {
		if (speed == null) {
			return;
		}
		for (int i = 0; i < speed.length; i++) {
			this.speed.add(speed[i]);
		}
	}
	/**
	 * return orientation : return the property orientation.
	 */
	public int[] getOrientation() {
		if (orientation.size() == 0) {
			return null;
		}
		Integer[] result = new Integer[orientation.size()];
		orientation.toArray(result);
		return XUtil.IntegerToint(result);
	}
	/**
	 * param orientation : set the property orientation.
	 */
	public void setOrientation(int[] orientation) {
		if (orientation == null) {
			return;
		}
		for (int i = 0; i < orientation.length; i++) {
			this.orientation.add(orientation[i]);
		}
	}
	/**
	 * return delay : return the property delay.
	 */
	public int[] getDelay() {
		if (delay.size() == 0) {
			return null;
		}
		Integer[] result = new Integer[delay.size()];
		delay.toArray(result);
		return XUtil.IntegerToint(result);
	}
	/**
	 * param delay : set the property delay.
	 */
	public void setDelay(int[] delay) {
		if (delay == null) {
			return;
		}
		for (int i = 0; i < delay.length; i++) {
			this.delay.add(delay[i]);
		}
	}
	
	public void addUnitPos(IntPair p)
	{
		origin = p;
	}
	
	public UnitPathData() {
	
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
		setPath(path);
		setAnimaID(animaID);
		setSpeed(speed);
		setOrientation(orientation);
		setDelay(delay);
	}
	
	public UnitPathData(UnitPathData data) {
		copy(data);
	}
	
	public void copy(UnitPathData data)
	{
		if(data == null) return;
		setPath(XUtil.copyArray(data.getPath()));
		setAnimaID(XUtil.copyArray(data.getAnimaID()));
		setSpeed(XUtil.copyArray(data.getSpeed()));
		setOrientation(XUtil.copyArray(data.getOrientation()));
		setDelay(XUtil.copyArray(data.getDelay()));
		origin = data.origin.getCopy();
	}
	
	public UnitPathData getCopy()
	{
		UnitPathData result = new UnitPathData(this);
		return result;
	}
}
