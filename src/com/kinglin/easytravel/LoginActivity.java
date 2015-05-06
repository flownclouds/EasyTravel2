package com.kinglin.easytravel;


import java.io.IOException;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.User;
import com.kinglin.serverconnect.ServerConnection;

@SuppressLint({ "InflateParams", "HandlerLeak", "ShowToast" })
public class LoginActivity extends Activity {

	static final int REGISTER_SUCCESS = 1;
	static final int REGISTER_FAILED = 2;
	static final int LOGIN_SUCCESS = 3;
	static final int LOGIN_FAILED = 4;
	static final int GET_USER_DATA_SUCCESS = 5;
	static final int GET_USER_DATA_FAILED = 6;
	
	EditText etLoginUserName,etLoginPassword;
	Button btnLogin,btnQQLogin,btnWeiboLogin,btnSignUp;
	
	User registerUser = new User();
	User loginUser = new User();
	
	MyHandler myHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		initContext();
		myHandler = new MyHandler();
		
		btnLogin.setOnClickListener(new LoginClickListener());
		btnQQLogin.setOnClickListener(new OtherLoginClickListener());
		btnWeiboLogin.setOnClickListener(new OtherLoginClickListener());
		btnSignUp.setOnClickListener(new SignUpBtnClickListener());
	}
	
	
	private void initContext(){
		
		etLoginUserName = (EditText) findViewById(R.id.et_loginUserName);
		etLoginPassword = (EditText) findViewById(R.id.et_loginPassword);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnQQLogin = (Button) findViewById(R.id.btn_qqlogin);
		btnWeiboLogin = (Button) findViewById(R.id.btn_weiboLogin);
		btnSignUp = (Button) findViewById(R.id.btn_signup);
	}
	
	
	private class LoginClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String username = etLoginUserName.getText().toString();
			String password = etLoginPassword.getText().toString();
			
			if(username.equals("")|| password.equals("")){
				Toast.makeText(getApplicationContext(),"please enter username&password", 1000).show();
			}else {
				loginUser.setUsername(username);
				loginUser.setPassword(password);
				
				LoginThread loginThread = new LoginThread();
				loginThread.start();
			}
		}
	}
	
	
	private class OtherLoginClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.other_login_dlg, null);
			builder.setView(view);
			
			final EditText etOtherLoginUsername= (EditText) view.findViewById(R.id.et_otherLoginUsername);
			final EditText etOtherLoginPassword = (EditText) view.findViewById(R.id.et_otherLoginPassword);
			
			if (v.getId() == R.id.btn_qqlogin) {
				builder.setTitle("QQ");
			}
			else if(v.getId() == R.id.btn_weiboLogin){
				builder.setTitle("Weibo");
			}
				
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					String username = etOtherLoginUsername.getText().toString();
					String password = etOtherLoginPassword.getText().toString();
					Toast.makeText(LoginActivity.this, username+","+password, Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			builder.show();
			
		}	
	}
	
	
	private class SignUpBtnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.sign_up_dlg, null);
			builder.setView(view);
			
			final EditText etSignupUsername= (EditText) view.findViewById(R.id.et_signupUsername);
			final EditText etSignupPassword = (EditText) view.findViewById(R.id.et_signupPassword);
			final EditText etSignupConfirmPsd= (EditText) view.findViewById(R.id.et_signupConfirmPsd);
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					String username = etSignupUsername.getText().toString();
					String password = etSignupPassword.getText().toString();
					String confirmpsd = etSignupConfirmPsd.getText().toString();
					if (!password.equals(confirmpsd)) {
						etSignupConfirmPsd.setText("");
						Toast.makeText(LoginActivity.this, "conformpsd is not in accord with password", Toast.LENGTH_SHORT).show();
					}
					else {
						
						registerUser.setUsername(username);
						registerUser.setPassword(password);
						RegisterThread registerThread = new RegisterThread();
						registerThread.start();
					}
					
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			builder.show();
			
		}
	}

	
	public class RegisterThread extends Thread{
		public void run() {
			
			ServerConnection sc = new ServerConnection();
			Message msg = Message.obtain();
			
			try {
				JSONObject json_registerResult = sc.UserRegister(registerUser);
				
				if (json_registerResult.get("registerResult").equals("yes")) {
					msg.arg1 = REGISTER_SUCCESS;
					myHandler.sendMessage(msg);
				}else {
					msg.arg1 = REGISTER_FAILED;
					msg.obj = json_registerResult.getString("why");
					myHandler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//鐧婚檰澶勭悊绾跨▼
	public class LoginThread extends Thread{
		
		public void run() {
			
			ServerConnection sc = new ServerConnection();
			Message msg = Message.obtain();
			long userId;
			
			try {
				JSONObject json_loginUser = sc.UserLogin(loginUser);
				
				if(json_loginUser.get("loginResult").toString().equals("no")){
					msg.arg1 = LOGIN_FAILED;
					myHandler.sendMessage(msg);
				}else{
					/*
					 * 鐒跺悗鍒癲efault鏁版嵁搴撲腑鐨剈ser琛ㄤ腑鎵炬湁娌℃湁杩欎釜鐢ㄦ埛
					 * 濡傛灉娌℃湁锛屾柊寮�嚎绋婫etAllUserData锛屽悜鏈嶅姟鍣ㄨ姹傜敤鎴锋墍鏈夋暟鎹�
					 * 濡傛灉鏈夛紝鍒欒繘鍏ヨ鐢ㄦ埛鏁版嵁搴擄紝骞跺皢鎵�湁琛ㄤ腑鐨勬暟鎹甶d鍜屾渶鍚庝慨鏀规椂闂村彇鍑�
					 * 骞舵墦鍖呮垚json浼犵粰鏈嶅姟鍣�
					 */
					
					msg.arg1 = LOGIN_SUCCESS;
					myHandler.sendMessage(msg);
					userId = (long) json_loginUser.get("userId");
					
					DBHelper helper=new DBHelper(getApplicationContext(), "default.db", null, 1);
				    SQLiteDatabase defaultdb=helper.getWritableDatabase();
				   
				    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
				    boolean hasBeenLogedIn = mdi.hasBeenLogedIn(userId);
				    
				    if (!hasBeenLogedIn) {
						GetAllUserDataThread getAllUserDataThread = new GetAllUserDataThread(userId);
						getAllUserDataThread.start();
					}else {
						helper=new DBHelper(getApplicationContext(), userId+".db", null, 1);
					    SQLiteDatabase userdb=helper.getWritableDatabase();
					    mdi = new ModelDaoImp(userdb);
					    JSONObject json_userAllDataSnapshot = mdi.getAllDataSnapshot();
					    GetUserChangedDataThread getUserChangedDataThread = new GetUserChangedDataThread(userId,json_userAllDataSnapshot);
					    getUserChangedDataThread.start();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//杩欐槸閽堝绗竴娆″湪璇ユ墜鏈轰笂鐧婚檰鐨勭敤鎴�
	//鍚戞湇鍔″櫒璇锋眰鑾峰彇鐢ㄦ埛鎵�湁鐨勪俊鎭�鐒跺悗淇濆瓨鍒版湰鍦版暟鎹簱锛屽苟杞埌UserInformationActivity
	public class GetAllUserDataThread extends Thread{
		
		long userId;
		GetAllUserDataThread(long userId){
			this.userId = userId;
		}
		
		public void run(){
			ServerConnection sc = new ServerConnection();
			Message msg = Message.obtain();
			
			//鑾峰彇鐢ㄦ埛鎵�湁淇℃伅
			JSONObject json_allUserData;
			try {
				json_allUserData = sc.getAllUserData(userId);
				if (json_allUserData != null) {
					msg.arg1 = GET_USER_DATA_SUCCESS;
					myHandler.sendMessage(msg);
					
					//涓虹敤鎴峰紑鍒涘缓鏁版嵁搴擄紝骞跺皢鎵�湁淇℃伅瀛樿繘鍘�
					DBHelper helper=new DBHelper(getApplicationContext(), userId+".db", null, 1);
				    SQLiteDatabase userdb=helper.getWritableDatabase();
				    ModelDaoImp mdi = new ModelDaoImp(userdb);
				    
				    if (mdi.saveNewUserAllData(json_allUserData)) {
				    	//灏嗚鐢ㄦ埛涓汉淇℃伅浼犲埌UserInformationActivity
					    User user = mdi.getUserInformation();
					    Intent intent = new Intent(getApplicationContext(),UserInformationActivity.class);
					    intent.putExtra("userLogin", (Serializable)user);
					    startActivity(intent);
					}
				}else {
					msg.arg1 = GET_USER_DATA_FAILED;
					myHandler.sendMessage(msg);
				}
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	//杩欐槸閽堝鏇剧粡鍦ㄨ鎵嬫満涓婄櫥闄嗚繃鐨勭敤鎴�
	//浼犲叆鐨勮鐢ㄦ埛鎵�湁淇℃伅鐨刬d鍜屾渶鍚庝慨鏀规椂闂达紝浠庢湇鍔℃眰鑾峰彇瑕佷慨鏀圭殑鎶婂唴瀹瑰苟瀛樺埌鏁版嵁搴擄紝鐒跺悗杞埌UserInformationActivity
	public class GetUserChangedDataThread extends Thread{
		long userId;
		JSONObject json_userAllDataSnapshot;
		
		GetUserChangedDataThread(long userId,JSONObject json_userAllDataSnapshot){
			this.userId = userId;
			this.json_userAllDataSnapshot = json_userAllDataSnapshot;
		}
		
		public void run(){
			ServerConnection sc = new ServerConnection();
			JSONObject json_userAllChangedData= sc.getAllUserChangedData(userId,json_userAllDataSnapshot);
			
			DBHelper helper=new DBHelper(getApplicationContext(), userId+".db", null, 1);
		    SQLiteDatabase userdb=helper.getWritableDatabase();
		    ModelDaoImp mdi = new ModelDaoImp(userdb);
		    
		    if (mdi.saveUserChangedData(json_userAllChangedData)) {
		    	User user = mdi.getUserInformation();
			    Intent intent = new Intent(getApplicationContext(),UserInformationActivity.class);
			    intent.putExtra("userLogin", (Serializable)user);
			    startActivity(intent);
			}
		}
	}
	
	public class MyHandler extends Handler{

		MyHandler() {  
			super(); 
		}  

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.arg1) {
			case REGISTER_SUCCESS:
				Toast.makeText(getApplicationContext(), "register success,please login", 1000).show();
				break;
			case REGISTER_FAILED:
				Toast.makeText(getApplicationContext(), (CharSequence) msg.obj, 1000).show();
				break;
			case LOGIN_SUCCESS:
				Toast.makeText(getApplicationContext(), "login success", 1000).show();
				break;
			case LOGIN_FAILED:
				Toast.makeText(getApplicationContext(), "login failed,wrong username&password", 1000).show();
				break;
			case GET_USER_DATA_SUCCESS:
				Toast.makeText(getApplicationContext(), "get user data success", 1000).show();
				break;
			case GET_USER_DATA_FAILED:
				Toast.makeText(getApplicationContext(), "get user data failed", 1000).show();
				break;
			default:
				break;
			}
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
