package com.wjslegers.asteroidsgl;

import android.opengl.GLES20;
import android.util.Log;

import com.wjslegers.asteroidsgl.Entities.Mesh;

import java.nio.FloatBuffer;

import static android.opengl.GLU.gluErrorString;
import static com.wjslegers.asteroidsgl.Utils.Utils.require;
import static com.wjslegers.asteroidsgl.config.WIDTH_POINTS;

public class GLManager {
    public final static String TAG = "GLManager";
    private static final int OFFSET = 0;
    private static int MVPMatrixHandle; //handle to the model-view-projection matrix

    //shader source code (could be loaded from textfile!)
    private final static String vertexShaderCode =
            "uniform mat4 modelViewProjection;\n" + // A constant representing the combined model/view/projection matrix.
                    "attribute vec4 position;\n" +      // Per-vertex position information that we will pass in.
                    "void main() {\n" +                 // The entry point for our vertex shader.
                    "    gl_Position = modelViewProjection\n" +    // gl_Position is a special variable used to store the final position.
                    "        * position;\n" +// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
                    "    gl_PointSize = " + WIDTH_POINTS + ";\n" + //pixel width of points
                    "}\n";

    private final static String fragmentShaderCode =
            "precision mediump float;\n" + //we don't need high precision floats for fragments
                    "uniform vec4 color;\n" + // a constant color to apply to all pixels
                    "void main() {\n" + // The entry point for our fragment shader.
                    "  gl_FragColor = color;\n" + // Pass the color directly through the pipeline.
                    "}\n";

    //handles various GL objects:
    private static int glProgramHandle; //handle to the compiled shader program
    private static int colorUniformHandle; //handle to the color setting
    private static int positionAttributeHandle; //handle to the vertex position setting


    public static void draw(final Mesh model, final float[] modelViewMatrix, final float[] color){
        setShaderColor(color);
        uploadMesh(model._vertexBuffer);
        setModelViewProjection(modelViewMatrix);
        drawMesh(model._drawMode, model._vertexCount);
    }

    private static void setShaderColor(final float[] color) {
        final int COUNT = 1;
        // set color for drawing the pixels of our geometry
        GLES20.glUniform4fv(GLManager.colorUniformHandle, COUNT, color, OFFSET);;
        checkGLError("setShaderColor");
    }

    private static void uploadMesh(final FloatBuffer vertexBuffer) {
        final boolean NORMALIZED = false;
        // enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(GLManager.positionAttributeHandle);
        // prepare the vertex coordinate data
        GLES20.glVertexAttribPointer(GLManager.positionAttributeHandle, Mesh.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, NORMALIZED, Mesh.VERTEX_STRIDE,
                vertexBuffer);
        checkGLError("uploadMesh");
    }

    private static void drawMesh(final int drawMode, final int vertexCount) {
        require(drawMode == GLES20.GL_TRIANGLES
                || drawMode == GLES20.GL_LINES
                || drawMode == GLES20.GL_POINTS, "Invalid draw mode");
        // draw the previously uploaded vertices
        GLES20.glDrawArrays(drawMode, OFFSET, vertexCount);
        // disable vertex array
        GLES20.glDisableVertexAttribArray(GLManager.positionAttributeHandle);
        checkGLError("drawMesh");
    }

    private static void setModelViewProjection(final float[] modelViewMatrix) {
        final int COUNT = 1;
        final boolean TRANSPOSED = false;
        GLES20.glUniformMatrix4fv(MVPMatrixHandle, COUNT, TRANSPOSED, modelViewMatrix, OFFSET);
        checkGLError("setModelViewProjection");
    }

    public static void buildProgram(){
        final int vertex = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragment = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        glProgramHandle = linkShaders(vertex, fragment);
        // delete the shaders as they're linked into our program now and no longer necessary
        GLES20.glDeleteShader(vertex);
        GLES20.glDeleteShader(fragment);
        //get the handles to our shader settings so that we can manipulate these later
        positionAttributeHandle = GLES20.glGetAttribLocation(glProgramHandle, "position");
        colorUniformHandle = GLES20.glGetUniformLocation(glProgramHandle, "color");
        MVPMatrixHandle = GLES20.glGetUniformLocation(glProgramHandle, "modelViewProjection");
        //activate the program
        GLES20.glUseProgram(glProgramHandle);
        GLES20.glLineWidth(config.LINE_WIDTH);
        checkGLError("buildProgram");
    }

    private static int compileShader(final int type, final String shaderCode){
        require(type == GLES20.GL_VERTEX_SHADER || type == GLES20.GL_FRAGMENT_SHADER, "Invalid shader type");
        final int handle = GLES20.glCreateShader(type); // Create a shader object and store its handle
        GLES20.glShaderSource(handle, shaderCode); // Pass in the code
        GLES20.glCompileShader(handle); // then compile the shader
        Log.d(TAG, "Shader Compile Log: \n" + GLES20.glGetShaderInfoLog(handle));
        checkGLError("compileShader");
        return handle;
    }

    private static int linkShaders(final int vertexShader, final int fragmentShader){
        final int handle = GLES20.glCreateProgram();
        GLES20.glAttachShader(handle, vertexShader);
        GLES20.glAttachShader(handle, fragmentShader);
        GLES20.glLinkProgram(handle);
        Log.d(TAG, "Shader Link Log: \n" + GLES20.glGetProgramInfoLog(handle));
        checkGLError("linkShaders");
        return handle;
    }


    public static void checkGLError(final String func){
        int error;
        while((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR){
            Log.e(func, "glError " + 	gluErrorString( error));
        }
    }
}
