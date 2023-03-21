package com.nocompany.iso;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.SceneObject;
import com.nocompany.iso.tiles.AnimationPack;
import com.nocompany.iso.utils.AssetLoader;

public class BotTestMove implements SceneObject {

    private AnimationPack animationPack;
    private float x;
    private float y;

    private float aimX;
    private float aimY;

    private float time;

    private float velocity = 6f;

    private int rotation = 0;
    private int mode = 0;
    private int texureSize;

    private boolean active;

    private final float DEACTIVATE_TIME = 10.0f;
    private float deactivateTimer = 0.0f;

    public BotTestMove(){
        animationPack = AssetLoader.GetInstance().getAnimationPack("HUMAN");
        texureSize = animationPack.getAnimations("walk", 0).getKeyFrame(0).getWidth();
    }

    public void setAimPosition( float x, float y ){
        deactivateTimer = 0f;
        this.aimX = x;
        this.aimY = y;
    }

    @Override
    public void render(SpriteBatch batch) {

        Texture frame;
        if( mode == 0 ){
            frame = animationPack.getAnimations("idle2" , rotation).getKeyFrame(time, true);
        }else{
            frame = animationPack.getAnimations("walk" , rotation).getKeyFrame(time, true);
        }
        batch.draw( frame , x - texureSize/2 , y );

    }

    @Override
    public void renderShadow(SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {

        time += delta;
        deactivateTimer += delta;

        if( deactivateTimer > DEACTIVATE_TIME ){
            this.active = false;
        }

        if(time > 20.0f)
            time = 0;

        float deltaX = aimX - x;
        float deltaY = aimY - y;

        float dynamicCoef = 2;

        if( Math.abs( deltaX ) > 0.01f ){
            x += ( deltaX ) * dynamicCoef * delta;
        }

        if( Math.abs( deltaY ) > 0.01f ){
            y += ( deltaY ) * dynamicCoef * delta;
        }

        int shift = 4;
        boolean UP = deltaY > shift;
        boolean DOWN = deltaY < shift;
        boolean MIDX = deltaX < shift && deltaX > -shift;

        boolean LEFT = deltaX < shift;
        boolean RIGHT = deltaX > shift;
        boolean MIDY = deltaY < shift && deltaY > -shift;

        if( MIDY && MIDX ){
            mode = 0;
            return;
        }

        mode = 1;

        if( UP && MIDX ){
            rotation = 0;
            return;
        }

        if( DOWN && MIDX ){
            rotation = 180;
            return;
        }

        if( RIGHT && MIDY ){
            rotation = 90;
            return;
        }

        if( LEFT && MIDY ){
            rotation = 270;
            return;
        }

        if( UP && RIGHT ){
            rotation = 45;
            return;
        }

        if( DOWN && RIGHT ){
            rotation = 135;
            return;
        }

        if( DOWN && LEFT ){
            rotation = 225;
            return;
        }

        if( UP && LEFT ){
            rotation = 315;
            return;
        }
//        x = aimX;
//        y = aimY;

    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return getY();
    }

    @Override
    public void setX(float x) {
        this.x = x ;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public GameObjectTypes getObjectType() {
        return null;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
