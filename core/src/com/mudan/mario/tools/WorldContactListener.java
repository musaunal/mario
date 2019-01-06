package com.mudan.mario.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mudan.mario.MarioBros;
import com.mudan.mario.sprites.Items.Item;
import com.mudan.mario.sprites.Items.Mushroom;
import com.mudan.mario.sprites.Mario;
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

        switch (cDef) {
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT)
                    ((InteracticeTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    ((InteracticeTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
            case MarioBros.ENEMY_BIT | MarioBros.BRICK_BIT:
            case MarioBros.ENEMY_BIT | MarioBros.COIN_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit(((Enemy) fixB.getUserData()));
                ((Enemy) fixB.getUserData()).onEnemyHit(((Enemy) fixA.getUserData()));
                break;
            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
            case MarioBros.MARIO_BIT | MarioBros.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(((Mario) fixA.getUserData()).b2body.getLinearVelocity().x * -1, 0)
                            , ((Mario) fixA.getUserData()).b2body.getWorldCenter()
                            , true);
                else
                    ((Mario) fixB.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(((Mario) fixB.getUserData()).b2body.getLinearVelocity().x * -1, 0)
                            , ((Mario) fixB.getUserData()).b2body.getWorldCenter()
                            , true);
                break;
            case MarioBros.MARIO_BIT | MarioBros.GATE_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario) fixA.getUserData()).isLevelUp = true;
                else
                    ((Mario) fixB.getUserData()).isLevelUp = true;
                break;
            case MarioBros.MARIO_BIT | MarioBros.BRICK_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario)fixA.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(((Mario)fixA.getUserData()).b2body.getLinearVelocity().x * -2, 0)
                            , ((Mario)fixA.getUserData()).b2body.getWorldCenter()
                            , true);
                else
                    ((Mario)fixB.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(((Mario)fixB.getUserData()).b2body.getLinearVelocity().x * -2, 0)
                            , ((Mario)fixB.getUserData()).b2body.getWorldCenter()
                            , true);
                break;
            case MarioBros.MARIO_BIT | MarioBros.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT)
                    ((Mario)fixA.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(0, 10)
                            , ((Mario)fixA.getUserData()).b2body.getWorldCenter()
                            , true);
                else
                    ((Mario)fixB.getUserData()).b2body.applyLinearImpulse(
                            new Vector2(0, 10)
                            , ((Mario)fixB.getUserData()).b2body.getWorldCenter()
                            , true);
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
