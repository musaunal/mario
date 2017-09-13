package com.mudan.mario.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.mudan.mario.MarioBros;
import com.mudan.mario.scenes.Hud;
import com.mudan.mario.screens.PlayScreen;
import com.mudan.mario.sprites.Mario;

/**
 * Created by musa on 22.08.2017.
 */

public class Brick extends InteracticeTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen ,object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()){
            setCategoryFilter(MarioBros.DESTROYED_BIT);    // mariomuz DESROYED_BIT ile collision yapamadığı için yok tuğla kırılmış oluyor
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
