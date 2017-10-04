package com.mudan.mario.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mudan.mario.MarioBros;

/**
 * Created by musa on 3.10.2017.
 */

public class Controller implements Disposable {
    private Viewport viewport;
    private Stage stage;
    boolean upPressed, downPressed ,leftPressed ,rigthPressed;
    OrthographicCamera camera;

    public Controller (MarioBros mb){
        camera = new OrthographicCamera();
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT ,camera);
        stage = new Stage(viewport ,mb.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.left().bottom();

        Image left = new Image(new Texture("left.png"));
        left.setSize(40,40);
        left.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed= true;
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image sag = new Image(new Texture("sag.png"));
        sag.setSize(40,40);
        sag.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rigthPressed = true;
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rigthPressed = false;
            }
        });

        table.add(left).size(left.getWidth(), left.getHeight()).padRight(315);
        table.add(sag).size(left.getWidth(), left.getHeight());

        stage.addActor(table);
    }

    public void draw(){
        stage.draw();
    }

    public void resize(int x, int y){
        viewport.update(x,y);
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRigthPressed() {
        return rigthPressed;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
