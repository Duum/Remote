package com.example.remote;
import infraredCodeSerivce.SerialSerivce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.infraredcode.CodeClass;
import com.example.infraredcode.CodeClassControl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("UseValueOf")
public class MainActivity extends Activity {
     
	
	/*�����Ҫ�����µļ���
	 * public void onClick(View v) {
				// TODO Auto-generated method stub
			if(WorkMode==0){
				
					MyCodeClass.SendData("VOLUME_UP");
			}else
			{
			    	CodeClass.ReceiveData("VOLUME_UP");    	
			    
				}
		ע��Ŀǰ���ݿ�֧�����漸����ť
	 * ��������µİ�ť�����ͽ��յ�ֵһ��Ҫ������ѡȡ
   {"NUM1"�����ּ���,"PAUSE","LIKE"��ϲ���� ,"SINALSOURCE" ,"SLEEP"��˯�ߣ�,
   "MENU","MUTE","NUM2","NUM3","NUM4","NUM5","NUM6","NUM7","NUM9","NUM0",
   "OK","POWER"����Դ����,"PLAY","PRE","NEXT","VOLUME_DOWN","VOLUME_UP" };
	*�������Ҫ���ӵļ�û������������� codeClassControl�е�String[] allCodeName���뼴��
		*/
	private final String path ="/dev/ttyMFD1";//�洢���ڵĵ�·����ע������Intel�������GPS����	
	private final int baudrate=115200;
	private Button Study;
	private Button Edit;
	private Button StudyOver;
	private Button DelCode;
	private ImageButton play;
	private ImageButton pre;
	private ImageButton next;
	private ImageButton volume_up;
	private ImageButton volume_down;
	private ImageButton power;
	private RelativeLayout buttofield;
	private CodeClassControl MyControl ;//����������
	private Spinner NameSpinner; //����ѡ��
	private String NameSpinId;
	private TextView StatePrompt;
	
	//��������Ҫ�������µ�ң�������ƣ�������ò�Ӧ�÷����仯������Ϊfinal
	private String newCodename;//�����洢��ң����������
	private Map<String,byte[]> newCodevalue;//
	private byte ReadFlag=1;
	
	AlertDialog.Builder builder;
	public  ArrayAdapter<String> NameAdapter;
	public ArrayList<String> allCodeName;//�����洢Ŀǰ�Ѿ����ڵĺ��������
	private int WorkMode=0;//WorkModeΪ����ģʽ0Ϊ����ģʽ��1Ϊѧϰģʽ
	private CodeClass MyCodeClass1;//���CodeClassװȡ���Ǵ洢���յ��м���
	private CodeClass MyCodeClass;//����ʵ��������洢�˱���ľ�����ֵ�������CodeClass���з��͵Ĳ���
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		builder = new AlertDialog.Builder(this);//����dialog��builder
		MyControl=new CodeClassControl(this);
		MyCodeClass1=MyControl.GetNECCode();//
		MyCodeClass1.CommitDB();
		allCodeName=MyControl.getALLClassName();//��ȡ���еĺ���������ƣ�	
		Study=(Button)findViewById(R.id.study);
		DelCode=(Button)findViewById(R.id.delete);	
		Edit=(Button)findViewById(R.id.edit);
		NameSpinner=(Spinner)findViewById(R.id.nameSpin);
		StudyOver=(Button)findViewById(R.id.studyover);
		play=(ImageButton)findViewById(R.id.play);
		pre=(ImageButton)findViewById(R.id.prev);
		next=(ImageButton)findViewById(R.id.next);
		volume_up=(ImageButton)findViewById(R.id.volume_add);
		volume_down=(ImageButton)findViewById(R.id.volume_down);
		power=(ImageButton)findViewById(R.id.power);
		buttofield=(RelativeLayout) findViewById(R.id.buttonfield);
		StatePrompt=(TextView) findViewById(R.id.prompt);
		IntentFilter filter = new IntentFilter();    
		filter.addAction("RECRIVE_ENABLE");//׼������
		filter.addAction("RECRIVECOMPELETE");//�������
		registerReceiver(ViewandDataReceiver, filter);
		
