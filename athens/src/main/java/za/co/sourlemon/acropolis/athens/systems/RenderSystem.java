/*
 * The MIT License
 *
 * Copyright 2013 Sour Lemon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package za.co.sourlemon.acropolis.athens.systems;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import static com.hackoeur.jglm.Matrices.*;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import za.co.sourlemon.acropolis.athens.mesh.Mesh;
import za.co.sourlemon.acropolis.athens.nodes.RenderNode;
import za.co.sourlemon.acropolis.athens.shader.Program;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.id.EntityID;
import za.co.sourlemon.acropolis.tokyo.components.State;

/**
 *
 * @author Daniel
 */
public class RenderSystem extends AbstractSystem
{

    public static final Vec3 X_AXIS = new Vec3(1, 0, 0);
    public static final Vec3 Y_AXIS = new Vec3(0, 1, 0);
    public static final Vec3 Z_AXIS = new Vec3(0, 0, 1);

    private final int screenWidth, screenHeight;
    private final Map<EntityID, Mat4> worlds = new HashMap<>();
    private final Map<Program, Map<EntityID, Mesh>> objects = new HashMap<>();

    public RenderSystem(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public boolean init()
    {
        try
        {
            Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
            Display.create();

            GL11.glClearColor(1, 1, 1, 1);
        } catch (LWJGLException ex)
        {
            System.err.println("Error creating display: " + ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void update(Engine engine, double time, double dt)
    {
        for (Map m : objects.values())
        {
            m.clear();
        }
        
        for (RenderNode node : engine.getNodeList(RenderNode.class))
        {
            EntityID id = node.getEntity().getId();
            worlds.put(id, getMatrix(node.state));
            Program shader = null; //resourceManager.getShader(node.renderable.shader);
            Mesh mesh = null;// resourceManager.getMesh(node.renderable.mesh);
            Map<EntityID, Mesh> meshes = objects.get(shader);
            if (meshes == null)
            {
                meshes = new HashMap<>();
                objects.put(shader, meshes);
            }
            meshes.put(id, mesh);
        }
        
        for (Map.Entry<Program, Map<EntityID, Mesh>> e : objects.entrySet())
        {
            Program shader = e.getKey();
            shader.use();
            shader.setView(null);
            shader.setProjection(null);
            shader.setSun(null);
            for (Map.Entry<EntityID, Mesh> e2 : e.getValue().entrySet())
            {
                shader.setWorld(worlds.get(e2.getKey()));
                Mesh mesh = e2.getValue();
                mesh.draw();
            }
        }
    }

    private Mat4 getMatrix(State state)
    {
        Mat4 monkeyWorld = new Mat4(1f);

        monkeyWorld = translate(monkeyWorld, state.pos);

        monkeyWorld = rotate(monkeyWorld, state.rot.getX(), X_AXIS);
        monkeyWorld = rotate(monkeyWorld, state.rot.getZ(), Z_AXIS);
        monkeyWorld = rotate(monkeyWorld, state.rot.getY(), Y_AXIS);
        // don't ask...
        monkeyWorld = rotate(monkeyWorld, state.rot.getW(), X_AXIS);

        monkeyWorld = scale(monkeyWorld, state.scale);

        return monkeyWorld;
    }

    @Override
    public void destroy()
    {
        Display.destroy();
    }

}
