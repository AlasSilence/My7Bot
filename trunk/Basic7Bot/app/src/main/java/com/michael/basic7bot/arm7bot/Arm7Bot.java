package com.michael.basic7bot.arm7bot;

import android.util.Log;

import com.michael.basic7bot.arm7bot.model.IK6Point;
import com.michael.basic7bot.arm7bot.model.PVector;
import com.michael.basic7bot.arm7bot.util.Tools;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot {
    /*****Arm7Bot的默认参数****/
    private static int SERVO_NUM=7;//舵机数量
    public  static int getServoNum() {
        return SERVO_NUM;
    }

    private byte[] defaultMode = {(byte)0xfe,(byte)0xf5,0x01};//可控模式
    private byte[] protectionMode={(byte)0xfe,(byte)0xf5,0x02};//半无力模式
    private byte[] forcelessMode = {(byte)0xfe,(byte)0xf5,0x00};//无力模式




    public  byte[] DefaultMode() {
        return defaultMode;
    }
    public  byte[] ProtectionMode() {
        return protectionMode;
    }
    public  byte[] ForcelessMode() {
        return forcelessMode;
    }

    private static Arm7Bot_Calibrate arm7Bot_calibrate = new Arm7Bot_Calibrate();//校准用
    private static Arm7Bot_Check     arm7Bot_check     = new Arm7Bot_Check();    //检查收发数据
    private static Arm7Bot_Receiver  arm7Bot_receiver  = new Arm7Bot_Receiver(); //处理接受数据
    private static Arm7Bot_Send      arm7Bot_send      = new Arm7Bot_Send();     //准备发送数据
    public static Arm7Bot_Receiver getArm7Bot_receiver() {
        return arm7Bot_receiver;
    }
    public static Arm7Bot_Send getArm7Bot_send() {
        return arm7Bot_send;
    }
    public static Arm7Bot_Calibrate getArm7Bot_calibrate() {
        return arm7Bot_calibrate;
    }
    public static Arm7Bot_Check getArm7Bot_check() {
        return arm7Bot_check;
    }

    /****IK6 方式移动***/
    public byte[] moveIK6(IK6Point ik6Point){
        byte[] IK6 = ik6Point.getbyte();
        if(IK6 != null && arm7Bot_check.checkSendIK6(IK6)){
            Log.d("Arm7Bot","IK6 : "+ Tools.bytesToHexString(IK6));
            return IK6;
        }
        else
            return null;
    }

}