		//ע�������Serial����Ϣ������ֻ�и�����Ϣ�����Ż�֪����Ӧ���շ�����
		Intent intent = new Intent(MainActivity.this,SerialSerivce.class);  
		intent.putExtra("SerialPath",path);
		intent.putExtra("baudrate",baudrate);
		startService(intent);
		//������װ��������
		 //Intent intentSerivce = new Intent();  
	   // bindService(intent, conn, Context.BIND_AUTO_CREATE);//��Activity��service��  �󶨺�����cnn �� onServiceConnected����
		NameAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allCodeName);
		NameSpinner.setAdapter(NameAdapter);
		
		
		SharedPreferences sharedPref = getSharedPreferences("Userchoice",MODE_PRIVATE);
		int spinnerValue = sharedPref.getInt("userChoiceSpinner",-1);
		if(spinnerValue != -1) 
		  // set the value of the spinner 
		NameSpinner.setSelection(spinnerValue);
	    NameSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){        	
       public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        	{
    	      /*get the CodeClass �����Codeclass����з��Ͳ����� */
    	    NameSpinId=arg0.getItemAtPosition(arg2).toString();
        	 MyCodeClass=MyControl.GetCodeclass(NameSpinId);//�˴���ȡͨ��ID��ȡ�û�ѡ���ң������
        	 arg0.setVisibility(View.VISIBLE);
        	 
        	 /*Save and Retrieve Selected Spinner Position*/
        	 int userChoice=arg2;
        	 SharedPreferences UserItem= getSharedPreferences("Userchoice",0);
        	 SharedPreferences.Editor prefEditor = UserItem.edit();
        	 prefEditor.putInt("userChoiceSpinner",userChoice);
        	 prefEditor.commit();
        	}

		   	@Override
              public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				MyCodeClass=MyControl.GetNECCode();//���ʲô��ûѡ����NEC����
			}
	    });
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
				          		MyControl.DelCodeClass(NameSpinId);
				          		 allCodeName.remove(NameSpinId);
				          		NameAdapter.notifyDataSetChanged();
		                         
          	                    }}).setNegativeButton("ȡ��", null).show(); 
          		}
          		
          	}});
	 
	    Edit.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		 WorkMode=2;
          		newCodevalue=new HashMap<String,byte[]>();
          		newCodename=NameSpinId;
          		StatePrompt.setText("�����޸�ң����:"+NameSpinId+",���������Ҫѧϰ�İ���"+"ѧϰ��Ϻ������");
          	}
          	
          	});
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
                      		   WorkMode=1;//�л�����ģʽΪ�޸�ģʽ
                      		 newCodevalue=new HashMap<String,byte[]>();
                      		StatePrompt.setText("����ѧϰң����:"+newCodename+",���������Ҫѧϰ�İ���"+"ѧϰ��Ϻ������");
                      		// msgService.receiveCodevalue=new HashMap<String,byte[]>();
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
     
          		if(ReadFlag==0)
          		{
          			Intent intent = new Intent(); 
          	    	 intent.setAction("INTERRUPT"); 
                    sendBroadcast(intent);  
          	        ReadFlag=1;
          	     
          	      Toast.makeText(MainActivity.this, "ѧϰ���ж�",
              			 Toast.LENGTH_LONG).show();
          		}else if(ReadFlag==2){
          			
          			if(WorkMode==2){
          				 MyCodeClass=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                   	  MyCodeClass.CommitDB();
                   	Toast.makeText(MainActivity.this, "�޸����",
              			     Toast.LENGTH_SHORT).show();    
                 	  ReadFlag=1;
          			}
          			else if(WorkMode==1)
          			{ 
          				MyCodeClass1=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                	  MyCodeClass1.CommitDB();;
                	  allCodeName.add(newCodename);
                	  //NameSpinner.removeAllViews();
                	//  NameAdapter.add(newCodename);����Ҫ�úÿ���
                	  NameSpinner.setSelection(allCodeName.indexOf(newCodename));
                	  Toast.makeText(MainActivity.this, "ѧϰ��ϣ�������ѡ��մ�����ң������",
               			     Toast.LENGTH_SHORT).show();    
                	  ReadFlag=1;
          			}
          		}
          		else{
          			          Toast.makeText(MainActivity.this, "���ȿ�ʼѧϰ",
                 			 Toast.LENGTH_LONG).show();
          
          		}
                 
                 enableDisableView(buttofield,true);
          		 WorkMode=0;
          		StatePrompt.setText("");
          	}
          });
		 setButtonFocusChanged(buttofield);
		play.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(WorkMode==0){
							MyCodeClass.SendData("PLAY");
						}
						else{
					    	CodeClass.ReceiveData("PLAY");
						}		    	
						
					}			
				});					
		power.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(WorkMode==0){
			
						MyCodeClass.SendData("POWER");
					}
					else{
				    	CodeClass.ReceiveData("POWER"); 
				  
				    	   	
					}
				}			
			});	
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(WorkMode==0){
			
					MyCodeClass.SendData("NEXT");
				}
					else{
			      	CodeClass.ReceiveData("NEXT"); 
			   
			  
				}
			}			
		});
		pre.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(WorkMode==0){
			
					MyCodeClass.SendData("PRE");
				}else
				{
			    	CodeClass.ReceiveData("PRE"); 
			    
				
			       	
				}
			}			
		});	
		volume_up.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			if(WorkMode==0){
				
					MyCodeClass.SendData("VOLUME_UP");
			}else
			{
			    	CodeClass.ReceiveData("VOLUME_UP");    	
			    
				}
			}			
		});
		volume_down.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(WorkMode==0){
				
					MyCodeClass.SendData("VOLUME_DOWN");
				}
				else{
			    	CodeClass.ReceiveData("VOLUME_DOWN");
			    	
				}
			}			
		});
		}

		  	
		  	
		  	
		    public final static float[] BT_SELECTED=new float[] {    
		        2, 0, 0, 0, 2,    
		        0, 2, 0, 0, 2,    
		        0, 0, 2, 0, 2,    
		        0, 0, 0, 1, 0 };   
		  	public final static float[] BT_NOT_SELECTED=new float[]{
		  	   1, 0, 0, 0, 0,    
		       0, 1, 0, 0, 0,    
		       0, 0, 1, 0, 0,    
		       0, 0, 0, 1, 0 
		  	};
 
  	public final static  OnFocusChangeListener buttonOnFocusChangeListener=new OnFocusChangeListener() {  
		@Override  
  	  public void onFocusChange(View v, boolean hasFocus) {   
  	   if (hasFocus) {   
  	    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));   
  	    v.setBackgroundDrawable(v.getBackground());   
  	   }   
  	   else  
  	   {   
  	    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));   
  	     v.setBackgroundDrawable(v.getBackground());   
  	   }   
  	  }   
  	 };   
  	 public final static OnTouchListener buttonOnTouchListener=new OnTouchListener() {     
  		  @Override    
  		  public boolean onTouch(View v, MotionEvent event) {     
  		   if(event.getAction() == MotionEvent.ACTION_DOWN){     
  		    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));     
  		    v.setBackgroundDrawable(v.getBackground());     
  		    }     
  		    else if(event.getAction() == MotionEvent.ACTION_UP){     
  		     v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));     
  		     v.setBackgroundDrawable(v.getBackground());     
  		    }     
  		   return false;     
  		  }
  	 };
  	public final static void setButtonFocusChanged(View inView)     
  	 {     
  	  
  	    if ( inView instanceof ViewGroup ) {
  	        ViewGroup group = (ViewGroup)inView;

  	        for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
  	            group.getChildAt(idx).setOnTouchListener(buttonOnTouchListener); 
  	          group.getChildAt(idx).setOnFocusChangeListener(buttonOnFocusChangeListener);
  	        }  
  	        }
  	       
  	 }  
  	private void enableDisableView(View view, boolean enabled) {
  	    view.setEnabled(enabled);
  	     
  	    if ( view instanceof ViewGroup ) {
  	        ViewGroup group = (ViewGroup)view;

  	        for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
  	            enableDisableView(group.getChildAt(idx), enabled);
  	            if(!enabled)
  	          {ColorMatrix cm = new ColorMatrix();     
  	      	cm.setSaturation(0); 
  	          group.getChildAt(idx).getBackground().setColorFilter(new ColorMatrixColorFilter(cm));
  	        }else{
  	        	group.getChildAt(idx).getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));  
  	        }
  	        }
  	    }
  	}
    private BroadcastReceiver ViewandDataReceiver = new BroadcastReceiver() {

 			@Override
 			public void onReceive(Context context, Intent intent) {
 				String Action = intent.getAction();
 				if (Action.equals("RECRIVE_ENABLE"))
 				{
 					ReadFlag=0;
 					enableDisableView(buttofield,false);
 					  Toast.makeText(MainActivity.this, "�뽫ң������׼������տڰ�����Ӧ����",
                			     Toast.LENGTH_SHORT).show();     
 					
 				}else if(Action.equals("RECRIVECOMPELETE")){
 					enableDisableView(buttofield,true);
 					String codeName=intent.getStringExtra("codename");
 				byte[] codeValueValue = intent.getByteArrayExtra("codevalue");
 				  newCodevalue.put(codeName, codeValueValue);
 					 Toast.makeText(MainActivity.this,  codeName+"��,ѧϰ���",
            			     Toast.LENGTH_SHORT).show();  
 					ReadFlag=2;
 				}
 				// TODO Auto-generated method stub
 				
 			}};
  	
  	
  	
	@Override
	 protected void onDestroy() {/*������ֹͣserivce*/
		 
		MainActivity.this.stopService(new
	   Intent(MainActivity.this,
	   SerialSerivce.class));
	  super.onDestroy();
	
	
	}
	
	
	}


	
