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

import com.gokhankanber.spaceinvaders.provider.Asset;
import com.gokhankanber.spaceinvaders.provider.Config;
import java.util.Random;

public class CommandAlienShip extends Model
{
    private int waitTime;
    private float startPoint;
    private int direction;

    public enum State
    {
        IDLE,
        FLYING
    }

    private State state;

    public CommandAlienShip(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta;

        if(isIdle())
        {
            if(stateTime >= waitTime)
            {
                stateTime = 0;
                fly();
            }
        }
        else if(isFlying())
        {
            bounds.x += direction * Config.COMMAND_ALIEN_SHIP_VELOCITY;

            if(checkWorld())
            {
                position.x += direction * Config.COMMAND_ALIEN_SHIP_VELOCITY;
            }
        }
    }

    private boolean checkWorld()
    {
        if((direction == 1 && bounds.x > Config.WIDTH) || (direction == -1 && bounds.x + bounds.width < 0))
        {
            idle();
            iWorld.setResetWorld(true);

            return false;
        }

        return true;
    }

    public void idle()
    {
        state = State.IDLE;
        iWorld.stopCommandAlienShipSound();
        waitTime = getRandomValue(Config.MIN_TIME, Config.MAX_TIME);
        startPoint = (getRandomValue(0, 1) == 0 ? -Asset.COMMAND_ALIEN_SHIP[2] : Config.WIDTH);
        direction = (startPoint < 0 ? 1 : -1);
        setPositionX(startPoint);
        setBounds(8, 0);
    }

    public void fly()
    {
        state = State.FLYING;
        iWorld.playCommandAlienShipSound();
        iWorld.setResetWorld(true);
    }

    public boolean isIdle()
    {
        return state == State.IDLE;
    }

    public boolean isFlying()
    {
        return state == State.FLYING;
    }

    private int getRandomValue(int min, int max)
    {
        Random random = new Random();

        return random.nextInt(max - min + 1) + min;
    }
}
