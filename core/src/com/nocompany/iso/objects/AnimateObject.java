package com.nocompany.iso.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nocompany.iso.utils.AssetLoader;

import java.util.concurrent.ThreadLocalRandom;

public class AnimateObject implements SceneObject {

    Animation<Texture> animation;
    Texture currentFrame;
    float currentTime;
    float x;
    float y;
    boolean isActive;

    public AnimateObject( float x, float y){
        this.animation = AssetLoader.GetInstance().GetAnimObject();
        this.currentFrame = animation.getKeyFrame(0, true);
        this.currentTime = (float) ThreadLocalRandom.current().nextDouble(0.0f, 0.6f);
        this.x = x;
        this.y = y;
        this.isActive = true;
    }

    @Override
    public void render(SpriteBatch batch) {
//        System.out.println("Draw " + x + " " + y);
    }

    @Override
    public void renderShadow(SpriteBatch batch) {
        batch.draw(currentFrame, x, y);
    }

    @Override
    public void update(float dt) {
//        System.out.println("Update " + x + " " + y);
        currentTime += dt;
        currentFrame = animation.getKeyFrame(currentTime, true);
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
        return 0;
    }

    @Override
    public void setX(float x) {
        this.x = x;
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
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
