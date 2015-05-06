package com.kinglin.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinglin.model.Coin;
import com.kinglin.model.Configuration;
import com.kinglin.model.Note;
import com.kinglin.model.Route;
import com.kinglin.model.Treasure;
import com.kinglin.model.User;

public class ModelDaoImp implements ModelDao {

	SQLiteDatabase db;
	
	public ModelDaoImp(SQLiteDatabase db) {
		this.db = db;
	}

	//����û��������ļ�
	public Configuration getUserConfiguration() {//��õ�ǰ��������Ϣ����Configuration���󷵻�

		Configuration configuration = null;
		Cursor cursor=db.rawQuery("select * from configuration where configurationId=?",new String[]{"1"});//Ĭ��Configuration��Ψһ��¼��configurationIdΪ1�����Ը�
		if (cursor.moveToFirst()) {
			long configurationId=cursor.getLong(cursor.getColumnIndex("configurationId"));
			String loginUser=cursor.getString(cursor.getColumnIndex("loginUser"));
			int syncByWifi=cursor.getInt(cursor.getColumnIndex("syncByWifi"));
			int trackOrNot=cursor.getInt(cursor.getColumnIndex("trackOrNot"));
			String info=cursor.getString(cursor.getColumnIndex("info"));
			int changed = cursor.getInt(cursor.getColumnIndex("changed"));
			configuration = new Configuration(configurationId, loginUser,syncByWifi,trackOrNot,info,changed);
		}
		cursor.close();
	    return configuration;
	}

	@Override
	public void setUserConfiguration(Configuration configuration) {//�������configuration���󱣴浽��
		db.execSQL("update configuration set loginUser=?,syncByWifi=?,trackOrNot=?,info=?,changed=? where configurationId=?",
				new Object[]{configuration.getLoginUser(),configuration.getSyncByWifi(),configuration.getTrackOrNot(),configuration.getInfo(),configuration.getChanged(),configuration.getConfigurationId()});
	}
	
	public void saveConfiguration(Configuration configuration){
		db.execSQL("insert into configuration(configurationId,loginUser,syncByWifi,trackOrNot,info,changed) values(?,?,?,?,?,?)",
				new Object[]{configuration.getConfigurationId(),
				configuration.getLoginUser(),configuration.getSyncByWifi(),
				configuration.getTrackOrNot(),configuration.getInfo(),configuration.getChanged()});
	}

