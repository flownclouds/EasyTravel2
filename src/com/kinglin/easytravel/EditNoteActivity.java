package com.kinglin.easytravel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.FileService;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.easytravel.AddNoteActivity.Picture;
import com.kinglin.model.Note;

@SuppressLint({ "InflateParams", "ShowToast" })
public class EditNoteActivity extends Activity {

	SQLiteDatabase db;
	
	RelativeLayout rlayoutEditNote;
	EditText etEditTitle,etEditContent;
	Spinner spinnerEditPermission;
	ImageButton ibtnEditWeather;
	LinearLayout llayoutEditNewImage,llayoutEditNewOther;
	PopupWindow popViewEdit,popSelectPic;
	Button btnEditCancel,btnEditSave;
	
	Note editNote;
	
	int editPermission = 0;		//记录选择的permission，默认为0，即公开
	int editWeather = 1;		//记录选择的天气，默认为1，即晴
	String photoFilePath = null;
	int pictureNum = 0;
	int picNum = 0;
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
		setContentView(R.layout.activity_edit_note);
		
		//对控件初始化，需在下方实现
		initContext();
		
		db = dbInit();
		
		//获取要编辑的记事
		editNote = (Note)getIntent().getSerializableExtra("editNote");
		editWeather = editNote.getWeather();
		
		//将现有的记事内容显示出来
		showNote(editNote);
		
		spinnerEditPermission.setOnItemSelectedListener(new PermissionSelectedListener());
		ibtnEditWeather.setOnClickListener(new WeatherBtnClickListener());
		
		btnEditSave.setOnClickListener(new SaveClickListener());
		
