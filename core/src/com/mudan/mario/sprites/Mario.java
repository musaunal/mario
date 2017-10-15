package com.mudan.mario.sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mudan.mario.MarioBros;
import com.mudan.mario.screens.PlayScreen;
import com.mudan.mario.sprites.enemies.Enemy;
import com.mudan.mario.sprites.enemies.Turtle;

/**
 * Created by musa on 21.08.2017.
 */

public class Mario extends Sprite {

    public enum State {FALLING , JUMPING , RUNNING , STANDING , GROWING , DEAD}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;
    private TextureRegion marioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;
    private Animation<TextureRegion> marioRun;

    private float stateTimer;
    private boolean runningRight;
    private boolean isMarioBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean isMarioDead;

    public Mario (PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16 , 0, 16, 16));
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i*16 , 0, 16, 32));
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0 ,16 ,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0 ,16 ,32);
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0 ,16 ,16);

        defineMario();
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0 ,16 ,32);
        setBounds(0,0,16/MarioBros.PPM, 16/MarioBros.PPM);
        setRegion(marioStand);
    }

    public void update (float dt){
        if (isMarioBig)
            setPosition(b2body.getPosition().x - getWidth()/2 , b2body.getPosition().y - getHeight()/2 - 6 /MarioBros.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth()/2 , b2body.getPosition().y - getHeight()/2);
        setRegion(getFrame(dt));
        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();
        if (!isMarioDead && b2body.getPosition().y < 0){
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            isMarioDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList())
                fixture.setFilterData(filter);
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    private void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();      // gövde
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.BRICK_BIT | MarioBros.COIN_BIT | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();       // kafa
        head.set(new Vector2(-2/ MarioBros.PPM, 6/ MarioBros.PPM), new Vector2( 2/ MarioBros.PPM, 8/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;       // A sensor shape collect collision data but not renponse collision

        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }

    private void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);  // küçük olanı yok ettik

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10/MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();      // gövde
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = (short) (MarioBros.GROUND_BIT | MarioBros.BRICK_BIT | MarioBros.COIN_BIT | MarioBros.ENEMY_BIT
                        | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14/ MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();       // kafa
        head.set(new Vector2(-2/ MarioBros.PPM, 6/ MarioBros.PPM), new Vector2( 2/ MarioBros.PPM, 8/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;       // A sensor shape collect collision data but not renponse collision

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    private TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = isMarioBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = isMarioBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = isMarioBig ? bigMarioStand : marioStand;
                break;
        }
        if ((b2body.getLinearVelocity().x <0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if (isMarioDead)
            return State.DEAD;
        else if (runGrowAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y <0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return  State.RUNNING;
        else
            return State.STANDING;
    }

    private void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(16/ MarioBros.PPM, 32/MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();      // gövde
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.BRICK_BIT | MarioBros.COIN_BIT | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT | MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();       // kafa
        head.set(new Vector2(-2/ MarioBros.PPM, 6/ MarioBros.PPM), new Vector2( 2/ MarioBros.PPM, 8/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;       // A sensor shape collect collision data but not renponse collision

        b2body.createFixture(fdef).setUserData(this);

    }

    public void grow(){
        if(!isMarioBig){
            runGrowAnimation = true;
            isMarioBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(),getY(),getWidth(),getHeight() *2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    public boolean isBig(){
        return isMarioBig;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else {
            if (isMarioBig){
                isMarioBig = false;
                timeToRedefineMario = true;
                setBounds(getX(),getY(),getWidth(),getHeight()/2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }else {
                MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                isMarioDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public boolean isDead(){
        return isMarioDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }
}
