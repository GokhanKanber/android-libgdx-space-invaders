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

package com.gokhankanber.spaceinvaders.controller.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gokhankanber.spaceinvaders.model.CommandAlienShip;
import com.gokhankanber.spaceinvaders.model.Explosion;
import com.gokhankanber.spaceinvaders.model.Ground;
import com.gokhankanber.spaceinvaders.model.Invader;
import com.gokhankanber.spaceinvaders.model.Laser;
import com.gokhankanber.spaceinvaders.model.LaserCannon;
import com.gokhankanber.spaceinvaders.model.Shield;
import com.gokhankanber.spaceinvaders.model.World;
import com.gokhankanber.spaceinvaders.provider.Asset;
import com.gokhankanber.spaceinvaders.provider.Config;
import java.util.List;

public class WorldRenderer
{
    // Board: Score, lives, and level number.
    private final int paddingTop = 4;
    private final int digitWidth = 48;
    private final int digitHeight = 18;
    private final int digitSpaceWidth = 6;
    private final int scoreWidth = 4 * digitWidth + 3 * digitSpaceWidth;
    private final int boardX = 16;
    private final int boardY = (int) Config.getHeight() - paddingTop - digitHeight;
    private final int livesX = boardX + scoreWidth + 2 * digitWidth;
    private final int levelNumberX = livesX + 3 * digitWidth;

    // Render
    private World world;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Asset asset;
    private Ground ground;
    private LaserCannon laserCannon;
    private List<Shield> shields;
    private List<Invader> invaders;
    private List<Laser> lasers;
    private List<Explosion> explosions;
    private CommandAlienShip commandAlienShip;
    private SpriteCache cache;
    private int cacheId;

    private TextureRegion groundRegion;
    private TextureRegion commandAlienShipRegion;
    private TextureRegion commandAlienShipExplosion;
    private TextureRegion invaderLaserExplosion;
    private TextureRegion playerLaserExplosion;
    private Animation<TextureRegion> firstInvader;
    private Animation<TextureRegion> secondInvader;
    private Animation<TextureRegion> thirdInvader;
    private TextureRegion invaderExplosion;
    private TextureRegion laserCannonRegion;
    private TextureRegion shieldRegion;
    private Animation<TextureRegion> playerExplosion;
    private Animation<TextureRegion> laserOne;
    private Animation<TextureRegion> laserTwo;
    private Animation<TextureRegion> laserThree;
    private TextureRegion playerLaser;
    private TextureRegion[] digits;
    private int invaderLoaderCount;

    public WorldRenderer(World world, SpriteBatch batch, OrthographicCamera camera)
    {
        this.world = world;
        this.batch = batch;
        this.camera = camera;
        asset = world.getAsset();

        ground = world.getGround();
        laserCannon = world.getLaserCannon();
        shields = world.getShields();
        invaders = world.getInvaders();
        lasers = world.getLasers();
        explosions = world.getExplosions();
        commandAlienShip = world.getCommandAlienShip();

        createModelViews();
        resetWorld(true);
    }

    public void render()
    {
        drawBoard();

        batch.begin();

        drawInvaders();
        drawCommandAlienShip();
        drawLaserCannon();
        drawLasers();
        drawExplosions();

        batch.end();
    }

    public void dispose()
    {
        groundRegion.getTexture().dispose();
        cache.dispose();
    }

