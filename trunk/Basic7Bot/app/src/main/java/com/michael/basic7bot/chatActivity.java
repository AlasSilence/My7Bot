package com.michael.basic7bot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.michael.basic7bot.Bluetooth.ServerOrCilent;

public class chatActivity extends Activity implements OnItemClickListener ,OnClickListener, SeekBar.OnSeekBarChangeListener {
	/** Called when the activity is first created. */
	private Button sendButton;
	private Button disconnectButton;
	private Button changemode;
	private Button motorButton;
	private Button upButton,downButton,leftButton,rightButton;
	private Button catchButton,releaseButton;
	private byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};
	private byte[] status={(byte)0xfe,(byte)0xf5,0x02};
	private byte[] motorPosition={(byte)0xfe,(byte)0xf9,0x03,0x74,0x03,0x74,0x02,0x69,0x03,0x74,0x03,0x74,0x03,0x74,0x01,0x48};//16
	private int[] position={0,175,100};
	private int[] direction={100,100,100};
	//deviceListAdapter mAdapter;
	Context mContext;
	private TextView command;
	private TextView xyz;
	private SeekBar moveZ;
	private SeekBar pointx;
	private SeekBar pointy;
	private SeekBar pointz;

	/* 一些常量，代表服务器的名称 */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	private BluetoothServerSocket mserverSocket = null;
	//private ServerThread startServerThread = null;
	private clientThread clientConnectThread = null;
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread mreadThread = null;;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		mContext = this;
		init();
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

	private  void change(){
		int j=0;
		for(int i=2;i<7;i=i+2){
			if(position[j]>0){
				IK6[i]=(byte)((position[j]/128)&0x7F);
				IK6[i+1]=(byte)(position[j++]&0x7F);
			}
			else{
				IK6[i]=(byte) (((byte)((-position[j]/128)&0x7F))|0x08);
				IK6[i+1]=(byte)(-position[j++]&0x7F);
			}
		}
	}


	private void changdirection(){
		int j=0;
		for(int i=8;i<13;i=i+2){
			if(direction[j]>0){
				IK6[i]=(byte)((direction[j]/128)&0x7F);
				IK6[i+1]=(byte)(direction[j++]&0x7F);
			}
			else{
				IK6[i]=(byte) (((byte)((-direction[j]/128)&0x7F))|0x08);
				IK6[i+1]=(byte)(-direction[j++]&0x7F);
			}
		}

	}

	private void init() {
		motorButton=(Button)findViewById(R.id.btn_motor);
		changemode=(Button)findViewById(R.id.btn_mode2);
		disconnectButton=(Button)findViewById(R.id.btn_disconnect);
		sendButton= (Button)findViewById(R.id.btn_msg_send);
		upButton=(Button)findViewById(R.id.btn_up);
		downButton=(Button)findViewById(R.id.btn_down);
		leftButton=(Button)findViewById(R.id.btn_left);
		rightButton=(Button)findViewById(R.id.btn_right);
		releaseButton=(Button)findViewById(R.id.btn_release);
		catchButton=(Button)findViewById(R.id.btn_catch);
		xyz=(TextView)findViewById(R.id.textView_Positive);
		command=(TextView)findViewById(R.id.textView_Command);
		//Move Z!
		moveZ=(SeekBar)findViewById(R.id.seekBar);
		moveZ.setMax(435);
		moveZ.setProgress(position[2]);
		moveZ.setOnSeekBarChangeListener(this);

		motorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				command.setText(bytesToHexString(motorPosition));
				sendMessageHandle(motorPosition);
			}
		});

		changemode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				byte[] test={(byte)0xfe,(byte)0xf5,0x02};
				sendMessageHandle(status);
				command.setText(bytesToHexString(test));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(IK6));
			}
		});

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				byte[] test={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};
				IK6=test;
				sendMessageHandle(test);
				command.setText(bytesToHexString(test));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(test));
			}
		});


		//Move
		upButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// TODO Auto-generated method stub
				position[1]=position[1]+5;
				change();
				sendMessageHandle(IK6);
				command.setText(bytesToHexString(IK6));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(IK6));
				return  true;
			}
		});

		downButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// TODO Auto-generated method stub
				position[1]=position[1]-5;
				change();
				sendMessageHandle(IK6);
				command.setText(bytesToHexString(IK6));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(IK6));
				return  true;
			}
		});

		leftButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				// TODO Auto-generated method stub
				position[0]=position[0]+5;
				change();
				sendMessageHandle(IK6);
				command.setText(bytesToHexString(IK6));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(IK6));
				return  true;
			}
		});

		rightButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				position[0]=position[0]-5;
				change();
				sendMessageHandle(IK6);
				command.setText(bytesToHexString(IK6));
				xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
				Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
				Log.d("Test",bytesToHexString(IK6));
				return true;
			}


		});

		//Catch
		catchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IK6[20]=0;
				IK6[21]=0;
				sendMessageHandle(IK6);
			}
		});

		releaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IK6[20]=0x01;
				IK6[21]=0x48;
				sendMessageHandle(IK6);
			}
		});


		disconnectButton= (Button)findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT)
				{
					shutdownClient();
				}
				Bluetooth.isOpen = false;
				Bluetooth.serviceOrCilent=ServerOrCilent.NONE;
				Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public synchronized void onResume() {
		/**
		 * 设置为横屏
		 */
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();

		if(Bluetooth.isOpen)
		{
			Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(Bluetooth.serviceOrCilent==ServerOrCilent.CILENT)
		{
			String address = Bluetooth.BlueToothAddress;
			if(!address.equals("null"))
			{
				device = mBluetoothAdapter.getRemoteDevice(address);
				clientConnectThread = new clientThread();
				clientConnectThread.start();
				Bluetooth.isOpen = true;
			}
			else
			{
				Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
			}
		}
		else if(Bluetooth.serviceOrCilent==ServerOrCilent.SERVICE)
		{
//			startServerThread = new ServerThread();
//			startServerThread.start();
			Bluetooth.isOpen = true;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
		position[2]=progress;
		change();
		sendMessageHandle(IK6);
		command.setText(bytesToHexString(IK6));
		xyz.setText("x = "+position[0]+"\n"+"y = "+position[1]+"\n"+"z = "+position[2]+"\n");
		Log.d("Test",position[0]+"  "+position[1]+"  "+position[2]+"  ");
		Log.d("Test",bytesToHexString(IK6));
//		if(seekBar.getId()==R.id.seekBar){
//			direction[0]=progress;
//			changdirection();
//			sendMessageHandle(IK6);
//			command.setText(bytesToHexString(IK6));
//			xyz.setText("vec56.x = "+direction[0]+"\n"+"vec56.y = "+direction[1]+"\n"+"vec56.z = "+direction[2]+"\n");
//			Log.d("Test",direction[0]+"  "+direction[1]+"  "+direction[2]+"  ");
//			Log.d("Test",bytesToHexString(IK6));
//		}
//		else if(seekBar.getId()==R.id.seekBar2){
//			direction[1]=progress;
//			changdirection();
//			sendMessageHandle(IK6);
//			command.setText(bytesToHexString(IK6));
//			xyz.setText("x = "+direction[0]+"\n"+"y = "+direction[1]+"\n"+"z = "+direction[2]+"\n");
//			Log.d("Test",direction[0]+"  "+direction[1]+"  "+direction[2]+"  ");
//			Log.d("Test",bytesToHexString(IK6));
//		}
//		else if(seekBar.getId()==R.id.seekBar3){
//			direction[2]=-progress;
//			changdirection();
//			sendMessageHandle(IK6);
//			command.setText(bytesToHexString(IK6));
//			xyz.setText("x = "+direction[0]+"\n"+"y = "+direction[1]+"\n"+"z = "+direction[2]+"\n");
//			Log.d("Test",direction[0]+"  "+direction[1]+"  "+direction[2]+"  ");
//			Log.d("Test",bytesToHexString(IK6));
//		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}


	//开启客户端
	private class clientThread extends Thread {
		public void run() {
			try {
				//创建一个Socket连接：只需要服务器在注册时的UUID号
				// socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				//连接
				Message msg2 = new Message();
				msg2.obj = "请稍候，正在连接服务器:"+Bluetooth.BlueToothAddress;
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
	//发送数据
	private void sendMessageHandle(byte[] msg)
	{
		if (socket == null)
		{
			Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//list.add(new deviceListItem(msg, false));
		//mAdapter.notifyDataSetChanged();
		//mListView.setSelection(list.size() - 1);
	}


	//读取数据
	private class readThread extends Thread {
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
						byte[] buf_data = new byte[bytes];
						for(int i=0; i<bytes; i++)
						{
							buf_data[i] = buffer[i];
						}
						String s = new String(buf_data);
						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						//LinkDetectedHandler.sendMessage(msg);
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


	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT)
		{
			shutdownClient();
		}
		else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE)
		{
			//	shutdownServer();
		}
		Bluetooth.isOpen = false;
		Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
	}
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
	public class deviceListItem {
		String message;
		boolean isSiri;

		public deviceListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
}