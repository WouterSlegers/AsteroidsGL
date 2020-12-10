package com.wjslegers.asteroidsgl.Utils;

import android.content.res.Resources;
import android.graphics.PointF;
import android.opengl.Matrix;

import com.wjslegers.asteroidsgl.Entities.GLEntity;

import static com.wjslegers.asteroidsgl.config.FOLLOW_PERCENTAGE;
import static com.wjslegers.asteroidsgl.config.METERS_TO_SHOW_X;
import static com.wjslegers.asteroidsgl.config.METERS_TO_SHOW_Y;
import static com.wjslegers.asteroidsgl.config.WORLD_HEIGHT;
import static com.wjslegers.asteroidsgl.config.WORLD_WIDTH;


public class Camera {

    private PointF _lookAt = new PointF(0f, 0f);


    private int _screenHeight = 0;
    private int _screenWidth = 0;

    private float _metersToShowX = 0;
    private float _metersToShowY = 0;
    private int _offset = 0;
    private float _left = 0;
    private float _right = 0;
    private float _bottom = 0;
    private float _top = 0;
    private float _near = 0f;
    private float _far = 1f;

    public float[] _VPM = new float[4 * 4]; //In essence, it is our our Camera
    public float[] _VPMInView = new float[4 * 4]; //In essence, it is our our Camera

    public Camera() {
        _screenHeight = getScreenHeight();
        _screenWidth = getScreenWidth();
        setMetersToShow(METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        Matrix.orthoM(_VPMInView, _offset, 0, _metersToShowX, _metersToShowY, 0, _near, _far); //position object relative to screen, not world

        lookAt(0f, 0f);
        update();
        Matrix.orthoM(_VPM, _offset, _left, _right, _bottom, _top, _near, _far); //orthographic view

    }

    //could add inView, but game world isn't so big anyway
    public void update(){
        _left = _lookAt.x - _metersToShowX/2;
        _right = _lookAt.x + _metersToShowX/2;
        _bottom = _lookAt.y + _metersToShowY/2;
        _top = _lookAt.y - _metersToShowY/2;

        if (_left < 0) {
            _right -= _left;
            _left -= _left;
        } else if (_right > WORLD_WIDTH) {
            _left -= _right - WORLD_WIDTH;
            _right -= _right - WORLD_WIDTH;
        }

        if (_top < 0) {
            _bottom -= _top;
            _top -= _top;
        } else if (_bottom > WORLD_HEIGHT) {
            _top -= _bottom - WORLD_HEIGHT;
            _bottom -= _bottom - WORLD_HEIGHT;
        }

        Matrix.orthoM(_VPM, _offset, _left, _right, _bottom, _top, _near, _far); //orthographic view
    }

    public void lookAt(final float x, final float y){
        _lookAt.x = (_lookAt.x * (1-FOLLOW_PERCENTAGE)) + x * FOLLOW_PERCENTAGE;
        _lookAt.y = (_lookAt.y * (1-FOLLOW_PERCENTAGE)) + y * FOLLOW_PERCENTAGE;
    }
    public void lookAt(final PointF pos){
        lookAt(pos.x, pos.y);
    }
    public void lookAt(final GLEntity e){
        if (e != null){
            lookAt(e._x, e._y);
        }
    }

    private void setMetersToShow(float metersToShowX, float metersToShowY) {
        if (metersToShowX <= 0f && metersToShowY <= 0f)
            throw new IllegalArgumentException("One of the dimensions must be provided!");
        //formula: new height = (original height / original width) x new width
        _metersToShowX = metersToShowX;
        _metersToShowY = metersToShowY;
        if (metersToShowX == 0f || metersToShowY == 0f) {
            if (metersToShowY > 0f) { //if Y is configured, calculate X
                _metersToShowX = ((float) _screenWidth / _screenHeight) * metersToShowY;
            } else { //if X is configured, calculate Y
                _metersToShowY = ((float) _screenHeight / _screenWidth) * metersToShowX;
            }
        }
    }

    public float getMetersToShowX() {
        return _metersToShowX;
    }
    public float getMetersToShowY() {
        return _metersToShowY;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
