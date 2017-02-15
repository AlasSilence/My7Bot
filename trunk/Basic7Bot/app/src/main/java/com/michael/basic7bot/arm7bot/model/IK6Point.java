package com.michael.basic7bot.arm7bot.model;

import com.michael.basic7bot.arm7bot.Arm7Bot_Send;

/**
 * Created by MichaelJiang on 2017/2/15.
 */

public class IK6Point {
    private int[] data = new int[10];
    private int[] position={0,175,100};    //现在point6点的坐标
    private int[] vec56={0,0,-300};  //现在Point6点的向量朝向
    private int[] vec67={-1,-1 ,100};  //现在Point6点的向量朝向
    private int moto6 = 500;//此时是否吸取或者机械手抓取
    private byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00,
                        0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};//IK6 发送数组

    public void changeData(){
        for(int i = 0 ;i < 3 ;i++){
            data[i]   = position[i];//0 1 2
            data[i+3] = vec56[i];   //3 4 5
            data[i+6] = vec67[i];   //6 7 8
        }
        data[9] = moto6;
    }

    public int[] getData(){
        return data;
    }

    public byte[] getbyte() {
        changeData();
        return Arm7Bot_Send.changeIK6(data);
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int[] getVec56() {
        return vec56;
    }

    public void setVec56(int[] vec56) {
        this.vec56 = vec56;
    }

    public int[] getVec67() {
        return vec67;
    }

    public void setVec67(int[] vec67) {
        this.vec67 = vec67;
    }

    public int getMoto6() {
        return moto6;
    }

    public void setMoto6(int moto6) {
        this.moto6 = moto6;
    }
}
