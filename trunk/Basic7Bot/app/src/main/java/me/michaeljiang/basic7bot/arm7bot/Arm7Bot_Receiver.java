package me.michaeljiang.basic7bot.arm7bot;

import static me.michaeljiang.basic7bot.arm7bot.Arm7Bot_Calibrate.calibrationPosition;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot_Receiver {
    /*****上位机接受到arduinoDue发回的数据并解析现在各舵机的角度*****/
    public static void analysisReceived(int[] command){
        int[] motor=new int[7];
        int[] force=new int[7];
        byte[] test = new byte[14];
        int flag=command[16];
        int mul;
        String result="";
        for(int i=1;i<8;i++){
            motor[i-1]=(command[i*2]&0x07)*128+(command[i*2+1]);
            test[2*(i-1)]= (byte) ((byte)command[i*2]&0x07);
            test[2*(i-1)+1]= (byte) command[i*2+1];
            force[i-1]=command[i*2]>>3;
            mul=1;if(force[i-1]>7)mul=-1;force[i-1]=(force[i-1]&0x07)*mul;
//            result+="motor"+i+" = "+motor[i-1]+"     "+(motor[i-1]/1000.0*180.0);
//            result+="\t"+force[i-1];
        }
        if(flag==1){

        }
        else {
            result+="移动";
        }
        calibrationPosition(motor);
//        Log.d("Di", Tools.bytesToHexString(test));
    }
}
