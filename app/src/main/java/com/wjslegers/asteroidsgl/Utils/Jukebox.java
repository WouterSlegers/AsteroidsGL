package com.wjslegers.asteroidsgl.Utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

public class Jukebox {
    SoundPool _soundPool = null;
    private static final int MAX_STREAMS = 4;
    private static int _backgroundStreamId = 0;
    public static boolean _backgroundPlaying = false;

    public static int HURT = 0;
    public static int DEATH = 0;
    public static int EXPLOSION = 0;
    public static int LASER = 0;
    public static int DENIED = 0;

    public static int BACKGROUND_FUNKY = 0;
    public static int BACKGROUND_CHILL = 0;

    public Jukebox(final Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(MAX_STREAMS)
                .build();
        loadSounds(context);
    }

    private void loadSounds(final Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("laser2.wav");
            LASER = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("hurt.wav");
            DENIED = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("death2.wav");
            DEATH = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("explosion.wav");
            EXPLOSION = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("denied.wav");
            HURT = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("funky_loop.wav");
            BACKGROUND_FUNKY = _soundPool.load(descriptor, 3);
            descriptor = assetManager.openFd("chill_loop.wav");
            BACKGROUND_CHILL = _soundPool.load(descriptor, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void play(final int soundID, final int loop, final int priority) { //-1 to loop, higher priority = higher value
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final float rate = 1.0f; //speed

        if (soundID > 0) {
            if (soundID == BACKGROUND_FUNKY || soundID == BACKGROUND_CHILL){
                _soundPool.stop(_backgroundStreamId);
                _backgroundStreamId = _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
            } else {
                _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
            }
        }
    }

    public void destroy() {
        _soundPool.release();
        _soundPool = null;
    }

    public void onResume() {
        _soundPool.autoResume();
    }

    public void onPause() {
        _soundPool.autoPause();
    }

    public void playBackgroundMusic() {
        switch (Utils.RNG.nextInt(2)){
            case 0:
                play(BACKGROUND_CHILL, -1, 3);
                break;
            case 1:
                play(BACKGROUND_FUNKY, -1, 3);
                break;
        }
        if (_backgroundStreamId != 0){
            _backgroundPlaying = true;
        }
    }
}
