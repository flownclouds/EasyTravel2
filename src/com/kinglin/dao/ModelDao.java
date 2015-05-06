package com.kinglin.dao;

import java.util.List;

import org.json.JSONObject;

import com.kinglin.model.Coin;
import com.kinglin.model.Configuration;
import com.kinglin.model.Note;
import com.kinglin.model.Route;
import com.kinglin.model.Treasure;
import com.kinglin.model.User;

public interface ModelDao {
	
	Configuration getUserConfiguration();
	
	public void setUserConfiguration(Configuration configuration);
	
	public void saveConfiguration(Configuration configuration);
	
	public void addNote(Note note);
	
	public List<Note> getAllNotes();
	
	public void deleteNote(Note note);
	
	public void updateNote(Note note);
	
	public Note findNoteByPosition(int position);
	
	//传入用户id，到default数据库的user表中查找是否有该用户存在（此时已经在default数据库中），没有返回false并插入，有则返回true
	public boolean hasBeenLogedIn(long userId);
	
	//这里是获得该用户所有数据的id和最后修改时间，并打包成json返回
	public JSONObject getAllDataSnapshot();
	
	//这里是传入新登录的用户的所有数据，需要解析json，然后将数据存到此用户的数据库，并将default数据库中的响应数据做修改，保存成功返回true
	public boolean saveNewUserAllData(JSONObject json_allUserData);
	
	//这里是传入用户本地数据和服务器数据不一样的地方，需要解析json，然后将数据存到此用户的数据库，并将default数据库中的响应数据做修改，成功返回true
	public boolean saveUserChangedData(JSONObject json_userAllChangedData);
	
	//获取用户详细信息
	public User getUserInformation();
	
	//这个函数是查询coin表，并将所有的grade加起来，返回他们的和
	public int getCoinPoint();
	
	//这个函数是查询treasure表，返回条目的个数
	public int getTreasureCount();
	
	//这里是修改用户基本信息时使用，将传入的用户更新信息保存，同时要将operation和lastchangedtime重新赋值
	public boolean saveUser(User user);
	
	//获取积分项列表
	public List<Coin> getAllCoin();
	
	//获取宝藏项列表
	public List<Treasure> getAllTreasure();
	
	//这个需要罗娟同学和林策同学共同完成
	//这是遍历数据库中所有表的所有数据，然后取出那些operation不为0的数据，并打包成json
	public JSONObject getAllChangedData();
	
	//这里需要遍历所有表，然后将operation不为0的全改为0，并将为4的删除
	public void changeAllOperation();
	
	//这里将获得的notes一起存到数据库
	public void saveAllNotes(List<Note> notes);
	
	//这里将获得的routes一起存到数据库
	public void saveAllRoutes(List<Route> routes);
	
	//这里将获得的coins一起存到数据库
	public void saveAllCoins(List<Coin> coins);
	
	//这里将获得的treasures一起存到数据库
	public void saveAllTreasures(List<Treasure> treasures);
}