		btnEditCancel.setOnClickListener(new CancelClickListener());
	}



	//这里对所有控件findviewbyid
	private void initContext() {
		
		rlayoutEditNote = (RelativeLayout) findViewById(R.id.rl_editNote);
		etEditTitle = (EditText) findViewById(R.id.et_editTitle);
		spinnerEditPermission = (Spinner) findViewById(R.id.spinner_editPermission);
		ibtnEditWeather = (ImageButton) findViewById(R.id.ibtn_editWeather);
		etEditContent = (EditText) findViewById(R.id.et_editContent);
		llayoutEditNewImage = (LinearLayout) findViewById(R.id.ll_editNewImage);
		llayoutEditNewOther = (LinearLayout) findViewById(R.id.ll_editNewOther);
		btnEditCancel = (Button) findViewById(R.id.btn_editCancel);
		btnEditSave = (Button) findViewById(R.id.btn_editSave);
		
		//设置spinner相关
		//建立数据源
		String[] statusItems = getResources().getStringArray(R.array.status_spinner);
		//建立Adapter并绑定数据源
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//绑定Adapter到控件
		spinnerEditPermission.setAdapter(adapter);
		
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
		DBHelper helper=new DBHelper(EditNoteActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //查询default数据库中Configuration表的loginUser,返回相应的db
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
        String userId = mdi.getUserConfiguration().getLoginUser();
       if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(EditNoteActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}
	
	//显示现有记事的内容
	public void showNote(Note note){
		
		etEditTitle.setText(note.getTitle());
		spinnerEditPermission.setSelection(note.getPermission());
		ibtnEditWeather.setImageResource(showWeather(note.getWeather()));
		etEditContent.setText(note.getText());
		
		String[] imagePath = new String[4];
		for (int j = 0; j < 4; j++) {
			imagePath[j] = "";
		}
		
		imagePath = editNote.getPictures().split(";");
		for (int j = 0; j < imagePath.length; j++) {
			
			if (imagePath[j].equals("") == false) {
				
				File file = new File(imagePath[j]);
				if (file.exists()) {

					pictures[pictureNum].imagePath = imagePath[pictureNum];
         		    pictures[pictureNum].num = pictureNum;
         		    
					Bitmap bitmap = BitmapFactory.decodeFile(imagePath[j]);
					addNewImage(bitmap);
					pictureNum ++;
					picNum ++;
				}
			}
		}
		
		/*
		 * 下面对数据库中存的用户图片、视频等进行处理显示
		 */
	}
	
	//选择permission的响应事件
	private class PermissionSelectedListener implements OnItemSelectedListener{
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			editPermission = position;
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	}
	
	//点击选择天气按钮的响应事件
	private class WeatherBtnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(popViewEdit!=null && popViewEdit.isShowing())
			{
				popViewEdit.dismiss();
			}
			else {
				View view = LayoutInflater.from(EditNoteActivity.this).inflate(R.layout.popmenu, null);
				popViewEdit = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				ImageView imgSun = (ImageView) view.findViewById(R.id.iv_sun);
				ImageView imgCloud = (ImageView) view.findViewById(R.id.iv_cloud);
				ImageView imgRain = (ImageView) view.findViewById(R.id.iv_rain);
				ImageView imgSnow = (ImageView) view.findViewById(R.id.iv_snow);
				
				imgSun.setOnClickListener(new WeatherClick());
				imgCloud.setOnClickListener(new WeatherClick());
				imgRain.setOnClickListener(new WeatherClick());
				imgSnow.setOnClickListener(new WeatherClick());
				
				popViewEdit.setAnimationStyle(R.style.popwin_anim_style);
				popViewEdit.setFocusable(false);
				popViewEdit.setOutsideTouchable(true);
				popViewEdit.showAsDropDown(v, 0, 0);
			}
		}
		
	}
	
	//选择某天气的响应事件
	private class WeatherClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.iv_sun:
				ibtnEditWeather.setImageResource(R.drawable.ic_sun);
				editWeather = 1;
				break;
			case R.id.iv_cloud:
				ibtnEditWeather.setImageResource(R.drawable.ic_cloud);
				editWeather = 2;
				break;
			case R.id.iv_rain:
				ibtnEditWeather.setImageResource(R.drawable.ic_rain);
				editWeather = 3;
				break;
			case R.id.iv_snow:
				ibtnEditWeather.setImageResource(R.drawable.ic_snow);
				editWeather = 4;
				break;
			default:
				break;
			}
			popViewEdit.dismiss();
			
		}
	}
	
	//点击popupwindow外部取消 
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        if(popViewEdit == null || !popViewEdit.isShowing()) {
        	
            return super.dispatchTouchEvent(ev);  
        }  
        boolean isOut = isOutOfBounds(ev);  
        if(ev.getAction()==MotionEvent.ACTION_DOWN && isOut) {  
            popViewEdit.dismiss();  
            return true;  
        }  
        return false;  
    }  
  
    //是否在popuwindow外部  
    private boolean isOutOfBounds(MotionEvent event) {  
        final int x=(int) event.getX();  
        final int y=(int) event.getY();  
        int slop = ViewConfiguration.get(EditNoteActivity.this).getScaledWindowTouchSlop();  
        View decorView = popViewEdit.getContentView();  
        return (x<-slop)||(y<-slop)  
        ||(x>(decorView.getWidth()+slop))  
        ||(y>(decorView.getHeight()+slop));  
    }  
    
	
	//初始化SatelliteMenu
	private void initSatelliteMenu(){
		//设置SatelliteMenu相关
		SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.sat_menu_edit);
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
						Toast.makeText(getApplicationContext(), "You can not add more than 4 pictures", 100);
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
		View view = LayoutInflater.from(EditNoteActivity.this).inflate(R.layout.popmenu_selectpic, null);
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
		
		popSelectPic.showAtLocation(rlayoutEditNote, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		
	}
	
	//获取图片
	private class SelectFromAlbumClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ShowImageGroupActivity.class);
			intent.putExtra("num", 4 - picNum);
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
    
    
	@SuppressLint({ "ShowToast", "NewApi" }) 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  
		
        if (requestCode == 1) {//已存在图片 
        	
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
	
	//动态添加一张新图片
	private void addNewImage(Bitmap bitmap){
		AlphaAnimation animation = new AlphaAnimation(0.0f,1.0f);
		animation.setDuration(500);
		animation.setStartOffset(0);
		
		final FrameLayout fLayoutImg = new FrameLayout(EditNoteActivity.this);
		
		final ImageView img = new ImageView(EditNoteActivity.this);
		FileService fileService = new FileService(getApplicationContext());
		Bitmap cfsBitmap = fileService.confessBitmap(bitmap);
		img.setImageBitmap(cfsBitmap);
		img.setScaleType(ScaleType.CENTER_CROP);
		img.setId(pictureNum);
		fLayoutImg.addView(img);

		ImageView iv_delete = new ImageView(EditNoteActivity.this);
		iv_delete.setImageResource(R.drawable.ic_delete);

		FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fLayoutImg.addView(iv_delete, fParams);
		fLayoutImg.startAnimation(animation);
		iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlphaAnimation animation_remove = new AlphaAnimation(1.0f,0.0f);
				animation_remove.setDuration(500);
				animation_remove.setStartOffset(0);
				fLayoutImg.startAnimation(animation_remove);
				
				for (int i = 0; i < 4; i++) {
					if (pictures[i].num == img.getId()) {
						//获得待删除的图片的路径及其压缩图片的路径
						File file = new File(pictures[i].imagePath);
						FileService fileService = new FileService(getApplicationContext());
						String cfsImagePath = fileService.getCfsImagePath("/easyTravel/savefile/tempImages/", pictures[i].imagePath);
						File cfsFile = new File(cfsImagePath);
						//删除原图片和压缩图片
						if (file.exists() && cfsFile.exists()) {
							file.delete();
							cfsFile.delete();
						}
						//处理数组中的数据
						for (int j = i; j < 3; j++) {
							
							pictures[j].imagePath = pictures[j+1].imagePath;
							pictures[j].num = pictures[j+1].num;
						}
						pictures[3].imagePath = null;
						pictures[3].num = -1;
						break;
					}
				}
				
				pictureNum --;
				llayoutEditNewImage.removeView(fLayoutImg);
				
			}
		});
		
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(150, 150);
		lParams.setMargins(0, 10, 15, 0);
		llayoutEditNewImage.addView(fLayoutImg,lParams);
		
	}

	//动态添加一个新视频
	private void addNewVideo(){
		final FrameLayout fLayoutOther = new FrameLayout(EditNoteActivity.this);
		
		VideoView videoView = new VideoView(EditNoteActivity.this);
		//videoView.setVideoURI(uri);
		fLayoutOther.addView(videoView);
		
		ImageView iv_delete = new ImageView(EditNoteActivity.this);
		iv_delete.setImageResource(R.drawable.ic_delete);
		iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				llayoutEditNewOther.removeView(fLayoutOther);
				
			}
		});
		FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fLayoutOther.addView(iv_delete, fParams);
		
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(150, 150);
		lParams.setMargins(0, 10, 15, 10);
		llayoutEditNewOther.addView(fLayoutOther, lParams);
		
	}
	
	//点击保存按钮的响应事件
	private class SaveClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			//下面是保存的按钮响应,现将页面上信息获取到editnote中
			String editContent = etEditContent.getText().toString();
			if (editContent.length() == 0) {
				Toast.makeText(getApplicationContext(), "Text should not be empty", 1000).show();
			}
			else if (editContent.length() > 100) {
				Toast.makeText(getApplicationContext(), "Text should not be more than 100", 1000).show();
			}
			else {
				String editTitle = etEditTitle.getText().toString();
				
				if (editTitle.length() == 0) {
					if (editContent.length() < 5) {
						editTitle = editContent;
					}
					else {
						editTitle = editContent.substring(0, 5);
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
				
				editNote.setTitle(editTitle);
				editNote.setPermission(editPermission);
				editNote.setWeather(editWeather);
				editNote.setText(editContent);
				editNote.setPictures(picturesPath);
				
				/*
				 * 这里要对图片等其他信息进行保存
				 */
				
				ModelDaoImp mdi = new ModelDaoImp(db);
				mdi.updateNote(editNote);
				Toast.makeText(getApplicationContext(), "修改记事成功", 1000).show();
				
				EditNoteActivity.this.finish();
				overridePendingTransition(0, R.anim.center_out);
				
			}
			
		}
	}
	
	//点击取消按钮的响应事件
	private class CancelClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			EditNoteActivity.this.finish();
			overridePendingTransition(0, R.anim.center_out);
		}
	}
	
	//显示用户之前选择的天气
	private int showWeather(int id){
		switch (id) {
		case 1:
			return R.drawable.ic_sun;
		case 2:
			return R.drawable.ic_cloud;
		case 3:
			return R.drawable.ic_rain;
		case 4:
			return R.drawable.ic_snow;
		default:
			return R.drawable.ic_sun;
		}
	}
	

}
