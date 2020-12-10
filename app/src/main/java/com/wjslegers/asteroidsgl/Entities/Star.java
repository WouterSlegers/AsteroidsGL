package com.wjslegers.asteroidsgl.Entities;

import android.graphics.Color;
import android.opengl.GLES20;

import com.wjslegers.asteroidsgl.Utils.Utils;

import static com.wjslegers.asteroidsgl.Utils.Utils.between;
import static com.wjslegers.asteroidsgl.Utils.Utils.randomLocation;
import static com.wjslegers.asteroidsgl.config.MIN_YELLOW_VALUE;
import static com.wjslegers.asteroidsgl.config.START_INTENSITY;

public class Star extends PointEntity {
    private static Mesh m = null; //Q&D pool

    public Star(){
        super();
        float[] location = randomLocation();
        _x = location[0];
        _y = location[1];

        float _red = between(MIN_YELLOW_VALUE, 1f);
        float _green = between(MIN_YELLOW_VALUE, _red);
        float max = Math.max(_red, _green);
        _red = (_red/max)*START_INTENSITY; //normalise
        _green = (_green/max)*START_INTENSITY;

        setColors(_red, _green, 0f, 1f);
    }

}
