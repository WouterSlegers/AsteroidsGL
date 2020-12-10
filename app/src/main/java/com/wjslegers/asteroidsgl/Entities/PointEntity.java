package com.wjslegers.asteroidsgl.Entities;

import android.opengl.GLES20;

import static com.wjslegers.asteroidsgl.config.BULLET_TIME_TO_LIVE;

public class PointEntity extends GLEntity {
    private static Mesh POINT_MESH = new Mesh(Mesh.POINT, GLES20.GL_POINTS); //Q&D pool, Mesh.POINT is just [0,0,0] float array

    public float _ttl = 0;
    public PointEntity() {
        _mesh = POINT_MESH; //all point entities use the exact same mesh
        _isAlive = false;
    }


    @Override
    public void update(final double dt){
        if(_ttl > 0) {
            _ttl -= dt;
            super.update(dt);
        } else {
            _isAlive = false;
        }
    }

    @Override
    public void render(final float[] viewportMatrix){
        super.render(viewportMatrix);
    }

}
