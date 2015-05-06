package com.kinglin.easytravel;

import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Treasure;

public class TreasureActivity extends Activity {

	ImageButton ibtnTreasureReturn;
	ListView lvTreasureDetails;
	
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure);
		
		initContext();
		db = dbInit();
		
		showListView();
		
		ibtnTreasureReturn.setOnClickListener(new TreasureReturnClickListener());
		
	}

	private void initContext(){
		
		ibtnTreasureReturn = (ImageButton) findViewById(R.id.ibtn_treasureReturn);
		lvTreasureDetails = (ListView) findViewById(R.id.lv_treasureDetails);
	}
	
	//这个函数获取当前用户的数据库
	public SQLiteDatabase dbInit() {
		
		//先连接上default数据库，第一次使用会初始化各种实体对应的表
		DBHelper helper=new DBHelper(this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //查询default数据库中Configuration表的loginUser,返回相应的db
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
	
	private class TreasureReturnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			TreasureActivity.this.finish();
			overridePendingTransition(0, R.anim.center_out);
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private void showListView() {
		/*
		 * 这里是获取宝藏项列表，然后显示出来
		 */
		ModelDaoImp mdi = new ModelDaoImp(db);
		List<Treasure> treasures = mdi.getAllTreasure();
	}



}
