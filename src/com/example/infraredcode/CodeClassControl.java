package com.example.infraredcode;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
 

public class CodeClassControl {
	//ArrayList CodeList = new ArrayList();
	private DefalutCode mDefaultCode;
    private CodeClass mCodeClass;
    private Context mContext2;
    private DBhelper myDBhelper;
    private ArrayList<String> allName;
	public CodeClassControl(Context ctx){
		mContext2=ctx;
		mDefaultCode=new DefalutCode(mContext2);	
	//获取到所有的遥控器名
	}
	public ArrayList<String> getALLClassName()
	{
		allName=new ArrayList<String>();
		mCodeClass=new CodeClass(mContext2);
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase(); 
		Cursor cursor = db.rawQuery("select NAME from remote ", null);
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
        String name =cursor.getString(cursor.getColumnIndex("NAME"));
        allName.add(name);
        }
		 db.close();
		return allName;
		
	}
	/*read a codeclass instance from DB by a exist codename */
	public CodeClass GetCodeclass(String CodeName){
		//mCodeClass=new CodeClass();
		mCodeClass=new CodeClass(mContext2);
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase();  
		
		 Cursor cursor = db.query("remote", new String[]  
			        {"NAME","PLAY","PRE","NEXT","VOLUME_UP","VOLUME_DOWN" }, "NAME=?", new String[]  
			        { CodeName }, null, null, null); 
	       
try{
cursor.moveToFirst(); 
	//mCodeClass.mID = cursor.getInt(cursor.getColumnIndex("ID"));  
	mCodeClass.CodeName = cursor.getString(cursor.getColumnIndex("NAME")); 
	 //mCodeClass.CodeValue.put("POWER",cursor.getString(cursor.getColumnIndex("POWER")));
	 mCodeClass.CodeValue.put("PLAY",cursor.getBlob(cursor.getColumnIndex("PLAY")));
	 mCodeClass.CodeValue.put("PRE",cursor.getBlob(cursor.getColumnIndex("PRE")));
	 mCodeClass.CodeValue.put("NEXT",cursor.getBlob(cursor.getColumnIndex("NEXT")));
	 mCodeClass.CodeValue.put("VOLUME_UP",cursor.getBlob(cursor.getColumnIndex("VOLUME_UP")));
	 mCodeClass.CodeValue.put("VOLUME_DOWN",cursor.getBlob(cursor.getColumnIndex("VOLUME_DOWN")));}
catch(Exception e){
		
	 }
	 db.close();
	return mCodeClass; 
			                //从数据库中读取CodeName的表
	}
	public CodeClass GetCodeclass(Integer id){//通过ID获得遥控器类。
		mCodeClass=new CodeClass(mContext2);
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase();  
		
		 Cursor cursor = db.query("remote", new String[]  
			        {"NAME","PLAY","PRE","NEXT","VOLUME_UP","VOLUME_DOWN" }, "ID=?", new String[]  
			        { id.toString() }, null, null, null);  
			       
		 try{
		 cursor.moveToFirst(); 
			//mCodeClass.mID = cursor.getInt(cursor.getColumnIndex("ID"));  
			mCodeClass.CodeName = cursor.getString(cursor.getColumnIndex("NAME")); 
			 //mCodeClass.CodeValue.put("POWER",cursor.getString(cursor.getColumnIndex("POWER")));
			 mCodeClass.CodeValue.put("PLAY",cursor.getBlob(cursor.getColumnIndex("PLAY")));
			 mCodeClass.CodeValue.put("PRE",cursor.getBlob(cursor.getColumnIndex("PRE")));
			 mCodeClass.CodeValue.put("NEXT",cursor.getBlob(cursor.getColumnIndex("NEXT")));
			 mCodeClass.CodeValue.put("VOLUME_UP",cursor.getBlob(cursor.getColumnIndex("VOLUME_UP")));
			 mCodeClass.CodeValue.put("VOLUME_DOWN",cursor.getBlob(cursor.getColumnIndex("VOLUME_DOWN")));}
		 catch(Exception e){
				
			 }
			 db.close();
			return mCodeClass;  
			                //从数据库中读取CodeName的表
	}
	public void delCodeclass(Integer id)//删除某一条数据
	{
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase();  
		db.execSQL("delete from remote where ID ="+ id.toString());
		db.execSQL("ALTER TABLE table AUTO_INCREMENT = 1");
		db.close();	
	}

	public CodeClass CreaeCodeClass(String CodeName,Map<String,byte[]> map,byte[] header)
	{
		mCodeClass= new CodeClass(mContext2,CodeName,map,header);
		return mCodeClass;
	}
	public CodeClass CreaeCodeClass(String CodeName,Map<String,byte[]> map)
	{
		mCodeClass= new CodeClass(mContext2,CodeName,map);
		return mCodeClass;
	}
	public CodeClass GetNECCode()
	{
		return mDefaultCode.getNecCode();
	}
	/*delete a codeclass data in  DB by ID */
	public void DelCodeClass (Integer id){
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase();
	        db.execSQL("delete from remote where ID=?", new Object[]  
	        { id.toString() });  
	        db.close();
	        
	}
	/*delete a codeclass data in  DB by codename */
	public void DelCodeClass(String codename)//删除某一条数据
	{
		if(codename==null){
			return;
		}
		myDBhelper=new DBhelper(mContext2);
		SQLiteDatabase db = myDBhelper.getReadableDatabase();  
		db.execSQL("delete from remote where NAME ='"+codename+"'");
		//db.execSQL("ALTER TABLE table AUTO_INCREMENT = 1");
		db.close();	
	}

}
