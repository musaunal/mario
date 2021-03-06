package com.mudan.mario.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mudan.mario.MarioBros;
import com.mudan.mario.screens.PlayScreen;
import com.mudan.mario.sprites.enemies.Enemy;
import com.mudan.mario.sprites.enemies.Turtle;
import com.mudan.mario.sprites.tileObjects.Brick;
import com.mudan.mario.sprites.tileObjects.Coin;
import com.mudan.mario.sprites.enemies.Goomba;
import com.mudan.mario.sprites.tileObjects.Gate;

/**
 * Created by musa on 22.08.2017.
 */

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    private int[] indexes;
    public B2WorldCreator (PlayScreen screen , int level){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        if (level == 0)
            indexes = new int[]{4,5,7,6,9,8,10};
        else if (level == 1)
            indexes = new int[]{2,3,5,4,6,7,8};

        // zemin
        for (MapObject object : map.getLayers().get(indexes[0]).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // boru
        for (MapObject object : map.getLayers().get(indexes[1]).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBros.PPM, rect.getHeight() / 2 / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }

        // tuğla
        for (MapObject object : map.getLayers().get(indexes[2]).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }

        //gate
        for (MapObject object : map.getLayers().get(indexes[6]).getObjects().getByType(RectangleMapObject.class)){
            new Gate(screen, object);
        }

        //coin
        for (MapObject object : map.getLayers().get(indexes[3]).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(indexes[4]).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX() /MarioBros.PPM, rect.getY() /MarioBros.PPM));
        }
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(indexes[5]).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
    public Array<Enemy> enemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
