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

package com.gokhankanber.spaceinvaders.controller.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.gokhankanber.spaceinvaders.R;
import com.gokhankanber.spaceinvaders.SpaceInvaders;
import com.gokhankanber.spaceinvaders.controller.BaseController;
import com.gokhankanber.spaceinvaders.controller.main.MainInputController.InputListener;
import com.gokhankanber.spaceinvaders.provider.Config;
import com.gokhankanber.spaceinvaders.view.GameScreen;

public class MainMenuController extends BaseController
{
    private final float blockSize = 32;

    private final float logoTextureWidth = 256;
    private final float logoTextureHeight = 64;
    private final float logoX = (Config.WIDTH - logoTextureWidth) / 2;
    private final float logoY = Config.getHeight() - blockSize - logoTextureHeight;
    private Texture logoTexture;

    private final float mainMenuItemWidth = 320;
    private final float mainMenuItemX = (Config.WIDTH - mainMenuItemWidth) / 2;
    private final int menuItemFontSize = 24;
    private Texture[] menuItemTextures;
    private float mainMenuHeight;
    private float[] mainMenuItemsFontX;
    private float[] mainMenuItemsFontY;
    private float[] mainMenuItemsBoundY;
    private String[] mainMenuItems;
    private Rectangle playBounds;
    private MainInputController inputController;

    public MainMenuController(SpaceInvaders game)
    {
        super(game);
    }

    @Override
    public void init()
    {
        super.init();

        inputController = new MainInputController(camera, inputListener);
        Gdx.input.setInputProcessor(inputController);
        Gdx.input.setCatchBackKey(false);

        logoTexture = asset.getLogoTexture();
        initMainMenu();
    }

    private void initMainMenu()
    {
        mainMenuItems = new String[]{
                game.getResources().getString(R.string.play)
        };

        int menuItemsLength = mainMenuItems.length;
        mainMenuItemsFontX = new float[menuItemsLength];
        mainMenuItemsFontY = new float[menuItemsLength];
        mainMenuItemsBoundY = new float[menuItemsLength];
        mainMenuHeight = Config.getHeight() - blockSize - logoTextureHeight;

        int i = 0;
        menuItemTextures = new Texture[menuItemsLength];

        for(String item : mainMenuItems)
        {
            menuItemTextures[i] = asset.getTexture(item);
            mainMenuItemsFontX[i] = (Config.WIDTH - item.length() * menuItemFontSize) / 2;
            mainMenuItemsBoundY[i] = (mainMenuHeight + (menuItemsLength * blockSize)) / 2 - (i + 1) * blockSize;
            mainMenuItemsFontY[i] = mainMenuItemsBoundY[i] + (blockSize - menuItemFontSize) / 2;
            i++;
        }

        playBounds = new Rectangle(mainMenuItemX, mainMenuItemsBoundY[0], mainMenuItemWidth, blockSize);
    }

    @Override
    public void update(float delta)
    {
    }

    @Override
    public void draw(float delta)
    {
        clear();

        batch.begin();
        batch.draw(logoTexture, logoX, logoY, logoTextureWidth, logoTextureHeight);

        int menuItemsLength = mainMenuItems.length;

        for(int i = 0; i < menuItemsLength; i++)
        {
            batch.draw(menuItemTextures[i], mainMenuItemsFontX[i], mainMenuItemsFontY[i], mainMenuItems[i].length() * menuItemFontSize, menuItemFontSize);
        }

        batch.end();
    }

    @Override
    public void release()
    {
        super.release();

        logoTexture.dispose();

        for(Texture texture : menuItemTextures)
        {
            texture.dispose();
        }
    }

    private InputListener inputListener = new InputListener()
    {
        @Override
        public void check(float x, float y)
        {
            if(playBounds.contains(x, y))
            {
                asset.playButtonSound();
                game.setScreen(new GameScreen(game));
            }
        }
    };
}
