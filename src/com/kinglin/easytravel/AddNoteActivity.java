package com.kinglin.easytravel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Note;
import com.kinglin.dao.FileService;

@SuppressLint({ "InflateParams", "ShowToast", "ClickableViewAccessibility" })
public class AddNoteActivity extends Activity implements OnTouchListener,OnGestureListener{

	SQLiteDatabase db;
	
	RelativeLayout rlayoutAddNote;
	EditText etAddTitle,etAddContent;
	Spinner spinnerAddPermission;
	ImageButton ibtnAddWeather;
	LinearLayout llayoutAddNewImage,llayoutAddNewOther;
	PopupWindow popViewAdd,popSelectPic;
	TextView tvAddTips;
	
	GestureDetector mAddGestureDetector;		//手势操作
	private int minDistance = 100;			//上滑保存时手势滑动的最短距离
	
	int addPermission = 0;		//记录选择的permission，默认为0，即公开
	int addWeather = 1;		//记录选择的天气，默认为1，即晴
	String photoFilePath = null;
	int pictureNum = 0;
	String picturesPath = "";
	
	class Picture{
		String imagePath;
		int num;
	};
	
	Picture[] pictures = new Picture[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_note);
		
		//对控件初始化，需在下方实现
		initContext();
		
		db = dbInit();
		
		spinnerAddPermission.setOnItemSelectedListener(new PermissionSelectedListener());
		ibtnAddWeather.setOnClickListener(new WeatherBtnClickListener());
		
