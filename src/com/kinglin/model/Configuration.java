package com.kinglin.model;

public class Configuration {

	long configurationId;//id�̶�����Ϊֻ��һ����¼
	String loginUser;  //��ǰ�û�id���ж��Ƿ��ǵ�½״̬,defaultΪδ��¼������Ϊ��½
	int syncByWifi;  //1Ϊwifi��ͬ����0Ϊ��ͬ��
	int trackOrNot;  //1Ϊ·�����٣�0Ϊ������
	String info;  //��������Ϣ

	int changed;
	
	public Configuration() {
	
	}

	public Configuration(long configurationId, String loginUser, int syncByWifi, int trackOrNot, String info,int changed) {
		this.configurationId = configurationId;
		this.loginUser = loginUser;
		this.syncByWifi=syncByWifi;
		this.trackOrNot=trackOrNot;
		this.info=info;
		this.changed = changed;
	}

	
	public long getConfigurationId() {
		return configurationId;
	}


	public void setConfigurationId(long configurationId) {
		this.configurationId = configurationId;
	}


	public int getSyncByWifi() {
		return syncByWifi;
	}

	public void setSyncByWifi(int syncByWifi) {
		this.syncByWifi = syncByWifi;
	}

	public int getTrackOrNot() {
		return trackOrNot;
	}

	public void setTrackOrNot(int trackOrNot) {
		this.trackOrNot = trackOrNot;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public int getChanged() {
		return changed;
	}

	public void setChanged(int changed) {
		this.changed = changed;
	}

   
}
