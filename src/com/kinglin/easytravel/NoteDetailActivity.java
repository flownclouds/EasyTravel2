package com.kinglin.easytravel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.kinglin.dao.DBHelper;
import com.kinglin.dao.FileService;
import com.kinglin.dao.ModelDaoImp;
import com.kinglin.model.Note;

public class NoteDetailActivity extends Activity implements OnTouchListener,OnGestureListener{

	SQLiteDatabase db;
	
	RelativeLayout rlnoteDetail;
	TextView tvDetailTitle,tvDetailPermission,tvDetailTime,tvDetailContent;
	ImageButton ibtnDetailLeft,ibtnDetailRight;
	ImageView ivDetailWeather;
	LinearLayout llDetailImage,llDetailOther;
	
	GestureDetector mDetailGestureDetector;
	private int minDistance = 100;
	
	List<Note> notes;
	Note noteDetail;
	int position;
	
	Bitmap[] bitmaps = new Bitmap[4];	//����������µ�����ͼƬ
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_note_detail);
	
		initContext();
		
		db = dbInit();
		
		//�����ȡ����ҳ�洫������note����
		notes = (List<Note>) getIntent().getSerializableExtra("notes");
		noteDetail = (Note) getIntent().getSerializableExtra("noteDetail");
		position = (int)getIntent().getSerializableExtra("notePosition");
		
		//��ʾ������ϸ��Ϣ
		showNote(noteDetail);
		
		ibtnDetailLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToPreviousNote();
			}
		});
		
		ibtnDetailRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnToNextNote();
			}
		});
		
		rlnoteDetail.setOnTouchListener(this);
		rlnoteDetail.setLongClickable(true);
	}
	
	@SuppressWarnings("deprecation")
	private void initContext(){
		mDetailGestureDetector = new GestureDetector(this);
		
		rlnoteDetail = (RelativeLayout) findViewById(R.id.rl_notedetail);
		tvDetailTitle = (TextView) findViewById(R.id.tv_detailTitle);
		ibtnDetailLeft = (ImageButton) findViewById(R.id.ibtn_detailLeft);
		ibtnDetailRight = (ImageButton) findViewById(R.id.ibtn_detailRight);
		tvDetailPermission = (TextView) findViewById(R.id.tv_detailPermission);
		ivDetailWeather = (ImageView) findViewById(R.id.iv_detailWeather);
		tvDetailTime = (TextView) findViewById(R.id.tv_detailTime);
		tvDetailContent = (TextView) findViewById(R.id.tv_detailContent);
		llDetailImage = (LinearLayout) findViewById(R.id.ll_detailImage);
		llDetailOther = (LinearLayout) findViewById(R.id.ll_detailOther);
		
	}
	
	//���������ȡ��ǰ�û������ݿ�
	public SQLiteDatabase dbInit() {
		
		//��������default���ݿ⣬��һ��ʹ�û��ʼ������ʵ���Ӧ�ı�
		DBHelper helper=new DBHelper(NoteDetailActivity.this, "default.db", null, 1);
	    SQLiteDatabase defaultdb=helper.getWritableDatabase();
        
        //��ѯdefault���ݿ���Configuration���loginUser,������Ӧ��db
	    ModelDaoImp mdi = new ModelDaoImp(defaultdb);
        String userId = mdi.getUserConfiguration().getLoginUser();
        if (userId.equals("default")) {
			return defaultdb;
		}else {
			DBHelper helper1=new DBHelper(NoteDetailActivity.this, userId+".db", null, 1);
		    SQLiteDatabase userdb=helper1.getWritableDatabase();
		    return userdb;
		}
	}
	
	//������ʾ������ϸ��Ϣ�ĺ���
	public void showNote(Note note){
		tvDetailTitle.setText(note.getTitle());
		tvDetailTime.setText(note.getTime());
		tvDetailContent.setText(note.getText());
		ivDetailWeather.setImageResource(showWeather(note.getWeather()));
		
		if(note.getPermission() == 0){
			tvDetailPermission.setText("public");
		}
		else if(note.getPermission() == 1){
			tvDetailPermission.setText("private");
		}
		
		/*
		 * ��������ݿ��д���û�ͼƬ����Ƶ�Ƚ��д�����ʾ
		 */
		String[] imagePath = new String[4];
		for (int j = 0; j < 4; j++) {
			imagePath[j] = "";
			bitmaps[j] = null;
		}
		
		imagePath = note.getPictures().split(";");
		for (int j = 0; j < imagePath.length; j++) {
			
			if (imagePath[j].equals("") == false) {
				
				File file = new File(imagePath[j]);
				if (file.exists()) {
         		    
					bitmaps[j] = BitmapFactory.decodeFile(imagePath[j]);
					addNewImage(bitmaps[j], j);
					
					bitmaps[j] = getMatchWindowBitmap(bitmaps[j]);
				}
			}
		}
		
	}
	
	//��ͼƬ��ȱ���Ļ��ȳ�����ͼƬѹ���ɿ�Ⱥ���Ļ�����ͬ��ͼƬ
	@SuppressWarnings("deprecation")
	private Bitmap getMatchWindowBitmap(Bitmap bitmap){
		
		WindowManager wm = this.getWindowManager();
	    int width = wm.getDefaultDisplay().getWidth();
	    int height = wm.getDefaultDisplay().getHeight();
	    
	    Bitmap cfsBitmap = null;
		int bmpwidth = bitmap.getWidth();
		int bmpheight = bitmap.getHeight();
		
		if (bmpwidth > width) {
			int newWidth = width;
			float scaleWidth = ((float) newWidth) / bmpwidth;
			int newHeight=(int)(scaleWidth*bmpheight);
			cfsBitmap = Bitmap.createScaledBitmap(bitmap,newWidth , newHeight, false);
		}
		else {
			cfsBitmap = bitmap;
		}
	    
		return cfsBitmap;
	}
	
	/*
	 * ת����һ������һ���������ڲ���ȥ���ݿ�����
	 * 
	 * �Ҵ�shownoteactivity�а�notes��������
	 * 
	 * ����ֱ��notes��get(i)�Ϳ�����
	 */
	
	
	//������һ����������
	public void turnToPreviousNote() {
		
		if (position == 0) {
			Toast.makeText(getApplicationContext(), "This is the first note!", 1000).show();
		}
		else {
			//ModelDaoImp mdi = new ModelDaoImp(db);
			//Note previousNote = mdi.findNoteByPosition(position-1);
			Note previousNote = notes.get(position-1);
			
			Intent intent = new Intent();
			intent.setClass(NoteDetailActivity.this, NoteDetailActivity.class);
			intent.putExtra("notes", (Serializable)notes);
			intent.putExtra("noteDetail", (Serializable)previousNote);
			intent.putExtra("notePosition", position-1);
			NoteDetailActivity.this.finish();
			startActivity(intent);
			overridePendingTransition(R.anim.left_in, R.anim.right_out);	
		}
		
	}
	
	//������һ����������
	private void turnToNextNote() {
		if (position == notes.size()-1) {
			Toast.makeText(getApplicationContext(), "This is the last note!", 1000).show();
		}
		else {
			//ModelDaoImp mdi = new ModelDaoImp(db);
			//Note nextNote = mdi.findNoteByPosition(position+1);
			Note nextNote = notes.get(position+1);
			
			Intent intent = new Intent();
			intent.setClass(NoteDetailActivity.this, NoteDetailActivity.class);
			intent.putExtra("notes", (Serializable)notes);
			intent.putExtra("noteDetail", (Serializable)nextNote);
			intent.putExtra("notePosition", position+1);
			NoteDetailActivity.this.finish();
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
			
	}
	
	//��ʾ�û�֮ǰѡ�������
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
	
	//��̬���һ����ͼƬ
	private void addNewImage(Bitmap bitmap, int num){
		AlphaAnimation animation = new AlphaAnimation(0.0f,1.0f);
		animation.setDuration(500);
		animation.setStartOffset(0);
		
		FrameLayout fLayoutImg = new FrameLayout(NoteDetailActivity.this);
		
		ImageView img = new ImageView(NoteDetailActivity.this);
		img.setImageBitmap(bitmap);
		img.setScaleType(ScaleType.CENTER_CROP);
		img.setOnClickListener(new ViewBigPictureClickListener(num));
		
		fLayoutImg.addView(img);
		fLayoutImg.startAnimation(animation);
		
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(150, 150);
		lParams.setMargins(0, 10, 15, 0);
		
		llDetailImage.addView(fLayoutImg,lParams);
	}
	
	//�鿴��ͼ�¼�����
	private class ViewBigPictureClickListener implements OnClickListener{
		
		int num;    //��ʾ�����ͼƬ�ǵڼ���
		
		public ViewBigPictureClickListener(int num) {
			super();
			this.num = num;
		}

		@Override
		public void onClick(View v) {
			View view = LayoutInflater.from(NoteDetailActivity.this).inflate(R.layout.big_picture, null);
			final PopupWindow popBigPic = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			
			ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
			List<ImageView> views = new ArrayList<ImageView>(); 
			
			for(int i = 0; i < bitmaps.length ; i++){
				if (null != bitmaps[i] && bitmaps[i].getHeight() != 0 && bitmaps[i].getWidth() != 0) {
					ImageView iv = new ImageView(NoteDetailActivity.this);
			        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			        iv.setLayoutParams(lp);
			        iv.setPadding(30, 0, 30, 0);
			        iv.setImageBitmap(bitmaps[i]);
			        views.add(iv);
				}
		    }  
			viewPager.setAdapter(new ViewPagerAdapter(views));
			viewPager.setCurrentItem(num);
			
			popBigPic.setFocusable(true);
			popBigPic.setAnimationStyle(R.style.popwin_bigpic_anim_style);
			
			ColorDrawable cDrawable = new ColorDrawable(0xe0000000);
			popBigPic.setBackgroundDrawable(cDrawable);
			popBigPic.showAtLocation(rlnoteDetail, Gravity.CENTER, 0, 0);
		}
	}
	
	//ViewPager������
	public class ViewPagerAdapter extends PagerAdapter{

		private List<ImageView> views;  
		 
        public ViewPagerAdapter(List<ImageView> views) {
           this.views = views;  
        }
        
        //��ȡ��ǰ���������
		@Override
		public int getCount() {
			return views.size();
		}

		//�ж��Ƿ��ɶ������ɽ���
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
		
		//����һ������������������PagerAdapter������ѡ���ĸ�������ڵ�ǰ��ViewPager��
		@Override
        public Object instantiateItem(View arg0,int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1); 
        }
		
		//�Ǵ�ViewGroup���Ƴ���ǰView
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            ((ViewPager) arg0).removeView(views.get(arg1));  
        }
		
	}
	
	//���һ����¼���Ӧ
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if(e1.getX() - e2.getX() > minDistance){
			turnToNextNote();
		}
		else if (e2.getX() - e1.getX() > minDistance) {
			turnToPreviousNote();
		}
		
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return mDetailGestureDetector.onTouchEvent(event);
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
