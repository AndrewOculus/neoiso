package com.nocompany.iso.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;

public class UITexture {

    private float width, height, posx, posy;
    private SizeType type;
    private Texture texture;
    private OrthographicCamera camera;

    public UITexture( Texture texture, SizeType type, float width, float height, float posx, float posy){        
        this.type = type;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.posx = posx;
        this.posy = posy;
    }

    public void setCamera( OrthographicCamera camera ){
        this.camera = camera;
    }

    public void update(){
        if( type == SizeType.OnlyWidth ){
            this.width = camera.viewportWidth * width;
            this.height = this.width * texture.getHeight()/texture.getWidth();
            this.posx = camera.viewportWidth * this.posx;
            this.posy = camera.viewportHeight * this.posy;
        }else if( type == SizeType.OnlyHeight ){
            this.height = camera.viewportHeight * height;
            this.width = this.height * texture.getWidth()/texture.getHeight();
            this.posx = camera.viewportWidth * this.posx;
            this.posy = camera.viewportHeight * this.posy;
        }else if( type == SizeType.Both ){

        }
    }

    public void setX( float x ){
        posx = x;
    }

    public void setY( float y ){
        posy = y;
    }

    public float getSizeX(){
        return width;
    }

    public float getY(){
        return posy;
    }

    public float getX(){
        return posx;
    }

    public float getSizeY(){
        return height;
    }

    public Texture getTexture(){
        return texture;
    }
}