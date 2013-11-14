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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import za.co.sourlemon.acropolis.athens.ResourceManager;
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.components.Sun;
import za.co.sourlemon.acropolis.athens.components.Camera;
import za.co.sourlemon.acropolis.athens.components.KeyboardComponent;
import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.components.Window;
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

    private final Map<EntityID, Mat4> worlds = new HashMap<>();
    private final Map<Program, Map<EntityID, Renderable>> objects = new HashMap<>();
    private final ResourceManager resourceManager = new ResourceManager();

    @Override
    public boolean init(Engine engine)
    {
        Window window = engine.getGlobal(Window.class);
        try
        {
            Display.setDisplayMode(new DisplayMode(window.width, window.height));
            Display.setResizable(true);
            Display.create();

            glClearColor(1, 1, 1, 1);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
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
        Window window = engine.getGlobal(Window.class);
        window.closing = Display.isCloseRequested();
        if (Display.wasResized())
        {
            window.width = Display.getWidth();
            window.height = Display.getHeight();
            glViewport(0, 0, window.width, window.height);
        }
        
        KeyboardComponent keyboard = engine.getGlobal(KeyboardComponent.class);
        Keyboard.poll();
        for (int i = 0; i < Keyboard.getKeyCount(); i++)
        {
            keyboard.pressed[i] = false;
            keyboard.released[i] = false;
            keyboard.down[i] = Keyboard.isKeyDown(i);
        }
        while (Keyboard.next())
        {
            int key = Keyboard.getEventKey();
            keyboard.pressed[key] = Keyboard.getEventKeyState();
            keyboard.released[key] = !keyboard.pressed[key];
        }
        
        MouseComponent mouse = engine.getGlobal(MouseComponent.class);
        Mouse.poll();
        for (int i = 0; i < Mouse.getButtonCount(); i++)
        {
            mouse.pressed[i] = false;
            mouse.pressed[i] = false;
            mouse.down[i] = Mouse.isButtonDown(i);
            mouse.dx = 0;
            mouse.dy = 0;
        }
        while (Mouse.next())
        {
            int btn = Mouse.getEventButton();
            if (btn != -1)
            {
                mouse.pressed[btn] = Mouse.getEventButtonState();
                mouse.released[btn] = !mouse.pressed[btn];
            }
            else
            {
                mouse.dx += Mouse.getEventDX();
                mouse.dy += Mouse.getEventDY();
            }
            mouse.x = Mouse.getEventX();
            mouse.y = Mouse.getEventY();
        }
        mouse.nx = normalise(mouse.x, window.width);
        mouse.ny = -normalise(mouse.y, window.height);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        for (Map m : objects.values())
        {
            m.clear();
        }
        
        // calculate the world matrix of each renderable,
        // and add them to their respective "shader buckets"
        for (RenderNode node : engine.getNodeList(RenderNode.class))
        {
            EntityID id = node.getEntity().getId();
            worlds.put(id, getMatrix(node.state));
            
            Program program = resourceManager.getProgram(node.renderable.shader);
            Map<EntityID, Renderable> renderables = objects.get(program);
            if (renderables == null)
            {
                renderables = new HashMap<>();
                objects.put(program, renderables);
            }
            renderables.put(id, node.renderable);
        }
        
        Camera camera = engine.getGlobal(Camera.class);
        
        // for each shader, draw each object that uses that shader
        for (Map.Entry<Program, Map<EntityID, Renderable>> e : objects.entrySet())
        {
            Program program = e.getKey();
            program.use();
            program.setView(camera.view);
            program.setProjection(camera.projection);
            program.setSun(engine.getGlobal(Sun.class).location);
            for (Map.Entry<EntityID, Renderable> e2 : e.getValue().entrySet())
            {
                Renderable renderable = e2.getValue();
                program.setWorld(worlds.get(e2.getKey()));
                program.setColour(renderable.colour);
                program.setOpacity(renderable.opacity);
                Mesh mesh = resourceManager.getMesh(renderable.mesh);
                mesh.draw();
            }
        }
        
        Display.update();
    }
    
    private float normalise(int x, int length)
    {
        return (float)x/(float)length-(float)length*0.5f;
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
        resourceManager.unloadAll();
        Display.destroy();
    }

}
