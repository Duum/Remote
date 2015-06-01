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
     
	
	/*如果需要增加新的键盘
	 * public void onClick(View v) {
				// TODO Auto-generated method stub
			if(WorkMode==0){
				
					MyCodeClass.SendData("VOLUME_UP");
			}else
			{
			    	CodeClass.ReceiveData("VOLUME_UP");    	
			    
				}
		注意目前数据库支持下面几个按钮
	 * 如果增加新的按钮，发送接收的值一定要从下面选取
   {"NUM1"（数字键）,"PAUSE","LIKE"（喜爱） ,"SINALSOURCE" ,"SLEEP"（睡眠）,
   "MENU","MUTE","NUM2","NUM3","NUM4","NUM5","NUM6","NUM7","NUM9","NUM0",
   "OK","POWER"（电源键）,"PLAY","PRE","NEXT","VOLUME_DOWN","VOLUME_UP" };
	*如果你想要增加的键没有在里面可以在 codeClassControl中的String[] allCodeName加入即可
		*/
	private final String path ="/dev/ttyMFD1";//存储串口的的路径，注意这是Intel开发板的GPS串口	
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
	private CodeClassControl MyControl ;//红外编码控制
	private Spinner NameSpinner; //下拉选框
	private String NameSpinId;
	private TextView StatePrompt;
	
	//这个输入框被要求输入新的遥控器名称，这个引用不应该发生变化所以设为final
	private String newCodename;//用来存储新遥控器的名称
	private Map<String,byte[]> newCodevalue;//
	private byte ReadFlag=1;
	
	AlertDialog.Builder builder;
	public  ArrayAdapter<String> NameAdapter;
	public ArrayList<String> allCodeName;//用来存储目前已经存在的红外编码名
	private int WorkMode=0;//WorkMode为工作模式0为发送模式，1为学习模式
	private CodeClass MyCodeClass1;//这个CodeClass装取得是存储接收的中间量
	private CodeClass MyCodeClass;//编码实例，里面存储了编码的具体码值，对这个CodeClass进行发送的操作
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		builder = new AlertDialog.Builder(this);//创建dialog的builder
		MyControl=new CodeClassControl(this);
		MyCodeClass1=MyControl.GetNECCode();//
		MyCodeClass1.CommitDB();
		allCodeName=MyControl.getALLClassName();//获取所有的红外编码名称；	
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
		filter.addAction("RECRIVE_ENABLE");//准备接收
		filter.addAction("RECRIVECOMPELETE");//接收完毕
		registerReceiver(ViewandDataReceiver, filter);
		
		//注意这里的Serial是消息驱动，只有给它消息，它才会知行相应的收发动作
		Intent intent = new Intent(MainActivity.this,SerialSerivce.class);  
		intent.putExtra("SerialPath",path);
		intent.putExtra("baudrate",baudrate);
		startService(intent);
		//将编码装入适配器
		 //Intent intentSerivce = new Intent();  
	   // bindService(intent, conn, Context.BIND_AUTO_CREATE);//将Activity与service绑定  绑定后会调用cnn 里 onServiceConnected方法
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
    	      /*get the CodeClass （这个Codeclass会进行发送操作） */
    	    NameSpinId=arg0.getItemAtPosition(arg2).toString();
        	 MyCodeClass=MyControl.GetCodeclass(NameSpinId);//此处获取通过ID获取用户选择的遥控器类
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
				MyCodeClass=MyControl.GetNECCode();//如果什么都没选就是NEC编码
			}
	    });
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
				          		MyControl.DelCodeClass(NameSpinId);
				          		 allCodeName.remove(NameSpinId);
				          		NameAdapter.notifyDataSetChanged();
		                         
          	                    }}).setNegativeButton("取消", null).show(); 
          		}
          		
          	}});
	 
	    Edit.setOnClickListener(new OnClickListener()
        {
          	public void onClick(View v){
          		 WorkMode=2;
          		newCodevalue=new HashMap<String,byte[]>();
          		newCodename=NameSpinId;
          		StatePrompt.setText("正在修改遥控器:"+NameSpinId+",请逐个单击要学习的按键"+"学习完毕后点击完毕");
          	}
          	
          	});
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
                      		   WorkMode=1;//切换工作模式为修改模式
                      		 newCodevalue=new HashMap<String,byte[]>();
                      		StatePrompt.setText("正在学习遥控器:"+newCodename+",请逐个单击要学习的按键"+"学习完毕后点击完毕");
                      		// msgService.receiveCodevalue=new HashMap<String,byte[]>();
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
     
          		if(ReadFlag==0)
          		{
          			Intent intent = new Intent(); 
          	    	 intent.setAction("INTERRUPT"); 
                    sendBroadcast(intent);  
          	        ReadFlag=1;
          	     
          	      Toast.makeText(MainActivity.this, "学习被中断",
              			 Toast.LENGTH_LONG).show();
          		}else if(ReadFlag==2){
          			
          			if(WorkMode==2){
          				 MyCodeClass=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                   	  MyCodeClass.CommitDB();
                   	Toast.makeText(MainActivity.this, "修改完毕",
              			     Toast.LENGTH_SHORT).show();    
                 	  ReadFlag=1;
          			}
          			else if(WorkMode==1)
          			{ 
          				MyCodeClass1=MyControl.CreaeCodeClass(newCodename,newCodevalue,null);
                	  MyCodeClass1.CommitDB();;
                	  allCodeName.add(newCodename);
                	  //NameSpinner.removeAllViews();
                	//  NameAdapter.add(newCodename);这里要好好看看
                	  NameSpinner.setSelection(allCodeName.indexOf(newCodename));
                	  Toast.makeText(MainActivity.this, "学习完毕，您可以选择刚创建的遥控器了",
               			     Toast.LENGTH_SHORT).show();    
                	  ReadFlag=1;
          			}
          		}
          		else{
          			          Toast.makeText(MainActivity.this, "请先开始学习",
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
 					  Toast.makeText(MainActivity.this, "请将遥控器对准红外接收口按下相应按键",
                			     Toast.LENGTH_SHORT).show();     
 					
 				}else if(Action.equals("RECRIVECOMPELETE")){
 					enableDisableView(buttofield,true);
 					String codeName=intent.getStringExtra("codename");
 				byte[] codeValueValue = intent.getByteArrayExtra("codevalue");
 				  newCodevalue.put(codeName, codeValueValue);
 					 Toast.makeText(MainActivity.this,  codeName+"键,学习完毕",
            			     Toast.LENGTH_SHORT).show();  
 					ReadFlag=2;
 				}
 				// TODO Auto-generated method stub
 				
 			}};
  	
  	
  	
	@Override
	 protected void onDestroy() {/*在这里停止serivce*/
		 
		MainActivity.this.stopService(new
	   Intent(MainActivity.this,
	   SerialSerivce.class));
	  super.onDestroy();
	
	
	}
	
	
	}


	
