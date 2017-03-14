package org.ubibots.motiondectction;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ubibots.motiondectction.Bluetooth.ServerOrCilent;
import org.ubibots.motiondectction.model.Arm7Bot;
import org.ubibots.motiondectction.util.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.view.MotionEvent;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8U;

public class ChatActivity extends Activity implements OnClickListener, CameraBridgeViewBase.CvCameraViewListener2 {
    /**
     * Called when the activity is first created.
     */
    private static boolean canRecognition = false;
    private static int type = 0;//0还没开始   1移动到hit点  2移动到wait点
    private static int wait = 6;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private JavaCameraView openCvCameraView;
    private Point[] borderPoint = new Point[3];
    private boolean isBorder = false;
    private BackgroundSubtractorMOG2 pMOG4;
    private Mat mogImg;
    private List<Integer> sendList;


    private Button motorButton;
    private Button btn_Postion1;
    private Button btn_Postion2;
    private Button btn_Postion3;
    private Button btn_Postion4;
    private Button btn_rest;

    private TextView info;
    private clientThread clientConnectThread = null;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private readThread mreadThread = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    SharedPreferences saveDate;
    SharedPreferences loadDate;

    //机械手需要使用的变量及控制
    Arm7Bot arm7Bot = new Arm7Bot();


    Context mContext;
    //	private RockerView myRock;
    private Handler updateUI;


