package com.mudan.mario.sprites.tileObjects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mudan.mario.MarioBros;
import com.mudan.mario.screens.PlayScreen;

/**
 * Created by musa on 22.08.2017.
 */

public abstract class InteracticeTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;

    public InteracticeTileObject(PlayScreen screen, Rectangle bounds){
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() /2)/ MarioBros.PPM, (bounds.getY() + bounds.getHeight() /2)/MarioBros.PPM);

        body = world.createBody(bdef);
        shape.setAsBox(bounds.getWidth()/2/MarioBros.PPM, bounds.getHeight()/2/MarioBros.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }

    public abstract void onHeadHit();
    public  void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell (){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * MarioBros.PPM / 16) ,(int)(body.getPosition().y * MarioBros.PPM /16));
        // 16 tile sizemız PPM ile çarpıtık çünkü daha önce herşeyi PPM ile bölmüştük
    }
}
