package com.kinglin.easytravel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Configuration;
import com.kinglin.serverconnect.ServerConnection;

@SuppressLint("ShowToast")
public class MainActivity extends Activity {

	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		
		//这里获取当前用户的数据库
		db = dbInit();
		
		ModelDaoImp mdi = new ModelDaoImp(db);
		Configuration configuration = mdi.getUserConfiguration();
		
		//当用户处于登陆状态
		if (!configuration.getLoginUser().equals("default")) {
			
			//当用户选择wifi下同步
			if (configuration.getSyncByWifi() == 1) {
				
				//判断网络状态，0为0为未联网，1为连上移动网络，2为连上wifi
				switch (connectToNetwork()) {
				case 0:
					Toast.makeText(getApplicationContext(), "未联网！", 1000).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "移动网，不同步！", 1000).show();
					break;
				case 2:
					Toast.makeText(getApplicationContext(), "wifi，正在同步！", 1000).show();
					
					//向服务器同步数据
					ServerConnection severConnection = new ServerConnection(db);
					severConnection.syncData();
					break;
				default:
					break;
				}
			}
		}
		
		//如果选择了路径跟踪
		if (configuration.getTrackOrNot()==1) {
			
			//开启路径跟踪的service
			
		}
		
		Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
		startActivity(intent);
		this.finish();
	}

	
	//这个函数获取当前用户的数据库
	public SQLiteDatabase dbInit() {
		
		//先连接上default数据库，第一次使用会初始化各种实体对应的表
		DBHelper helper=new DBHelper(MainActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
	   
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);

	    //查询default数据库中Configuration表的loginUser,返回相应的db
        
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(MainActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}

	//判断是否连上网，0为未联网，1为连上移动网络，2为连上wifi
	int connectToNetwork(){
		return 0;
	}

}
