package com.mudan.mario.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.mudan.mario.MarioBros;
import com.mudan.mario.scenes.Hud;
import com.mudan.mario.screens.PlayScreen;

/**
 * Created by musa on 22.08.2017.
 */

public class Brick extends InteracticeTileObject {
    public Brick(PlayScreen screen, Rectangle bounds){
        super(screen ,bounds);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
      //  Gdx.app.log("Brick" , "Collision");
        setCategoryFilter(MarioBros.DESTROYED_BIT);    // mariomuz DESROYED_BIT ile collision yapamadığı için yok tuğla kırılmış oluyor
        getCell().setTile(null);
        Hud.addScore(200);
        MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
