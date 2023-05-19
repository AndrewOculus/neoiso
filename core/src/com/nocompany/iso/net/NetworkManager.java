package com.nocompany.iso.net;

import com.nocompany.iso.Settings;
import com.nocompany.iso.objects.GameObject;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.SceneObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class NetworkManager implements Runnable {

    private static NetworkManager networkManager;

    public static NetworkManager getInstance(){

        if(networkManager == null){
            networkManager = new NetworkManager();
        }

        return networkManager;
    }

    private final String hostname = "81.5.99.70";//"127.0.0.1"; //
    private final int port = 27019;

    private boolean isRunning = false;

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private Queue<NetworkMessage> queue;

    private HashMap<String, int[]> result;
    private HashMap<String, List<SceneObject>> objects;

    private Thread thisThread;

    private ArrayList<Float> floatArrayList;


    public NetworkManager(){
        Open();
    }

    public void Open(){

        if( socket != null ){
            if( !socket.isClosed() ){
                return;
            }else {
                Close();
            }
        }

        try {
            socket = new Socket(hostname, port);
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        queue = new ArrayDeque<>();
        result = new HashMap<>();
        objects = new HashMap<>();

        isRunning = true;

        thisThread = new Thread(this);
        thisThread.start();

    }

    public void AddToQueue( NetworkMessage networkMessage){
        queue.add(networkMessage);


        synchronized(thisThread) {
            thisThread.notify();
        }
    }

    public ArrayList<Float> getFloatArrayList(){
        return floatArrayList;
    }

    public int[] GetResult( NetworkMessage networkMessage){
        int[] rs = result.get(String.format("%d_%d", networkMessage.x, networkMessage.y));

        if(rs != null){
            result.remove(String.format("%d_%d", networkMessage.x, networkMessage.y));
        }

        return rs;
    }

    public List<SceneObject> GetObjects( NetworkMessage networkMessage){
        List<SceneObject> objs = objects.get(String.format("%d_%d", networkMessage.x, networkMessage.y));

        if(objs != null){
            objects.remove(String.format("%d_%d", networkMessage.x, networkMessage.y));
        }

        return objs;
    }

    @Override
    public void run() {

        while (isRunning){

            if(queue.isEmpty()){

                synchronized(thisThread) {
                    try {
                        thisThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            NetworkMessage networkMessage = queue.poll();

            if(networkMessage.networkType == NetworkType.MapRequest){
                try {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 20);
                    byteBuffer.putInt(0);
                    byteBuffer.putInt(networkMessage.x);
                    byteBuffer.putInt(networkMessage.y);

                    output.write(byteBuffer.array());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] data = new byte[Settings.GRID_TILES_WIDTH * Settings.GRID_TILES_HEIGHT *4];

                try {
                    DataInputStream dataInputStream = new DataInputStream(input);
                    dataInputStream.readFully(data, 0, data.length);

                    ByteBuffer readBytes = ByteBuffer.wrap(data);

                    int[] mapFragment = new int[Settings.GRID_TILES_WIDTH * Settings.GRID_TILES_HEIGHT];

                    for( int i = 0 ; i < Settings.GRID_TILES_WIDTH * Settings.GRID_TILES_HEIGHT ; i++ ){
                        mapFragment[i] = readBytes.getInt();
                    }

                    result.put(String.format("%d_%d", networkMessage.x, networkMessage.y), mapFragment);

                    System.out.println(String.format("laod %d_%d", networkMessage.x, networkMessage.y));

                    //game objects

                    byte[] objectsBytes = new byte[512*4];
                    dataInputStream.readFully(objectsBytes, 0, objectsBytes.length);

                    ByteBuffer objectsBytesBuffer = ByteBuffer.wrap(objectsBytes);
                    objectsBytesBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    int objectsCount = objectsBytesBuffer.getInt();
                    objectsBytesBuffer.order(ByteOrder.BIG_ENDIAN);

                    if(objectsCount == 0 || objectsCount > 100 || objectsCount < 0 ){
                        System.out.println("Empty objects " + objectsCount);
                        continue;
                    }else {
                        System.out.println("Objects: " + objectsCount);
                    }

                    List<SceneObject> sceneObjectList = new ArrayList<>();
                    try{
                        for( int i = 0 ; i < objectsCount ; i++){

                            int type = objectsBytesBuffer.getInt();
                            float xxx = objectsBytesBuffer.getFloat();
                            float yyy = objectsBytesBuffer.getFloat();

                            System.out.println("type: " + type + " " + xxx + " " + yyy);

                            sceneObjectList.add(new GameObject(xxx, yyy, GameObjectTypes.values()[type]));
                        }
                    }catch(Exception e){
                        
                    }

                    objects.put(String.format("%d_%d", networkMessage.x, networkMessage.y), sceneObjectList);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(networkMessage.networkType == NetworkType.PositionRequest){
                try {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 20);

                    byteBuffer.putInt(1);
                    byteBuffer.putFloat(networkMessage.fx);
                    byteBuffer.putFloat(networkMessage.fy);

                    output.write(byteBuffer.array());

                    byte[] sizeBytes = new byte[4];

                    DataInputStream dataInputStream = new DataInputStream(input);
                    dataInputStream.read(sizeBytes, 0, sizeBytes.length);

                    ByteBuffer byteBufferSize = ByteBuffer.wrap(sizeBytes);
                    byteBufferSize.order(ByteOrder.LITTLE_ENDIAN);
                    int size = byteBufferSize.getInt();

                    System.out.println( size );

                    if( size > 0 ){

                        byte[] inputBytes = new byte[4 * 3 * size];

                        dataInputStream.read(inputBytes, 0, inputBytes.length);

                        ByteBuffer inputBytesBufferSize = ByteBuffer.wrap(inputBytes);
                        inputBytesBufferSize.order(ByteOrder.LITTLE_ENDIAN);

                        ArrayList<Float> floatArrayList1 = new ArrayList<>();

                        for(int i = 0 ; i < size ; i++){

                            float fid = inputBytesBufferSize.getFloat();
                            float fxxx = inputBytesBufferSize.getFloat();
                            float fyyy = inputBytesBufferSize.getFloat();

                            floatArrayList1.add(fid);
                            floatArrayList1.add(fxxx);
                            floatArrayList1.add(fyyy);

                            // System.out.print(fid +" "+fxxx + " " + fyyy + ": ");

                        }
                        floatArrayList = floatArrayList1;
                        // System.out.println("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void Close(){

        isRunning = false;

        synchronized (thisThread){
            thisThread.notify();
        }

        if(socket != null){
            if( !socket.isClosed() ){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket = null;
        }
    }
}
