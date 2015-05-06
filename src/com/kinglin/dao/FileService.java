package com.kinglin.dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

public class FileService {
	private Context context;
	public FileService(Context context) {
		this.context = context;
	}
	
	public void saveToSD(String fname,String fcontent)throws Exception{//�����ı�
		File file=new File(Environment.getExternalStorageDirectory(), fname);//�ļ�Ŀ¼���ļ�����
		FileOutputStream outputStream=new FileOutputStream(file);
		outputStream.write(fcontent.getBytes());
		outputStream.close();
	}
    //�����ļ�
	public void save(String fname,String fcontent) throws Exception{//�����ı�
		//IO j2se
		FileOutputStream outputStream=context.openFileOutput(fname, Context.MODE_PRIVATE);;
		outputStream.write(fcontent.getBytes());
		outputStream.close();
	}
	
	public String read(String filename) throws Exception{//��ȡ�ı�
		FileInputStream inStream=context.openFileInput(filename);
		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int len=0;
		while((len=inStream.read(buffer))!= -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data=outStream.toByteArray();
		return new String(data);	
	}
	
	//���sd��·��
	public String getSDPath(){
		File SDdir=null;
		boolean sdCardExist=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir=Environment.getExternalStorageDirectory();
		}
		if (SDdir!=null) {
			return SDdir.toString();
		}
		else{
			return null;
		}
	}
	
	//ͨ��·������ļ�������׺��
	public String getNameString(String path){
        String b = path.substring(path.lastIndexOf("/") + 1, path.length());
		return b;
	}
	
	/*public String getFileName(String pathandname){  //��·����ȡ�ļ���
        
        int start=pathandname.lastIndexOf("/");  
        int end=pathandname.lastIndexOf(".");  
        if(start!=-1 && end!=-1){  
            return pathandname.substring(start+1,end);    
        }else{  
            return null;  
        }  
          
    }*/  
	
    /*public static String getExtensionName(String filename) {  //��ȡ�ļ���չ��  
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1))) {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }*/ 
	
	//ͨ���ļ��������ļ���
	@SuppressLint("ShowToast")
	public void createSDCardDir(String name){
		if (getSDPath()==null) {
			Toast.makeText(this.context,"SD�������ڻ���д����", 1).show();
		}
		else {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				// ����һ���ļ��ж��󣬸�ֵΪ�ⲿ�洢����Ŀ¼
				File sdcardDir =Environment.getExternalStorageDirectory();
				 //�õ�һ��·����������sdcard���ļ���·��������
				String newPath=sdcardDir.getPath()+name;
                //String newPath=sdcardDir.getPath()+"/savefile/tempImages/";//newPath�ڳ�����Ҫ����
                File imgFile = new File(newPath);
                if (!imgFile.exists()) {
                	   //�������ڣ�����Ŀ¼��������Ӧ��������ʱ�򴴽�
                	imgFile.mkdirs();
                	System.out.println("�����ļ��гɹ���Ŀ¼��"+newPath);
				}
			}
			else {
				System.out.println("�����ļ���ʧ��");
			}
		}
	}
	
	public String saveMyVideo(String oldPath)throws IOException{
		 File sdcardDir =Environment.getExternalStorageDirectory();
		 String newPath=sdcardDir.getPath()+"/easyTravel/savefile/tempVideos/";
		 String aPath=getNameString(oldPath);
		 String dpath=newPath+aPath;
		 copyFile(oldPath,dpath);
		return dpath;
	}
	
	//����·�������ļ�
    @SuppressWarnings("resource")
	public void copyFile(String oldPath, String newPath) {
	    try {   
	        int bytesum = 0;   
	        int byteread = 0;   
	        File oldfile = new File(oldPath);   
	        if (oldfile.exists()) { //�ļ�����ʱ   
	            InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ� 
	            int length = inStream.available();
	            FileOutputStream fs = new FileOutputStream(newPath);   
	            byte[] buffer = new byte[length];  
	            while ( (byteread = inStream.read(buffer)) != -1) {   
	                bytesum += byteread; //�ֽ��� �ļ���С   
	                System.out.println(bytesum);   
	                fs.write(buffer, 0, byteread);   
	            }   
	            inStream.close();   
	        }   
	    }   
	    catch (Exception e) {   
	        System.out.println("���Ƶ����ļ���������");   
	        e.printStackTrace();   
	    }   
	  
	}  

    //����ѹ�����ͼƬ
	public void saveMyImg(int percent, Bitmap bitmap, String newPath, String oldPath)throws IOException{
		 File sdcardDir = Environment.getExternalStorageDirectory();
         newPath = sdcardDir.getPath() + newPath + getNameString(oldPath);
		 File f = new File(newPath);
		 f.createNewFile();
		 FileOutputStream fOut=null;
		 try {
			 fOut=new FileOutputStream(f);
			 bitmap.compress(Bitmap.CompressFormat.JPEG, percent, fOut);
			 fOut.flush();
			 fOut.close();
		  } catch (FileNotFoundException e) {
			e.printStackTrace();
		  }
		   catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  

	}
	
	//����ԭ����δѹ����ͼƬ��ͼƬ����Ϊ����ʱ��+ԭͼ������
	public String saveRealImg(String newPath, String oldPath)throws IOException{
		File sdcardDir =Environment.getExternalStorageDirectory();
		String name = (String) new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA));
		newPath=sdcardDir.getPath() + newPath + name + getNameString(oldPath);//newPath�ڳ�����Ҫ����
		copyFile(oldPath,newPath);
	    return newPath;
	}
	
	//ԭͼƬת��Ϊѹ��ͼƬ
	public Bitmap confessBitmap(Bitmap bitmap){
		
		Bitmap cfsBitmap;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width >= height+10) {
			
			int newWidth = 440;
			float scaleWidth = ((float) newWidth) / width;
			int newHeight=(int)(scaleWidth*height);
			cfsBitmap = Bitmap.createScaledBitmap(bitmap,newWidth , newHeight, false);
		}
		 else {
			int newWidth = 240;
			float scaleWidth = ((float) newWidth) / width;
			int newHeight=(int)(scaleWidth*height); 
			cfsBitmap = Bitmap.createScaledBitmap(bitmap,newWidth , newHeight, false);
		} 
		
		return cfsBitmap;
	}
	
	//����ԭͼƬ·�����ѹ��ͼƬ��·��
	public String getCfsImagePath(String cfsImagePath, String oldPath){
		
		File sdcardDir = Environment.getExternalStorageDirectory();
		cfsImagePath = sdcardDir.getPath() + cfsImagePath + getNameString(oldPath);
		
		return cfsImagePath;
	}
	
	//�������ͼƬ��·��
	public String getPhotoFilePath(){
		String fileName = "/easyTravel/savefile/cameraImages/";
		createSDCardDir(fileName);
	    new DateFormat();
	    //��������Ϊ�ļ�����ȷ��Ψһ��
	    File sdcardDir = Environment.getExternalStorageDirectory();
	    String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg"; 
	    fileName = sdcardDir + fileName + name;
	    return fileName;
	}
	
	private int getFileLen(String path) throws IOException {
	      File dF = new File(path); 
	      FileInputStream fis= new FileInputStream(dF);
	      int fileLen=fis.available();
	      return fileLen;
	}
	
}
