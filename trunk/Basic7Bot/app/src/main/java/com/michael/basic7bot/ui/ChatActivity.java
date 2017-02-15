package com.michael.basic7bot.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.michael.basic7bot.arm7bot.Arm7Bot_Receiver;
import com.michael.basic7bot.arm7bot.model.IK6Point;
import com.michael.basic7bot.R;
import com.michael.basic7bot.arm7bot.Arm7Bot;
import com.michael.basic7bot.bloothtooth.BluetoothService;
import com.michael.basic7bot.ui.view.RockerView;

public class ChatActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	//	private Button upButton,downButton,leftButton,rightButton;


	//  空间声明
	private Button sendButton;
	private Button disconnectButton;
	private Button changemode;
	private Button motorButton;
	private Button catchButton,releaseButton;
	private Button rise,drop;
	private TextView xyz;


	private clientThread clientConnectThread = null;
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread mreadThread = null;;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


	//机械手需要使用的变量及控制
	Arm7Bot arm7Bot=new Arm7Bot();
	IK6Point ik6Point = new IK6Point();

	private ChatActivity mychatActivity;
	Context mContext;
	private RockerView myRock;
	private Handler updateUI;
	public Arm7Bot getArm7Bot(){
		return arm7Bot;
	}
	public int[] getPosition(){
		return ik6Point.getPosition();
	}


	public static String bytesToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if(hexString.equals("0")){
				hexString="00";
			}
			if(hexString.length()==1){
				hexString = "0" + hexString;
			}
			result += hexString.toUpperCase()+"  ";
		}
		return result;
	}



	public boolean moveXY(int x,int y){
		int [] position=ik6Point.getPosition();
		ik6Point.getPosition()[0]=x;
		ik6Point.getPosition()[1]=y;
		byte[] IK6 = arm7Bot.moveIK6(ik6Point);
		if(IK6 !=null){
			sendMessageHandle(IK6);
			xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
			Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
			Log.d("Test",bytesToHexString(IK6));
			return true;
		}
		else{
			ik6Point.getPosition()[0]=position[0];
			ik6Point.getPosition()[1]=position[1];
			myRock.setContext(mychatActivity);
			return false;
		}
	}



	public boolean moveZ(int z){
		int [] position=ik6Point.getPosition();
		ik6Point.getPosition()[2]=z;
		byte[] IK6 = arm7Bot.moveIK6(ik6Point);
		if(IK6 !=null){
			sendMessageHandle(IK6);
			xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
			Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
			Log.d("Test",bytesToHexString(IK6));
			return true;
		}
		else{
			ik6Point.getPosition()[2]=position[2];
			myRock.setContext(mychatActivity);
			return false;
		}
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		mContext = this;
		mychatActivity=this;
		init();
	}

	private void init() {
		motorButton=(Button)findViewById(R.id.btn_motor);
		motorButton.setOnClickListener(this);
		changemode=(Button)findViewById(R.id.btn_mode2);
		changemode.setOnClickListener(this);
		disconnectButton=(Button)findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(this);
		sendButton= (Button)findViewById(R.id.btn_msg_send);
		sendButton.setOnClickListener(this);
		releaseButton=(Button)findViewById(R.id.btn_release);
		releaseButton.setOnClickListener(this);
		catchButton=(Button)findViewById(R.id.btn_catch);
		catchButton.setOnClickListener(this);
		drop=(Button)findViewById(R.id.btn_drop);
		rise=(Button)findViewById(R.id.btn_rise);


		drop.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// TODO Auto-generated method stub
				int[] position=ik6Point.getPosition();
				ik6Point.getPosition()[2]=position[2]-1;
				moveZ(position[2]-1);
				return  true;
			}
		});

		rise.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// TODO Auto-generated method stub
				int[] position=ik6Point.getPosition();
				ik6Point.getPosition()[2]=position[2]+1;
				moveZ(position[2]+1);
				return  true;
			}
		});

		//xyz坐标及命令显示
		xyz=(TextView)findViewById(R.id.text_infomation);

		//自定义移动控件myRock
		myRock=(RockerView)findViewById(R.id.view) ;
		myRock.setContext(this);


		//定义hangdle用来接收线程传来的传感器数据，并对数据进行处理，将其转换成16进制模式。
		updateUI=new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				if(msg.what==1){
					Bundle temp=(Bundle)msg.obj;
					int[] receiver=temp.getIntArray("receiver");
					byte[] ttemp=new byte[receiver.length];
					if(receiver.length==17){
						for(int i=0;i<receiver.length;i++){
							ttemp[i]=(byte)receiver[i];
						}
						Arm7Bot_Receiver.analysisReceived(receiver);
						//command.setText(bytesToHexString(ttemp));
					}
				}
			}
		};
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	// 设置横屏并且取消蓝牙
	@Override
	public synchronized void onResume() {
		/**
		 * 设置为横屏
		 */
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();

		if(BluetoothService.isOpen)
		{
			Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
			return;
		}
		else{
			String address = BluetoothService.BlueToothAddress;
			if(!address.equals("null"))
			{
				device = mBluetoothAdapter.getRemoteDevice(address);
				clientConnectThread = new clientThread();
				clientConnectThread.start();
				BluetoothService.isOpen = true;
			}
			else
			{
				Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
			}
		}
	}

	//初始化内容
	@Override
	protected void onDestroy() {
		super.onDestroy();
		shutdownClient();
		BluetoothService.isOpen = false;
	}


	private void messageShow(byte[] message){
		int[] position=ik6Point.getPosition();
		xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
		Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
	}


	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.btn_mode2){
			// TODO Auto-generated method stub
			byte[] test={(byte)0xfe,(byte)0xf5,0x02};
			sendMessageHandle(test);
			messageShow(test);
		}
		else if(view.getId()==R.id.btn_catch){
			ik6Point.setMoto6(0);
			byte[] IK6 = arm7Bot.moveIK6(ik6Point);
			if(IK6 != null)
				sendMessageHandle(IK6);
		}
		else if(view.getId()==R.id.btn_release){
			ik6Point.setMoto6(200);
			byte[] IK6 = arm7Bot.moveIK6(ik6Point);
			if(IK6 != null){
				sendMessageHandle(IK6);
				Log.d("Arm7bot","send");
			}

		}
		else if(view.getId()==R.id.btn_msg_send){
			byte[] test={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};
			sendMessageHandle(test);
			int[] temp={0,175,100};
			ik6Point.setPosition(temp);
			myRock.setContext(mychatActivity);
			messageShow(test);
		}
		else if(view.getId()==R.id.btn_disconnect){
			shutdownClient();
			BluetoothService.isOpen = false;
			Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();
		}
		else if(view.getId()==R.id.btn_motor){
			//command.setText(bytesToHexString(arm7Bot.getMotorPosition()));
//			sendMessageHandle(arm7Bot.getMotorPosition());
		}
	}



	/*-------------------------------------------蓝牙相关--------------------------------------------------*/

	//发送数据
	private void sendMessageHandle(byte[] msg)
	{
		if (socket == null)
		{
//			Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//读取数据
	public class readThread extends Thread {
		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;
			try {
				mmInStream = socket.getInputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (true) {
				try {
					// Read from the InputStream
					if( (bytes = mmInStream.read(buffer)) > 0 )
					{
						int[] buf_data = new int[bytes];
						for(int i=0; i<bytes; i++)
						{
							buf_data[i] = buffer[i];
						}
						Bundle data=new Bundle();
						data.putIntArray("receiver",buf_data);
						Message msg = new Message();
						msg.obj = data;
						msg.what = 1;
						updateUI.sendMessage(msg);
					}
				} catch (IOException e) {
					try {
						mmInStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

	//开启客户端
	public class clientThread extends Thread {
		public void run() {
			try {
				//创建一个Socket连接：只需要服务器在注册时的UUID号
				// socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				//连接
				Message msg2 = new Message();
				msg2.obj = "请稍候，正在连接服务器:"+BluetoothService.BlueToothAddress;
				msg2.what = 0;
				//LinkDetectedHandler.sendMessage(msg2);

				socket.connect();

				Message msg = new Message();
				msg.obj = "已经连接上服务端！可以发送信息。";
				msg.what = 0;
				//LinkDetectedHandler.sendMessage(msg);
				//启动接受数据
				mreadThread = new readThread();
				mreadThread.start();
			}
			catch (IOException e)
			{
				Log.e("connect", "", e);
				Message msg = new Message();
				msg.obj = "连接服务端异常！断开连接重新试一试。";
				msg.what = 0;
				//LinkDetectedHandler.sendMessage(msg);
			}
		}
	};


	/* 停止客户端连接 */
	private void shutdownClient() {
		new Thread() {
			public void run() {
				if(clientConnectThread!=null)
				{
					clientConnectThread.interrupt();
					clientConnectThread= null;
				}
				if(mreadThread != null)
				{
					mreadThread.interrupt();
					mreadThread = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket = null;
				}
			};
		}.start();
	}

}