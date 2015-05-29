package android_serialport_api;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.infraredcode.CodeClass;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SerialSerivce extends Service{
	private SerialApplican mySerialApplican;
	private final String tag="SerialPortserice";
	private String label;
	public byte ReadComplete=0;//��ʾ�������
	public byte ReadFlag=0;//readflag��ʾ���յ�fpga���͵���ͷ����
	private Receivedhandler mhandler; 
	public Map<String,byte[]> receiveCodevalue;
	@Override  
    public void onCreate() {  
        Log.i(tag, "ExampleService-onCreate");  
        super.onCreate(); 
		IntentFilter filter = new IntentFilter();    
		filter.addAction("SEND");
		filter.addAction("RECRIVE");//׼������
		filter.addAction("RECRIVECOMPELETE");//�������
		registerReceiver(ControlReceiver, filter);
		mhandler =new Receivedhandler(Looper.myLooper());	
		
	}
	@Override  
	   public void onStart(Intent intent, int startId) { 
		String device=intent.getStringExtra("SerialPath");
		int baudrate=intent.getIntExtra("baudrate",9600);
		System.out.println(baudrate);
		try {
			mySerialApplican=new SerialApplican(mhandler,new File(device),baudrate);}
 	catch(SecurityException | IOException e){
		
	}
	
	        Log.i(tag, "ExampleService-onStart");  	        
	    } 
	//����һ���㲥���������������Ϣ��������Ϣ������ִ����Ӧ�Ĳ���
	private BroadcastReceiver ControlReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {  
		String Action = intent.getAction();
		if(Action.equals("SEND")){
			byte[] sValue = intent.getByteArrayExtra("data");
			for(int i=0;i<sValue.length;i++)
			{
				System.out.println(Integer.toHexString(sValue[i]) );
			}
				Write(sValue);
		}
		else if(Action.equals("RECRIVE")){
			ReadFlag=0;
			label = intent.getStringExtra("data");//��ȡ���ռ�ֵ
			receiveCodevalue=new HashMap<String,byte[]>();
			read();
			
						 		}
		else if(Action.equals("RECRIVECOMPELETE"))
		{
			
			
		}
		 Log.i(tag,"intent" + intent); 
		
		}
	};
	

	public  void Write(byte[] comand){
	
	
		mySerialApplican.Write(comand);
	}
    private void read(){
    	 mySerialApplican.read();
    }
   	public void stopRead(){
    	 mySerialApplican.ReadStop();
    	
    }
    public void StopAll()
    {
    	
    }
	@Override
	public IBinder onBind(Intent intent) {//���activity��Ҫ�����serivce����ø÷���
		// TODO Auto-generated method stub
		//ע��IBINDER�Ǹ��ӿ�
		return new MsgBinder();  
	}
	public class MsgBinder extends Binder{  
        /** 
         * ��ȡ��ǰService��ʵ�� 
         * @return 
         */  
        public SerialSerivce getService(){  
            return SerialSerivce.this;  
        }  
    }  
	
	 public  class Receivedhandler extends Handler {    //���handler�������߳��л��      
	
	     public Receivedhandler(Looper looper){
	            super(looper);
	     }
		@Override
		public void handleMessage(Message msg) {//���handle������Ļ֪ͨ�ĸ��ºͽ������ݵ��ռ�
			
				switch (msg.what) {
				case 0x10:
					byte[] nee=(byte[])msg.obj;
					if(nee[0]==-86&&nee[1]==2)
					{
						ReadFlag=1;
						System.out.println("fpga���ַ�Ӧ");
					}else if(ReadFlag==1)
					{
						    ReadComplete=0;
						    receiveCodevalue.put(label,nee);
						    ReadComplete=1;
						    Toast.makeText(SerialSerivce.this, label+"��,ѧϰ���",
								     Toast.LENGTH_SHORT).show();	
						    stopRead();
					}
					break;
				case 0x11:
			
					break;
				case 0x12:
					
					break;
		
	  }
	}
	
	
}
	 

	 
}
