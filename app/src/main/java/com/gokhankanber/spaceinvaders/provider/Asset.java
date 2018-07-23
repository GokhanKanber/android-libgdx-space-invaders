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

package com.gokhankanber.spaceinvaders.provider;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Asset
{
    // x, y, width, height, color (rgba)
    public static final int[] COMMAND_ALIEN_SHIP = {0, 0, 48, 8, 0x590f90ff};
    public static final int[] INVADER_LASER_EXPLOSION = {48, 0, 6, 8, 0xff0000ff};
    public static final int[] PLAYER_LASER_EXPLOSION = {54, 0, 8, 8, 0xff0000ff};
    public static final int[] INVADER_ONE = {0, 8, 32, 8, 0x0000ffff};
    public static final int[] INVADER_TWO = {32, 8, 32, 8, 0x00ff00ff};
    public static final int[] INVADER_THREE = {0, 16, 32, 8, 0xffff00ff};
    public static final int[] INVADER_EXPLOSION = {32, 16, 16, 8, 0xffffffff};
    public static final int[] PLAYER = {48, 16, 16, 8, 0x088817ff};
    public static final int[] SHIELD = {0, 24, 22, 16, 0xdf251cff};
    public static final int[] PLAYER_EXPLOSION = {22, 24, 32, 8, 0x088817ff};
    public static final int[] LASERS = {22, 32, 36, 8, 0xffffffff};
    public static final int[] PLAYER_LASER = {58, 32, 1, 8, 0xbcbcbcff};
    public static final int[] DIGITS = {0, 40, 30, 9, 0x088817ff};
    private final int[][] textures = {COMMAND_ALIEN_SHIP, INVADER_LASER_EXPLOSION, PLAYER_LASER_EXPLOSION,
            INVADER_ONE, INVADER_TWO, INVADER_THREE, INVADER_EXPLOSION, PLAYER, SHIELD, PLAYER_EXPLOSION,
            LASERS, PLAYER_LASER, DIGITS};

    private static Asset instance;
    private Character characters;
    private Sound sound;
    private Texture sprites;

    private Asset()
    {
    }

    public static Asset get()
    {
        if(instance == null)
        {
            instance = new Asset();
            instance.init();
        }

        return instance;
    }

    public void init()
    {
        characters = Character.get();
        sound = Sound.get();
        loadSprites();
    }

    public void dispose()
    {
        characters.clear();
        sound.releaseAll();
        sprites.dispose();
        instance = null;
    }

    private void loadSprites()
    {
        long[] pixels = {79478399524L, 35465881527027848L, 143974802288293368L, 288160024483101692L,
                494094168732515324L, 1152903989486976504L, 259449837396736144L, 72198404817573444L,
                270220100878992400L, 2303626359716708896L, 4610630470486722544L, 4151256299207396824L,
                4610630470621470716L, 459383036458833908L, 986316459088024596L, 3462155613973775200L,
                108088040395636864L, 270220101390172608L, 567462211972235712L, 986303368095866876L,
                1148435430057459710L, 162135771474575358L, 405332831587287038L, 743098474610311166L,
                1152851170226606080L, 2305807825949451264L, 4611668471342891008L, 9223363549999925248L,
                -4369035770880L, -3201352396800L, -3850161373184L, -3299258415104L, -4398046511104L,
                -3005403290496L, -2050538789728L, -3208033782368L, -142992962196249376L, -287670431210986336L,
                -576181327866796832L, -576182484689406560L, -1442304186124337152L, 0L, -5144481092362829824L,
                0L, -6052844513435582464L, 0L, -6191770384585457664L, 0L, -3384417049378816L};

        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        drawPixels(pixmap, pixels);
        sprites = new Texture(pixmap);
        pixmap.dispose();
    }

    public Texture getSprites()
    {
        return sprites;
    }

    public Texture getTexture(int color)
    {
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(color);
        pixel.fill();
        Texture texture = new Texture(pixel);
        pixel.dispose();

        return texture;
    }

    /**
     * Creates logo texture: SPACE INVADERS.
     * @return texture.
     */
    public Texture getLogoTexture()
    {
        long[] logo = {0L, 0L, 1064058883661824L, 1695875460366336L, 1062568030879744L,
                114101812854784L, 112184116248576L, 1062155676278784L, 0L, 0L, 9108079551690210364L,
                1762708713864455776L, 1764960693967939132L, 1764960693966109702L,
                1760411018030246918L, 9107994124790687292L};

        Pixmap pixmap = new Pixmap(64, 16, Pixmap.Format.RGBA8888);
        drawPixels(pixmap, logo, 0xffff00ff);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    /**
     * Creates texture for specified text (menu items).
     * @param text for texture content.
     * @return texture.
     */
    public Texture getTexture(String text, int color)
    {
        if(text == null)
        {
            text = "";
        }

        Pixmap pixmap = new Pixmap(text.length() * 8, 8, Pixmap.Format.RGBA8888);
        drawText(pixmap, text, color);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    public Texture getTexture(String text)
    {
        return getTexture(text, 0xffffffff);
    }

    /**
     * Draws pixels of characters of specified text.
     * @param pixmap to draw pixels.
     * @param text data.
     */
    private void drawText(Pixmap pixmap, String text, int color)
    {
        int index = 0;

        for(char character : text.toCharArray())
        {
            byte[] bytes = characters.getBytes(character);
            drawPixels(pixmap, bytes, color, index);
            index += Byte.SIZE;
        }
    }

    private int getColor(int x, int y)
    {
        int color = 0xffffffff;
        int max = textures.length - 1;

        for(int i = max; i >= 0; i--)
        {
            if(x >= textures[i][0] && y >= textures[i][1])
            {
                color = textures[i][4];
                break;
            }
        }

        return color;
    }

    private void drawPixels(Pixmap pixmap, byte[] bytes, int color, int index)
    {
        if(bytes != null)
        {
            int length = bytes.length;

            for(int y = 0; y < length; y++)
            {
                for(int x = 7; x >= 0; x--)
                {
                    if(((bytes[y] >> x) & 1) == 1)
                    {
                        pixmap.drawPixel(index + (7 - x), y, color);
                    }
                }
            }
        }
    }

    private void drawPixels(Pixmap pixmap, long[] bytes, int color)
    {
        if(bytes != null)
        {
            int length = bytes.length;

            for(int y = 0; y < length; y++)
            {
                for(int x = 63; x >= 0; x--)
                {
                    if(((bytes[y] >> x) & 1) == 1)
                    {
                        pixmap.drawPixel(63 - x, y, (color != 0 ? color : getColor(63 - x, y)));
                    }
                }
            }
        }
    }

    private void drawPixels(Pixmap pixmap, long[] bytes)
    {
        drawPixels(pixmap, bytes, 0);
    }

    public void playButtonSound()
    {
        sound.play(Sound.Track.BUTTON.getIndex());
    }

    public void playInvaderMoveOneSound()
    {
        sound.play(Sound.Track.INVADER_MOVE_ONE.getIndex());
    }

    public void playInvaderMoveTwoSound()
    {
        sound.play(Sound.Track.INVADER_MOVE_TWO.getIndex());
    }

    public void playLaserSound()
    {
        sound.play(Sound.Track.LASER.getIndex());
    }

    public void playExplosionSound()
    {
        sound.play(Sound.Track.EXPLOSION.getIndex());
    }

    public void playCommandAlienShipSound()
    {
        sound.play(Sound.Track.COMMAND_ALIEN_SHIP.getIndex(), true);
    }

    public void playLaserCannonSound()
    {
        sound.play(Sound.Track.LASERCANNON.getIndex());
    }

    public void stopCommandAlienShipSound()
    {
        sound.stop(Sound.Track.COMMAND_ALIEN_SHIP.getIndex());
    }
}
