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
		
		
		//�����ȡ��ǰ�û������ݿ�
		db = dbInit();
		
		ModelDaoImp mdi = new ModelDaoImp(db);
		Configuration configuration = mdi.getUserConfiguration();
		
		//���û����ڵ�½״̬
		if (!configuration.getLoginUser().equals("default")) {
			
			//���û�ѡ��wifi��ͬ��
			if (configuration.getSyncByWifi() == 1) {
				
				//�ж�����״̬��0Ϊ0Ϊδ������1Ϊ�����ƶ����磬2Ϊ����wifi
				switch (connectToNetwork()) {
				case 0:
					Toast.makeText(getApplicationContext(), "δ������", 1000).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "�ƶ�������ͬ����", 1000).show();
					break;
				case 2:
					Toast.makeText(getApplicationContext(), "wifi������ͬ����", 1000).show();
					
					//�������ͬ������
					ServerConnection severConnection = new ServerConnection(db);
					severConnection.syncData();
					break;
				default:
					break;
				}
			}
		}
		
		//���ѡ����·������
		if (configuration.getTrackOrNot()==1) {
			
			//����·�����ٵ�service
			
		}
		
		Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
		startActivity(intent);
		this.finish();
	}

	
	//���������ȡ��ǰ�û������ݿ�
	public SQLiteDatabase dbInit() {
		
		//��������default���ݿ⣬��һ��ʹ�û��ʼ������ʵ���Ӧ�ı�
		DBHelper helper=new DBHelper(MainActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
	   
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);

	    //��ѯdefault���ݿ���Configuration���loginUser,������Ӧ��db
        
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(MainActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}

	//�ж��Ƿ���������0Ϊδ������1Ϊ�����ƶ����磬2Ϊ����wifi
	int connectToNetwork(){
		return 0;
	}

}
