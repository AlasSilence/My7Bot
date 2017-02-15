package com.michael.basic7bot.arm7bot;


import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot_Send {
    /**
     * 根据相关指令将数据转换为IK6接受指令
     * @param data  = position + vec56 + vec67 + moto6 顺序不能调整
     *  private int[] position={0,175,100};    //现在point6点的坐标
        private int[] vec56={100,100,100};  //现在Point6点的向量朝向
        private int[] vec67={100,100,100};  //现在Point6点的向量朝向
        private int moto6 = 500;//此时是否吸取或者机械手抓取
     * @return 机械手接受的byte数组或者为null
     */
    @Nullable
    public static byte[] changeIK6(int[] data){
        byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00,0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};//IK6 发送数组
        if (data.length!=10){
            Log.d("Arm7Bot","Arm7Bot_Send:changeIK6(int[] data) data is invalid");
            return null;
        }
        int j = 0;
        for(int i=2;i<19;i=i+2){//Point6 的xyz变化
            if(data[j]>0){
                IK6[i]=(byte)((data[j]/128)&0x7F);
                IK6[i+1]=(byte)(data[j++]&0x7F);
            }
            else if(data[j]==0){
                IK6[i]=0x08;
                IK6[i+1]=0x00;
                j++;
            }
            else{
                IK6[i]=(byte) (((byte)((-data[j]/128)&0x7F))|0x08);
                IK6[i+1]=(byte)(-data[j++]&0x7F);
            }
        }
        //moto6转换
        IK6[20] = (byte)((data[9]/128)&0x7F);
        IK6[21] = (byte)(data[9]&0x7F);
        return IK6;
    }
}
