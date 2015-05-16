package android_serialport_api;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.example.remote.MainActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
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
	public byte ReadFlag=0;
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
			System.out.println("���յ��㲥");
			byte[] sValue = intent.getByteArrayExtra("data");
			System.out.println("���յ�����");
			System.out.println(sValue[0]);
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
				byte[] nee=(byte[]) msg.obj;
			    receiveCodevalue.put(label,nee);
				ReadFlag=1;
				System.out.println(nee);
				break;
			case 0x11:
		
				break;
			case 0x12:
				
				break;
	
}
}
	
	
}
}
