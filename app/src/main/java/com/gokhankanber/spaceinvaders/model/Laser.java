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
import java.util.List;

public class Laser extends Model
{
    private enum State
    {
        FIRED,
        EXPLODING
    }

    private enum Source
    {
        INVADER,
        LASERCANNON
    }

    private enum Type
    {
        LASER1,
        LASER2,
        LASER3,
        LASER4
    }

    private State state = State.FIRED;
    private Source source = Source.INVADER;
    private Type type;
    private List<Invader> invaders;
    private List<Shield> shields;
    private CommandAlienShip commandAlienShip;
    private LaserCannon laserCannon;
    private Ground ground;

    public Laser(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    public boolean isSourceLaserCannon()
    {
        return source == Source.LASERCANNON;
    }

    public void setSourceLaserCannon()
    {
        source = Source.LASERCANNON;
        type = Type.LASER4;
    }

    public boolean isLaser1()
    {
        return type == Type.LASER1;
    }

    public boolean isLaser2()
    {
        return type == Type.LASER2;
    }

    public boolean isLaser3()
    {
        return type == Type.LASER3;
    }

    public boolean isLaser4()
    {
        return type == Type.LASER4;
    }

    public void setType(int type)
    {
        switch(type)
        {
            case 0:
                this.type = Type.LASER1;
                break;
            case 1:
                this.type = Type.LASER2;
                break;
            case 2:
                this.type = Type.LASER3;
                break;
        }
    }

    @Override
    public void setListener(IWorld iWorld)
    {
        super.setListener(iWorld);

        switch(source)
        {
            case INVADER:
                setAcceleration(-Config.LASER_ACCELERATION);
                laserCannon = iWorld.getLaserCannon();
                invaders = iWorld.getInvaders();
                ground = iWorld.getGround();
                break;
            case LASERCANNON:
                setAcceleration(Config.LASER_ACCELERATION);
                invaders = iWorld.getInvaders();
                commandAlienShip = iWorld.getCommandAlienShip();
                break;
            default:
                throw new IllegalArgumentException("Set a valid source before calling this method.");
        }

        shields = iWorld.getShields();
    }

    @Override
    public void update(float delta)
    {
        if(isFired())
        {
            velocity.y = acceleration.y * delta;
            bounds.y += velocity.y;

            checkCollision();
            checkWorld();

            position.x = bounds.x;
            position.y = bounds.y;
        }

        stateTime += delta;
    }

    public void setAcceleration(float accelerationValue)
    {
        acceleration.y = accelerationValue;
    }

    @Override
    protected void checkCollision()
    {
        super.checkCollision();

        switch(source)
        {
            case INVADER:
                checkCollisionInvader();
                break;
            case LASERCANNON:
                checkCollisionLaserCannon();
                break;
            default:
                throw new IllegalArgumentException("Set a valid source before calling this method.");
        }

        checkCollisionShield();
    }

    private void checkCollisionInvader()
    {
        if(checkCollisionY(laserCannon.bounds))
        {
            if(invaders.size() > 0)
            {
                explode();
                laserCannon.die();
            }
        }

        if(checkCollisionY(ground.bounds))
        {
            explode();
            Explosion explosion = new Explosion(bounds.x, bounds.y, Asset.INVADER_LASER_EXPLOSION[2] * 2, Asset.INVADER_LASER_EXPLOSION[3] * 2);
            explosion.ground();
            iWorld.addExplosion(explosion);
        }
    }

    private void checkCollisionLaserCannon()
    {
        int i = 0;

        for(Invader invader : invaders)
        {
            if(checkCollisionY(invader.bounds))
            {
                iWorld.playExplosionSound();
                explode();
                Explosion explosion = new Explosion(invader.getX(), invader.getY(), Asset.INVADER_EXPLOSION[2] * 2, Asset.INVADER_EXPLOSION[3] * 2);
                explosion.setListener(iWorld);
                explosion.invader();
                iWorld.addExplosion(explosion);
                iWorld.removeInvader(i);
                break;
            }

            i++;
        }

        if(checkCollisionY(commandAlienShip.bounds))
        {
            iWorld.playExplosionSound();
            explode();
            Explosion explosion = new Explosion(commandAlienShip.getX(), commandAlienShip.getY(), Asset.COMMAND_ALIEN_SHIP[2], Asset.COMMAND_ALIEN_SHIP[3] * 2);
            explosion.setListener(iWorld);
            explosion.commandAlienShip();
            iWorld.addExplosion(explosion);
            iWorld.removeCommandAlienShip();
        }
    }

    private void checkCollisionShield()
    {
        for(Shield shield : shields)
        {
            if(checkCollisionY(shield.bounds))
            {
                explode();
                Explosion explosion = null;

                switch(source)
                {
                    case INVADER:
                        explosion = new Explosion(bounds.x, bounds.y, Asset.INVADER_LASER_EXPLOSION[2] * 2, Asset.INVADER_LASER_EXPLOSION[3] * 2);
                        explosion.ground();
                        break;
                    case LASERCANNON:
                        explosion = new Explosion(bounds.x, bounds.y, Asset.PLAYER_LASER_EXPLOSION[2] * 2, Asset.PLAYER_LASER_EXPLOSION[3] * 2);
                        explosion.space();
                        break;
                }

                if(explosion != null)
                {
                    iWorld.addExplosion(explosion);
                }
            }
        }
    }

    private void checkWorld()
    {
        if((source == Source.LASERCANNON) && (bounds.y + bounds.height > Config.getHeight() - 22))
        {
            bounds.y = bounds.y - 4;
            explode();
            Explosion explosion = new Explosion(bounds.x, bounds.y, Asset.PLAYER_LASER_EXPLOSION[2] * 2, Asset.PLAYER_LASER_EXPLOSION[3] * 2);
            explosion.space();
            iWorld.addExplosion(explosion);
        }
    }

    public void explode()
    {
        state = State.EXPLODING;
    }

    public boolean isFired()
    {
        return state == State.FIRED;
    }

    public boolean isExploding()
    {
        return state == State.EXPLODING;
    }
}
