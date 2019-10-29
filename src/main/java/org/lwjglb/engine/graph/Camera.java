package org.lwjglb.engine.graph;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjglb.engine.Scene;
import org.lwjglb.engine.items.GameItem;

import java.util.List;
import java.util.Map;

public class Camera {

    private final Vector3f position;
    
    private final Vector3f rotation;
    
    private Matrix4f viewMatrix;

    private Vector3f characterMin;
    private Vector3f characterMax;


    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        viewMatrix = new Matrix4f();

        characterMin = new Vector3f(-3, -3, -3);
        characterMax = new Vector3f(3, 6, 3);
    }
    
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    
    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }
    
    public void movePosition(float offsetX, float offsetY, float offsetZ, Scene scene) {


        Vector3f newPos = new Vector3f(0,0,0);
        newPos.set(position);

        if ( offsetZ != 0 ) {
            newPos.x = position.x + (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            newPos.z = position.z + (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            newPos.x = position.x + (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            newPos.z = position.z + (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        newPos.y = position.y + offsetY;

        Vector3f newPosMin = new Vector3f(newPos);
        Vector3f newPosMax = new Vector3f(newPos);

        newPosMin.add(characterMin);
        newPosMax.add(characterMax);

        Map<Mesh, List<GameItem>> gameMeshes = scene.getGameMeshes();

        boolean collided= false;

        for (Map.Entry<Mesh, List<GameItem>> instancedMesh : gameMeshes.entrySet()) {
            for (GameItem gameItem : instancedMesh.getValue()) {
                Vector3f max = new Vector3f();

                Vector3f min = new Vector3f();

                Mesh mesh = gameItem.getMeshes()[0];

                Vector3f maxScaled = (new Vector3f(mesh.getMax())).mul(gameItem.getScale());
                Vector3f minScaled = (new Vector3f(mesh.getMin())).mul(gameItem.getScale());

                min.set(gameItem.getPosition());
                max.set(gameItem.getPosition());
                min.add(minScaled);
                max.add(maxScaled);


                if (min.x  < newPosMax.x && newPosMin.x < max.x) {
                    if (min.y  < newPosMax.y && newPosMin.y < max.y) {
                        if (min.z  < newPosMax.z && newPosMin.z < max.z) {
                            System.out.println("colliding with " + gameItem.getMeshId());

                            System.out.println("min is " + min.x + " " + min.y + " " + min.z);
                            System.out.println("camera is " + newPos.x + " " + newPos.y + " " + newPos.z);
                            System.out.println("max is " + max.x + " " + max.y + " " + max.z);
                            collided = true;
                            break;

                        }
                    }
                }



            }

        }

        if (!collided) {
            position.set(newPos);
        }
    }

    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}