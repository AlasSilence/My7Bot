package me.michaeljiang.basic7bot.arm7bot;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot_Calibrate {
    private static byte[] calibrationMotorPosition = {(byte)0xfe,(byte)0xf9,0x03,0x74,0x03,0x74,0x02,0x69,0x03,0x74,0x03,0x74,0x03,0x74,0x01,0x48};

    public static byte[] calibrationPosition(int[] motor){
        //参数
        motor[0]+=25;
        motor[1]-=13;
        motor[2]-=15;
        motor[3]-=10;
        motor[4]-=15;
        motor[5]-=10;
        motor[6]-=100 ;

        byte[] command=new byte[14];
        for(int i=0;i<7;i++){
            command[2*(i)]=(byte) (motor[i]/128);
            command[2*(i)+1]=(byte) ((byte)(motor[i]%128));
        }
        for(int i= 2;i<16;i++){
            calibrationMotorPosition[i] =command[i-2];
        }
        return command;
    }

}
