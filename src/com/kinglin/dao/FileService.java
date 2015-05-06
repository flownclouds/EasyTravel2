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
	
	public void saveToSD(String fname,String fcontent)throws Exception{//保存文本
		File file=new File(Environment.getExternalStorageDirectory(), fname);//文件目录，文件名称
		FileOutputStream outputStream=new FileOutputStream(file);
		outputStream.write(fcontent.getBytes());
		outputStream.close();
	}
    //保存文件
	public void save(String fname,String fcontent) throws Exception{//保存文本
		//IO j2se
		FileOutputStream outputStream=context.openFileOutput(fname, Context.MODE_PRIVATE);;
		outputStream.write(fcontent.getBytes());
		outputStream.close();
	}
	
	public String read(String filename) throws Exception{//读取文本
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
	
	//获得sd卡路径
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
	
	//通过路径获得文件名及后缀名
	public String getNameString(String path){
        String b = path.substring(path.lastIndexOf("/") + 1, path.length());
		return b;
	}
	
	/*public String getFileName(String pathandname){  //从路径获取文件名
        
        int start=pathandname.lastIndexOf("/");  
        int end=pathandname.lastIndexOf(".");  
        if(start!=-1 && end!=-1){  
            return pathandname.substring(start+1,end);    
        }else{  
            return null;  
        }  
          
    }*/  
	
    /*public static String getExtensionName(String filename) {  //获取文件拓展名  
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1))) {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }*/ 
	
	//通过文件名创建文件夹
	@SuppressLint("ShowToast")
	public void createSDCardDir(String name){
		if (getSDPath()==null) {
			Toast.makeText(this.context,"SD卡不存在或者写保护", 1).show();
		}
		else {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				// 创建一个文件夹对象，赋值为外部存储器的目录
				File sdcardDir =Environment.getExternalStorageDirectory();
				 //得到一个路径，内容是sdcard的文件夹路径和名字
				String newPath=sdcardDir.getPath()+name;
                //String newPath=sdcardDir.getPath()+"/savefile/tempImages/";//newPath在程序中要声明
                File imgFile = new File(newPath);
                if (!imgFile.exists()) {
                	   //若不存在，创建目录，可以在应用启动的时候创建
                	imgFile.mkdirs();
                	System.out.println("创建文件夹成功，目录："+newPath);
				}
			}
			else {
				System.out.println("创建文件夹失败");
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
	
	//给定路径复制文件
    @SuppressWarnings("resource")
	public void copyFile(String oldPath, String newPath) {
	    try {   
	        int bytesum = 0;   
	        int byteread = 0;   
	        File oldfile = new File(oldPath);   
	        if (oldfile.exists()) { //文件存在时   
	            InputStream inStream = new FileInputStream(oldPath); //读入原文件 
	            int length = inStream.available();
	            FileOutputStream fs = new FileOutputStream(newPath);   
	            byte[] buffer = new byte[length];  
	            while ( (byteread = inStream.read(buffer)) != -1) {   
	                bytesum += byteread; //字节数 文件大小   
	                System.out.println(bytesum);   
	                fs.write(buffer, 0, byteread);   
	            }   
	            inStream.close();   
	        }   
	    }   
	    catch (Exception e) {   
	        System.out.println("复制单个文件操作出错");   
	        e.printStackTrace();   
	    }   
	  
	}  

    //保存压缩后的图片
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
	
	//保存原来的未压缩的图片，图片名称为保存时间+原图的名称
	public String saveRealImg(String newPath, String oldPath)throws IOException{
		File sdcardDir =Environment.getExternalStorageDirectory();
		String name = (String) new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA));
		newPath=sdcardDir.getPath() + newPath + name + getNameString(oldPath);//newPath在程序中要声明
		copyFile(oldPath,newPath);
	    return newPath;
	}
	
	//原图片转换为压缩图片
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
	
	//根据原图片路径获得压缩图片的路径
	public String getCfsImagePath(String cfsImagePath, String oldPath){
		
		File sdcardDir = Environment.getExternalStorageDirectory();
		cfsImagePath = sdcardDir.getPath() + cfsImagePath + getNameString(oldPath);
		
		return cfsImagePath;
	}
	
	//获得拍照图片的路径
	public String getPhotoFilePath(){
		String fileName = "/easyTravel/savefile/cameraImages/";
		createSDCardDir(fileName);
	    new DateFormat();
	    //用日期作为文件名，确保唯一性
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