    private void createModelViews()
    {
        Texture sprites = asset.getSprites();

        commandAlienShipRegion = new TextureRegion(sprites, Asset.COMMAND_ALIEN_SHIP[0],
                Asset.COMMAND_ALIEN_SHIP[1], Asset.COMMAND_ALIEN_SHIP[2] / 2, Asset.COMMAND_ALIEN_SHIP[3]);

        commandAlienShipExplosion = new TextureRegion(sprites, Asset.COMMAND_ALIEN_SHIP[2] / 2,
                Asset.COMMAND_ALIEN_SHIP[1], Asset.COMMAND_ALIEN_SHIP[2] / 2, Asset.COMMAND_ALIEN_SHIP[3]);

        invaderLaserExplosion = new TextureRegion(sprites, Asset.INVADER_LASER_EXPLOSION[0],
                Asset.INVADER_LASER_EXPLOSION[1], Asset.INVADER_LASER_EXPLOSION[2], Asset.INVADER_LASER_EXPLOSION[3]);

        playerLaserExplosion = new TextureRegion(sprites, Asset.PLAYER_LASER_EXPLOSION[0],
                Asset.PLAYER_LASER_EXPLOSION[1], Asset.PLAYER_LASER_EXPLOSION[2], Asset.PLAYER_LASER_EXPLOSION[3]);

        TextureRegion[] invaderOne = new TextureRegion(sprites, Asset.INVADER_ONE[0],
                Asset.INVADER_ONE[1], Asset.INVADER_ONE[2], Asset.INVADER_ONE[3])
                .split(Asset.INVADER_ONE[2] / 2, Asset.INVADER_ONE[3])[0];
        firstInvader = new Animation<>(1.0f, invaderOne[0], invaderOne[1]);

        TextureRegion[] invaderTwo = new TextureRegion(sprites, Asset.INVADER_TWO[0],
                Asset.INVADER_TWO[1], Asset.INVADER_TWO[2], Asset.INVADER_TWO[3])
                .split(Asset.INVADER_TWO[2] / 2, Asset.INVADER_TWO[3])[0];
        secondInvader = new Animation<>(1.0f, invaderTwo[0], invaderTwo[1]);

        TextureRegion[] invaderThree = new TextureRegion(sprites, Asset.INVADER_THREE[0],
                Asset.INVADER_THREE[1], Asset.INVADER_THREE[2], Asset.INVADER_THREE[3])
                .split(Asset.INVADER_THREE[2] / 2, Asset.INVADER_THREE[3])[0];
        thirdInvader = new Animation<>(1.0f, invaderThree[0], invaderThree[1]);

        invaderExplosion = new TextureRegion(sprites, Asset.INVADER_EXPLOSION[0], Asset.INVADER_EXPLOSION[1],
                Asset.INVADER_EXPLOSION[2], Asset.INVADER_EXPLOSION[3]);

        laserCannonRegion = new TextureRegion(sprites, Asset.PLAYER[0], Asset.PLAYER[1], Asset.PLAYER[2], Asset.PLAYER[3]);

        shieldRegion = new TextureRegion(sprites, Asset.SHIELD[0], Asset.SHIELD[1], Asset.SHIELD[2], Asset.SHIELD[3]);

        TextureRegion[] playerExplosions = new TextureRegion(sprites, Asset.PLAYER_EXPLOSION[0],
                Asset.PLAYER_EXPLOSION[1], Asset.PLAYER_EXPLOSION[2], Asset.PLAYER_EXPLOSION[3])
                .split(Asset.PLAYER_EXPLOSION[2] / 2, Asset.PLAYER_EXPLOSION[3])[0];
        playerExplosion = new Animation<>(0.1f, playerExplosions);

        TextureRegion[] lasers = new TextureRegion(sprites, Asset.LASERS[0], Asset.LASERS[1], Asset.LASERS[2],
                Asset.LASERS[3]).split(Asset.LASERS[2] / 12, Asset.LASERS[3])[0];
        laserOne = new Animation<>(0.1f, lasers[0], lasers[1], lasers[2], lasers[3]);
        laserTwo = new Animation<>(0.1f, lasers[4], lasers[5], lasers[6], lasers[7]);
        laserThree = new Animation<>(0.1f, lasers[8], lasers[9], lasers[10], lasers[11]);

        playerLaser = new TextureRegion(sprites, Asset.PLAYER_LASER[0], Asset.PLAYER_LASER[1],
                Asset.PLAYER_LASER[2], Asset.PLAYER_LASER[3]);

        digits = new TextureRegion(sprites, Asset.DIGITS[0], Asset.DIGITS[1], Asset.DIGITS[2],
                Asset.DIGITS[3]).split(Asset.DIGITS[2] / 10, Asset.DIGITS[3])[0];
    }

    /**
     * Creates a scoreboard with score, lives, and level number by using SpriteCache.
     */
    public void resetWorld(boolean scoreboard)
    {
        int size;
        int[] score = null;
        int[] lives = null;
        int[] level = null;

        if(scoreboard)
        {
            size = 11; // score: 4, lives: 1, level: 1, ground: 1, shields: 4
            score = buildNumber(laserCannon.points, 4);
            lives = buildNumber(laserCannon.lives > 0 ? laserCannon.lives : 0);
            level = buildNumber(laserCannon.level);
        }
        else
        {
            size = 5;
        }

        if(cache != null)
        {
            cache.dispose();
        }

        cache = new SpriteCache(size, false);
        cache.beginCache();

        add(score, boardX);
        add(lives, livesX);
        add(level, levelNumberX);
        addShields();
        addGround();

        cacheId = cache.endCache();
    }

    /**
     * Adds score, lives, and level number to board.
     * @param number
     */
    private void add(int[] number, int startX)
    {
        if(number != null)
        {
            int length = number.length;

            for(int i = 0; i < length; i++)
            {
                cache.add(digits[number[i]], startX + i * (digitWidth + digitSpaceWidth), boardY, digitWidth, digitHeight);
            }
        }
    }

    private void addGround()
    {
        groundRegion = new TextureRegion(asset.getTexture(0x234005ff));
        cache.add(groundRegion, ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight());
    }

    private void addShields()
    {
        for(Shield shield : shields)
        {
            cache.add(shieldRegion, shield.getX(), shield.getY(), shield.getWidth(), shield.getHeight());
        }
    }

