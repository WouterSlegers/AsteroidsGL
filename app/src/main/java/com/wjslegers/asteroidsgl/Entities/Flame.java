package com.wjslegers.asteroidsgl.Entities;

import com.wjslegers.asteroidsgl.Utils.Utils;

import static com.wjslegers.asteroidsgl.Utils.Utils.TO_RADIANS;
import static com.wjslegers.asteroidsgl.config.FLAME_COLOR;
import static com.wjslegers.asteroidsgl.config.FLAME_SIZE;
import static com.wjslegers.asteroidsgl.config.FLAME_SKEW;
import static com.wjslegers.asteroidsgl.config.FLAME_SKEW_REFRESH;

public class Flame extends GLEntity {
    int frame_counter = 0;
    float flame_skew = 0f;

    public Flame(final GLEntity source) {
        updatePosition(source);
        _height = FLAME_SIZE;
        _width = _height/2;

        setColors(FLAME_COLOR);
        _mesh = new Triangle();
        _mesh.setWidthHeight(_width, _height);
    }

    public void updatePosition(final GLEntity source){
        _isAlive = true;
        final float theta = (float) (source._rotation* TO_RADIANS);
        _x = source._x - (float)Math.sin(theta) * (source._height*0.5f + _height*0.35f);//looks better with bit of overlap
        _y = source._y + (float)Math.cos(theta) * (source._height*0.5f + _height*0.35f);

        if (frame_counter <= 0){
            frame_counter = FLAME_SKEW_REFRESH;
            flame_skew = Utils.between(-FLAME_SKEW, FLAME_SKEW);
        }
        frame_counter--;
        _rotation = source._rotation + flame_skew;
    }
}