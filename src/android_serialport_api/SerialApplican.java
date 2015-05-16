package android_serialport_api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Message;
import android_serialport_api.SerialSerivce.Receivedhandler;
public class SerialApplican {
	private SerialPort CodePort=null;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	public String Receivelable;
	public String mDataReceived;
	private Receivedhandler myhandler;
	public byte ReadFlag1;
	 byte[] buffer;
	
	public SerialApplican(Receivedhandler mhandler,File device,int baudrate) throws SecurityException, IOException{
		try {
		CodePort=new SerialPort(device,baudrate,0);
     	} catch (SecurityException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	   } 
		mInputStream=(FileInputStream) CodePort.getInputStream();
		mOutputStream=(FileOutputStream) CodePort.getOutputStream();
		myhandler=mhandler;
		
	}

	public void Write(byte[] data) {
		try {
		mOutputStream.write(data);
		}catch( IOException e){
			e.printStackTrace();	
		}	
	}
	public void Write(int data) {
		try {
		mOutputStream.write(data);
		}catch( IOException e){
			e.printStackTrace();
			
		}
		
	}
	public void read() {
		System.out.println("已经开始读了");
		  new Thread()//这是一个匿名类
		  {
			  @Override
			public void run()
			  {
				  
				  int size=0;
				  while (size == 0) {
					  System.out.println("为什么会空指针呢");
		
						try {
							size = mInputStream.available();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				buffer = new byte[size];
				for(int i=0;i<buffer.length;i++){
					   System.out.println(buffer[i]);
					  }
				try {
					mInputStream.read(buffer);
					Message m0 = new Message();
					m0.obj=buffer;//这里里面装的是接收的数据
					m0.what=0x10;//0x10表示接收成功
					myhandler.sendMessage(m0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		  }.start();
		  
		  
		  
				/*size 为数组的长度*/
			
		}
			
		
	
	public void closeSerialPort() {//关闭串口的操作
		if (CodePort != null) {
			CodePort.close();
			CodePort= null;
		}
	}
	
}
