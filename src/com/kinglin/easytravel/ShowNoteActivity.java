package com.kinglin.easytravel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.FileService;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Note;
import com.kinglin.serverconnect.ServerConnection;

@SuppressLint({ "ShowToast", "ClickableViewAccessibility" })
public class ShowNoteActivity extends Activity implements OnTouchListener,OnGestureListener{

	SQLiteDatabase db;
	
	RelativeLayout rlShowNote;
	ListView lvTimeLine;
	ImageButton ibtnTurnToAdd;
	
	List<Note> notes;
	GestureDetector mShowGestureDetector;		//手势操作
	private int minDistance = 100;			//上滑保存时手势滑动的最短距离
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_note);
	
		//对控件初始化，需在下方实现
		initContext();
		
		db = dbInit();
		lvTimeLine = (ListView) findViewById(R.id.lv_timeLine);
		//调用函数来获取并显示所有记事
		showNotes();
		
		lvTimeLine.setOnItemClickListener(new mOnItemClickListner());
		registerForContextMenu(this.lvTimeLine);
	
		ibtnTurnToAdd.setOnClickListener(new TurnToAddClickListener());
		//ibtnTurnToAdd.setOnTouchListener(new TurnToAddTouchListener());
		rlShowNote.setOnTouchListener(this);
		rlShowNote.setLongClickable(true);
	}
	


	//这里对所有控件findviewbyid
	@SuppressWarnings("deprecation")
	private void initContext() {
		mShowGestureDetector = new GestureDetector(this);
		
		rlShowNote = (RelativeLayout) findViewById(R.id.rl_showNote);
		lvTimeLine = (ListView) findViewById(R.id.lv_timeLine);
		ibtnTurnToAdd = (ImageButton) findViewById(R.id.ibtn_turn_to_add);
	}
	
	//这个函数获取当前用户的数据库
	public SQLiteDatabase dbInit() {
		
		//先连接上default数据库，第一次使用会初始化各种实体对应的表
		DBHelper helper=new DBHelper(ShowNoteActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //查询default数据库中Configuration表的loginUser,返回相应的db
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(ShowNoteActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}
	
	//返回该界面时调用此函数
	@Override
	protected void onPostResume() {
		super.onPostResume();
		showNotes();
	}
	
	
	//创建上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo){
		
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline_menu_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
        
	}
	
	//给contextMenu的每一个item添加响应事件
	@Override
	public boolean onContextItemSelected(MenuItem item){
		
		//获取点击的哪一项,infor.position代表listView的第几个item
		AdapterContextMenuInfo infor = (AdapterContextMenuInfo)item.getMenuInfo();
		
		Note shareNote = notes.get(infor.position);
		ServerConnection sc = new ServerConnection();
		
        switch(item.getItemId())
        {
        case R.id.delete:
        	
        	new AlertDialog.Builder(ShowNoteActivity.this)
        	.setMessage("Are you sure to delete this note?")
        	.setPositiveButton("Yes", new mDeleteListener(infor.position))
        	.setNegativeButton("Cancel", null)
        	.show();
        	
            return true;
            
        case R.id.edit:
            Note editNote = notes.get(infor.position);
        	
        	Intent editintent = new Intent();
        	editintent.setClass(ShowNoteActivity.this, EditNoteActivity.class);
        	editintent.putExtra("editNote", (Serializable)editNote);
        	startActivity(editintent);
        	overridePendingTransition(R.anim.center_in, R.anim.normal_fade_out);
        	
            return true;
            
        case R.id.qq:
        	
        	if (sc.shareNoteToQQ(shareNote)) {
				Toast.makeText(getApplicationContext(), "share to qq success", 1000).show();
			}else {
				Toast.makeText(getApplicationContext(), "share to qq failed", 1000).show();
			}
            return true;
            
        case R.id.weixin:
        	if (sc.shareNoteToWeixin(shareNote)) {
				Toast.makeText(getApplicationContext(), "share to weixin success", 1000).show();
			}else {
				Toast.makeText(getApplicationContext(), "share to weixin failed", 1000).show();
			}
            return true;
        
        case R.id.weibo:
        	if (sc.shareNoteToWeibo(shareNote)) {
				Toast.makeText(getApplicationContext(), "share to weibo success", 1000).show();
			}else {
				Toast.makeText(getApplicationContext(), "share to weibo failed", 1000).show();
			}
            return true;
            
        case R.id.turn_to_map:
        	
    		Intent mapintent = new Intent(getApplicationContext(), MapActivity.class);
    		mapintent.putExtra("notes", (Serializable)notes);
    		mapintent.putExtra("position", infor.position);
    		startActivity(mapintent);
    		overridePendingTransition(R.anim.right_in, R.anim.left_out);
    		
            return true;
            
        default:
            return super.onContextItemSelected(item);
        }
		
	}
	
	//当上下文菜单关闭时调用的方法
    @Override
    public void onContextMenuClosed(Menu menu) {
        
        super.onContextMenuClosed(menu);
    }
    
	//点击某条记事的事件监听
	private class mOnItemClickListner implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Note detailnote = notes.get(position);
			
			Intent detailintent = new Intent();
			detailintent.setClass(ShowNoteActivity.this, NoteDetailActivity.class);
			detailintent.putExtra("notes", (Serializable)notes);
			detailintent.putExtra("noteDetail", (Serializable)detailnote);
			detailintent.putExtra("notePosition", position);
			startActivity(detailintent);
			overridePendingTransition(R.anim.left_bottom_in, R.anim.normal_fade_out);
			
		}
		
	}
	
	//删除某记事的事件监听
	private class mDeleteListener implements android.content.DialogInterface.OnClickListener{

		int position;
		public mDeleteListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			//下面一段删除该条记事中的所有图片文件
			String[] imagePath = new String[4];
    		for (int j = 0; j < 4; j++) {
				imagePath = null;
			}
    		
    		imagePath = notes.get(position).getPictures().split(";");
    		
    		for (int i = 0; i < imagePath.length; i++) {
    			
    			if (imagePath[i].equals("") == false) {
    				FileService fileService = new FileService(getApplicationContext());
            		String cfsImagePath = fileService.getCfsImagePath("/easyTravel/savefile/tempImages/", imagePath[i]);
            		
    				File file = new File(imagePath[i]);
    				File cfsFile = new File(cfsImagePath);
    				file.delete();
    				cfsFile.delete();
    			}
			}
    		//在数据库中删除
			ModelDaoImp mdi = new ModelDaoImp(db);
			mdi.deleteNote(notes.get(position));
			showNotes();
		}
	}
	
	//适配器，要把里面的内容写到showNote（）函数中去
	@SuppressWarnings("null")
	private void showNotes(){
		
		ModelDaoImp mdi = new ModelDaoImp(db);
		notes = mdi.getAllNotes();
		
		//将notes的内容都显示出来
		String[] times = new String[100];
		Bitmap[] bitmaps = new Bitmap[100];
		String[] textContents = new String[100];
		for (int i = 0; i < 100; i++) {
			times[i] = null;
			bitmaps[i] = null;
			textContents[i] = null;
		}
		
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < notes.size(); i++) {
    		String[] imagePath = new String[4];
    		for (int j = 0; j < 4; j++) {
				imagePath = null;
			}
    		
        	times[i] = notes.get(i).getTime().substring(0, 10);
        	
        	if (notes.get(i).getPictures().equals("") == false) {
        		//将路径分割成多个，存放在imagePath数组中
        		imagePath = notes.get(i).getPictures().split(";");
        		
        		//将每条记事的第一张图片显示出来,这里显示的是压缩的图片
        		FileService fileService = new FileService(getApplicationContext());
        		String cfsImagePath = fileService.getCfsImagePath("/easyTravel/savefile/tempImages/", imagePath[0]);
        		
        		File file = new File(cfsImagePath);
				if (file.exists()) {
					bitmaps[i] = BitmapFactory.decodeFile(cfsImagePath);
					bitmaps[i] = getRoundedCornerBitmap(bitmaps[i]);
					textContents[i] = "";
				}
        	}
        	else {
				textContents[i] = notes.get(i).getText();
			}
        	
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemNoteId", notes.get(i).getNoteId());
            map.put("itemTime", times[i]);
            map.put("itemTitle", notes.get(i).getTitle());
            map.put("itemPermission", notes.get(i).getPermission());
            map.put("itemWeather", notes.get(i).getWeather());
            map.put("itemTextContent", textContents[i]);
            map.put("itemPictures", bitmaps[i]);
            map.put("itemVoice", null);
            map.put("itemVideo", null);
            map.put("itemLocationx", notes.get(i).getLocationx());
            map.put("itemLocationy", notes.get(i).getLocationy());
            map.put("itemLastChangeTime", notes.get(i).getLastChangeTime());
            map.put("itemOperation", notes.get(i).getOperation());
            lstImageItem.add(map);
        }
		
		lvTimeLine.setAdapter(new ListViewAdapter(this, lstImageItem));
        
	}
	
	//分享到其他平台
	public void shareToOther(){
	}
	
	//将图片转换为圆角图片
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap) 
	{
	    //绘制圆角矩形
	    Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), 
	         bitmap.getHeight(), Config.ARGB_8888); 
	     Canvas canvas = new Canvas(roundBitmap); 
	     //int color = Color.parseColor("#000000"); 
	     
	     Random random = new Random();
	     int[] colors = new int[]{
	    	Color.RED, Color.BLUE, Color.GREEN, Color.BLACK, Color.GRAY, 
	    	Color.YELLOW, Color.CYAN, Color.DKGRAY, Color.LTGRAY, Color.MAGENTA,
	     };
	     
	     Paint paint = new Paint(); 
	     Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	     RectF rectF = new RectF(rect); 
	     float roundPx = 30;      //转角设置80
	     //绘制
	     paint.setAntiAlias(true); 
	     canvas.drawARGB(0, 0, 0, 0);
	     
	     BlurMaskFilter bf = new BlurMaskFilter(8,BlurMaskFilter.Blur.INNER);
	     paint.setColor(colors[random.nextInt(9)]); 
	     paint.setMaskFilter(bf);
	     
	     canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	     canvas.drawBitmap(bitmap, rect, rect, paint);
	     //canvas.drawBitmap(bitmap, 0, 0, paint);
	     
	     return roundBitmap;
	}
	
	/*//给加号按钮添加touch事件监听
	private class TurnToAddTouchListener implements View.OnTouchListener{

		int lastX,lastY; 
	        
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			DisplayMetrics dm = getResources().getDisplayMetrics();  
		    final int screenWidth = dm.widthPixels;  
		    final int screenHeight = dm.heightPixels - 50; 
		     
			switch (event.getAction()) {  
	            
				case MotionEvent.ACTION_DOWN:  
	                lastX = (int) event.getRawX();  
	                lastY = (int) event.getRawY();  
	                break;  
	            
				case MotionEvent.ACTION_MOVE:  
	                int dx = (int) event.getRawX() - lastX;  
	                int dy = (int) event.getRawY() - lastY;  
	
	                int left = v.getLeft() + dx;  
	                int top = v.getTop() + dy;  
	                int right = v.getRight() + dx;  
	                int bottom = v.getBottom() + dy;  
	
	                if (left < 20) {  
	                    left = 20;  
	                    right = left + v.getWidth();  
	                }  
	
	                if (right > screenWidth-20) {  
	                    right = screenWidth-20;  
	                    left = right - v.getWidth();  
	                }  
	
	                if (top < 20) {  
	                    top = 20;  
	                    bottom = top + v.getHeight();  
	                }  
	
	                if (bottom > screenHeight-20) {  
	                    bottom = screenHeight-20;  
	                    top = bottom - v.getHeight();  
	                }  
	
	                v.layout(left, top, right, bottom);  
	
	                lastX = (int) event.getRawX();  
	                lastY = (int) event.getRawY();  
	
	                break;  
				case MotionEvent.ACTION_UP:  
	                break;  
            }  
			
			return false;
		}
	}*/
	
	//给加号按钮添加touch事件监听
	private class TurnToAddClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			
			Intent addintent = new Intent();
			addintent.setClass(ShowNoteActivity.this, AddNoteActivity.class);
			ShowNoteActivity.this.finish();
			startActivity(addintent);
			overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
		}
	}
	
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if(e1.getX() - e2.getX() > minDistance)
		{
			//这里是直接右划进入地图模块的响应函数
			Intent mapintent = new Intent(getApplicationContext(), MapActivity.class);
			mapintent.putExtra("notes", (Serializable)notes);
			startActivity(mapintent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
		else if(e2.getX() - e1.getX() > minDistance){
			
			/*从现在的db中取得username，若不是default就跳到用户信息管理界面，
			若为default就跳到登陆的界面*/
			
			
			//这里是直接左划进入个人信息模块的响应函数
			Intent userInforintent = new Intent(getApplicationContext(), UserInformationActivity.class);
			startActivity(userInforintent);
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
		}
		
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mShowGestureDetector.onTouchEvent(event);
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
