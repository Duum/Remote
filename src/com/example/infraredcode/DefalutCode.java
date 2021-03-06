package com.example.infraredcode;

import android.content.Context;

/*这里存储预置的编码*/
public class DefalutCode 
{
	private Context mContext1;
	protected DefalutCode(Context ctx){
		mContext1=ctx;
		
	}
public CodeClass getNecCode(){
	return new NecCode(mContext1);
}	
	



/*这里是默认存储的NEC编码类型*/
 public class NecCode extends CodeClass{
     private int []hifi_vup ={-106, 0,//packet length 150
    		               13, 2, //carrier T  525
    		               49, 0, //carrier high T 49
    		               85, 1, -84, 0,//NEC的码头
    		              
    		               21, 0, 23, 0,//1 address 
    		              
    		               21, 0, 22, 0,//2
    		               
    		               22, 0, 22, 0,//3
    		               
    		               21, 0, 23, 0,//4
    		             
    		               21, 0, 22, 0,//5
    		              
    		               22, 0, 22, 0,//6
    		              
    		               21, 0,23, 0,//7
    		               
    		               21, 0, 21, 0,//8
    		               
    		              
    		               22, 0, 65, 0,//1
    		            
    		               21, 0, 65, 0,//2
    		               
    		               21, 0, 65, 0,//3
    		              
    		               22, 0,  65, 0,//4
    		             
    		               21, 0, 65, 0,  //5
    		              
    		               21, 0,  65, 0,//6
    		              
    		               22, 0,   65, 0, //7
    		             
    		               21, 0,   65, 0,//8
    		            
    		               21, 0,   65, 0,//commad1
    		             
    		               22, 0, 22, 0, //2
    		    
    		               21, 0, 23, 0,//3
    		               
    		               21, 0,   65, 0,//4
    		            
    		               21, 0, 23, 0,//5
    		              
    		               21, 0,  22, 0,//6
    		             
    		               22, 0,     22, 0,//7
    		          
    		               21, 0,  23, 0,//8
    		             
    		               
    		               21, 0, 22, 0,//1
    		              
    		               22, 0,65, 0,//2
    		                
    		               21, 0,65, 0,//3
    		               
    		               21, 0, 23, 0, //4
    		               21, 0, 65, 0, //5
    		               21, 0, 65, 0, //6
    		               22, 0, 65, 0, //7
    		               21, 0, 65, 0,//8
    		               
    		               21,0, -17, 5,//1
    		               85, 1, 87, 0, //2
    		               21, 0, 72, 14,//3
    		               85, 1, 87, 0,//4
    		               21, 0, -96, 15//5
    		               };
	private int []hifi_vdn ={-106, 0, 12, 2, 49, 0, 85, 1, -83, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 21, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, -14, 5, 85, 1, 87, 0, 21, 0, 78, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	private int []hifi_mute={-106, 0, 13, 2, 49, 0, 85, 1, -84, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, -17, 5, 85, 1, 87, 0, 21, 0, 71, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	private int []hifi_next={-106, 0, 13, 2, 49, 0, 85, 1, -84, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, -17, 5, 85, 1, 87, 0, 21, 0, 71, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	private int []hifi_play={-106, 0, 12, 2, 49, 0, 85, 1, -83, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 21, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 21, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, -15, 5, 85, 1, 87, 0, 21, 0, 79, 14, 85, 1, 88, 0, 21, 0, -96, 15};
	private int []hifi_prev={-106, 0, 13, 2, 49, 0, 85, 1, -84, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, -17, 5, 85, 1, 87, 0, 21, 0, 72, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	private int []hifi_cli1={-106, 0, 12, 2, 49, 0, 85, 1, -83, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 66, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, 66, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 65, 0, 21, 0, -14, 5, 85, 1, 88, 0, 21, 0, 79, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	private int []hifi_cli2={-106, 0, 13, 2, 49, 0, 85, 1, -84, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 22, 0, 21, 0, 23, 0, 21, 0, 22, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, 65, 0, 22, 0, 65, 0, 21, 0, 65, 0, 21, 0, -18, 5, 85, 1, 87, 0, 21, 0, 72, 14, 85, 1, 87, 0, 21, 0, -96, 15};
	
	public NecCode(Context ctx1)
	{
		   super(ctx1);
			
			CodeName="NEC";
			//CodeValue=new HashMap<String, String>();
		
			CodeValue.put("VOLUME_UP",converse(hifi_vup));
			CodeValue.put("VOLUME_DOWN",converse(hifi_vdn));
			CodeValue.put("MUTE",converse(hifi_mute));
			CodeValue.put("NEXT",converse(hifi_next));
			CodeValue.put("PLAY",converse(hifi_play));
			CodeValue.put("PRE",converse(hifi_prev));
			CodeValue.put("NUM1",converse(hifi_cli1));
			CodeValue.put("NUM2",converse(hifi_cli2));
	}
	
	
	public byte[] converse(int[]data)
	{
	   byte[] buffer1=new byte[158];

		 for(int i=0;i<158;i++)
         {
             	if(data[i]<0)
             	{
             		buffer1[i]=(byte)(256+data[i]);//byte第一位是符号位
             	}
             	else
             		buffer1[i]=(byte) (data[i]);
             	            	
          }
		 return buffer1;
	}
}	

}

