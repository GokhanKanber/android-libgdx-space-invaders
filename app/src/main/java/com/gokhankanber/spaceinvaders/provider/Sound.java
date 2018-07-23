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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Sound
{
    private final int sampleRate = 48000;

    public enum Track
    {
        BUTTON(0, 600, 0.1f),
        INVADER_MOVE_ONE(1, 150, 0.1f),
        INVADER_MOVE_TWO(2, 140, 0.1f),
        LASER(3, 1000, 0.1f),
        EXPLOSION(4, 500, 0.1f),
        COMMAND_ALIEN_SHIP(5, 5, 3, 5000),
        LASERCANNON(6, 8, 1, 900);

        private final int index;
        private final float frequency;
        private final float duration;
        private final int maxValue;

        Track(int index, float frequency, float duration)
        {
            this(index, frequency, duration, Byte.MAX_VALUE);
        }

        Track(int index, float frequency, float duration, int maxValue)
        {
            this.index = index;
            this.frequency = frequency;
            this.duration = duration;
            this.maxValue = maxValue;
        }

        public int getIndex()
        {
            return index;
        }
    }

    private AudioTrack[] audioTracks;
    private static Sound instance;

    private Sound()
    {
    }

    public static Sound get()
    {
        if(instance == null)
        {
            instance = new Sound();
            instance.init();
        }

        return instance;
    }

    public void init()
    {
        Track[] trackList = Track.values();
        audioTracks = new AudioTrack[trackList.length];

        for(Track track : trackList)
        {
            audioTracks[track.index] = create(track.frequency, track.duration, track.maxValue);
        }
    }

    private AudioTrack create(float frequency, float duration, int maxValue)
    {
        byte[] bytes = new byte[(int) (sampleRate * duration)];

        for(int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) (Math.sin(2 * Math.PI * i * frequency / sampleRate) * maxValue);
        }

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_DEFAULT,
                AudioFormat.ENCODING_PCM_8BIT, bytes.length,
                AudioTrack.MODE_STATIC);

        track.write(bytes, 0, bytes.length);
        track.setStereoVolume(0.5f, 0.5f);

        return track;
    }

    public void play(int index)
    {
        play(index, false);
    }

    public void play(int index, boolean loop)
    {
        AudioTrack audioTrack = audioTracks[index];
        stop(index);

        if(loop)
        {
            audioTrack.setLoopPoints(0, (int) (sampleRate * Track.values()[index].duration), -1);
        }

        audioTrack.play();
    }

    public void stop(int index)
    {
        AudioTrack audioTrack = audioTracks[index];

        if(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
        {
            audioTrack.stop();
        }
    }

    public void releaseAll()
    {
        for(AudioTrack audioTrack : audioTracks)
        {
            audioTrack.release();
        }

        instance = null;
    }
}
