package com.mudan.mario.sprites.enemies;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mudan.mario.MarioBros;
import com.mudan.mario.screens.PlayScreen;
import com.mudan.mario.sprites.Mario;

/**
 * Created by musa on 15.09.2017.
 */

public class Turtle extends Enemy {
    public enum State {WALKING, SHELL}
    public State currentState;
    public State previousState;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean setToDestroy;
    private boolean destroyed;
    private float stateTime;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16 ,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16 ,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16 ,24);
        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        currentState = previousState = State.WALKING;

        setBounds(getX(),getY(), 16/ MarioBros.PPM, 24/ MarioBros.PPM);

    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();      // gövde
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.BRICK_BIT | MarioBros.COIN_BIT | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT | MarioBros.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5,8).scl(1/ MarioBros.PPM);
        vertice[1] = new Vector2(5,8).scl(1/ MarioBros.PPM);
        vertice[2] = new Vector2(-3,3).scl(1/ MarioBros.PPM);
        vertice[3] = new Vector2(3,3).scl(1/ MarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        if (currentState != State.SHELL){
            currentState = State.SHELL;
            velocity.x = 0;
        }
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth()/2 , b2body.getPosition().y - 8/MarioBros.PPM);
        b2body.setLinearVelocity(velocity);
    }

    public TextureRegion getFrame (float dt){
        TextureRegion region;

        switch (currentState){
            case SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }if (velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }
}
