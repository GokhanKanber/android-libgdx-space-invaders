/*
 * Copyright 2018 Gökhan Kanber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gokhankanber.spaceinvaders.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gokhankanber.spaceinvaders.SpaceInvaders;
import com.gokhankanber.spaceinvaders.provider.Asset;
import com.gokhankanber.spaceinvaders.provider.Config;

public abstract class BaseController implements IBaseController
{
    protected SpaceInvaders game;
    protected Asset asset;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;

    public BaseController(SpaceInvaders game)
    {
        this.game = game;
    }

    @Override
    public void init()
    {
        asset = game.getAsset();
        camera = new OrthographicCamera(Config.WIDTH, Config.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void release()
    {
        batch.dispose();
    }

    protected void clear()
    {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
