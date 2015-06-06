package infraredCodeSerivce;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.infraredcode.CodeClass;
import com.example.remote.MainActivity;

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
import android_serialport_api.SerialApplican;

public class SerialSerivce extends Service{
	private SerialApplican mySerialApplican;
	private final String tag="SerialPortserice";
	private String label;
	public byte ReadComplete=0;//��ʾ�������
	public byte ReadFlag=0;//readflag��ʾ���յ�fpga���͵���ͷ����
	private Receivedhandler mhandler; 
	private final byte[] Command_receive={(byte)0xAA,(byte)0x04,(byte)0x00,(byte)0x00};
	private final byte[] Command_recivecomplete={(byte)0xAA,(byte)0x88,(byte)0x00,(byte)0x00};
	@Override  
    public void onCreate() {  
        Log.i(tag, "ExampleService-onCreate");  
        super.onCreate(); 
		IntentFilter filter = new IntentFilter();    
		filter.addAction("SEND");
		filter.addAction("RECRIVE");//׼������
		filter.addAction("INTERRUPT");
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
			System.out.println(sValue.length);
			for(int i=0;i<sValue.length;i++)
			{
				System.out.println(Integer.toHexString(sValue[i]) );
			}
				Write(sValue);
		}
		else if(Action.equals("RECRIVE")){
			ReadFlag=0;
			label = intent.getStringExtra("data");//��ȡ���ռ�ֵ
			Write(Command_receive);
			read();
			
						 		}
		else if(Action.equals("INTERRUPT"))
		{
			System.out.println("���ִ���");
			Write(Command_recivecomplete);
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
					if(nee[0]==(byte)0xAA&&nee[1]==(byte)0x08)
					{
						ReadFlag=1;
						Intent intent = new Intent(); 
				    	 intent.setAction("RECRIVE_ENABLE"); 
				    	 sendBroadcast(intent);  
						System.out.println("fpga���ַ�Ӧ");
					}else if(ReadFlag==1)
					{
						   if (CodeClass.isReceiveRight(nee))
						   {
						    System.out.println(label);
						    ReadComplete=1;
						    ReadFlag=0;
						    Write(Command_recivecomplete);
						    stopRead();
						    Intent intent = new Intent(); 
					    	 intent.setAction("RECRIVECOMPELETE"); 
					    	 intent.putExtra("codename", label);
					    	 intent.putExtra("codevalue",nee);
					    	 sendBroadcast(intent);  
						   }else{
							   Intent intent = new Intent(); 
						    	 intent.setAction("RECRIVE_ERROR"); 
						    	 sendBroadcast(intent);  
							
							   Write(Command_receive);

						   }
						    
					}
					break;
				
		
	  }
	}
		
	
}
	 

	 
}
