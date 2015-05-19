package com.example.remote;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.infraredcode.CodeClass;
import com.example.infraredcode.CodeClassControl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android_serialport_api.SerialSerivce;

public class MainActivity extends Activity {
     
	private final String path ="/dev/ttyMFD1";//存储串口的的路径，注意这是Intel开发板的GPS串口	
	private final int baudrate=115200;
	private final byte[] Command_receive={(byte)0xAA,(byte)0x04,(byte)0x00,(byte)0x00};
	private final byte[] Command_recivecomplete={(byte)0xAA,(byte)0x88,(byte)0x00,(byte)0x00};
	//private final byte[] Command_receive={(byte)0x41,(byte)0x41,(byte)0x41,(byte)0x41};
	
	
	private SerialSerivce msgService;  //下面会获取实例
	private Map<String,byte[]> newCodevalue;//
	
	private Button Study;
	private Button StudyOver;
	private Button DelCode;
	private ImageButton play;
	
	private CodeClassControl MyControl ;//红外编码控制
	private Spinner NameSpinner; //下拉选框
	private int NameSpinId;
	
	//这个输入框被要求输入新的遥控器名称，这个引用不应该发生变化所以设为final
	private String newCodename;//用来存储新遥控器的名称
	
	AlertDialog.Builder builder;
	public  ArrayAdapter<String> NameAdapter;
	public ArrayList<String> allCodeName;//用来存储目前已经存在的红外编码名
	private int WorkMode=0;//WorkMode为工作模式0为发送模式，1为学习模式
	private CodeClass MyCodeClass1;//这个CodeClass装取得是存储接收的中间量
	private CodeClass MyCodeClass;//编码名类，里面存储了编码的具体码值，对这个CodeClass进行发送的操作
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		builder = new AlertDialog.Builder(this);//创建dialog的builder
		MyControl=new CodeClassControl(this);
		MyCodeClass1=MyControl.GetNECCode();//
		MyCodeClass1.WritetoDB();
		allCodeName=MyControl.getALLClassName();//获取所有的红外编码名称；	
		
		
		Study=(Button)findViewById(R.id.study);
		DelCode=(Button)findViewById(R.id.delete);
		play=(ImageButton)findViewById(R.id.play);
		NameSpinner=(Spinner)findViewById(R.id.nameSpin);
		StudyOver=(Button)findViewById(R.id.studyover);
		
		//注意这里的Serial是消息驱动，只有给它消息，它才会知行相应的收发动作
		Intent intent = new Intent(MainActivity.this,SerialSerivce.class);  
		intent.putExtra("SerialPath",path);
		intent.putExtra("baudrate",baudrate);
		startService(intent);
		//将编码装入适配器
		 //Intent intentSerivce = new Intent();  
	      bindService(intent, conn, Context.BIND_AUTO_CREATE);//将Activity与service绑定  绑定后会调用cnn 里 onServiceConnected方法
		
		NameAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allCodeName);
		//将适配器装入Spinner
		NameSpinner.setAdapter(NameAdapter);
		
		//为NameSpnner设置监听器，在这里选出真正要操作的CodeClass
	    NameSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){        	
       public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        	
        	{
    	   System.out.println("选中的条目是");
    	      NameSpinId=arg2+1;
        	  MyCodeClass=MyControl.GetCodeclass(allCodeName.get(arg2));//此处获取通过ID获取用户选择的遥控器类
        	 arg0.setVisibility(View.VISIBLE);
        	}

		   	@Override
              public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				MyCodeClass=MyControl.GetNECCode();//如果什么都没选就是NEC编码
			}
	    });
	  
	    System.out.println(NameSpinId);
	    DelCode.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		if(allCodeName.isEmpty()){
          			Toast.makeText(MainActivity.this, "无码可删",
            			     Toast.LENGTH_SHORT).show();
          		}
          		else{
          			new AlertDialog.Builder(MainActivity.this).setTitle("确定要删").setIcon(
          	          	     android.R.drawable.ic_dialog_info).setPositiveButton("确定", new DialogInterface.OnClickListener() {

          	                    public void onClick(DialogInterface dialog, int which) {
				          		MyControl.DelCodeClass(new Integer(NameSpinId));
				          		 allCodeName.remove(NameSpinId-1);
          	                    }}).setNegativeButton("取消", null).show(); 
          		}
          		
          	}});
	
		Study.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		
          		
          		/*输入新建遥控器名*/
          	
          	 final EditText newName=new EditText(MainActivity.this);
          		new AlertDialog.Builder(MainActivity.this).setTitle("请输入新遥控器的名称").setIcon(
          	     android.R.drawable.ic_dialog_info).setView(
          	    newName).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	newCodename=newName.getText().toString();
                    	if(!newCodename.equals("")){
                    		if (allCodeName.indexOf(newCodename)==-1)
                    		{
                    			
                      		Toast.makeText(MainActivity.this, "开启学习模式，请您选择要学习的按键",
                      			     Toast.LENGTH_SHORT).show();
                      		 WorkMode=1;//切换工作模式为学习模式
                      	
								msgService.Write(Command_receive);
                    		}
                    		else{
                    			Toast.makeText(MainActivity.this, "名称有重复，请重新输入",
                         			     Toast.LENGTH_SHORT).show();
                    			
                    		}
                    	}
						
                      		else{
                      			Toast.makeText(MainActivity.this, "输入的名称为空,未进入学习模式",
                         			     Toast.LENGTH_SHORT).show();
                      		}
                       
                    }}).setNegativeButton("取消", null).show(); 
          		
          		}
          });
		StudyOver.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          	
          		if(WorkMode==1&&msgService.ReadComplete==1)
          		{
          	     WorkMode=0;//切换工作模式为发送模式
          		    msgService.Write(Command_recivecomplete);//为fpga写入命令停止接收
                      newCodevalue=new HashMap<String,byte[]>();
                	  newCodevalue=msgService.receiveCodevalue;
                	  MyCodeClass1=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                	  MyCodeClass1.WritetoDB();
                	  allCodeName.add(newCodename);
                	//  NameAdapter.add(newCodename);这里要好好看看
                	  Toast.makeText(MainActivity.this, "学习完毕，您可以选择刚创建的遥控器了",
               			     Toast.LENGTH_SHORT).show();                        	                          	
          		}
          		else{
          			WorkMode=0;
          			Toast.makeText(MainActivity.this, "请先开始学习",
                 			 Toast.LENGTH_LONG).show();
          
          		}
                 
                 //通过IBINER获得接收到的数据
          	}
          });
        
play.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(WorkMode){
				case 0:
					MyCodeClass.SendData("PLAY");
					break;
			    case 1:
			    	Toast.makeText(MainActivity.this, "开始学习Play键",
              			     Toast.LENGTH_SHORT).show();
			    	 Intent intent = new Intent();
              		 intent.setAction("RECRIVE");
              		 intent.putExtra("data", "PLAY");//写入接收的字段
                     sendBroadcast(intent); //告知SerialSeivce开启接收进程	*/		    	
				}
			}
			
		});						
	}
   ServiceConnection conn = new ServiceConnection() {                
				@Override
				
				public void onServiceConnected(ComponentName name,
						IBinder service) {
					// TODO Auto-generated method stub
					System.out.println("绑定成功  ");
				 msgService = ((SerialSerivce.MsgBinder)service).getService();  
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
			}  
  	};
	@Override
	 protected void onDestroy() {/*在这里停止serivce*/
		 unbindService(conn); 
		MainActivity.this.stopService(new
	   Intent(MainActivity.this,
	   SerialSerivce.class));
	  super.onDestroy();
	
	
	}
	
	
	}


	
