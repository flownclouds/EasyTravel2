package com.kinglin.model;

public class Treasure {

	long treasureId;  //���ز���ʱ��
	String time;  //���ز���ʱ�䣬��id���ֿ�
	String content;  //��������
	
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
