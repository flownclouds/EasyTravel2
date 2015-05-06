package com.kinglin.easytravel;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.User;

@SuppressLint("ShowToast")
public class UserInformationActivity extends Activity {

	Button btnTurnToSetting,btnUserInfoCoinDetail,btnUserInfoTreasureDetail;
	EditText etUserInfoUsername,etUserInfoBirthday,etUserInfoHobby,etUserInfoCoin,etUserInfoTreasure;
	ImageButton ibtnUserInfoIcon,ibtnUserInfoSelectBirth,ibtnUserInfoEdit;
	RadioGroup rgUserInfoGender;
	RadioButton rbtnUserInfoMale,rbtnUserInfoFemale;
	Button btnUserInfoCancel,btnUserInfoSave;
	LinearLayout llayoutUerInfoOperation;
	
	Calendar calendar;
	private int mYear;
	private int mMonth;
	private int mDay;
	
	User user;
	
	SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_information);
		
		initContext();
		db = dbInit();
		
		user = (User) getIntent().getSerializableExtra("userLogin");
		
		
		if (user != null) {
			showInformation(user);
		}else {
			/*
			 * ���ﵯ������δ��¼��
			 * ѯ���Ƿ��½
			 * ����ת��loginactivity
			 * ����ת��shownoteactivity
			 */
		}
		
		btnTurnToSetting.setOnClickListener(new TurnToSettingClickListener());
		ibtnUserInfoIcon.setOnClickListener(new SelectIconClickListener());
		
		rgUserInfoGender.setOnCheckedChangeListener(new SelectGenderListener());
		ibtnUserInfoSelectBirth.setOnClickListener(new SelectBirthClickListener());
		
		btnUserInfoCoinDetail.setOnClickListener(new CoinDetailClickListener());
		btnUserInfoTreasureDetail.setOnClickListener(new TreasureDetailClickListener());
		
		ibtnUserInfoEdit.setOnClickListener(new UserInfoEditClickListener());
		
		btnUserInfoCancel.setOnClickListener(new UserInfoCancelClickListener());
		btnUserInfoSave.setOnClickListener(new UserInfoSaveClickListener());
		
	}
	
	//��ʼ���ؼ�
	private void initContext(){
		btnTurnToSetting = (Button) findViewById(R.id.btn_turnToSetting);
		ibtnUserInfoIcon = (ImageButton) findViewById(R.id.ibtn_userInfoIcon);
		etUserInfoUsername = (EditText) findViewById(R.id.et_userInfoUsername);
		
		rgUserInfoGender = (RadioGroup) findViewById(R.id.rg_userInfoGender);
		rbtnUserInfoMale = (RadioButton) findViewById(R.id.rbtn_userInfoMale);
		rbtnUserInfoFemale = (RadioButton) findViewById(R.id.rbtn_userInfoFemale);
		
		etUserInfoBirthday = (EditText) findViewById(R.id.et_userInfoBirthday);
		ibtnUserInfoSelectBirth = (ImageButton) findViewById(R.id.ibtn_userInfoSelectBirth);
		etUserInfoHobby = (EditText) findViewById(R.id.et_userInfoHobby);
		
		etUserInfoCoin = (EditText) findViewById(R.id.et_userInfoCoin);
		btnUserInfoCoinDetail = (Button) findViewById(R.id.btn_userInfoCoinDetail);
		etUserInfoTreasure = (EditText) findViewById(R.id.et_userInfoTeasure);
		btnUserInfoTreasureDetail = (Button) findViewById(R.id.btn_userInfoTeasureDetail);
		
		ibtnUserInfoEdit = (ImageButton) findViewById(R.id.ibtn_userInfoEdit);
		llayoutUerInfoOperation = (LinearLayout) findViewById(R.id.ll_userInfoOperation);
		btnUserInfoCancel = (Button) findViewById(R.id.btn_userInfoCancel);
		btnUserInfoSave = (Button) findViewById(R.id.btn_userInfoSave);
		
		ibtnUserInfoIcon.setClickable(false);
		etUserInfoUsername.setFocusable(false);
		rbtnUserInfoMale.setClickable(false);
		rbtnUserInfoFemale.setClickable(false);
		ibtnUserInfoSelectBirth.setClickable(false);
		etUserInfoHobby.setFocusable(false);
		
		//��ȡ��ǰ��������
		calendar = Calendar.getInstance();
		
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
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

	//��ʾ���ݿ��д洢���û�������Ϣ
	@SuppressWarnings("unused")
	private void showInformation(User user){
		/*
		 * ���ｫuser����Ϣ����ʾ����
		 */
		
		/*
		 * �������˻��ֺͱ�������Ҫ�ڴ�ҳ����ʾ������
		 */
		ModelDaoImp mdi = new ModelDaoImp(db);
		int coinPoint = mdi.getCoinPoint();
		int treasureCount = mdi.getTreasureCount();
		
	}
	
	//������ð�ť���¼���Ӧ
	private class TurnToSettingClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
			UserInformationActivity.this.finish();
			startActivity(intent);
			overridePendingTransition(R.anim.right_top_in, R.anim.left_bottom_out);
		}
	}
	
	//���ѡ��ͷ��ť���¼���Ӧ
	private class SelectIconClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			/*
			 * ������Ҫ�򿪱�����ᣬȻ��ѡ��һ����Ƭ
			 * �߼��ͼ����������Ƭһ��
			 * Ҳ��Ҫ��ԭ��Ƭ����һ�£���ʾ��������һ��
			 */
		}
	}
	
	//ѡ���Ա���¼���Ӧ
	private class SelectGenderListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			if (UserInformationActivity.this.rbtnUserInfoMale.getId() == checkedId) {
				user.setGender(1);
			}
			else if (UserInformationActivity.this.rbtnUserInfoFemale.getId() == checkedId) {
				user.setGender(0);
			}
		}
	}
	
	//ѡ�����հ�ť���¼���Ӧ
	private class SelectBirthClickListener implements android.view.View.OnClickListener{

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			DatePickerDialog dpdlg = new DatePickerDialog(UserInformationActivity.this,
					new DateSelectListener(), mYear, mMonth, mDay);
			dpdlg.getDatePicker().setMaxDate(calendar.getTimeInMillis());
			dpdlg.show();
		}
	}
	
	//���ڿؼ��Ļص�����
	private class DateSelectListener implements OnDateSetListener{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			etUserInfoBirthday.setText(new StringBuilder().append(mYear).append(
					"-"+((mMonth+1) < 10 ? "0"+(mMonth+1) : (mMonth))).append(
					"-"+((mDay < 10) ? "0"+mDay : mDay)));
			
		}
	}
	
	//����������鰴ť���¼���Ӧ
	private class CoinDetailClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(getApplicationContext(), CoinActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.center_in, R.anim.normal_fade_out);
		}
	}
	
	//����������鰴ť���¼���Ӧ
	private class TreasureDetailClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(getApplicationContext(), TreasureActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.center_in, R.anim.normal_fade_out);
		}
	}
	
	//����༭��ť���¼���Ӧ
	private class UserInfoEditClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			TranslateAnimation tAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 10, 
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			tAnimation.setDuration(500);
			tAnimation.setStartOffset(0);
			tAnimation.setFillAfter(true);
			tAnimation.setAnimationListener(new UserInfoEditClickAnimationListener());
			ibtnUserInfoEdit.startAnimation(tAnimation);
			
		}	
	}
	
	//���༭��ť�Ķ�����Ӽ���Ч��
	private class UserInfoEditClickAnimationListener implements AnimationListener{

		@Override
		public void onAnimationStart(Animation animation) {

			TranslateAnimation tAnimationll = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
			tAnimationll.setDuration(500);
			tAnimationll.setStartOffset(0);
			llayoutUerInfoOperation.startAnimation(tAnimationll);
			llayoutUerInfoOperation.setVisibility(View.VISIBLE);
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			ibtnUserInfoEdit.setVisibility(View.GONE);
			ibtnUserInfoEdit.setClickable(false);
			
			ibtnUserInfoIcon.setClickable(true);
			etUserInfoUsername.setFocusableInTouchMode(true);
			rbtnUserInfoMale.setClickable(true);
			rbtnUserInfoFemale.setClickable(true);
			ibtnUserInfoSelectBirth.setClickable(true);
			etUserInfoHobby.setFocusableInTouchMode(true);
			
			btnUserInfoCoinDetail.setClickable(false);
			btnUserInfoTreasureDetail.setClickable(false);
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	//���ȡ����ť���¼���Ӧ
	private class UserInfoCancelClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			UserInformationActivity.this.finish();
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	}
	
	//������水ť���¼���Ӧ
	private class UserInfoSaveClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			/*
			 * ������ӻ�ȡ����ؼ����ݱ��浽user����Ĵ���
			 */
			ModelDaoImp mdi = new ModelDaoImp(db);
			if (mdi.saveUser(user)) {
				Toast.makeText(getApplicationContext(), "save success", 1000).show();
			}else {
				Toast.makeText(getApplicationContext(), "save failed", 1000).show();
			}
			
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	}
}
