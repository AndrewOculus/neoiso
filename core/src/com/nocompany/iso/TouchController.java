package com.nocompany.iso;

import com.badlogic.gdx.Gdx;

public class TouchController {

    private static TouchController touchController;

    public static TouchController getInstance(){

        if(touchController == null){
            touchController = new TouchController();
        }

        return touchController;
    }

    public TouchController(){

    }

    static final int TOUCHES = 5;
    int[] newTouches = new int[TOUCHES];
    int[] oldTouches = new int[TOUCHES];
    int activeTouches = 0;

    int cornerX = 0;
    int cornerY = 0;

    int currentX = 0;
    int currentY = 0;

    int zoomCorner0X = 0;
    int zoomCorner0Y = 0;

    int zoomCorner1X = 0;
    int zoomCorner1Y = 0;

    float newZoom = 0;
    float oldZoom = 0;
    float tmpZoom = 0;

    public void update(){

        activeTouches = 0;
        for (int i = 0; i < TOUCHES; i++) {
            newTouches[i] = Gdx.input.isTouched(i) ? 1 : 0;
            activeTouches += newTouches[i];
        }

//        for (int i = 0; i < TOUCHES; i++) {
//            Gdx.app.log("DEBUG", newTouches[0] + " " + oldTouches[0]);
//        }

        if( activeTouches == 1 && newTouches[0] == 1 ){

            if( oldTouches[0] == 0 && newTouches[0] == 1 ){
                cornerX = Gdx.input.getX();
                cornerY = Gdx.graphics.getHeight() - Gdx.input.getY();
            }

            currentX = Gdx.input.getX(0);
            currentY = Gdx.graphics.getHeight() - Gdx.input.getY(0);
        }

        if( oldTouches[0] == 1 && newTouches[0] == 0 ){
            cornerX = 0;
            cornerY = 0;

            currentX = 0;
            currentY = 0;
        }

        if( newTouches[0] == 0 && newTouches[1] == 0  ) {
            newZoom = 0;
            oldZoom = 0;
            tmpZoom = 0;
        }


        if( activeTouches == 2 && newTouches[0] == 1 && newTouches[1] == 1 ){

            if( oldTouches[1] == 0 && newTouches[1] == 1 ){
                zoomCorner0X = Gdx.input.getX(0);
                zoomCorner0Y = Gdx.graphics.getHeight() - Gdx.input.getY(0);

                zoomCorner1X = Gdx.input.getX(1);
                zoomCorner1Y = Gdx.graphics.getHeight() - Gdx.input.getY(1);

                tmpZoom = (float)( Math.sqrt( Math.pow(zoomCorner1X - zoomCorner0X, 2) + Math.pow(zoomCorner1Y - zoomCorner0Y, 2) ) );
                oldZoom = tmpZoom;
            }

            zoomCorner0X = Gdx.input.getX(0);
            zoomCorner0Y = Gdx.graphics.getHeight() - Gdx.input.getY(0);

            zoomCorner1X = Gdx.input.getX(1);
            zoomCorner1Y = Gdx.graphics.getHeight() - Gdx.input.getY(1);

            tmpZoom = oldZoom;
            oldZoom = (float)( Math.sqrt( Math.pow(zoomCorner1X - zoomCorner0X, 2) + Math.pow(zoomCorner1Y - zoomCorner0Y, 2) ) );

        }


        for (int i = 0; i < TOUCHES; i++) {
            oldTouches[i] = newTouches[i];
        }

    }

    public int getJosticX0(){
        return currentX - cornerX;
    }

    public int getJosticY0(){
        return currentY - cornerY;
    }

    public float getZoom(){
        return 0.001f * ( tmpZoom - oldZoom );
    }

}