		//下面是上划动作的响应代码
		rlayoutAddNote.setOnTouchListener(this);
		rlayoutAddNote.setLongClickable(true);
		
	}

	//这里对所有控件findviewbyid
	@SuppressWarnings("deprecation")
	private void initContext() {
		
		mAddGestureDetector = new GestureDetector(this);
		
		rlayoutAddNote = (RelativeLayout) findViewById(R.id.rl_addNote);
		etAddTitle = (EditText) findViewById(R.id.et_addTitle);
		spinnerAddPermission = (Spinner) findViewById(R.id.spinner_addPermission);
		ibtnAddWeather = (ImageButton) findViewById(R.id.ibtn_addWeather);
		
		etAddContent = (EditText) findViewById(R.id.et_addContent);
		tvAddTips = (TextView) findViewById(R.id.tv_addTips);
		llayoutAddNewImage = (LinearLayout) findViewById(R.id.ll_addNewImage);
		llayoutAddNewOther = (LinearLayout) findViewById(R.id.ll_addNewOther);
		
		//设置spinner相关
		//建立数据源
		String[] statusItems = getResources().getStringArray(R.array.status_spinner);
		//建立Adapter并绑定数据源
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//绑定Adapter到控件
		spinnerAddPermission.setAdapter(adapter);
		
		tvAddTips.setText("Tips: 可上滑保存哦！");
		
		initSatelliteMenu();
		
		for (int i = 0; i < 4; i++) {
			pictures[i] = new Picture();
			pictures[i].imagePath = "";
			pictures[i].num = -1;

		}
	}
	
	//这个函数获取当前用户的数据库
	public SQLiteDatabase dbInit() {
		
		//先连接上default数据库，第一次使用会初始化各种实体对应的表
		DBHelper helper=new DBHelper(AddNoteActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //查询default数据库中Configuration表的loginUser,返回相应的db
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(AddNoteActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}

	//选择permission的响应事件
	private class PermissionSelectedListener implements OnItemSelectedListener{
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			addPermission = position;
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
		
	}
	
	//点击选择天气按钮的响应事件
	private class WeatherBtnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(popViewAdd!=null && popViewAdd.isShowing())
			{
				popViewAdd.dismiss();
			}
			else {
				View view = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.popmenu, null);
				popViewAdd = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				ImageView imgSun = (ImageView) view.findViewById(R.id.iv_sun);
				ImageView imgCloud = (ImageView) view.findViewById(R.id.iv_cloud);
				ImageView imgRain = (ImageView) view.findViewById(R.id.iv_rain);
				ImageView imgSnow = (ImageView) view.findViewById(R.id.iv_snow);
				
				imgSun.setOnClickListener(new WeatherClick());
				imgCloud.setOnClickListener(new WeatherClick());
				imgRain.setOnClickListener(new WeatherClick());
				imgSnow.setOnClickListener(new WeatherClick());
				
				popViewAdd.setAnimationStyle(R.style.popwin_anim_style);
				popViewAdd.setFocusable(false);
				popViewAdd.setOutsideTouchable(true);
				popViewAdd.showAsDropDown(v, 0, 0);
			}
		}
		
	}
	
	//选择某天气的响应事件
	private class WeatherClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.iv_sun:
				ibtnAddWeather.setImageResource(R.drawable.ic_sun);
				addWeather = 1;
				break;
			case R.id.iv_cloud:
				ibtnAddWeather.setImageResource(R.drawable.ic_cloud);
				addWeather = 2;
				break;
			case R.id.iv_rain:
				ibtnAddWeather.setImageResource(R.drawable.ic_rain);
				addWeather = 3;
				break;
			case R.id.iv_snow:
				ibtnAddWeather.setImageResource(R.drawable.ic_snow);
				addWeather = 4;
				break;
			default:
				break;
			}
			popViewAdd.dismiss();
			
		}
	}
	
	//点击popupwindow外部取消 
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        if(popViewAdd == null || !popViewAdd.isShowing()) {
        	
            return super.dispatchTouchEvent(ev);  
        }  
        boolean isOut = isOutOfBounds(ev);  
        if(ev.getAction()==MotionEvent.ACTION_DOWN && isOut) {  
            popViewAdd.dismiss();  
            return true;  
        }  
        return false;  
    }  
  
    //是否在popuwindow外部  
    private boolean isOutOfBounds(MotionEvent event) {  
        final int x=(int) event.getX();  
        final int y=(int) event.getY();  
        int slop = ViewConfiguration.get(AddNoteActivity.this).getScaledWindowTouchSlop();  
        View decorView = popViewAdd.getContentView();  
        return (x<-slop)||(y<-slop)  
        ||(x>(decorView.getWidth()+slop))  
        ||(y>(decorView.getHeight()+slop));  
    }  
    
	
	//初始化SatelliteMenu
	private void initSatelliteMenu(){
		//设置SatelliteMenu相关
		SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.sat_menu_add);
		List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(1, R.drawable.ic_video));
		items.add(new SatelliteMenuItem(2, R.drawable.ic_position));
		items.add(new SatelliteMenuItem(3, R.drawable.ic_recorder2));
		items.add(new SatelliteMenuItem(4, R.drawable.ic_camera));
		menu.addItems(items);
		
		menu.setSatelliteDistance(220);
		menu.setMainImage(R.drawable.ic_plus);
		
		menu.setOnItemClickedListener(new SateliteClickedListener() {
			
			@Override
			public void eventOccured(int id) {
				//Log.i("sat", "Clicked on " + id);
				
				switch (id) {
				case 1:
					//etContent.setText("你点击的item的是：视频");
					
					addNewVideo();
					break;
				case 2:
					//etContent.setText("你点击的item的是：位置");
					break;
				case 3:
					//etContent.setText("你点击的item的是：语音");
					break;
				case 4:
					if (pictureNum == 4) {
						Toast.makeText(getApplicationContext(), "You can not add more than 4 pictures", 100).show();
					}
					else {
						selectPicPopupWindow();
					}
					break;
				default:
					break;
				}	
			}
		});
		
	}
	
	//弹出选择照片或拍照的菜单
	private void selectPicPopupWindow(){
		View view = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.popmenu_selectpic, null);
		popSelectPic = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		Button btnTakeAPhoto = (Button) view.findViewById(R.id.btn_takeAPhoto);
		Button btnSelectPicFromAlbum = (Button) view.findViewById(R.id.btn_selectPicFromAlbum);
		Button btnSelectPicCancel = (Button) view.findViewById(R.id.btn_selectPicCancel);
		
		btnTakeAPhoto.setOnClickListener(new TakeAPhotoClickListener());
		btnSelectPicFromAlbum.setOnClickListener(new SelectFromAlbumClickListener());
		btnSelectPicCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popSelectPic.dismiss();
			}
		});
		
		popSelectPic.setFocusable(true);
		popSelectPic.setAnimationStyle(R.style.popwin_select_anim_style);
		ColorDrawable cDrawable = new ColorDrawable(0xd0B0E2FF);
		popSelectPic.setBackgroundDrawable(cDrawable);
		
		popSelectPic.showAtLocation(rlayoutAddNote, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		
	}
	
	//从相册中选择图片
	private class SelectFromAlbumClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(getApplicationContext(), ShowImageGroupActivity.class);
			intent.putExtra("num", 4 - pictureNum);
			startActivityForResult(intent, 1);
			popSelectPic.dismiss();
		}
	}
    
	//拍摄照片
    private class TakeAPhotoClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

			FileService service=new FileService(getApplicationContext());
   		 	photoFilePath = service.getPhotoFilePath();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoFilePath)));
			
			startActivityForResult(intent, 2); 
			popSelectPic.dismiss();
		}
    }
    
    //接收子窗口关闭时传回来的数据
	@SuppressLint({ "ShowToast", "NewApi" }) 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  
		
        if (requestCode == 1) {//从相册中选择
        	
        	if (data != null) {     

            	List<String> pathList = null;
	            Bundle bundle = data.getExtras(); 
	            
	            if (bundle != null) {
	            	pathList = bundle.getStringArrayList("pathList"); // 得到子窗口的回传数据
	            	Toast.makeText(getApplicationContext(), "选中 " + pathList.size() + " 张图片", Toast.LENGTH_LONG).show();
	            }
	            for (int i = 0; i < pathList.size(); i++) {
	        		pictures[pictureNum].imagePath = pathList.get(i);
	     		    pictures[pictureNum].num = pictureNum;
	     		    
	     		    addNewImage(BitmapFactory.decodeFile(pathList.get(i)));
	     		    pictureNum ++;
				}
        	}
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {//拍照图片获取 
    		
   		 	Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath);
   		 	
   		 	pictures[pictureNum].imagePath = photoFilePath;
		    pictures[pictureNum].num = pictureNum;
        	
    		addNewImage(bitmap);
    		pictureNum ++;
        }
        
    }
	
	//动态添加一张新图片,图片在该函数中压缩过
	private void addNewImage(Bitmap bitmap){
		AlphaAnimation animation = new AlphaAnimation(0.0f,1.0f);
		animation.setDuration(500);
		animation.setStartOffset(0);
		
		final FrameLayout fLayoutImg = new FrameLayout(AddNoteActivity.this);
		
		final ImageView img = new ImageView(AddNoteActivity.this);
		FileService fileService = new FileService(getApplicationContext());
		Bitmap cfsBitmap = fileService.confessBitmap(bitmap);
		img.setImageBitmap(cfsBitmap);
		img.setScaleType(ScaleType.CENTER_CROP);
		img.setId(pictureNum);
		fLayoutImg.addView(img);

		ImageView iv_delete = new ImageView(AddNoteActivity.this);
		iv_delete.setImageResource(R.drawable.ic_delete);
		iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlphaAnimation animation_remove = new AlphaAnimation(1.0f,0.0f);
				animation_remove.setDuration(500);
				animation_remove.setStartOffset(0);
				fLayoutImg.startAnimation(animation_remove);
				
				for (int i = 0; i < 4; i++) {
					if (pictures[i].num == img.getId()) {
						for (int j = i; j < 3; j++) {
							pictures[j].imagePath = pictures[j+1].imagePath;
							pictures[j].num = pictures[j+1].num;
						}
						pictures[3].imagePath = null;
						pictures[3].num = -1;
					}
				}
				
				pictureNum --;
				
				llayoutAddNewImage.removeView(fLayoutImg);
				
				if (pictureNum == 0) {
					tvAddTips.setVisibility(View.VISIBLE);
				}
			}
		});
		FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fLayoutImg.addView(iv_delete, fParams);
		fLayoutImg.startAnimation(animation);
		
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(150, 150);
		lParams.setMargins(0, 20, 15, 10);
		llayoutAddNewImage.addView(fLayoutImg,lParams);
		
		tvAddTips.setVisibility(View.GONE);
	}
	
	//动态添加一个新视频
	private void addNewVideo(){
		final FrameLayout fLayoutOther = new FrameLayout(AddNoteActivity.this);
		
		VideoView videoView = new VideoView(AddNoteActivity.this);
		//videoView.setVideoURI(uri);
		fLayoutOther.addView(videoView);
		
		ImageView iv_delete = new ImageView(AddNoteActivity.this);
		iv_delete.setImageResource(R.drawable.ic_delete);
		iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				llayoutAddNewOther.removeView(fLayoutOther);
				
			}
		});
		FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fLayoutOther.addView(iv_delete, fParams);
		
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(150, 150);
		lParams.setMargins(0, 20, 15, 10);
		llayoutAddNewOther.addView(fLayoutOther, lParams);
		
		tvAddTips.setVisibility(View.GONE);
	}
	
	/*//点击返回键的事件响应
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Intent intent = new Intent(getApplicationContext(), ShowNoteActivity.class);
		AddNoteActivity.this.finish();
		startActivity(intent);
		overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
		
		return super.onKeyDown(keyCode, event);
	}*/
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//上滑事件响应
		if(e1.getY() - e2.getY() > minDistance)
		{
			String addContent = etAddContent.getText().toString();
			
			if (addContent.length() == 0) {
				
				View view = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.popwin_alert, null);
				final PopupWindow popwinAlert = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				Button btnAlertYes = (Button) view.findViewById(R.id.btn_alertYes);
				Button btnAlertNo = (Button) view.findViewById(R.id.btn_alertNo);
				
				btnAlertYes.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), ShowNoteActivity.class);
						startActivity(intent);
						AddNoteActivity.this.finish();
						overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
						popwinAlert.dismiss();
					}
				});
				btnAlertNo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						popwinAlert.dismiss();
					}
				});
				
				popwinAlert.setFocusable(true);
				ColorDrawable cDrawable = new ColorDrawable(0xe0ffffff);
				popwinAlert.setBackgroundDrawable(cDrawable);
				popwinAlert.showAtLocation(rlayoutAddNote, Gravity.CENTER, 0, 0);
				
				//弹出框时父窗口颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();  
				lp.alpha = 0.7f;  
				getWindow().setAttributes(lp);
				//窗口消失时父窗口颜色恢复正常
				popwinAlert.setOnDismissListener(new OnDismissListener() {
				    @Override  
				    public void onDismiss() {  
				        WindowManager.LayoutParams lp = getWindow().getAttributes();  
				        lp.alpha = 1f;  
				        getWindow().setAttributes(lp); 
				    }  
				});  
				
			}
			else if(addContent.length() > 100){
				Toast.makeText(getApplicationContext(), "Text should not be more than 100", 1000).show();
			}
			else{
				String addTitle = etAddTitle.getText().toString();
				
				if(addTitle.length() == 0)
				{
					if (addContent.length() < 5) {
						addTitle = addContent;
					}
					else {
						addTitle = addContent.substring(0, 5);
					}
				}
				
				//图片处理
				FileService service=new FileService(getApplicationContext());
			    String fileName = "/easyTravel/savefile/images/";
			    service.createSDCardDir(fileName);
			    
			    String cfsFileName = "/easyTravel/savefile/tempImages/";
			    service.createSDCardDir(cfsFileName);
			    
				for (int i = 0; i < pictureNum; i++) {
					
				    try {
				    	//将原图片保存在/savefile/images文件夹下，并且将图片路径存入数据库
						pictures[i].imagePath = service.saveRealImg(fileName, pictures[i].imagePath);
						picturesPath = picturesPath + pictures[i].imagePath +";";
						
						//将压缩图片保存在/savefile/tempImages文件夹下
						File file = new File(pictures[i].imagePath);
						if (file.exists()) {
							Bitmap bm = BitmapFactory.decodeFile(pictures[i].imagePath);
							Bitmap cfsBitmap = service.confessBitmap(bm);
							service.saveMyImg(100, cfsBitmap, cfsFileName, pictures[i].imagePath);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				    
				}

				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");       
				String time = sDateFormat.format(new java.util.Date());
				
				Note note = new Note();
				note.setNoteId(System.currentTimeMillis());
				note.setTitle(addTitle);
				note.setPermission(addPermission);
				note.setWeather(addWeather);
				note.setText(addContent);
				note.setTime(time);
				note.setPictures(picturesPath);
				
				/*
				 * 这里写获取页面数据的代码，复制给note对象
				 * 
				 * 文字不超过100，图片不超过三张，视频、语音、位置均不超过一个
				 * 
				 * 其中默认天气为晴，权限为public，标题为文字前5个字
				 * 
				 * 对于图片，视频和语音，这里获取的是他们的现有路径
				 * 
				 * 保存的时候（下面的addNote函数）再转存到项目文件夹下
				 * 
				 */
				ModelDaoImp mdi = new ModelDaoImp(db);
				mdi.addNote(note);
				
				Toast.makeText(getApplicationContext(), "添加记事成功", 1000).show();
				
				Intent intent = new Intent(getApplicationContext(), ShowNoteActivity.class);
				AddNoteActivity.this.finish();
				startActivity(intent);
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
			}
			
		}
		
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mAddGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

}
