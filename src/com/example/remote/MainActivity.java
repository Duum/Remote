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
     
	private final String path ="/dev/ttyMFD1";//�洢���ڵĵ�·����ע������Intel�������GPS����	
	private final int baudrate=115200;
	private final byte[] Command_receive={(byte)0xAA,(byte)0x04,(byte)0x00,(byte)0x00};
	private final byte[] Command_recivecomplete={(byte)0xAA,(byte)0x88,(byte)0x00,(byte)0x00};
	//private final byte[] Command_receive={(byte)0x41,(byte)0x41,(byte)0x41,(byte)0x41};
	
	
	private SerialSerivce msgService;  //������ȡʵ��
	private Map<String,byte[]> newCodevalue;//
	
	private Button Study;
	private Button StudyOver;
	private Button DelCode;
	private ImageButton play;
	
	private CodeClassControl MyControl ;//����������
	private Spinner NameSpinner; //����ѡ��
	private int NameSpinId;
	
	//��������Ҫ�������µ�ң�������ƣ�������ò�Ӧ�÷����仯������Ϊfinal
	private String newCodename;//�����洢��ң����������
	
	AlertDialog.Builder builder;
	public  ArrayAdapter<String> NameAdapter;
	public ArrayList<String> allCodeName;//�����洢Ŀǰ�Ѿ����ڵĺ��������
	private int WorkMode=0;//WorkModeΪ����ģʽ0Ϊ����ģʽ��1Ϊѧϰģʽ
	private CodeClass MyCodeClass1;//���CodeClassװȡ���Ǵ洢���յ��м���
	private CodeClass MyCodeClass;//�������࣬����洢�˱���ľ�����ֵ�������CodeClass���з��͵Ĳ���
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		builder = new AlertDialog.Builder(this);//����dialog��builder
		MyControl=new CodeClassControl(this);
		MyCodeClass1=MyControl.GetNECCode();//
		MyCodeClass1.WritetoDB();
		allCodeName=MyControl.getALLClassName();//��ȡ���еĺ���������ƣ�	
		
		
		Study=(Button)findViewById(R.id.study);
		DelCode=(Button)findViewById(R.id.delete);
		play=(ImageButton)findViewById(R.id.play);
		NameSpinner=(Spinner)findViewById(R.id.nameSpin);
		StudyOver=(Button)findViewById(R.id.studyover);
		
		//ע�������Serial����Ϣ������ֻ�и�����Ϣ�����Ż�֪����Ӧ���շ�����
		Intent intent = new Intent(MainActivity.this,SerialSerivce.class);  
		intent.putExtra("SerialPath",path);
		intent.putExtra("baudrate",baudrate);
		startService(intent);
		//������װ��������
		 //Intent intentSerivce = new Intent();  
	      bindService(intent, conn, Context.BIND_AUTO_CREATE);//��Activity��service��  �󶨺�����cnn �� onServiceConnected����
		
		NameAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allCodeName);
		//��������װ��Spinner
		NameSpinner.setAdapter(NameAdapter);
		
		//ΪNameSpnner���ü�������������ѡ������Ҫ������CodeClass
	    NameSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){        	
       public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        	
        	{
    	   System.out.println("ѡ�е���Ŀ��");
    	      NameSpinId=arg2+1;
        	  MyCodeClass=MyControl.GetCodeclass(allCodeName.get(arg2));//�˴���ȡͨ��ID��ȡ�û�ѡ���ң������
        	 arg0.setVisibility(View.VISIBLE);
        	}

		   	@Override
              public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				MyCodeClass=MyControl.GetNECCode();//���ʲô��ûѡ����NEC����
			}
	    });
	  
	    System.out.println(NameSpinId);
	    DelCode.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		if(allCodeName.isEmpty()){
          			Toast.makeText(MainActivity.this, "�����ɾ",
            			     Toast.LENGTH_SHORT).show();
          		}
          		else{
          			new AlertDialog.Builder(MainActivity.this).setTitle("ȷ��Ҫɾ").setIcon(
          	          	     android.R.drawable.ic_dialog_info).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

          	                    public void onClick(DialogInterface dialog, int which) {
				          		MyControl.DelCodeClass(new Integer(NameSpinId));
				          		 allCodeName.remove(NameSpinId-1);
          	                    }}).setNegativeButton("ȡ��", null).show(); 
          		}
          		
          	}});
	
		Study.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		
          		
          		/*�����½�ң������*/
          	
          	 final EditText newName=new EditText(MainActivity.this);
          		new AlertDialog.Builder(MainActivity.this).setTitle("��������ң����������").setIcon(
          	     android.R.drawable.ic_dialog_info).setView(
          	    newName).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    	newCodename=newName.getText().toString();
                    	if(!newCodename.equals("")){
                    		if (allCodeName.indexOf(newCodename)==-1)
                    		{
                    			
                      		Toast.makeText(MainActivity.this, "����ѧϰģʽ������ѡ��Ҫѧϰ�İ���",
                      			     Toast.LENGTH_SHORT).show();
                      		 WorkMode=1;//�л�����ģʽΪѧϰģʽ
                      	
								msgService.Write(Command_receive);
                    		}
                    		else{
                    			Toast.makeText(MainActivity.this, "�������ظ�������������",
                         			     Toast.LENGTH_SHORT).show();
                    			
                    		}
                    	}
						
                      		else{
                      			Toast.makeText(MainActivity.this, "���������Ϊ��,δ����ѧϰģʽ",
                         			     Toast.LENGTH_SHORT).show();
                      		}
                       
                    }}).setNegativeButton("ȡ��", null).show(); 
          		
          		}
          });
		StudyOver.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          	
          		if(WorkMode==1&&msgService.ReadComplete==1)
          		{
          	     WorkMode=0;//�л�����ģʽΪ����ģʽ
          		    msgService.Write(Command_recivecomplete);//Ϊfpgaд������ֹͣ����
                      newCodevalue=new HashMap<String,byte[]>();
                	  newCodevalue=msgService.receiveCodevalue;
                	  MyCodeClass1=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                	  MyCodeClass1.WritetoDB();
                	  allCodeName.add(newCodename);
                	//  NameAdapter.add(newCodename);����Ҫ�úÿ���
                	  Toast.makeText(MainActivity.this, "ѧϰ��ϣ�������ѡ��մ�����ң������",
               			     Toast.LENGTH_SHORT).show();                        	                          	
          		}
          		else{
          			WorkMode=0;
          			Toast.makeText(MainActivity.this, "���ȿ�ʼѧϰ",
                 			 Toast.LENGTH_LONG).show();
          
          		}
                 
                 //ͨ��IBINER��ý��յ�������
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
			    	Toast.makeText(MainActivity.this, "��ʼѧϰPlay��",
              			     Toast.LENGTH_SHORT).show();
			    	 Intent intent = new Intent();
              		 intent.setAction("RECRIVE");
              		 intent.putExtra("data", "PLAY");//д����յ��ֶ�
                     sendBroadcast(intent); //��֪SerialSeivce�������ս���	*/		    	
				}
			}
			
		});						
	}
   ServiceConnection conn = new ServiceConnection() {                
				@Override
				
				public void onServiceConnected(ComponentName name,
						IBinder service) {
					// TODO Auto-generated method stub
					System.out.println("�󶨳ɹ�  ");
				 msgService = ((SerialSerivce.MsgBinder)service).getService();  
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
			}  
  	};
	@Override
	 protected void onDestroy() {/*������ֹͣserivce*/
		 unbindService(conn); 
		MainActivity.this.stopService(new
	   Intent(MainActivity.this,
	   SerialSerivce.class));
	  super.onDestroy();
	
	
	}
	
	
	}


	
