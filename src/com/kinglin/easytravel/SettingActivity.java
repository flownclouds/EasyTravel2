package com.kinglin.easytravel;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;

public class SettingActivity extends Activity {

	ImageButton ibtnSettingExpandAboutUs;
	LinearLayout llayoutSettingAboutUs;
	
	int rotate = 0;
	
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		
		initContext();
		db = dbInit();
		
		ibtnSettingExpandAboutUs.setOnClickListener(new ExpandAboutUsClickListener());
		
		/*
		 * ����Ӧ�û���һ��wifi��ͬ�����Ƿ���ٵİ�ť���
		 * 
		 * Ȼ�����˳����ҳ���ʱ��finish���ģ�Ҫ����mdi��setUserConfiguration��configuration������
		 */
		
	}

	//���������ȡ��ǰ�û������ݿ�
	public SQLiteDatabase dbInit() {
		
		//��������default���ݿ⣬��һ��ʹ�û��ʼ������ʵ���Ӧ�ı�
		DBHelper helper=new DBHelper(this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //��ѯdefault���ݿ���Configuration���loginUser,������Ӧ��db
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}
	
	private void initContext(){
		
		ibtnSettingExpandAboutUs = (ImageButton) findViewById(R.id.ibtn_settingExpandAboutUs);
		llayoutSettingAboutUs = (LinearLayout) findViewById(R.id.ll_settingAboutUs);
	}
	
	//���about usչ����ť���¼���Ӧ
	private class ExpandAboutUsClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			RotateAnimation rAnimation;
			
			if (rotate == 0) {
				
				rAnimation = new RotateAnimation(0, 180,
						Animation.RELATIVE_TO_SELF, 0.5f,
		                Animation.RELATIVE_TO_SELF, 0.5f);
				rAnimation.setDuration(500);
				rAnimation.setFillAfter(true);
				ibtnSettingExpandAboutUs.startAnimation(rAnimation);
				
				llayoutSettingAboutUs.setVisibility(View.VISIBLE);
				
				TranslateAnimation tAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
						Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
				tAnimation.setDuration(500);
				tAnimation.setStartOffset(0);
				tAnimation.setFillAfter(true);
				
				llayoutSettingAboutUs.startAnimation(tAnimation);
				
				rotate = 1;
			}
			else {
				
				rAnimation = new RotateAnimation(180, 360,
						Animation.RELATIVE_TO_SELF, 0.5f,
		                Animation.RELATIVE_TO_SELF, 0.5f);
				rAnimation.setDuration(500);
				rAnimation.setFillAfter(true);
				ibtnSettingExpandAboutUs.startAnimation(rAnimation);
				
				TranslateAnimation tAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
				tAnimation.setDuration(500);
				tAnimation.setStartOffset(0);
				tAnimation.setFillAfter(true);
				
				llayoutSettingAboutUs.startAnimation(tAnimation);
				
				rotate = 0;
			}
			
		}
	}
	
}
