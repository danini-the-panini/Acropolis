/*
 * The MIT License
 *
 * Copyright 2013 Daniel Smith <jellymann@gmail.com>.
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import za.co.sourlemon.acropolis.athens.ResourceManager;
import za.co.sourlemon.acropolis.athens.components.Sun;
import za.co.sourlemon.acropolis.athens.components.Camera;
import za.co.sourlemon.acropolis.athens.components.KeyboardComponent;
import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.components.Window;
import za.co.sourlemon.acropolis.athens.mesh.Mesh;
import za.co.sourlemon.acropolis.athens.nodes.RenderNode;
import za.co.sourlemon.acropolis.athens.nodes.ViewportNode;
import za.co.sourlemon.acropolis.athens.shader.Program;
import za.co.sourlemon.acropolis.athens.utils.MouseUtils;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.id.ID;
import za.co.sourlemon.acropolis.tokyo.nodes.BBoxNode;
import za.co.sourlemon.acropolis.tokyo.utils.StateUtils;

/**
 *
 * @author Daniel
 */
public class RenderSystem extends AbstractSystem
{

    private final Map<ID<Entity>, Mat4> worlds = new HashMap<>();
    private final Map<Program, Map<ID<Entity>, RenderNode>> objects = new HashMap<>();
    private final ResourceManager resourceManager = new ResourceManager();

    @Override
    public boolean init(Engine engine)
    {
        Window window = engine.getGlobal(Window.class);
        try
        {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            DisplayMode chosen = modes[0];

            for (DisplayMode mode : modes)
            {
                if (mode.getWidth() == window.width
                        && mode.getHeight() == window.height
                        && mode.isFullscreenCapable())
                {
                    chosen = mode;
                }
                break;
            }

            chosen = (DisplayMode) JOptionPane.showInputDialog(null, "Display Options",
                    "Display Mode", JOptionPane.QUESTION_MESSAGE, null,
                    modes, chosen);
            if (chosen == null)
            {
                return false;
            }
            Display.setDisplayMode(chosen);
            Display.setFullscreen(JOptionPane.showConfirmDialog(null, "Fullscreen?", "Display Options", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION);
            Display.setVSyncEnabled(true);
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
        }

        KeyboardComponent keyboard = engine.getGlobal(KeyboardComponent.class);
        Keyboard.poll();
        for (int i = 0; i < keyboard.down.length; i++)
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
            } else
            {
                mouse.dx += Mouse.getEventDX();
                mouse.dy += Mouse.getEventDY();
            }
            mouse.x = Mouse.getEventX();
            mouse.y = Mouse.getEventY();
        }
        mouse.nx = ((float) mouse.x / (float) window.width) * 2.0f - 1.0f;
        mouse.ny = ((float) mouse.y / (float) window.height) * 2.0f - 1.0f;
        
        Vec3[] mp
                = MouseUtils.mouseToWorld(
                        window,
                        engine.getGlobal(Camera.class),
                        mouse);
        
        mouse.near = mp[0];
        mouse.far = mp[1];

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (Map m : objects.values())
        {
            m.clear();
        }

        // calculate the world matrix of each renderable,
        // and add them to their respective "shader buckets"
        for (RenderNode node : engine.getNodeList(RenderNode.class))
        {
            ID<Entity> id = node.getEntity().getId();
            worlds.put(id, StateUtils.getMatrix(node.state));

            Program program = resourceManager.getProgram(node.renderable.shader);
            Map<ID<Entity>, RenderNode> renderables = objects.get(program);
            if (renderables == null)
            {
                renderables = new HashMap<>();
                objects.put(program, renderables);
            }
            renderables.put(id, node);
        }

        for (ViewportNode vpnode : engine.getNodeList(ViewportNode.class))
        {
            Camera camera = vpnode.camera;
            glViewport(
                    (int) (window.width * vpnode.viewport.x),
                    (int) (window.height * vpnode.viewport.y),
                    (int) (window.width * vpnode.viewport.width),
                    (int) (window.height * vpnode.viewport.height));

            // for each shader, draw each object that uses that shader
            for (Map.Entry<Program, Map<ID<Entity>, RenderNode>> e : objects.entrySet())
            {
                Program program = e.getKey();
                program.use();
                program.setView(camera.viewMatrix);
                program.setProjection(camera.projection);
                program.setSun(engine.getGlobal(Sun.class).location);
                program.setEye(camera.eye);
                for (Map.Entry<ID<Entity>, RenderNode> e2 : e.getValue().entrySet())
                {
                    RenderNode node = e2.getValue();
                    program.setWorld(worlds.get(e2.getKey()));
                    program.setColour(node.renderable.colour);
                    program.setOpacity(node.renderable.opacity);
                    Mesh mesh = resourceManager.getMesh(node.mesh);
                    mesh.draw();
                }
            }
            
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
            glDisable(GL_CULL_FACE);
            
            Program program = resourceManager.getProgram("passthrough");
            program.use();
            program.setView(camera.viewMatrix);
            program.setProjection(camera.projection);
            for (BBoxNode node : engine.getNodeList(BBoxNode.class))
            {
                program.setWorld(node.bbox.getMatrix());
                
                Vec3 e = node.bbox.getExtents();
                
                glBegin(GL_QUADS);
                {
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), e.getY(), -e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), e.getY(), -e.getZ());
                    
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), -e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), -e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), -e.getY(), -e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), -e.getY(), -e.getZ());
                    
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), -e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), -e.getY(), -e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(e.getX(), e.getY(), -e.getZ());
                    
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), -e.getY(), e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), -e.getY(), -e.getZ());
                    glColor3f(1, 0, 1);
                    glVertex3f(-e.getX(), e.getY(), -e.getZ());
                    
                }
                glEnd();
                
            }
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
            glEnable(GL_CULL_FACE);
            
        }

        Display.update();
    }

    @Override
    public void destroy()
    {
        resourceManager.unloadAll();
        Display.destroy();
    }

}
