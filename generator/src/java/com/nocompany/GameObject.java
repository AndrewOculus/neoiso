package com.nocompany;

public class GameObject {
    private int gameObjectType;
    private float x, y;

    public GameObject(int gameObjectType, float x, float y){
        this.x = x;
        this.y = y;
        this.gameObjectType = gameObjectType;
    }

    public int getGameObjectType(){
        return this.gameObjectType;
    }

    public float getY(){
        return this.y;
    }

    public float getX(){
        return this.x;
    }
}