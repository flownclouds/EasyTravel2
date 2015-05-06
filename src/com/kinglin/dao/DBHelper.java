package com.kinglin.dao;

import com.kinglin.model.Configuration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建用户表,这里要对其他的表进行初始化创建
	    String configuration_table="create table configuration(configurationId long primary key,loginUser text,syncByWifi integer,trackOrNot integer,info text,changed integer)";
	    String user_table="create table user (userId long primary key,gender integer,username text,password text,picture text,birthday text,hobby text,friends text,lastChangeTime long,operation integer)"; 
	    String note_table="create table note(noteId long primary key,time text,permission integer,weather integer,text text,title text,pictures text,voice text,locationx double,locationy double,video text,lastChangeTime long,operation integer)";
	    String moment_table="create table moment(momentId long primary key,userId long,userName text,time text,weather integer,text text,pictures text,voice text,locationx text,locationy text,video text)";
	    String route_table="create table route(time long primary key,locationx double,locationy double)";
	    String treasure_table="create table treasure(treasureId long primary key,time text,content text)";
	    String coin_table="create table coin(coinId long primary key,time text,grade integer,content text)";
	    db.execSQL(configuration_table); 
	    db.execSQL(user_table);  
	    db.execSQL(note_table); 
	    db.execSQL(moment_table); 
	    db.execSQL(route_table); 
	    db.execSQL(treasure_table); 
	    db.execSQL(coin_table); 
		
	    Configuration fconfiguration=new Configuration(1,"default",0,1,null,0);
	    ModelDaoImp mdi = new ModelDaoImp(db);
	    mdi.saveConfiguration(fconfiguration);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
