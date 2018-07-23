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

import java.util.List;

/**
 * World interface for models.
 */
public interface IWorld
{
    List<Invader> getInvaders();
    List<Shield> getShields();
    CommandAlienShip getCommandAlienShip();
    LaserCannon getLaserCannon();
    Ground getGround();
    float getShieldBorder();
    void removeInvader(int index);
    void removeCommandAlienShip();
    void removeShields();
    void setResetLaserCannon();
    void setCheckGame();
    void setResetWorld(boolean resetWorld);
    void endGame(boolean wait);
    void addExplosion(Explosion explosion);
    void playLaserCannonSound();
    void playExplosionSound();
    void playInvaderMoveOneSound();
    void playInvaderMoveTwoSound();
    void playCommandAlienShipSound();
    void stopCommandAlienShipSound();
}
