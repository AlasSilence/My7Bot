package com.michael.basic7bot.arm7bot;

import android.util.Log;

/**
 * Created by MichaelJiang on 2017/2/14.
 */

public class Arm7Bot_Send {

    private byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};//IK6 发送数组
     private int[] position={0,175,100};    //现在point6点的坐标
    private int[] direction={100,100,100};  //现在Point6点的向量朝向

    public  void change(){
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

    public void newChange(){
        //x,y成比例可以保持他他的向量朝向
        Log.d("ni","x = "+direction[0]+"\t"+"y = "+direction[1]+"\t"+"z = "+direction[2]+"\t");
        int j=0;
        for(int i=14;i<19;i=i+2){
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
    public void changdirection(){
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
}
