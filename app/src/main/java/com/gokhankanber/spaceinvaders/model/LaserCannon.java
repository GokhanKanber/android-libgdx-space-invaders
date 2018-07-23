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
import com.gokhankanber.spaceinvaders.provider.Config;

public class LaserCannon extends Model
{
    private enum State
    {
        READY,
        ALIVE,
        DYING,
        DEAD
    }

    private State state;
    private final float padding = 32;
    private Vector2 startPoint;
    public int points;
    public int lives = 3;
    public int level;

    public LaserCannon(float x, float y, float width, float height)
    {
        super(x, y, width, height);

        startPoint = new Vector2(x, y);
        alive();
    }

    public void reset(float x, float y)
    {
        level++;
        setPosition(x, y);
    }

    @Override
    public void update(float delta)
    {
        if(isReady())
        {
            wait(delta, 2);

            if(stateTime == 0)
            {
                alive();
            }
        }
        else if(isDying())
        {
            wait(delta, 2);

            if(stateTime == 0)
            {
                if(lives < 0)
                {
                    dead();
                }
                else
                {
                    ready();
                    setPosition(startPoint.x, startPoint.y);
                }
            }
        }
        else if(isDead())
        {
            iWorld.endGame(false);
        }

        stateTime += delta;
    }

    /**
     * Moves laser cannon by amount of change in x coordinate.
     * @param amount is change in x coordinate.
     */
    public void move(float amount)
    {
        velocity.x = amount;
        bounds.x += velocity.x;
        checkWorld();
        setPosition(bounds.x, bounds.y);
    }

    /**
     * Checks bounds of laser cannon in x coordinate to stay laser cannon in world.
     */
    public void checkWorld()
    {
        if(bounds.x + bounds.width > Config.WIDTH - padding)
        {
            bounds.x = Config.WIDTH - padding - bounds.width;
        }
        else if(bounds.x < padding)
        {
            bounds.x = padding;
        }
    }

    public void die()
    {
        iWorld.playLaserCannonSound();
        dying();
        lives--;
        iWorld.setResetWorld(true);
        iWorld.setResetLaserCannon();
        Explosion explosion = new Explosion(bounds.x, bounds.y, getWidth(), getHeight());
        explosion.laserCannon();
        explosion.time = 2;
        iWorld.addExplosion(explosion);
    }

    public void ready()
    {
        stateTime = 0;
        state = State.READY;
    }

    public void alive()
    {
        stateTime = 0;
        state = State.ALIVE;
    }

    public void dying()
    {
        stateTime = 0;
        state = State.DYING;
    }

    public void dead()
    {
        stateTime = 0;
        state = State.DEAD;
    }

    public boolean isReady()
    {
        return state == State.READY;
    }

    public boolean isAlive()
    {
        return state == State.ALIVE;
    }

    public boolean isDying()
    {
        return state == State.DYING;
    }

    public boolean isDead()
    {
        return state == State.DEAD;
    }
}
