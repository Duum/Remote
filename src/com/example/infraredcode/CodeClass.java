package com.example.infraredcode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;


public class CodeClass {  //ע�������������ݿ����
	protected String CodeName;
	protected String[] CodeValuename;//�洢���а�ť������
	protected static byte[] Codeheader={(byte) 0xAA,0X2};//��������ͷ����β
	private DBhelper mHelper;
	protected int mID;
	protected Map<String,byte[]> CodeValue=new HashMap<String,byte[]>();
	private Context mContext;//����context ��������Ϣ��
	protected CodeClass(Context ctx)
	{
		mContext=ctx;
	}
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map,byte[] header)
   {
	   CodeName=codeName;
	   init();//��ֹ�����ָ���쳣
	   CodeValue.putAll(map);
	   Codeheader=header;
	   mContext=ctx;
   }     /*����ڵĳ����ܴ���CodeClass��codeClassֻ����CodeClassControl����*/
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map)
   {
	   CodeName=codeName;
	   init();//��ֹ�����ָ���쳣
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*����ڵĳ����ܴ���CodeClass��codeClassֻ����CodeClassControl����*/
    public void SendData(String Codename)//����Ӧ����ң�ص���ֵͨ�����ڷ���
    {   
    	Intent intent = new Intent(); 
    	 intent.setAction("SEND"); 
    	 System.out.println("���͵�������");
    	 System.out.println( CodeValue.get(Codename));
    	 intent.putExtra("data", CodeValue.get(Codename)); 
    	 mContext.sendBroadcast(intent);  
    }

    public void WritetoDB()//ְ�ֻܷ���ȷֻ�����������ݿ⣬һ������²�Ҫ�����ݿ�
    {
    
    	 mHelper = new DBhelper(mContext); 
    	  SQLiteDatabase db = mHelper.getWritableDatabase();
    	  System.out.println("�����ݿ�");
    	  /*����д��������Ӧ��������Ӧ��*/
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
    
    //��������������ֱ����ʵ�֣�����ͷ���ж��Ƿ�Ϊ������ͷ��ȥ��ͷ���������뺯��
    public static byte[] codeMerger(byte[] byte_2){  //��ͷ�ϲ�����
        byte[] byte_3 = new byte[Codeheader.length+byte_2.length];  
        System.arraycopy(Codeheader, 0, byte_3, 0, Codeheader.length);  
        System.arraycopy(byte_2, 0, byte_3, Codeheader.length, byte_2.length);     
        return byte_3;  //�����ͷ
    }
	 public static boolean isReceiveRight(byte[] data)//�жϽ��յ���ͷ�Ƿ�����
	 {
		 int a=data.length;
		 if((a>1)&(data[a-2]==(byte)0xAA)&(data[a-1]==(byte)0x08)){
			 return true;
		 }
		 else{
			 return false;
		 }
		 
	 }
	 public static byte[] removeHeader(byte[] data)//ȥ����ͷ
	 {
		return Arrays.copyOfRange(data,2,data.length);
	 }

}
