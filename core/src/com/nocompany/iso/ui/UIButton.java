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
        
        private static OrthographicCamera orthographicCamera;
        private static float windowsOffsetX, windowsOffsetY;
        private static float buttonOffsetX, buttonOffsetY;
        private static float buttonSizeX, buttonSizeY;
        private static float windowsSizeX, windowsSizeY;
        private static UIButtonType buttonType;
        private static Texture idle, press;
        private static String description;

        public UIButtonBuilder(){
            windowsOffsetX = 0;
            windowsOffsetY = 0;
            buttonOffsetX = 0;
            buttonOffsetY = 0;
            buttonSizeX = 1;
            buttonSizeY = 1;
            idle = null;
            press = null;
            orthographicCamera = null;
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

        public UIButtonBuilder setCamera( OrthographicCamera orthographicCamera ){
            this.orthographicCamera = orthographicCamera;
            return this;
        }

        public UIButton build(){
            UIButton button = new UIButton();
            button.orthographicCamera = orthographicCamera;
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

    private OrthographicCamera orthographicCamera;
    private float windowsOffsetX, windowsOffsetY;
    private float buttonOffsetX, buttonOffsetY;
    private float buttonSizeX, buttonSizeY;
    private float windowsSizeX, windowsSizeY;

    private UIButtonType buttonType;
    private Texture idle, press;
    private String description;
    private boolean isTouch;

    public UIButton(){

    }
        
    public void draw( SpriteBatch spriteBatch ){
        
        float offsetX = windowsOffsetX + buttonOffsetX * windowsSizeX;
        float offsetY = windowsOffsetY + buttonOffsetY * windowsSizeY;

        if( isTouch )
            spriteBatch.draw( press, offsetX, offsetY, buttonSizeX * orthographicCamera.viewportWidth, buttonSizeY * orthographicCamera.viewportWidth );
        else
            spriteBatch.draw( idle, offsetX, offsetY, buttonSizeX * orthographicCamera.viewportWidth, buttonSizeY * orthographicCamera.viewportWidth );
        
        isTouch = false;
    }

    public void setWindowsOffset( float x, float y ){
        this.windowsOffsetX = x;
        this.windowsOffsetY = y;
    }

    public void setWindowsSize( float x, float y ){
        this.windowsSizeX = x;
        this.windowsSizeY = y;
    }

    public void setCamera( OrthographicCamera orthographicCamera ){
        this.orthographicCamera = orthographicCamera;
    }

    public boolean isTouch(float x, float y){

        float offsetX = windowsOffsetX + buttonOffsetX * windowsSizeX;
        float offsetY = windowsOffsetY + buttonOffsetY * windowsSizeY;

        if( x > offsetX && y > offsetY && x < offsetX + buttonSizeX * orthographicCamera.viewportWidth &&  y < offsetY + buttonSizeY * orthographicCamera.viewportWidth ){
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