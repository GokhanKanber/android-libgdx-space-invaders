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

public class Explosion extends Model
{
    private enum State
    {
        START,
        END
    }

    private enum Type
    {
        INVADER,
        LASER_CANNON,
        COMMAND_ALIEN_SHIP,
        GROUND,
        SPACE
    }

    private State state = State.START;
    private Type type;
    public float time = 0.3f;

    public Explosion(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    @Override
    public void update(float delta)
    {
        if(isStart())
        {
            wait(delta, time);

            if(stateTime == 0)
            {
                if(isInvader())
                {
                    iWorld.setCheckGame();
                }
                else if(isCommandAlienShip())
                {
                    iWorld.setResetWorld(true);
                }

                end();
            }
        }

        stateTime += delta;
    }

    public void end()
    {
        state = State.END;
    }

    public boolean isStart()
    {
        return state == State.START;
    }

    public boolean isEnd()
    {
        return state == State.END;
    }

    public void invader()
    {
        type = Type.INVADER;
    }

    public void laserCannon()
    {
        type = Type.LASER_CANNON;
    }

    public void commandAlienShip()
    {
        type = Type.COMMAND_ALIEN_SHIP;
    }

    public void ground()
    {
        type = Type.GROUND;
    }

    public void space()
    {
        type = Type.SPACE;
    }

    public boolean isInvader()
    {
        return type == Type.INVADER;
    }

    public boolean isLaserCannon()
    {
        return type == Type.LASER_CANNON;
    }

    public boolean isCommandAlienShip()
    {
        return type == Type.COMMAND_ALIEN_SHIP;
    }

    public boolean isGround()
    {
        return type == Type.GROUND;
    }

    public boolean isSpace()
    {
        return type == Type.SPACE;
    }

    @Override
    public String toString()
    {
        return type.toString();
    }
}
