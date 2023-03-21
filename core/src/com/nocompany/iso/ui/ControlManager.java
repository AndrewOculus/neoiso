package com.nocompany.iso.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;

public class ControlManager {

    final float grabTimer = 0.1f;

    private boolean prevTouch0 = false;
    private boolean nextTouch0 = false;
    private float pressTimer = 0;
    private PressType pressType = PressType.NO_PRESS;

    public ControlManager(){

    }

    public void update( float dt ){

        if( pressType == PressType.FREE || pressType == PressType.CLICK ){
            pressType = PressType.NO_PRESS;
        }

        prevTouch0 = nextTouch0;
        nextTouch0 = Gdx.input.isTouched(0);

        if( !prevTouch0 && nextTouch0 ){
            pressTimer = 0;
            pressType = PressType.WAIT;
            return;
        }

        if( prevTouch0 && !nextTouch0 ){
            if( pressType == PressType.WAIT ){
                pressType = PressType.CLICK;
                return;
            }else if( pressType == PressType.GRAB ){
                pressType = PressType.FREE;
                return;
            }
        }

        if( pressType == PressType.WAIT && pressTimer > grabTimer ){
            pressType = PressType.GRAB;
        }

        if( pressType == PressType.WAIT ){
            pressTimer += dt;
        }
    }
    
    public PressType getPress(){
        return pressType;
    }

}