    private void drawBoard()
    {
        cache.setProjectionMatrix(camera.combined);
        cache.begin();
        cache.draw(cacheId);
        cache.end();
    }

    private void drawInvaders()
    {
        if(world.isLoading())
        {
            loadInvaders();
        }

        for(Invader invader : invaders)
        {
            if(invader.isVisible())
            {
                switch(invader.getRowIndex())
                {
                    case 0:
                    case 1:
                        batch.draw(firstInvader.getKeyFrame(invader.getStateTime(), true), invader.getX(), invader.getY(), Invader.INVADER_MAX_WIDTH, Invader.INVADER_MAX_HEIGHT);
                        break;
                    case 2:
                    case 3:
                        batch.draw(secondInvader.getKeyFrame(invader.getStateTime(), true), invader.getX(), invader.getY(), Invader.INVADER_MAX_WIDTH, Invader.INVADER_MAX_HEIGHT);
                        break;
                    case 4:
                        batch.draw(thirdInvader.getKeyFrame(invader.getStateTime(), true), invader.getX(), invader.getY(), Invader.INVADER_MAX_WIDTH, Invader.INVADER_MAX_HEIGHT);
                        break;
                }
            }
        }
    }

    private void drawLasers()
    {
        for(Laser laser : lasers)
        {
            if(laser.isLaser1())
            {
                batch.draw(laserOne.getKeyFrame(laser.getStateTime(), true), laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight());
            }
            else if(laser.isLaser2())
            {
                batch.draw(laserTwo.getKeyFrame(laser.getStateTime(), true), laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight());
            }
            else if(laser.isLaser3())
            {
                batch.draw(laserThree.getKeyFrame(laser.getStateTime(), true), laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight());
            }
            else if(laser.isLaser4())
            {
                batch.draw(playerLaser, laser.getX(), laser.getY(), laser.getWidth(), laser.getHeight());
            }
        }
    }

    private void loadInvaders()
    {
        if(invaderLoaderCount < invaders.size())
        {
            invaders.get(invaderLoaderCount).setVisible(true);
            invaderLoaderCount++;
        }
        else
        {
            invaderLoaderCount = 0;
            world.ready();
        }
    }

    private void drawCommandAlienShip()
    {
        if(commandAlienShip.isFlying())
        {
            batch.draw(commandAlienShipRegion, commandAlienShip.getX(), commandAlienShip.getY(), Asset.COMMAND_ALIEN_SHIP[2], Asset.COMMAND_ALIEN_SHIP[3] * 2);
        }
    }

    private void drawLaserCannon()
    {
        if(laserCannon.isAlive())
        {
            batch.draw(laserCannonRegion, laserCannon.getX(), laserCannon.getY(), laserCannon.getWidth(), laserCannon.getHeight());
        }
        else if(laserCannon.isReady())
        {
            int stateTime = (int) (laserCannon.getStateTime() * 10);

            if(stateTime > 2)
            {
                int stateTimeMod = stateTime % 6;

                if(stateTimeMod == 0 || stateTimeMod == 1 || stateTimeMod == 2)
                {
                    batch.draw(laserCannonRegion, laserCannon.getX(), laserCannon.getY(), laserCannon.getWidth(), laserCannon.getHeight());
                }
            }
        }
        else if(laserCannon.isDying())
        {
            batch.draw(playerExplosion.getKeyFrame(laserCannon.getStateTime(), true), laserCannon.getX(), laserCannon.getY(), laserCannon.getWidth(), laserCannon.getHeight());
        }
    }

    private void drawExplosions()
    {
        for(Explosion explosion : explosions)
        {
            if(explosion.isInvader())
            {
                batch.draw(invaderExplosion, explosion.getX(), explosion.getY(), explosion.getWidth(), explosion.getHeight());
            }
            else if(explosion.isCommandAlienShip())
            {
                batch.draw(commandAlienShipExplosion, explosion.getX(), explosion.getY(), explosion.getWidth(), explosion.getHeight());
            }
            else if(explosion.isSpace())
            {
                batch.draw(playerLaserExplosion, explosion.getX(), explosion.getY(), explosion.getWidth(), explosion.getHeight());
            }
            else if(explosion.isGround())
            {
                batch.draw(invaderLaserExplosion, explosion.getX(), explosion.getY(), explosion.getWidth(), explosion.getHeight());
            }
        }
    }

    public int[] buildNumber(int number, int digits)
    {
        String value = String.valueOf(number);
        int length = value.length();

        for(int i = 0; i < digits - length; i++)
        {
            value = "0" + value;
        }

        int[] numbers = new int[value.length()];
        int i = 0;

        for(char c : value.toCharArray())
        {
            numbers[i] = Integer.parseInt(String.valueOf(c));
            i++;
        }

        return numbers;
    }

    public int[] buildNumber(int number)
    {
        return buildNumber(number, 1);
    }
}
