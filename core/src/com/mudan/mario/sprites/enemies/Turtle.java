package com.mudan.mario.sprites.enemies;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
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
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHEEL, DEAD}
    public State currentState;
    public State previousState;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean setToDestroy;
    private boolean destroyed;
    private float stateTime;
    private float deadRotationDegree;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16 ,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16 ,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16 ,24);
        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegree =0;
        destroyed =false;

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
        fdef.restitution = 0.7f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if (currentState != State.STANDING_SHELL){
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public State getCurrentState(){
        return currentState;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (b2body.getPosition().y < 0)
            killed();
        if (currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth()/2 , b2body.getPosition().y - 8/MarioBros.PPM);
        if (currentState == State.DEAD){
            deadRotationDegree +=3;
            rotate(deadRotationDegree);
            if (stateTime > 5 && !destroyed){
                world.destroyBody(b2body);
                destroyed = true;
            }
        }
        else
            b2body.setLinearVelocity(velocity);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle){
            if (((Turtle) enemy).currentState == State.MOVING_SHEEL && currentState != State.MOVING_SHEEL){
                killed();
            }else if (currentState == State.MOVING_SHEEL && ((Turtle)enemy).currentState == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        }else if (currentState != State.MOVING_SHEEL)
            reverseVelocity(true, false);
    }

    public TextureRegion getFrame (float dt){
        TextureRegion region;

        switch (currentState){
            case MOVING_SHEEL:
            case STANDING_SHELL:
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

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHEEL;
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for (Fixture fixture : b2body.getFixtureList()){
            fixture.setUserData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
