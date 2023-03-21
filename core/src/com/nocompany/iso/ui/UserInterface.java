package com.nocompany.iso.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import java.util.LinkedList;
import com.nocompany.iso.ui.Widget;

public class UserInterface {

    LinkedList<Widget> widgets;
    ControlManager controlManager;
    OrthographicCamera camera;
    SpriteBatch batch; 
    ShapeRenderer shapeRenderer;

    public UserInterface(){
        widgets = new LinkedList<Widget>();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
		camera.position.set(0, 0, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        controlManager = new ControlManager();
    }

    public void draw(){
        batch.begin();
        for( Widget widget: widgets ){
            widget.draw(batch);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for( Widget widget: widgets ){
            widget.debugDraw(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public void addWidget( Widget widget ){
        widget.update(camera);
        widgets.add( widget );
    }

    Widget grabWidget = null;
    float offsetX = 0;
    float offsetY = 0;

    public void update( float dt ){

        if( controlManager.getPress() == PressType.WAIT ){
            float px = Gdx.input.getX();
            float py = Gdx.graphics.getHeight() - Gdx.input.getY();

            grabWidget = null;

            // System.out.println("" + px + " " + py);
            int idx = 0;
            for( Widget widget: widgets ){
                boolean touch = widget.isTouched(px, py, camera);
                boolean isButton = widget.isButtonTouch(px, py);

                if( touch && !isButton ){
                    grabWidget = widget;
                    offsetX = widget.uiTexture().getX() - px + Gdx.graphics.getWidth()/2;
                    offsetY = widget.uiTexture().getY() - py + Gdx.graphics.getHeight()/2;

                    widgets.remove(idx);
                    widgets.add(widget);

                    break;
                }

                idx++;
            }
        }

        if( controlManager.getPress() == PressType.GRAB && grabWidget != null ){
            System.out.println("Grab window");
            grabWidget.setPos( Gdx.input.getX() + offsetX, Gdx.graphics.getHeight() - Gdx.input.getY() + offsetY );
        }

        controlManager.update(dt);
        // System.out.println(  ); 
    }

}