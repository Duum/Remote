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


public class CodeClass {  //ע�������������ݿ����
	protected String CodeName;
	protected String[] CodeValuename;//�洢���а�ť������
	protected static byte[] Codeheader={(byte)0xAA,(byte)0x02};//��������ͷ����β
	private DBhelper mHelper;
	protected int mID;
	protected Map<String,byte[]> CodeValue=new HashMap<String,byte[]>();
	private static Context mContext;//����context ��������Ϣ��
	protected CodeClass(Context ctx)
	{
		mContext=ctx;
	}
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map,byte[] header)
   {
	   CodeName=codeName;
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*����ڵĳ����ܴ���CodeClass��codeClassֻ����CodeClassControl����*/
   protected CodeClass(Context ctx,String codeName,Map<String,byte[]> map)
   {
	   CodeName=codeName;
	   CodeValue.putAll(map);
	   mContext=ctx;
   }     /*����ڵĳ����ܴ���CodeClass��codeClassֻ����CodeClassControl����*/

    public void SendData(String Codename)//����Ӧ����ң�ص���ֵͨ�����ڷ���
    {   
    	
    		if(CodeValue.get(Codename)==null)
    				{
    			Toast.makeText(mContext,"���������δѧϰ",
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
  		 intent.putExtra("data", Codename);//д����յ��ֶ�
  		 mContext.sendBroadcast(intent);
  		
  		Toast toast=Toast.makeText( mContext, "��ʼѧϰ"+Codename+"��",
 			     Toast.LENGTH_SHORT);
  		toast.setGravity(Gravity.TOP, 0,100 );
  		toast.show();
    }
    //��������������ֱ����ʵ�֣�����ͷ���ж��Ƿ�Ϊ������ͷ��ȥ��ͷ���������뺯��
    public static byte[] codeMerger(byte[] byte_2){//��ͷ�ϲ�����
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
    	
        return byte_3;  //�����ͷ
        
    }
	 public static boolean isReceiveRight(byte[] data)//�жϽ��յ���ͷ�Ƿ�����
	 {
		int a= ((int)data[1]<< 8)|((int)data[0] & 0xFF);
		System.out.println(data.length);
		System.out.println(a);
		System.out.println("���ݳ���");
		if(data.length==a+8&&data[data.length-1]==(byte)0x0f&&data[data.length-2]==(byte)0xa0)
			{
			
			return true;
			
			}else{
	
			return false;
			}
		
	 }
	 public static byte[] removeHeader(byte[] data)//ȥ����ͷ
	 {
		return Arrays.copyOfRange(data,2,data.length);
	 }

}
