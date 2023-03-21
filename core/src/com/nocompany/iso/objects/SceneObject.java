package com.nocompany.iso.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface SceneObject {

    void render(SpriteBatch batch);

    void renderShadow(SpriteBatch batch);

    void update(float dt);

    float getX();

    float getY();

    float getZ();

    void setX(float x);

    void setY(float y);

    GameObjectTypes getObjectType();

    boolean isActive();

    void setActive(boolean active);

}
