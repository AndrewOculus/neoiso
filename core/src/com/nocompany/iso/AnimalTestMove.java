package com.nocompany.iso;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.SceneObject;
import com.nocompany.iso.tiles.AnimationPack;
import com.nocompany.iso.utils.AssetLoader;
import com.nocompany.iso.utils.IsometricHelper;

public class AnimalTestMove implements SceneObject {

    private AnimationPack animationPack;
    private float x;
    private float y;

    private float _2dX;
    private float _2dY;
    private float isoX;
    private float isoY;

    private Vector2 _2dToIsoTmp;

    private float time;

    private float velocity = 6f;

    private int rotation = 0;
    private int mode = 0;

    private int texureSize;

    private boolean active = true;
    private TouchController touchController;

    private Texture inWaterTexture;
    private boolean inWater = false;

    public AnimalTestMove(String animal){
        _2dToIsoTmp = new Vector2();
        animationPack = AssetLoader.GetInstance().getAnimationPack(animal);
        texureSize = animationPack.getAnimations("walk", 0).getKeyFrame(0).getWidth();
        touchController = TouchController.getInstance();
        inWaterTexture = AssetLoader.GetInstance().GetInWater();
    }

    @Override
    public void update(float delta){

        time += delta;

        if(time > 20.0f)
            time = 0;

        boolean W = false;
        boolean S = false;
        boolean D = false;
        boolean A = false;

        switch(Gdx.app.getType()) {
            case Android:


                int deltaX = touchController.getJosticX0();
                int deltaY = touchController.getJosticY0();

                float mag = (float)( Math.sqrt( deltaX* deltaX + deltaY*deltaY ) );

//                Gdx.app.log("DEBUG", deltaX + " " + deltaY);

                W = deltaY > 15;
                S = deltaY < -15;

                D = deltaX > 15;
                A = deltaX < -15;

                velocity = 1f * mag * delta;


                break;
            case Desktop:

                velocity = 200 * delta;

                W = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
                S = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
                D = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
                A = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);

                break;
        }



        int keys = (W?1:0) + (S?1:0) + (D?1:0) + (A?1:0);
        mode = 0;

        if(keys != 0){
            mode = 1;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                mode = 2;
                velocity = 900 * delta;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
                mode = 2;
                velocity = 1900 * delta;
            }
        }

        if(W && keys == 1){
            rotation = 315;//0;
            _2dY += velocity/2;
        }

        if(S && keys == 1){
            rotation = 135;//180;
            _2dY -= velocity/2;
        }

        if(A && keys == 1){
            rotation = 225;//270;
            _2dX -= velocity/2;
        }

        if(D && keys == 1){
            rotation = 45;//90;
            _2dX += velocity/2;
        }

        if(W && A){
            rotation = 270;//315;
            _2dY += velocity/2;
            _2dX -= velocity/2;
        }

        if(W && D){
            rotation = 0;//45;
            _2dY += velocity/2;
            _2dX += velocity/2;
        }

        if(S && A){
            rotation = 180;//225;
            _2dY -= velocity/2;
            _2dX -= velocity/2;
        }
        if(S && D){
            rotation = 90;//135;
            _2dY -= velocity/2;
            _2dX += velocity/2;
        }

        _2dToIsoTmp.x = _2dX;
        _2dToIsoTmp.y = _2dY;

        IsometricHelper.twoDToIso(_2dToIsoTmp);

        isoX = _2dToIsoTmp.x;
        isoY = _2dToIsoTmp.y;
    }

    @Override
    public void render(SpriteBatch batch){

        Texture frame = null;

        if(mode == 0){
            if (time > 18.0f)
                frame = animationPack.getAnimations("idle1" , rotation).getKeyFrame(time, true);
            else
                frame = animationPack.getAnimations("idle2" , rotation).getKeyFrame(time, true);
        }else if(mode == 1){
            frame = animationPack.getAnimations("walk" , rotation).getKeyFrame(time, true);
        }else if(mode == 2){
            frame = animationPack.getAnimations("run" , rotation).getKeyFrame(time, true);
        }

        if(!inWater){
            batch.draw( frame , isoX - texureSize/2 , isoY);
        }else{
            batch.draw( frame , isoX - texureSize/2 , isoY - texureSize/5);
            batch.draw( inWaterTexture , isoX - inWaterTexture.getWidth()/2 , isoY + texureSize/16 );
        }

    }

    @Override
    public void renderShadow(SpriteBatch batch) {

    }

    @Override
    public GameObjectTypes getObjectType() {
        return GameObjectTypes.EMPTY;
    }

    @Override
    public float getX(){
        return _2dX;
    }

    @Override
    public float getY(){
        return _2dY;
    }

    @Override
    public float getZ() {
        return GetIsoY();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void setX(float x){
        this._2dX = x;
    }

    @Override
    public void setY(float y){
        this._2dY = y;
    }

    public float GetIsoX(){
        return isoX;
    }

    public float GetIsoY(){
        return isoY;
    }

    public void setInWater(boolean inWater){
        this.inWater = inWater;
    }

}
