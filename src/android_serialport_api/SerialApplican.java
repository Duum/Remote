package android_serialport_api;

import infraredCodeSerivce.SerialSerivce.Receivedhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Message;
public class SerialApplican {
	private SerialPort CodePort=null;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	public String Receivelable;
	public String mDataReceived;
	private Receivedhandler myhandler;
	protected ReadThread myReadThread;
	byte ReadFlag1=0;
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
 private class ReadThread extends Thread{
	 public void run()
		  {		
			  while(!interrupted()) {
				  int size;
					try {
							byte[] buffer = new byte[400];
							if (mInputStream == null) return;
							size = mInputStream.read(buffer);
							if (size > 0) {
								sendReadData(onDataReceived(buffer, size));
							}
						}
					catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
	 
 }
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
		myReadThread=new ReadThread();
		myReadThread.start();
		}
	public void ReadStop(){
		myReadThread.interrupt();
		 System.out.println(myReadThread.isInterrupted());
	}	
	public void closeSerialPort() {//关闭串口的操作
		if (CodePort != null) {
			CodePort.close();
			CodePort= null;
		}
	}
 protected byte[] onDataReceived (byte[]bufer_a, int isize)
 {  
	 byte [] hehe=new byte[isize];
	 System.arraycopy(bufer_a, 0,hehe, 0, isize);
	 return hehe;
 }
 
	protected  void sendReadData(byte[] data)
	{
		Message m0 = new Message();
		
    	for(int i=0;i<data.length;i++){
         if (data[i]<0){
        	 data[i]=(byte)(data[i]+256);
         }
    		
   System.out.println(Integer.toHexString(data[i]) );
		  }
	m0.obj=data;//这里里面装的是接收的数据
	m0.what=0x10;//0x10表示接收成功
	myhandler.sendMessage(m0);
	}

	
	
}
