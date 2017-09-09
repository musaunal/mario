package com.mudan.mario.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mudan.mario.MarioBros;
import com.mudan.mario.sprites.enemies.Enemy;
import com.mudan.mario.sprites.tileObjects.InteracticeTileObject;

/**
 * Created by musa on 6.09.2017.
 */

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();   // collisionda iki nesne var temas eden ve edilen bir tanesi fixA diğeri fixB
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if (fixA.getUserData() == "head" || fixB.getUserData() == "head"){      // hangisinin head(temas eden) olduğunu bulduk
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() instanceof InteracticeTileObject){     // temas edilene(objecte) onHeadHit çağırıldı , kafayla vurulduğu için
                ((InteracticeTileObject)object.getUserData()).onHeadHit();
            }
        }

        switch (cDef){
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT :
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                // mario dead;
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
