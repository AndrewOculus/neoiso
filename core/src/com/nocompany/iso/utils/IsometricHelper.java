package com.nocompany.iso.utils;

import com.badlogic.gdx.math.Vector2;

public class IsometricHelper {

    public static void isoTo2D( Vector2 point ){
        float x = point.x;
        float y = point.y;

        point.x = (2.0f * y + x) / 2.0f;
        point.y = (2.0f * y - x) / 2.0f;
    }

    public static void twoDToIso( Vector2 point ){
        float x = point.x;
        float y = point.y;

        point.x = x - y;
        point.y = (x + y) / 2;
    }
}
