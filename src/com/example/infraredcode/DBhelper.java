package com.example.infraredcode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper  
{  
    private static String DATABASENAME = "Remote.db";  //���ݿ�����
    private static int DATABASEVERSION = 2;  
      
  
    /**  
     * (Context context, String name, CursorFactory factory,int version)  
     * @param context �����Ķ���  
     * @param name ���ݿ����� secb.db  
     * @param factory  �α깤��  
     * @param version ���ݿ�汾  
     */  
    public DBhelper(Context context)  
    {  
        super(context, DATABASENAME, null, DATABASEVERSION);  
    }  
      
    /**���ݿ��һ�α�ʹ��ʱ�������ݿ�  
     * @param db �������ݿ��  
     */  
    public void onCreate(SQLiteDatabase db)  
    {  
        //ִ���и�����Ϊ��sql���  
 
        db.execSQL("CREATE Table remote (ID integer primary key autoincrement,NAME varchar(20),TYPE varchar(20),NUM1 BLOB,PAUSE BLOB,LIKE BLOB,SINALSOURCE BLOB,SLEEP BLOB,MENU BLOB,MUTE BLOB,NUM2 BLOB,NUM3 BLOB,NUM4 BLOB,NUM5 BLOB,NUM6 BLOB,NUM7 BLOB,NUM9 BLOB,NUM0 BLOB,OK BLOB,POWER BLOB,PLAY BLOB,PRE BLOB,NEXT BLOB,VOLUME_DOWN BLOB,VOLUME_UP BLOB,UNIQUE(NAME))");  
    }  
  
    /**���ݿ�汾�����ı�ʱ�Żᱻ����,���ݿ�������ʱ�Żᱻ����;  
     * @param db �������ݿ�  
     * @param oldVersion �ɰ汾  
     * @param newVersion �°汾  
     */ 
    protected boolean isNotExist(SQLiteDatabase db,String Codename)//���ͬ����ң�����Ƿ����
    {
    	    boolean result = false ;
    
    	        //��ѯһ��
    		 
    		  Cursor cursor = db.rawQuery( "SELECT * FROM " + "remote" 
    	            , null );
    		  if(cursor.moveToFirst()==false){
    				System.out.println("��һ��");
    			  return true;
    		  };
    		 
    		  do{
    			  System.out.println("�ڶ���");
    			System.out.println(cursor.getString(cursor.getColumnIndex("NAME")));
    	        result = (cursor.getString(cursor.getColumnIndex("NAME")).equals(Codename) );
    	        if(result==true)
    	        {
    	        	break;
    	        	}
    	        
    		  } while(cursor.moveToNext());
    	 return !result;
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  
    {  
        db.execSQL("drop table if exists person");  
        onCreate(db);  
    }  
  
}  
