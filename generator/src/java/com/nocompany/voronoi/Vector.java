package com.nocompany.voronoi;

public class Vector {

    public double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void rotate(double deg) {
        x = x * Math.cos(deg * 180.0 / Math.PI) - y * Math.sin(deg * 180.0 / Math.PI);
        y = x * Math.sin(deg * 180.0 / Math.PI) + y * Math.cos(deg * 180.0 / Math.PI);
    }

    public double dist(){
        return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
    }

    public double dist2(){
        return Math.pow(x, 2.0) + Math.pow(y, 2.0);
    }

    public void nor(){
        double dist = dist();
        x /= dist;
        y /= dist;
    }
}