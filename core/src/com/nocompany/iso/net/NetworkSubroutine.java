package com.nocompany.iso.net;

public class NetworkSubroutine {

    private final static int TIMES = 30;
    private static int times = 0;

    public static void update(float fx, float fy){

        times += 1;

        if( times >= TIMES ){

            NetworkMessage networkMessage = new NetworkMessage(fx, fy);
            NetworkManager.getInstance().AddToQueue(networkMessage);
            times = 0;

        }

    }

}
