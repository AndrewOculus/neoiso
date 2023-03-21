package com.nocompany.iso.net;

public class NetworkMessage {

    NetworkType networkType;

    float fx;
    float fy;

    int x;
    int y;

    public NetworkMessage(float x, float y){
        this.fx = x;
        this.fy = y;
        this.networkType = NetworkType.PositionRequest;
    }


    public NetworkMessage(int x, int y){
        this.x = x;
        this.y = y;
        this.networkType = NetworkType.MapRequest;
    }

}
