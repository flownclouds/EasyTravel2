package com.kinglin.model;

public class Coin {

	long coinId;  //���ֲ���ʱ��
	String time;  //���ֲ���ʱ�䣬��id���ֿ�
	int grade;  //��ֵ
	String content;  //��������
	
	public Coin() {
	}

	public long getCoinId() {
		return coinId;
	}

	public void setCoinId(long coinId) {
		this.coinId = coinId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
