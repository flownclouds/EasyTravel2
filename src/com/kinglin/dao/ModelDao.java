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
	
	//�����û�id����default���ݿ��user���в����Ƿ��и��û����ڣ���ʱ�Ѿ���default���ݿ��У���û�з���false�����룬���򷵻�true
	public boolean hasBeenLogedIn(long userId);
	
	//�����ǻ�ø��û��������ݵ�id������޸�ʱ�䣬�������json����
	public JSONObject getAllDataSnapshot();
	
	//�����Ǵ����µ�¼���û����������ݣ���Ҫ����json��Ȼ�����ݴ浽���û������ݿ⣬����default���ݿ��е���Ӧ�������޸ģ�����ɹ�����true
	public boolean saveNewUserAllData(JSONObject json_allUserData);
	
	//�����Ǵ����û��������ݺͷ��������ݲ�һ���ĵط�����Ҫ����json��Ȼ�����ݴ浽���û������ݿ⣬����default���ݿ��е���Ӧ�������޸ģ��ɹ�����true
	public boolean saveUserChangedData(JSONObject json_userAllChangedData);
	
	//��ȡ�û���ϸ��Ϣ
	public User getUserInformation();
	
	//��������ǲ�ѯcoin���������е�grade���������������ǵĺ�
	public int getCoinPoint();
	
	//��������ǲ�ѯtreasure��������Ŀ�ĸ���
	public int getTreasureCount();
	
	//�������޸��û�������Ϣʱʹ�ã���������û�������Ϣ���棬ͬʱҪ��operation��lastchangedtime���¸�ֵ
	public boolean saveUser(User user);
	
	//��ȡ�������б�
	public List<Coin> getAllCoin();
	
	//��ȡ�������б�
	public List<Treasure> getAllTreasure();
	
	//�����Ҫ�޾�ͬѧ���ֲ�ͬѧ��ͬ���
	//���Ǳ������ݿ������б���������ݣ�Ȼ��ȡ����Щoperation��Ϊ0�����ݣ��������json
	public JSONObject getAllChangedData();
	
	//������Ҫ�������б�Ȼ��operation��Ϊ0��ȫ��Ϊ0������Ϊ4��ɾ��
	public void changeAllOperation();
	
	//���ｫ��õ�notesһ��浽���ݿ�
	public void saveAllNotes(List<Note> notes);
	
	//���ｫ��õ�routesһ��浽���ݿ�
	public void saveAllRoutes(List<Route> routes);
	
	//���ｫ��õ�coinsһ��浽���ݿ�
	public void saveAllCoins(List<Coin> coins);
	
	//���ｫ��õ�treasuresһ��浽���ݿ�
	public void saveAllTreasures(List<Treasure> treasures);
}
