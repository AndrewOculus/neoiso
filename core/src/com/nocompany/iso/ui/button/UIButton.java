package com.nocompany.iso.ui.button;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;

public class UIButton{

    public enum UIButtonType{
        BUTTON
    }

    public static class UIButtonBuilder{
        
        private static UIButtonType buttonType;
        private static float windowsOffsetX, windowsOffsetY;
        private static float buttonOffsetX, buttonOffsetY;
        private static float buttonSizeX, buttonSizeY;
        private static String description;
        private static Texture idle, press;

        public UIButtonBuilder(){
            windowsOffsetX = 0;
            windowsOffsetY = 0;
            buttonOffsetX = 0;
            buttonOffsetY = 0;
            buttonSizeX = 1;
            buttonSizeY = 1;
            idle = null;
            press = null;
        }

        public UIButtonBuilder setDescription( String description ){
            this.description = description;
            return this;
        }

        public UIButtonBuilder setWindowsOffset( float x, float y ){
            this.windowsOffsetX = x;
            this.windowsOffsetY = y;
            return this;
        }
        
        public UIButtonBuilder setButtonOffset( float x, float y ){
            this.buttonOffsetX = x;
            this.buttonOffsetY = y;
            return this;
        }

        public UIButtonBuilder setButtonSize( float x, float y ){
            this.buttonSizeX = x;
            this.buttonSizeY = y;
            return this;
        }

        public UIButtonBuilder setType( UIButtonType buttonType ){
            this.buttonType = buttonType;
            return this;
        }

        public UIButtonBuilder setIdle( Texture texture ){
            this.idle = texture;
            return this;
        }

        public UIButtonBuilder setPress( Texture texture ){
            this.press = texture;
            return this;
        }

        public UIButton build(){
            UIButton button = new UIButton();
            button.windowsOffsetX = windowsOffsetX;
            button.windowsOffsetY = windowsOffsetY;
            button.buttonOffsetX = buttonOffsetX;
            button.buttonOffsetY = buttonOffsetY;
            button.buttonSizeX = buttonSizeX;
            button.buttonSizeY = buttonSizeY;
            button.description = description;
            button.buttonType = buttonType;
            button.press = press;
            button.idle = idle;
            return button;
        }
    }

    private UIButtonType buttonType;
    private float windowsOffsetX, windowsOffsetY;
    private float buttonOffsetX, buttonOffsetY;
    private float buttonSizeX, buttonSizeY;
    private String description;
    private Texture idle, press;
    private boolean isTouch;

    public UIButton(){

    }

    public void setWindowsOffset( float x, float y ){
        this.windowsOffsetX = x;
        this.windowsOffsetY = y;
    }
        
    public void draw( SpriteBatch spriteBatch ){
        float offsetX = windowsOffsetX + buttonOffsetX;
        float offsetY = windowsOffsetY + buttonOffsetY;
        if( isTouch )
            spriteBatch.draw( press, offsetX, offsetY, buttonSizeX, buttonSizeY );
        else
            spriteBatch.draw( idle, offsetX, offsetY, buttonSizeX, buttonSizeY );
        isTouch = false;

    }

    public void debugDraw( ShapeRenderer shapeRenderer ){
        // float offsetX = windowsOffsetX + buttonOffsetX;
        // float offsetY = windowsOffsetY + buttonOffsetY;
        // shapeRenderer.rect( offsetX, offsetY, buttonSizeX, buttonSizeY );
    }

    public boolean isTouch(float x, float y){

        float offsetX = windowsOffsetX + buttonOffsetX;
        float offsetY = windowsOffsetY + buttonOffsetY;

        if( x > offsetX && y > offsetY && x < offsetX + buttonSizeX &&  y < offsetY + buttonSizeY ){
            isTouch = true;
            return true;
        }
        isTouch = false;
        return false;
    }

    public String getDescription(){
        return description;
    }

}