	@Override
	public void addNote(Note note) {//���һ�����¼�¼
		db.execSQL("insert into note(noteId,time,permission,weather,text,title,pictures,voice,locationx,locationy,video,lastChangeTime,operation) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[]{note.getNoteId(),note.getTime(),note.getPermission(),note.getWeather(),note.getText(),note.getTitle(),note.getPictures(),note.getVoice(),note.getLocationx(),note.getLocationy(),note.getVideo(),note.getLastChangeTime(),note.getOperation()});
		
	}

	@Override
	public List<Note> getAllNotes() {//��ѯ���м�¼
		List<Note> notes=new ArrayList<Note>();
		Cursor cursor=db.rawQuery("select * from note order by noteId asc",null);
		while (cursor.moveToNext()) {
			long noteId=cursor.getLong(cursor.getColumnIndex("noteId"));
			String time=cursor.getString(cursor.getColumnIndex("time"));
			int permission=cursor.getInt(cursor.getColumnIndex("permission"));
			int weather=cursor.getInt(cursor.getColumnIndex("weather"));
			String text=cursor.getString(cursor.getColumnIndex("text"));
			String title=cursor.getString(cursor.getColumnIndex("title"));
			String pictures=cursor.getString(cursor.getColumnIndex("pictures"));
			String voice=cursor.getString(cursor.getColumnIndex("voice"));
			double locationx=cursor.getDouble(cursor.getColumnIndex("locationx"));
			double locationy=cursor.getDouble(cursor.getColumnIndex("locationy"));
			String video=cursor.getString(cursor.getColumnIndex("video"));
			long lastChangeTime=cursor.getLong(cursor.getColumnIndex("lastChangeTime"));
			int operation=cursor.getInt(cursor.getColumnIndex("operation"));
			notes.add(new Note(noteId,time,permission,weather,text,title,pictures,voice,locationx,locationy,video,lastChangeTime,operation));
		}
		cursor.close();
		return notes;
	}

	@Override
	public void deleteNote(Note note) {//���ݴ����note��noteIdɾ����¼
		db.execSQL("delete from note where noteId=?",
				new Object[]{note.getNoteId()});
	}

	@Override
	public void updateNote(Note note) {//���ݴ����note��noteId���ļ�¼
		db.execSQL("update note set time=?,permission=?,weather=?,text=?,pictures=?,voice=?,locationx=?,locationy=?,video=?,lastChangeTime=?,operation=? where noteId=?",
				new Object[]{note.getTime(),note.getPermission(),note.getWeather(),note.getText(),note.getPictures(),note.getVoice(),note.getLocationx(),note.getLocationy(),note.getVideo(),note.getLastChangeTime(),note.getOperation(),note.getNoteId()});
	}


	@Override
	public Note findNoteByPosition(int position) {
		int offset=position-1;
		List<Note> notes=new ArrayList<Note>();
		Cursor cursor=db.rawQuery("select * from note order by noteId asc limit ?,?",
				 new String[]{String.valueOf(offset),"1"});
		while (cursor.moveToNext()) {
			long noteId=cursor.getLong(cursor.getColumnIndex("noteId"));
			String time=cursor.getString(cursor.getColumnIndex("time"));
			int permission=cursor.getInt(cursor.getColumnIndex("permission"));
			int weather=cursor.getInt(cursor.getColumnIndex("weather"));
			String text=cursor.getString(cursor.getColumnIndex("text"));
			String title=cursor.getString(cursor.getColumnIndex("title"));
			String pictures=cursor.getString(cursor.getColumnIndex("pictures"));
			String voice=cursor.getString(cursor.getColumnIndex("voice"));
			double locationx=cursor.getDouble(cursor.getColumnIndex("locationx"));
			double locationy=cursor.getDouble(cursor.getColumnIndex("locationy"));
			String video=cursor.getString(cursor.getColumnIndex("video"));
			long lastChangeTime=cursor.getLong(cursor.getColumnIndex("lastChangeTime"));
			int operation=cursor.getInt(cursor.getColumnIndex("operation"));
			notes.add(new Note(noteId,time,permission,weather,text,title,pictures,voice,locationx,locationy,video,lastChangeTime,operation));
		}
		cursor.close();
		return notes.get(0);
	}

	//�����û�id����default���ݿ��user���в����Ƿ��и��û����ڣ���ʱ�Ѿ���default���ݿ��У���û�з���false�����룬���򷵻�true
	public boolean hasBeenLogedIn(long userId) {
		return false;
	}

	//���������Ҫ�޾�ͬѧ���ֲ�ͬѧ��ͬ���
	//�����ǻ�ø��û��������ݵ�id������޸�ʱ�䣬�������json����
	@Override
	public JSONObject getAllDataSnapshot() {
		return null;
	}

	//���������Ҫ�޾�ͬѧ���ֲ�ͬѧ��ͬ���
	//�����Ǵ����µ�¼���û����������ݣ���Ҫ����json��Ȼ�����ݴ浽���ݿ⣬����ɹ�����true
	@Override
	public boolean saveNewUserAllData(JSONObject json_allUserData) {
		try {
			if (json_allUserData.getString("getResult").equals("yes")) {
				
				//�����ȡuser��Ϣ������
				
				//�����ȡconfiguration������
				
				//�����ȡnotes������
				
				//�����ȡroutes������
				
				//�����ȡcoins������
				
				//�����ȡtreasures������
				
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public User getUserInformation() {
		return null;
	}

	
	//���������Ҫ�޾�ͬѧ���ֲ�ͬѧ��ͬ���
	//�����Ǵ����û��������ݺͷ��������ݲ�һ���ĵط�����Ҫ����json��Ȼ�󱣴棬�ɹ�����true
	public boolean saveUserChangedData(JSONObject json_userAllChangedData) {
		return false;
	}

	@Override
	public int getCoinPoint() {
		return 0;
	}

	@Override
	public int getTreasureCount() {
		return 0;
	}

	@Override
	public boolean saveUser(User user) {
		return false;
	}

	@Override
	public List<Coin> getAllCoin() {
		return null;
	}

	@Override
	public List<Treasure> getAllTreasure() {
		return null;
	}

	@Override
	public JSONObject getAllChangedData() {
		return null;
	}

	@Override
	public void changeAllOperation() {
		
	}

	@Override
	public void saveAllNotes(List<Note> notes) {
	}

	@Override
	public void saveAllRoutes(List<Route> routes) {
	}

	@Override
	public void saveAllCoins(List<Coin> coins) {
		
	}

	@Override
	public void saveAllTreasures(List<Treasure> treasures) {
		
	}

}
