package com.michael.basic7bot.arm7bot;

import com.michael.basic7bot.arm7bot.model.PVector;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot {

    private static int SERVO_NUM=7;


    private byte[] defaultMode = {(byte)0xfe,(byte)0xf5,0x01};
    private byte[] protectionMode={(byte)0xfe,(byte)0xf5,0x02};
    private byte[] forcelessMode = {(byte)0xfe,(byte)0xf5,0x00};

    private static Arm7Bot_Calibrate arm7Bot_calibrate = new Arm7Bot_Calibrate();
    private static Arm7Bot_Check     arm7Bot_check     = new Arm7Bot_Check();
    private static Arm7Bot_Receiver  arm7Bot_receiver  = new Arm7Bot_Receiver();
    private static Arm7Bot_Send      arm7Bot_send      = new Arm7Bot_Send();


    public static int getServoNum() {
        return SERVO_NUM;
    }

    public static void setServoNum(int servoNum) {
        SERVO_NUM = servoNum;
    }

    public static Arm7Bot_Calibrate getArm7Bot_calibrate() {
        return arm7Bot_calibrate;
    }

    public static void setArm7Bot_calibrate(Arm7Bot_Calibrate arm7Bot_calibrate) {
        Arm7Bot.arm7Bot_calibrate = arm7Bot_calibrate;
    }

    public static Arm7Bot_Check getArm7Bot_check() {
        return arm7Bot_check;
    }

    public static void setArm7Bot_check(Arm7Bot_Check arm7Bot_check) {
        Arm7Bot.arm7Bot_check = arm7Bot_check;
    }

    public static Arm7Bot_Receiver getArm7Bot_receiver() {
        return arm7Bot_receiver;
    }

    public static void setArm7Bot_receiver(Arm7Bot_Receiver arm7Bot_receiver) {
        Arm7Bot.arm7Bot_receiver = arm7Bot_receiver;
    }

    public static Arm7Bot_Send getArm7Bot_send() {
        return arm7Bot_send;
    }

    public static void setArm7Bot_send(Arm7Bot_Send arm7Bot_send) {
        Arm7Bot.arm7Bot_send = arm7Bot_send;
    }

    public byte[] DefaultMode() {
        return defaultMode;
    }

    public byte[] ProtectionMode() {
        return protectionMode;
    }

    public byte[] ForcelessMode() {
        return forcelessMode;
    }
}
