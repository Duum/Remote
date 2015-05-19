package com.example.infraredcode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;


public class CodeClass {  //注意这个不会对数据库操作
	protected String CodeName;
	protected String[] CodeValuename;//存储所有按钮的名称
	protected static byte[] Codeheader={(byte) 0xAA,0X2};//这里是码头和码尾
	private DBhelper mHelper;
	protected int mID;
	protected Map<String,byte[]> CodeValue=new HashMap<String,byte[]>();
	private Context mContext;//传入context 帮助发消息用
	protected CodeClass(Context ctx)
	{
		mContext=ctx;
	}
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map,byte[] header)
   {
	   CodeName=codeName;
	   init();//防止出项空指针异常
	   CodeValue.putAll(map);
	   Codeheader=header;
	   mContext=ctx;
   }     /*外包内的程序不能创建CodeClass，codeClass只能由CodeClassControl创建*/
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map)
   {
	   CodeName=codeName;
	   init();//防止出项空指针异常
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*外包内的程序不能创建CodeClass，codeClass只能由CodeClassControl创建*/
    public void SendData(String Codename)//将对应红外遥控的码值通过串口发送
    {   
    	Intent intent = new Intent(); 
    	 intent.setAction("SEND"); 
    	 System.out.println("发送的数据是");
    	 System.out.println( CodeValue.get(Codename));
    	 intent.putExtra("data", CodeValue.get(Codename)); 
    	 mContext.sendBroadcast(intent);  
    }

    public void WritetoDB()//职能分划明确只有在这会打开数据库，一般情况下不要打开数据库
    {
    
    	 mHelper = new DBhelper(mContext); 
    	  SQLiteDatabase db = mHelper.getWritableDatabase();
    	  System.out.println("打开数据库");
    	  /*这里写的有问题应该是自适应的*/
    	   if(mHelper.isNotExist(db,CodeName))
    	   {
    	   db.execSQL("insert into remote(NAME,PLAY,PRE,NEXT,VOLUME_UP,VOLUME_DOWN) values(?,?,?,?,?,?)", new Object[]  
    		        { CodeName,codeMerger(CodeValue.get("PLAY")),codeMerger(CodeValue.get("PRE"))
    			   ,codeMerger(CodeValue.get("NEXT")),codeMerger(CodeValue.get("VOLUME_UP")),codeMerger(CodeValue.get("VOLUME_DOWN")) });
           db.close();
    	   }
    }
    private void init()
    {
    	byte[] m=new byte[2];
    	m[0]=-128;
    	m[1]=-128;
    	CodeValue.put("KEY",m);
    	CodeValue.put("PLAY", m);
    	CodeValue.put("PRE",m);
    	CodeValue.put("NEXT",m);
    	CodeValue.put("VOLUME_UP",m);
    	CodeValue.put("VOLUME_DOWN",m);
    }
    
    //下面的三个函数分别可以实现，加码头，判断是否为接受码头，去码头，三个编码函数
    public static byte[] codeMerger(byte[] byte_2){  //码头合并函数
        byte[] byte_3 = new byte[Codeheader.length+byte_2.length];  
        System.arraycopy(Codeheader, 0, byte_3, 0, Codeheader.length);  
        System.arraycopy(byte_2, 0, byte_3, Codeheader.length, byte_2.length);     
        return byte_3;  //添加码头
    }
	 public static boolean isReceiveRight(byte[] data)//判断接收的码头是否正常
	 {
		 int a=data.length;
		 if((a>1)&(data[a-2]==(byte)0xAA)&(data[a-1]==(byte)0x08)){
			 return true;
		 }
		 else{
			 return false;
		 }
		 
	 }
	 public static byte[] removeHeader(byte[] data)//去掉码头
	 {
		return Arrays.copyOfRange(data,2,data.length);
	 }

}
