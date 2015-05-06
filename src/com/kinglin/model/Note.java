package com.kinglin.model;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Note implements Serializable{

	long noteId;  //保存的时间
	String time;  //保存的时间,以免混淆
	int permission;  //0为公开，1为私密
	int weather;  //1~4代表不同的天气
	String text;  //文字
	String title; //标题
	String pictures;  //图片的路径，之间用分号隔开
	String voice;  //语音的路径
	double locationx;  //经度
	double locationy;  //纬度
	String video;  //视频的路径
	long lastChangeTime;
	int operation;//0：已同步1：新增2：修改3：删除
	
	public Note(long noteId, String time, int permission, int weather, String text, String title,String pictures, String voice, double locationx, double locationy, String video, long lastChangeTime, int operation) {
		this.noteId=noteId;
		this.time=time;
		this.permission=permission;
		this.weather=weather;
		this.text=text;
		this.title = title;
		this.pictures=pictures;
		this.voice=voice;
		this.locationx=locationx;
		this.locationy=locationy;
		this.video=video;
		this.lastChangeTime=lastChangeTime;
		this.operation=operation;
	}
    

	public Note() {
		
	}


	public long getNoteId() {
		return noteId;
	}


	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}


	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public int getWeather() {
		return weather;
	}

	public void setWeather(int weather) {
		this.weather = weather;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public double getLocationx() {
		return locationx;
	}

	public void setLocationx(double locationx) {
		this.locationx = locationx;
	}

	public double getLocationy() {
		return locationy;
	}

	public void setLocationy(double locationy) {
		this.locationy = locationy;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}


	public long getLastChangeTime() {
		return lastChangeTime;
	}


	public void setLastChangeTime(long lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}


	public int getOperation() {
		return operation;
	}


	public void setOperation(int operation) {
		this.operation = operation;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


}
