package com.mudan.mario.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by musa on 22.08.2017.
 */

public class Brick extends InteracticeTileObject {
    public Brick(World world, TiledMap map, Rectangle bounds){
        super(world, map,bounds);
        fixture.setUserData(this);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick" , "Collision");
    }
}
