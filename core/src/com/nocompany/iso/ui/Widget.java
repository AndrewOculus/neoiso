package com.nocompany.iso.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;

import com.nocompany.iso.ui.button.*;
import java.util.LinkedList;


public class Widget {

    private Texture texture;
    private UITexture UITexture;
    private LinkedList<UIButton> uiButtons;

    public Widget( UITexture UITexture ){
        this.texture = UITexture.getTexture();
        this.UITexture = UITexture;
        this.uiButtons = new LinkedList<>();
    }

    public void draw ( SpriteBatch batch ){
        batch.draw( texture, UITexture.getX(), UITexture.getY(), UITexture.getSizeX(), UITexture.getSizeY() );

        for( UIButton button : uiButtons ){
            button.draw( batch );
        }
    }

    public void debugDraw ( ShapeRenderer shapeRenderer ){
        for( UIButton button: uiButtons ){
            button.setWindowsOffset( UITexture.getX(), UITexture.getY() );
            button.debugDraw( shapeRenderer );
        }
    }

    public void update ( OrthographicCamera camera ){
        UITexture.setCamera(camera);
        UITexture.update();
    }

    public void setPos ( float x, float y ){
        UITexture.setX(x - Gdx.graphics.getWidth()/2);
        UITexture.setY(y - Gdx.graphics.getHeight()/2);
    }

    public UITexture uiTexture (){
        return UITexture;
    }

    public void addUIButton ( UIButton uiButton ){
        this.uiButtons.add(uiButton);
    }

    public boolean isButtonTouch( float px, float py ){

        float tx = px - Gdx.graphics.getWidth()/2 ;
        float ty = py - Gdx.graphics.getHeight()/2 ;

        for( UIButton button: uiButtons ){
            boolean isTouch = button.isTouch( tx, ty );
            if( isTouch ){
                System.out.println( button.getDescription() );
                return true;
            }
        }
        return false;
    }

    public boolean isTouched ( float px, float py, OrthographicCamera camera ){

        float tx = px - Gdx.graphics.getWidth()/2 ;
        float ty = py - Gdx.graphics.getHeight()/2 ;

        System.out.println( " " + UITexture.getX() + " " + UITexture.getY());

        if( tx > UITexture.getX() && ty > UITexture.getY() && tx < UITexture.getX()+UITexture.getSizeX() && ty < UITexture.getY()+UITexture.getSizeY()  ){
            return true;
        }
        return false;
    }

}