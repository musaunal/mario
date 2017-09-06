package com.mudan.mario.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mudan.mario.MarioBros;
import com.mudan.mario.screens.PlayScreen;

/**
 * Created by musa on 21.08.2017.
 */

public class Mario extends Sprite {

    public enum State {FALLING , JUMPNG , RUNNING , STANDING };
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation marioRun;
    private Animation marioJump;
    private float stateTimer;
    private boolean runningRight;

    public Mario (World world, PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(getTexture(), i*16 , 0, 16, 16));
        marioRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 4; i<6; i++)
            frames.add(new TextureRegion(getTexture() , i*16, 0 ,16, 16));
        marioJump = new Animation(0.1f, frames);

        defineMario();
        marioStand = new TextureRegion(getTexture(), 0, 10, 16, 16);
        setBounds(0,0,16/MarioBros.PPM, 16/MarioBros.PPM);
        setRegion(marioStand);
    }

    public void update (float dt){
        setPosition(b2body.getPosition().x - getWidth()/2 , b2body.getPosition().y - getHeight()/2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
    }

    public State getState(){
        if (b2body.getLinearVelocity().y > 0)
            return State.JUMPNG;
        if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32/ MarioBros.PPM, 32/MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }

}
