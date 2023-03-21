package com.nocompany.iso.net;

import com.nocompany.iso.BotTestMove;
import com.nocompany.iso.ObjectsRenderer;
import com.nocompany.iso.objects.SceneObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class NetworkMove {

    private HashMap<Float, BotTestMove> sceneObjectHashMap;
    private ObjectsRenderer objectsRenderer;

    public NetworkMove( ObjectsRenderer objectsRenderer ){
        sceneObjectHashMap = new HashMap<>();
        this.objectsRenderer = objectsRenderer;
    }

    public void update( ArrayList<Float> floatArrayList ) {

        if (floatArrayList != null) {
            ListIterator<Float> iterator = floatArrayList.listIterator();

            for( int i = 0 ; i < floatArrayList.size()/3 ; i++) {

                float id = iterator.next();
                float xx = iterator.next();
                float yy = iterator.next();

                BotTestMove sceneObject = sceneObjectHashMap.get(id);

                if( id == 0f)
                    continue;

                if( sceneObject == null ){
                    BotTestMove botTestMove = new BotTestMove();
                    botTestMove.setX( xx );
                    botTestMove.setY( yy );
                    botTestMove.setAimPosition( xx, yy );

                    botTestMove.setActive(true);
                    sceneObjectHashMap.put( id, botTestMove );
                    objectsRenderer.addTileObject( botTestMove );
                }else {
                    sceneObject.setAimPosition( xx, yy );
                    sceneObject.setActive(true);
                }
            }
        }
    }
}
