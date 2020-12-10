package com.wjslegers.asteroidsgl.Utils;

import android.util.Log;

import static com.wjslegers.asteroidsgl.config.WORLD_HEIGHT;
import static com.wjslegers.asteroidsgl.config.WORLD_WIDTH;


public abstract class Utils {
    public static final String TAG = "Utils";
    public static final double TO_DEGREES = 180.0/Math.PI;
    public static final double TO_RADIANS = Math.PI/180.0;
    public static long SECOND_IN_NANOSECONDS = 1_000_000_000;
    public static long MILLISECOND_IN_NANOSECONDS = 1_000_000;
    public static double NANOS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static double NANOS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS; //constant not config
    public static double SECONDS_TO_MILLI = 1_000;
    public final static java.util.Random RNG = new java.util.Random();

    //Could add theta to x,y and x,y to theta

    public static float wrap(float val, final float min, final float max) {
        if (val < min) {
            val = max;
        } else if (val > max) {
            val = min;
        }
        return val;
    }

    public static float clamp(float val, final float min, final float max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }


    public static float nextFloat(){
        return RNG.nextFloat();
    }

    public static int nextInt( final int max){
        return RNG.nextInt(max);
    }

    public static int between( final int min, final int max){
        return RNG.nextInt(max-min)+min;
    }

    public static float between( final float min, final float max){
        return min+RNG.nextFloat()*(max-min);
    }


    public static float[] randomLocation(){
        return new float[]{nextFloat()*WORLD_WIDTH, nextFloat()*WORLD_HEIGHT};
    }

    public static void expect(final boolean condition, final String tag) {
        Utils.expect(condition, tag, "Expectation was broken.");
    }
    public static void expect(final boolean condition, final String tag, final String message) {
        if(!condition) {
            Log.e(tag, message);
        }
    }
    public static void require(final boolean condition) {
        Utils.require(condition, "Assertion failed!");
    }
    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

}