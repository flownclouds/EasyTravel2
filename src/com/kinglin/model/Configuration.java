package com.kinglin.model;

public class Configuration {

	long configurationId;//id固定，因为只有一个记录
	String loginUser;  //当前用户id，判断是否是登陆状态,default为未登录，其他为登陆
	int syncByWifi;  //1为wifi下同步，0为不同步
	int trackOrNot;  //1为路径跟踪，0为不跟踪
	String info;  //软件相关信息

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
