package com.wjslegers.asteroidsgl.Entities;

import android.opengl.GLES20;

import com.wjslegers.asteroidsgl.Utils.Utils;

public class Border extends GLEntity {
    public Border(final float x, final float y, final float worldWidth, final float worldHeight){
        super();
        _x = x;
        _y = y;
        _width = worldWidth; //-1 so the border isn't obstructed by the screen edge
        _height = worldHeight;
        setColors(1f, 0f, 0.1f, 1f);
        _mesh = new Mesh(Mesh.generateLinePolygon(4, 10.0), GLES20.GL_LINES);
        _mesh.rotateZ(45* Utils.TO_RADIANS);
        _mesh.setWidthHeight(_width, _height); //will automatically normalize the mesh!
    }
}
