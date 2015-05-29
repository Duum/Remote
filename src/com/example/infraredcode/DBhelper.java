package com.example.infraredcode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper  
{  
    private static String DATABASENAME = "Remote.db";  //数据库名称
    private static int DATABASEVERSION = 2;  
      
  
    /**  
     * (Context context, String name, CursorFactory factory,int version)  
     * @param context 上下文对象  
     * @param name 数据库名称 secb.db  
     * @param factory  游标工厂  
     * @param version 数据库版本  
     */  
    public DBhelper(Context context)  
    {  
        super(context, DATABASENAME, null, DATABASEVERSION);  
    }  
      
    /**数据库第一次被使用时创建数据库  
     * @param db 操作数据库的  
     */  
    public void onCreate(SQLiteDatabase db)  
    {  
        //执行有更新行为的sql语句  
        db.execSQL("CREATE Table remote (ID integer primary key autoincrement,NAME varchar(20),POWER BLOB,PLAY BLOB,PRE BLOB,NEXT BLOB,VOLUME_DOWN BLOB,VOLUME_UP BLOB,UNIQUE(NAME))");  
    }  
  
    /**数据库版本发生改变时才会被调用,数据库在升级时才会被调用;  
     * @param db 操作数据库  
     * @param oldVersion 旧版本  
     * @param newVersion 新版本  
     */ 
    protected boolean isNotExist(SQLiteDatabase db,String Codename)//检查同名的遥控器是否存在
    {
    	    boolean result = false ;
    
    	        //查询一行
    		 
    		  Cursor cursor = db.rawQuery( "SELECT * FROM " + "remote" 
    	            , null );
    		  if(cursor.moveToFirst()==false){
    			  return true;
    		  };
    	        result = (!cursor.getString(cursor.getColumnIndex("NAME")).equals(Codename) );
    	 return result;
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  
    {  
        db.execSQL("drop table if exists person");  
        onCreate(db);  
    }  
  
}  