    byte[] motorPosition = {(byte) 0xfe, (byte) 0xf9, 0x01, 0x77, 0x03, 0x77, 0x02, 0x11, 0x01, 0x77, 0x02, 0x11, 0x02, 0x57, 0x02, 0x74};
    byte[] postionA = {(byte) 0xfe, (byte) 0xf9, 0x03, 0x74, 0x03, 0x74, 0x02, 0x69, 0x03, 0x74, 0x03, 0x74, 0x03, 0x74, 0x01, 0x48};
    byte[] postionB = {(byte) 0xfe, (byte) 0xf9, 0x03, 0x74, 0x03, 0x74, 0x02, 0x69, 0x03, 0x74, 0x03, 0x74, 0x03, 0x74, 0x01, 0x48};
    byte[] postionC = {(byte) 0xfe, (byte) 0xf9, 0x03, 0x74, 0x03, 0x74, 0x02, 0x69, 0x03, 0x74, 0x03, 0x74, 0x03, 0x74, 0x01, 0x48};
    byte[] postionD = {(byte) 0xfe, (byte) 0xf9, 0x03, 0x74, 0x03, 0x74, 0x02, 0x69, 0x03, 0x74, 0x03, 0x74, 0x03, 0x74, 0x01, 0x48};
    byte[] postionRest = {(byte) 0xfe, (byte) 0xf9, 0x03, 0x74, 0x03, 0x74, 0x02, 0x69, 0x03, 0x74, 0x03, 0x74, 0x03, 0x74, 0x01, 0x48};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mContext = this;
        sendList = new ArrayList<>();
        checkCamera();
        init();
    }


    private void init() {
        motorButton = (Button) findViewById(R.id.btn_motor);
        motorButton.setOnClickListener(this);
        Button changemode = (Button) findViewById(R.id.btn_mode2);
        changemode.setOnClickListener(this);
        Button disconnectButton = (Button) findViewById(R.id.btn_disconnect);
        disconnectButton.setOnClickListener(this);
        Button sendButton = (Button) findViewById(R.id.btn_start);
        sendButton.setOnClickListener(this);
        info = (TextView) findViewById(R.id.txt_info);
        Button btn_mode1 = (Button) findViewById(R.id.btn_mode1);
        btn_mode1.setOnClickListener(this);

        Button btn_savePostion1 = (Button) findViewById(R.id.savepostion1);
        Button btn_savePostion2 = (Button) findViewById(R.id.savepostion2);
        Button btn_savePostion3 = (Button) findViewById(R.id.savepostion3);
        Button btn_savePostion4 = (Button) findViewById(R.id.savepostion4);

        btn_Postion1 = (Button) findViewById(R.id.postion1);
        btn_Postion2 = (Button) findViewById(R.id.postion2);
        btn_Postion3 = (Button) findViewById(R.id.postion3);
        btn_Postion4 = (Button) findViewById(R.id.postion4);
        btn_rest = (Button)findViewById(R.id.btn_rest);

        btn_savePostion1.setOnClickListener(this);
        btn_savePostion2.setOnClickListener(this);
        btn_savePostion3.setOnClickListener(this);
        btn_savePostion4.setOnClickListener(this);

        btn_Postion1.setOnClickListener(this);
        btn_Postion2.setOnClickListener(this);
        btn_Postion3.setOnClickListener(this);
        btn_Postion4.setOnClickListener(this);
        btn_rest.setOnClickListener(this);
        saveDate = getSharedPreferences("postionInfo", Context.MODE_PRIVATE);
        loadDate = getSharedPreferences("postionInfo", Context.MODE_PRIVATE);

        postionA = Tools.loadData(loadDate.getString("postionA", Tools.saveData(motorPosition)));
        postionB = Tools.loadData(loadDate.getString("postionB", Tools.saveData(motorPosition)));
        postionC = Tools.loadData(loadDate.getString("postionC", Tools.saveData(motorPosition)));
        postionD = Tools.loadData(loadDate.getString("postionD", Tools.saveData(motorPosition)));
        postionRest = Tools.loadData(loadDate.getString("rest",Tools.saveData(arm7Bot.getMotorPosition())));

        //定义hangdle用来接收线程传来的传感器数据，并对数据进行处理，将其转换成16进制模式。
        updateUI = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1 && (wait++) % 100 > 5) {
                    Bundle temp = (Bundle) msg.obj;
                    int[] receiver = temp.getIntArray("receiver");
                    byte[] ttemp = new byte[receiver != null ? receiver.length : 0];

                    if ((receiver != null ? receiver.length : 0) == 17) {
                        for (int i = 0; i < receiver.length; i++) {
                            ttemp[i] = (byte) receiver[i];
                        }
                        Arm7Bot.analysisReceived(receiver);
                        info.setText(bytesToHexString(ttemp) + " 1 " + String.valueOf(arm7Bot.isMove()) + " 2 " + String.valueOf(canRecognition) + "Size: " + sendList.size());

                        if (type == 1 && !canRecognition) {
                            //移动到hit点
                            if (!arm7Bot.isMove()) {
                                type = 2;
                                motorButton.callOnClick();
                            }
                        } else if (type == 2 && !canRecognition) {
                            if (!arm7Bot.isMove()) {
                                type = 0;
                                canRecognition = true;
                            }
                        }
                    }
                }
            }
        };
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
    }


    // 设置横屏并且取消蓝牙
    @Override
    public synchronized void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();

        if (Bluetooth.isOpen) {
            Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
            String address = Bluetooth.BlueToothAddress;
            if (!address.equals("null")) {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new clientThread();
                clientConnectThread.start();
                Bluetooth.isOpen = true;
            } else {
                Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
            }
        } else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) {

            Bluetooth.isOpen = true;
        }
    }


    //初始化内容
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null) {
            openCvCameraView.disableView();
        }
        if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
            shutdownClient();
        }
        Bluetooth.isOpen = false;
        Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
    }

    /**
     * -------------------------------------------7Bot相关--------------------------------------------------
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_mode2) {
            // TODO Auto-generated method stub
            sendMessageHandle(arm7Bot.getProtectionMode());
        }
        else if(view.getId() == R.id.btn_rest){
            System.arraycopy(arm7Bot.getCalibrationMotorPosition(), 0, postionRest, 0, arm7Bot.getCalibrationMotorPosition().length);
            postionRest[14] = 0x03;
            postionRest[15] = 0x20;
            SharedPreferences.Editor editor = saveDate.edit();
            editor.putString("rest", Tools.saveData(postionRest));
            editor.apply();
            sendMessageHandle(postionRest);
        }
        else if (view.getId() == R.id.btn_start) {
            canRecognition = true;
            byte[] speed = {(byte) 0xFE, (byte) 0xF7, 0x49, 0x49, 0x49, 0x49, 0x49, 0x54, 0x47};
            sendMessageHandle(speed);
        } else if (view.getId() == R.id.btn_disconnect) {
            if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) {
                shutdownClient();
            }
            Bluetooth.isOpen = false;
            Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
            Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.btn_motor) {

            sendMessageHandle(postionRest);
            wait = 0;
        } else if (view.getId() == R.id.btn_mode1) {
            sendMessageHandle(arm7Bot.getForcelessMode());
        } else if (view.getId() == R.id.savepostion1) {
            System.arraycopy(arm7Bot.getCalibrationMotorPosition(), 0, postionA, 0, arm7Bot.getCalibrationMotorPosition().length);
            postionA[14] = 0x03;
            postionA[15] = 0x20;
            SharedPreferences.Editor editor = saveDate.edit();
            editor.putString("postionA", Tools.saveData(postionA));
            editor.apply();
        } else if (view.getId() == R.id.savepostion2) {
            System.arraycopy(arm7Bot.getCalibrationMotorPosition(), 0, postionB, 0, arm7Bot.getCalibrationMotorPosition().length);
            postionB[14] = 0x03;
            postionB[15] = 0x20;
            SharedPreferences.Editor editor = saveDate.edit();
            editor.putString("postionB", Tools.saveData(postionB));
            editor.apply();
        } else if (view.getId() == R.id.savepostion3) {
            System.arraycopy(arm7Bot.getCalibrationMotorPosition(), 0, postionC, 0, arm7Bot.getCalibrationMotorPosition().length);
            postionC[14] = 0x03;
            postionC[15] = 0x20;
            SharedPreferences.Editor editor = saveDate.edit();
            editor.putString("postionC", Tools.saveData(postionC));
            editor.apply();
        } else if (view.getId() == R.id.savepostion4) {
            System.arraycopy(arm7Bot.getCalibrationMotorPosition(), 0, postionD, 0, arm7Bot.getCalibrationMotorPosition().length);
            postionD[14] = 0x03;
            postionD[15] = 0x20;
            SharedPreferences.Editor editor = saveDate.edit();
            editor.putString("postionD", Tools.saveData(postionD));
            editor.apply();
        } else if (view.getId() == R.id.postion1) {
            sendMessageHandle(postionA);
            arm7Bot.setMove(true);
            wait = 0;
            type = 1;
        } else if (view.getId() == R.id.postion2) {
            sendMessageHandle(postionB);
            type = 1;
            arm7Bot.setMove(true);
            wait = 0;
        } else if (view.getId() == R.id.postion3) {
            sendMessageHandle(postionC);
            type = 1;
            arm7Bot.setMove(true);
            wait = 0;
        } else if (view.getId() == R.id.postion4) {
            sendMessageHandle(postionD);
            type = 1;
            arm7Bot.setMove(true);
            wait = 0;
        }
    }


    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.equals("0")) {
                hexString = "00";
            }
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            result += hexString.toUpperCase() + "  ";
        }
        return result;
    }


    public void hitPostion(int Postion) {
        if (Postion == 0) {
            btn_Postion1.callOnClick();
        } else if (Postion == 1) {
            btn_Postion2.callOnClick();
        } else if (Postion == 2) {
            btn_Postion3.callOnClick();
        } else {
            btn_Postion4.callOnClick();
        }
    }


    /**
     * -------------------------------------------权限相关--------------------------------------------------
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * -------------------------------------------OpenCV相关--------------------------------------------------
     */
    private void checkCamera() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            initCamera();
        }
    }


    private void initCamera() {
        openCvCameraView = (JavaCameraView) findViewById(R.id.camera);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        openCvCameraView.setCvCameraViewListener(this);
        openCvCameraView.setMaxFrameSize(800, 600);
        openCvCameraView.enableFpsMeter();
        openCvCameraView.enableView();

        openCvCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        Point point = new Point(event.getX(), event.getY());
                        if (borderPoint[2] == null) {
                            borderPoint[0] = point;
                            borderPoint[1] = null;
                            borderPoint[2] = new Point();
                            isBorder = false;
                        } else {
                            borderPoint[1] = point;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        borderPoint[2] = null;
                        isBorder = true;
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        pMOG4 = new BackgroundSubtractorMOG2();
        mogImg = new Mat();
    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat grayImg = inputFrame.gray();

        if (borderPoint[0] != null && borderPoint[1] != null) {
            Mat mask = new Mat(grayImg.size(), CV_8U, new Scalar(0));
            Core.rectangle(mask, borderPoint[0], borderPoint[1], new Scalar(255), -1);
            Mat tmpImg = new Mat();
            grayImg.copyTo(tmpImg, mask);
            mask.release();
            tmpImg.copyTo(grayImg);
            tmpImg.release();

            if (isBorder) {
                pMOG4.apply(grayImg, mogImg, 0.9);
                Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
                Imgproc.erode(mogImg, mogImg, kernelErode);
                kernelErode.release();

                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(mogImg, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
                hierarchy.release();
                if (canRecognition) {
                    if (contours.size() != 0 && sendList.size() == 0) {
                        MatOfPoint2f contoursPoint2fMat;
                        List<Point> pointList = new ArrayList<>();
                        for (MatOfPoint contour : contours) {
                            contoursPoint2fMat = new MatOfPoint2f();
                            pointList.addAll(contour.toList());
                            contoursPoint2fMat.fromList(pointList);
                            RotatedRect rotatedRect = Imgproc.minAreaRect(contoursPoint2fMat);
                            int position = (int) ((rotatedRect.center.x - borderPoint[0].x) / (borderPoint[1].x - borderPoint[0].x) * 4);
                            if (position >= 0 && position <= 3) {
                                if (!sendList.contains(position)) {
                                    sendList.add(position);
                                }
                            }
                            contoursPoint2fMat.release();
                            Core.line(grayImg, rotatedRect.center, rotatedRect.center, new Scalar(255), 10);
                            pointList.clear();
                        }
                        for (MatOfPoint contour : contours) {
                            contour.release();
                        }
                    }
                    if (sendList.size() != 0) {
                        canRecognition = false;
                        hitPostion(sendList.remove(0));
                    }
                }
            }
        }
        return grayImg;
    }


    /**
     * -------------------------------------------蓝牙相关--------------------------------------------------
     */
    //发送数据
    private void sendMessageHandle(byte[] msg) {
        if (socket == null) {
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
                    if ((bytes = mmInStream != null ? mmInStream.read(buffer) : 0) > 0) {
                        int[] buf_data = new int[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        Bundle data = new Bundle();
                        data.putIntArray("receiver", buf_data);
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
    private class clientThread extends Thread {
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                //连接
                Message msg2 = new Message();
                msg2.obj = "请稍候，正在连接服务器:" + Bluetooth.BlueToothAddress;
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
            } catch (IOException e) {
                Log.e("connect", "", e);
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重新试一试。";
                msg.what = 0;
                //LinkDetectedHandler.sendMessage(msg);
            }
        }
    }


    /* 停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            public void run() {
                if (clientConnectThread != null) {
                    clientConnectThread.interrupt();
                    clientConnectThread = null;
                }
                if (mreadThread != null) {
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
            }
        }.start();
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