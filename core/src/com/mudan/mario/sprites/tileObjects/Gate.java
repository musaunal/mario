package com.mudan.mario.sprites.tileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mudan.mario.MarioBros;
import com.mudan.mario.scenes.Hud;
import com.mudan.mario.screens.PlayScreen;
import com.mudan.mario.sprites.Items.ItemDef;
import com.mudan.mario.sprites.Items.Mushroom;
import com.mudan.mario.sprites.Mario;

/**
 * Created by musa on 22.08.2017.
 */

public class Gate extends InteracticeTileObject {
        private static TiledMapTileSet tileSet;

        public Gate(PlayScreen screen, MapObject object){
            super(screen, object);
            tileSet = map.getTileSets().getTileSet("tileset_gutter");
            fixture.setUserData(this);
            setCategoryFilter(MarioBros.GATE_BIT);
        }

        @Override
        public void onHeadHit(Mario mario) { }
    }