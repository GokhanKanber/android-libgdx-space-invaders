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

package com.gokhankanber.spaceinvaders.model;

import com.badlogic.gdx.math.Vector2;
import com.gokhankanber.spaceinvaders.SpaceInvaders;
import com.gokhankanber.spaceinvaders.provider.Asset;
import com.gokhankanber.spaceinvaders.provider.Config;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World
{
    private enum State
    {
        START,
        LOADING,
        READY,
        PAUSE,
        RESUME,
        ENDING,
        END
    }

    private final int maxLevel = 9;
    private final int groundHeight = 10;
    private int difficulty = Config.DIFFICULTY_NORMAL;
    private State state = State.LOADING;
    private float stateTime;
    private boolean resetLaserCannon;
    private boolean checkGame;
    private boolean resetWorld;
    private boolean waitEnding;
    private boolean gameOver;
    private Asset asset;
    private Ground ground;
    private List<Laser> lasers;
    private List<Explosion> explosions;

    // LaserCannon Properties
    private LaserCannon laserCannon;
    private final int laserCannonWidth = 32;
    private final int laserCannonHeight = 16;
    private final Vector2 laserCannonStartPoint = new Vector2((Config.WIDTH - laserCannonWidth) / 2, 32);

    // Shield Properties
    private List<Shield> shields;
    private final int shieldNumber = 4;
    private final int shieldWidth = 44;
    private final int shieldHeight = 32;
    private final int shieldSpace = 48;
    private final Vector2 shieldStartPoint = new Vector2((Config.WIDTH - (4 * shieldWidth + 3 * shieldSpace)) / 2, laserCannonStartPoint.y + 2 * laserCannonHeight);

    // CommandAlienShip Properties
    private CommandAlienShip commandAlienShip;
    private final int commandAlienShipPoints = 200;
    private final int commandAlienShipWidth = 32;
    private final int commandAlienShipHeight = 16;
    private final float commandAlienShipStartPointY = Config.getHeight() - 8 - commandAlienShipHeight;

    // Invader Properties
    private List<Invader> invaders;
    private final int invaderRows = 5;
    private final int invaderColumns = 11;
    private final Vector2 invaderStartPoint = new Vector2((Config.WIDTH - invaderColumns * Invader.INVADER_MAX_WIDTH) / 2,
            shieldStartPoint.y + shieldHeight + 60);
    private final int[] invaderPaddings = {4, 6, 8};
    private final int[] invaderWidths = {24, 22, 16};
    private final int[] invaderPoints = {10, 20, 30};
    private final int[] invaderLaserHeights = {14, 12, 14};
    private List<Integer> checkedColumns;
    private float laserCreateTime;
    private Random random;

    public World(SpaceInvaders game)
    {
        asset = game.getAsset();

        ground = new Ground(0, 0, Config.WIDTH, groundHeight);
        createLaserCannon();
        createCommandAlienShip();
        newLevel(true);
        random = new Random();
    }

    public boolean isResetWorld()
    {
        return resetWorld;
    }

    public void setResetWorld(boolean resetWorld)
    {
        this.resetWorld = resetWorld;
    }

    public boolean isWaitEnding()
    {
        return waitEnding;
    }

    public void newLevel(boolean restart)
    {
        if(restart)
        {
            laserCannon.points = 0;
            laserCannon.lives = 3;
            laserCannon.level = 0;
            laserCannon.alive();
            gameOver = false;
        }

        if(laserCannon.level < maxLevel)
        {
            loading();
            laserCannon.reset(laserCannonStartPoint.x, laserCannonStartPoint.y);
            commandAlienShip.idle();
            createShields();
            createInvaders();
            initLasers();
            initExplosions();
            resetWorld = true;
        }
        else
        {
            lasers.clear();
            explosions.clear();
            ending(false);
        }
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public void loading()
    {
        stateTime = 0;
        state = State.LOADING;
    }

    public void ready()
    {
        stateTime = 0;
        state = State.READY;
    }

    public void pause()
    {
        state = State.PAUSE;

        if(commandAlienShip.isFlying())
        {
            asset.stopCommandAlienShipSound();
        }
    }

    public void resume()
    {
        stateTime = 0;
        state = State.RESUME;

        if(commandAlienShip.isFlying())
        {
            asset.playCommandAlienShipSound();
        }
    }

    public void ending(boolean wait)
    {
        this.waitEnding = wait;
        stateTime = 0;
        state = State.ENDING;

        if(commandAlienShip.isFlying())
        {
            asset.stopCommandAlienShipSound();
        }
    }

    public void end()
    {
        stateTime = 0;
        state = State.END;
    }

    public boolean isStart()
    {
        return state == State.START;
    }

    public boolean isLoading()
    {
        return state == State.LOADING;
    }

    public boolean isReady()
    {
        return state == State.READY;
    }

    public boolean isPaused()
    {
        return state == State.PAUSE;
    }

    public boolean isResumed()
    {
        return state == State.RESUME;
    }

    public boolean isEnding()
    {
        return state == State.ENDING;
    }

    public boolean isEnd()
    {
        return state == State.END;
    }

    private void createLaserCannon()
    {
        laserCannon = new LaserCannon(laserCannonStartPoint.x, laserCannonStartPoint.y, laserCannonWidth, laserCannonHeight);
        laserCannon.setListener(iWorld);
    }

    private void createCommandAlienShip()
    {
        commandAlienShip = new CommandAlienShip(0, commandAlienShipStartPointY, commandAlienShipWidth, commandAlienShipHeight);
        commandAlienShip.setListener(iWorld);
    }

    private void createShields()
    {
        if(shields == null)
        {
            shields = new ArrayList<>();
        }
        else
        {
            shields.clear();
        }

        for(int i = 0; i < shieldNumber; i++)
        {
            Shield shield = new Shield(shieldStartPoint.x + i * (shieldWidth + shieldSpace), shieldStartPoint.y, shieldWidth, shieldHeight);
            shields.add(shield);
        }
    }

    private void createInvaders()
    {
        if(invaders == null)
        {
            invaders = new ArrayList<>();
        }
        else
        {
            invaders.clear();
        }

        if(checkedColumns == null)
        {
            checkedColumns = new ArrayList<>();
        }
        else
        {
            checkedColumns.clear();
        }

        Invader.speedRatio = 1;
        Invader.unitDirection = 1;
        Invader.unitLeft = invaderStartPoint.x;

        for(int y = 0; y < invaderRows; y++)
        {
            for(int x = 0; x < invaderColumns; x++)
            {
                Invader invader = new Invader(invaderStartPoint.x + (x * Invader.INVADER_MAX_WIDTH),
                        invaderStartPoint.y + 2 * y * Invader.INVADER_MAX_HEIGHT,
                        invaderWidths[getInvaderIndex(y)],
                        Invader.INVADER_MAX_HEIGHT);
                invader.setListener(iWorld);
                invader.setBounds(invaderPaddings[getInvaderIndex(y)], 0);
                invader.setColumnIndex(x);
                invader.setRowIndex(y);
                invader.setPoints(invaderPoints[getInvaderIndex(y)]);

                if(y == 0)
                {
                    invader.setLaser();
                }

                invaders.add(invader);

                Invader.unitRight = invader.getX() + Invader.INVADER_MAX_WIDTH;
            }
        }
    }

    private int getInvaderIndex(int i)
    {
        switch(i)
        {
            case 0:
            case 1:
                return 0;
            case 2:
            case 3:
                return 1;
            case 4:
                return 2;
            default:
                return 0;
        }
    }

    private void initLasers()
    {
        if(lasers == null)
        {
            lasers = new ArrayList<>();
        }
        else
        {
            lasers.clear();
        }
    }

    private void initExplosions()
    {
        if(explosions == null)
        {
            explosions = new ArrayList<>();
        }
        else
        {
            explosions.clear();
        }
    }

    public Asset getAsset()
    {
        return asset;
    }

    public LaserCannon getLaserCannon()
    {
        return laserCannon;
    }

    public Ground getGround()
    {
        return ground;
    }

    public List<Shield> getShields()
    {
        return shields;
    }

    public List<Invader> getInvaders()
    {
        return invaders;
    }

    public List<Laser> getLasers()
    {
        return lasers;
    }

    public List<Explosion> getExplosions()
    {
        return explosions;
    }

    public CommandAlienShip getCommandAlienShip()
    {
        return commandAlienShip;
    }

    public void update(float delta)
    {
        if(isResumed())
        {
            laserCannon.update(delta);

            if(laserCannon.isAlive())
            {
                updateInvaders(delta);
                commandAlienShip.update(delta);
                updateLasers(delta);
                updateExplosions(delta);

                wait(delta, laserCreateTime);

                if(stateTime == 0 && invaders.size() > 0)
                {
                    createInvaderLaser();
                }

                resetLaserCannon();
                checkGame();
            }
        }
        else if(isReady())
        {
            // Wait for 3 seconds on game start or touch to start.
            wait(delta, 3);

            if(stateTime == 0)
            {
                resume();
            }
        }
        else if(isEnding())
        {
            wait(delta, 1);

            if(stateTime == 0)
            {
                waitEnding = false;
            }
        }
    }

    private void updateInvaders(float delta)
    {
        for(Invader invader : invaders)
        {
            invader.update(delta);

            if(isEnding())
            {
                break;
            }
        }

        if(Invader.checkWorldState)
        {
            Invader.checkWorldState = false;
        }
    }

    private void updateLasers(float delta)
    {
        Iterator<Laser> it = lasers.iterator();

        while(it.hasNext())
        {
            Laser laser = it.next();
            laser.update(delta);

            if(laser.isExploding())
            {
                it.remove();
            }
        }
    }

    private void updateExplosions(float delta)
    {
        Iterator<Explosion> it = explosions.iterator();

        while(it.hasNext())
        {
            Explosion explosion = it.next();
            explosion.update(delta);

            if(explosion.isEnd())
            {
                it.remove();
            }
        }
    }

    private void setLaser(Invader invader)
    {
        boolean isChecked = false;

        if(invader.hasLaser())
        {
            isChecked = true;
            checkedColumns.add(invader.getColumnIndex());
        }
        else
        {
            for(int columnIndex : checkedColumns)
            {
                if(invader.getColumnIndex() == columnIndex)
                {
                    isChecked = true;
                    break;
                }
            }
        }

        if(!isChecked)
        {
            checkedColumns.add(invader.getColumnIndex());
            invader.setLaser();
        }
    }

    public void createLaser()
    {
        Iterator<Laser> it = lasers.iterator();
        boolean hasLaser = false;

        while(it.hasNext())
        {
            Laser laser = it.next();

            if(laser.isSourceLaserCannon())
            {
                hasLaser = true;
            }
        }

        if(!hasLaser)
        {
            asset.playLaserSound();
            Laser laser = new Laser(laserCannon.getX() + laserCannon.getWidth() / 2, laserCannon.getY() + laserCannon.getHeight(), 2, 12);
            laser.setSourceLaserCannon();
            laser.setListener(iWorld);
            lasers.add(laser);
        }
    }

    private void createInvaderLaser()
    {
        List<Integer> invaderLaserList = new ArrayList<>();

        if(laserCreateTime > 0)
        {
            int index = 0;

            for(Invader invader : invaders)
            {
                if(invader.hasLaser())
                {
                    invaderLaserList.add(index);
                }

                index++;
            }

            int invaderLaserIndex = random.nextInt(invaderLaserList.size());
            Invader invader = invaders.get(invaderLaserList.get(invaderLaserIndex));
            int type = random.nextInt(3);
            Laser laser = new Laser(invader.getX(), invader.getY(), 6, invaderLaserHeights[type]);
            laser.setListener(iWorld);
            laser.setType(type);
            lasers.add(laser);
            invaderLaserList.clear();
        }

        // set next laser create time
        laserCreateTime = random.nextInt(3) + random.nextFloat();
    }

    private void resetLaserCannon()
    {
        if(resetLaserCannon)
        {
            resetLaserCannon = false;

            lasers.clear();
            explosions.clear();
        }
    }

    private void checkGame()
    {
        if(checkGame)
        {
            checkGame = false;

            if(invaders.size() == 0)
            {
                newLevel(false);
            }
        }
    }

    public void wait(float delta, float waitTime)
    {
        if(stateTime >= waitTime)
        {
            stateTime = 0;
        }
        else
        {
            stateTime += delta;
        }
    }

    // World interface instance for models.
    private IWorld iWorld = new IWorld()
    {
        @Override
        public List<Invader> getInvaders()
        {
            return invaders;
        }

        @Override
        public List<Shield> getShields()
        {
            return shields;
        }

        @Override
        public CommandAlienShip getCommandAlienShip()
        {
            return commandAlienShip;
        }

        @Override
        public LaserCannon getLaserCannon()
        {
            return laserCannon;
        }

        @Override
        public Ground getGround()
        {
            return ground;
        }

        @Override
        public float getShieldBorder()
        {
            return shieldStartPoint.y + shieldHeight;
        }

        @Override
        public void removeInvader(int index)
        {
            float left = Config.WIDTH;
            float right = 0;

            laserCannon.points += invaders.get(index).getPoints();
            invaders.remove(index);

            if(invaders.size() > 0)
            {
                for(Invader invader : invaders)
                {
                    if(invader.getX() < left)
                    {
                        left = invader.getX();
                    }

                    if(invader.getX() + Invader.INVADER_MAX_WIDTH > right)
                    {
                        right = invader.getX() + Invader.INVADER_MAX_WIDTH;
                    }

                    setLaser(invader);
                }

                checkedColumns.clear();
                Invader.unitLeft = left;
                Invader.unitRight = right;
                Invader.speedRatio = 1 + (difficulty / invaders.size()) + (laserCannon.level / Config.MAX_LEVEL);
            }

            resetWorld = true;
        }

        @Override
        public void removeCommandAlienShip()
        {
            laserCannon.points += commandAlienShipPoints;
            commandAlienShip.idle();
        }

        @Override
        public void removeShields()
        {
            shields.clear();
            resetWorld = true;
        }

        @Override
        public void setResetLaserCannon()
        {
            resetLaserCannon = true;
        }

        @Override
        public void setCheckGame()
        {
            checkGame = true;
        }

        @Override
        public void endGame(boolean wait)
        {
            gameOver = true;
            ending(wait);
        }

        @Override
        public void setResetWorld(boolean resetWorld)
        {
            World.this.setResetWorld(resetWorld);
        }

        @Override
        public void addExplosion(Explosion explosion)
        {
            explosions.add(explosion);
        }

        @Override
        public void playLaserCannonSound()
        {
            asset.playLaserCannonSound();
        }

        @Override
        public void playExplosionSound()
        {
            asset.playExplosionSound();
        }

        @Override
        public void playInvaderMoveOneSound()
        {
            asset.playInvaderMoveOneSound();
        }

        @Override
        public void playInvaderMoveTwoSound()
        {
            asset.playInvaderMoveTwoSound();
        }

        @Override
        public void playCommandAlienShipSound()
        {
            asset.playCommandAlienShipSound();
        }

        @Override
        public void stopCommandAlienShipSound()
        {
            asset.stopCommandAlienShipSound();
        }
    };
}
