package com.example.infraredcode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.widget.Toast;


public class CodeClass {  //注意这个不会对数据库操作
	protected String CodeName;
	protected String[] CodeValuename;//存储所有按钮的名称
	protected static byte[] Codeheader={(byte)0xAA,(byte)0x02};//这里是码头和码尾
	private DBhelper mHelper;
	protected int mID;
	protected Map<String,byte[]> CodeValue=new HashMap<String,byte[]>();
	private static Context mContext;//传入context 帮助发消息用
	protected CodeClass(Context ctx)
	{
		mContext=ctx;
	}
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map,byte[] header)
   {
	   CodeName=codeName;
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*外包内的程序不能创建CodeClass，codeClass只能由CodeClassControl创建*/
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map)
   {
	   CodeName=codeName;
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*外包内的程序不能创建CodeClass，codeClass只能由CodeClassControl创建*/

    public void SendData(String Codename)//将对应红外遥控的码值通过串口发送
    {   
    	
    		if(CodeValue.get(Codename)==null)
    				{
    			Toast.makeText(mContext,"这个按键尚未学习",
       			     Toast.LENGTH_SHORT).show();
       		return;
    				}
       
    		
    	Intent intent = new Intent(); 
    	 intent.setAction("SEND"); 
    	 intent.putExtra("data",codeMerger(CodeValue.get(Codename))); 
    	 mContext.sendBroadcast(intent);  
    }

    public void  CommitDB(){
    	 mHelper = new DBhelper(mContext); 
   	  SQLiteDatabase db = mHelper.getWritableDatabase();
   	 ContentValues cv = new ContentValues();  
   	for (String key : CodeValue.keySet()) {
   		cv.put(key, CodeValue.get(key));
   	}
   //	System.out.println(mHelper.isNotExist(db,CodeName));
    if(mHelper.isNotExist(db,CodeName))
	   {
    	cv.put("NAME",CodeName);
    	db.insert("remote","NAME", cv);
	   }
    else
    {
    	   db.update("remote", cv, "NAME=?",new String[]{ CodeName});
   	  }
    db.close();
    }
    public static void ReceiveData(String Codename)
    {
    	 Intent intent = new Intent();
  		 intent.setAction("RECRIVE");
  		 intent.putExtra("data", Codename);//写入接收的字段
  		 mContext.sendBroadcast(intent);
  		
  		Toast toast=Toast.makeText( mContext, "开始学习"+Codename+"键",
 			     Toast.LENGTH_SHORT);
  		toast.setGravity(Gravity.TOP, 0,100 );
  		toast.show();
    }
    //下面的三个函数分别可以实现，加码头，判断是否为接受码头，去码头，三个编码函数
    public static byte[] codeMerger(byte[] byte_2){//码头合并函数
    	if (byte_2.length==0)
    	{
    		return null;
    		}
    	if (Codeheader.length==0){
    		return null;
    	}
        byte[] byte_3 = new byte[Codeheader.length+byte_2.length];
        System.arraycopy(Codeheader, 0, byte_3, 0, Codeheader.length); 
        System.arraycopy(byte_2, 0, byte_3, Codeheader.length, byte_2.length); 
        for(int i=0;i<byte_3.length;i++){
        if(byte_3[i]<0){
        	byte_3[i]=(byte) (byte_3[i]+256);
           }
        }
    	
        return byte_3;  //添加码头
        
    }
	 public static boolean isReceiveRight(byte[] data)//判断接收的码头是否正常
	 {
		int a= ((int)data[1]<< 8)|((int)data[0] & 0xFF);
		System.out.println(data.length);
		System.out.println(a);
		System.out.println("数据长度");
		if(data.length==a+8&&data[data.length-1]==(byte)0x0f&&data[data.length-2]==(byte)0xa0)
			{
			
			return true;
			
			}else{
	
			return false;
			}
		
	 }
	 public static byte[] removeHeader(byte[] data)//去掉码头
	 {
		return Arrays.copyOfRange(data,2,data.length);
	 }

}
