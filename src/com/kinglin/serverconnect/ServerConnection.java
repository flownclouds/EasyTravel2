package com.kinglin.serverconnect;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.database.sqlite.SQLiteDatabase;

import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Note;
import com.kinglin.model.User;

public class ServerConnection {

	SQLiteDatabase db;
	Socket socket = new Socket();
	InetSocketAddress ipAddress = null;
	int timeout = 3000;
	String ip = "115.156.249.15";
	int port = 12345;
	
	public ServerConnection() {
	}
	
	public ServerConnection(SQLiteDatabase db) {
		this.db = db;
	}
	
	@SuppressWarnings("null")
	public void syncData(){
		/*
		 * ���configuration��syncbywifiΪ1
		 * ��ôservice��ÿ����Сʱ�ͻ����һ���������
		 * ���������Ҫȥ�������ݿ������б����������
		 * Ȼ������operation��Ϊ0�Ĵ����json��������������ͬ��
		 * �ȴ����������سɹ���Ȼ����Щoperation����Ϊ0
		 */
		ModelDaoImp mdi = new ModelDaoImp(db);
		JSONObject json_allChangedData = mdi.getAllChangedData();
		if (json_allChangedData!=null) {
			/*
			 *���������������Ҫͬ��
			 *Ȼ������ͬ�����õ�����ֵ
			 */
			try {
				JSONObject json_syncResult = null;
				if (json_syncResult.getString("syncResult").equals("yes")) {
					mdi.changeAllOperation();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public JSONObject UserRegister(User registerUser) throws JSONException, IOException{
		
		ipAddress = new InetSocketAddress(ip, port);
		socket.connect(ipAddress, timeout);
		
		JSONObject json_registuser = new JSONObject();
		json_registuser.put("what", "register");
		json_registuser.put("username", registerUser.getUsername());
		json_registuser.put("password", registerUser.getPassword());
		
		BufferedWriter bw = null;
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(json_registuser.toString());
		bw.flush();
		
		InputStream ips = socket.getInputStream();
		byte[] in = new byte[100];
		ips.read(in);
		String registerResult = new String(in);
		
		JSONTokener jsonTokener = new JSONTokener(registerResult);
		JSONObject json_registerResult = (JSONObject) jsonTokener.nextValue();

		bw.close();
		ips.close();
		socket.close();
		
		return json_registerResult;
	}
	
	public JSONObject UserLogin(User loginUser) throws IOException, JSONException{
		
		ipAddress = new InetSocketAddress(ip, port);
		socket.connect(ipAddress, timeout);
		
		JSONObject json_loginUser = new JSONObject();
		json_loginUser.put("what", "login");
		json_loginUser.put("username", loginUser.getUsername());
		json_loginUser.put("password", loginUser.getPassword());
		
		BufferedWriter bw = null;
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(json_loginUser.toString());
		bw.flush();
		
		InputStream ips = socket.getInputStream();
		byte[] in = new byte[100];
		ips.read(in);
		String loginResult = new String(in);
		
		JSONTokener jsonTokener = new JSONTokener(loginResult);
		JSONObject json_loginResult = (JSONObject) jsonTokener.nextValue();
		
		bw.close();
		ips.close();
		socket.close();
		
		return json_loginResult;
		
	}

	public JSONObject getAllUserData(long userId) throws IOException, JSONException {
		ipAddress = new InetSocketAddress(ip, port);
		socket.connect(ipAddress, timeout);
		
		JSONObject json_getAllUserData = new JSONObject();
		json_getAllUserData.put("what", "getAllUserData");
		json_getAllUserData.put("userId", userId);
		
		BufferedWriter bw = null;
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(json_getAllUserData.toString());
		bw.flush();
		
		InputStream ips = socket.getInputStream();
		byte[] in = new byte[65535];
		ips.read(in);
		String getAllUserDataResult = new String(in);
		
		JSONTokener jsonTokener = new JSONTokener(getAllUserDataResult);
		JSONObject json_getAllUserDataResult = (JSONObject) jsonTokener.nextValue();
		
		bw.close();
		ips.close();
		socket.close();
		
		return json_getAllUserDataResult;
	}

	public JSONObject getAllUserChangedData(long userId,JSONObject json_userAllDataSnapshot){
		return null;
	}

	public boolean shareNoteToQQ(Note shareNote){
		return false;
	}
	
	public boolean shareNoteToWeibo(Note shareNote){
		return false;
	}
	
	public boolean shareNoteToWeixin(Note shareNote){
		return false;
	}
}
