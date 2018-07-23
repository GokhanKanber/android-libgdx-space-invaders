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

import com.badlogic.gdx.math.Rectangle;
import com.gokhankanber.spaceinvaders.provider.Config;

public class Invader extends Model
{
    public static final int INVADER_MAX_WIDTH = 32;
    public static final int INVADER_MAX_HEIGHT = 16;
    private int columnIndex;
    private int rowIndex;
    private int points;
    private boolean visible;
    private boolean laser;
    public static int unitDirection;
    public static boolean unitMoveDown;
    public static float unitLeft;
    public static float unitRight;
    public static float velocity;
    public static float speedRatio;
    public static boolean checkWorldState;
    public boolean moveState;
    private Ground ground;

    public Invader(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    @Override
    public void setListener(IWorld iWorld)
    {
        super.setListener(iWorld);

        ground = iWorld.getGround();
    }

    public int getColumnIndex()
    {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex)
    {
        this.columnIndex = columnIndex;
    }

    public int getRowIndex()
    {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex)
    {
        this.rowIndex = rowIndex;
    }

    public int getPoints()
    {
        return points;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean hasLaser()
    {
        return laser;
    }

    public void setLaser()
    {
        laser = true;
    }

    @Override
    public void update(float delta)
    {
        stateTime += delta * Invader.speedRatio;

        if((((int) stateTime) % 2 == 1 && !moveState)
                || (((int) stateTime) % 2 == 0 && moveState))
        {
            if(!checkWorldState)
            {
                checkWorld();

                if(!unitMoveDown)
                {
                    unitLeft += velocity;
                    unitRight += velocity;
                }

                if(moveState)
                {
                    iWorld.playInvaderMoveTwoSound();
                }
                else
                {
                    iWorld.playInvaderMoveOneSound();
                }
            }

            if(unitMoveDown)
            {
                if(bounds.y <= iWorld.getShieldBorder())
                {
                    iWorld.removeShields();
                }

                if(checkCollisionY(ground.bounds))
                {
                    iWorld.endGame(false);
                }
                else
                {
                    position.y -= Config.INVADER_VELOCITY;
                }
            }
            else
            {
                bounds.x += velocity;
                position.x += velocity;
            }

            moveState = !moveState;
        }
    }

    @Override
    protected boolean checkCollisionY(Rectangle rectangle)
    {
        bounds.y -= Config.INVADER_VELOCITY;

        return super.checkCollisionY(rectangle);
    }

    public void checkWorld()
    {
        checkWorldState = true;
        unitMoveDown = false;
        velocity = unitDirection * Config.INVADER_VELOCITY;

        if((unitDirection == 1 && unitRight == Config.WIDTH - Config.INVADER_MOVE_BORDER)
                || (unitDirection == -1 && unitLeft == Config.INVADER_MOVE_BORDER))
        {
            unitMoveDown = true;
            unitDirection *= -1;
        }
        else if(unitDirection == 1 && unitRight + velocity >= Config.WIDTH - Config.INVADER_MOVE_BORDER)
        {
            velocity = Config.WIDTH - Config.INVADER_MOVE_BORDER - unitRight;
        }
        else if(unitDirection == -1 && unitLeft + velocity <= Config.INVADER_MOVE_BORDER)
        {
            velocity = Config.INVADER_MOVE_BORDER - unitLeft;
        }
    }
}
