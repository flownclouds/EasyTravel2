package com.kinglin.model;

public class Treasure {

	long treasureId;  //宝藏产生时间
	String time;  //宝藏产生时间，和id区分开
	String content;  //宝藏内容
	
	public Treasure() {
	}


	public long getTreasureId() {
		return treasureId;
	}


	public void setTreasureId(long treasureId) {
		this.treasureId = treasureId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
