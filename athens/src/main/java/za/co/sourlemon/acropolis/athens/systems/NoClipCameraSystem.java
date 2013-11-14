/*
 * Copyright (c) 2013 Triforce - in association with the University of Pretoria and Epi-Use <Advance/>
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
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import org.lwjgl.input.Keyboard;
import za.co.sourlemon.acropolis.athens.components.KeyboardComponent;
import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.nodes.NoClipCameraNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel
 */
public class NoClipCameraSystem extends AbstractSystem
{
    @Override
    public void update(Engine engine, double t, double dt)
    {
        MouseComponent mouse = engine.getGlobal(MouseComponent.class);
        KeyboardComponent keyboard = engine.getGlobal(KeyboardComponent.class);
        
        for (NoClipCameraNode node : engine.getNodeList(NoClipCameraNode.class))
        {
            Vec3 dir = node.view.at.subtract(node.view.eye).getUnitVector();
            Vec3 right = dir.cross(node.view.up).getUnitVector();
            
            if (mouse.down[0])
            {
                final float lateral = -mouse.dx * node.camera.sensitivity;
                final float vertical = mouse.dy * node.camera.sensitivity;

                rotate(node.view, dir, right, lateral, vertical);
            }
            
            float amount = keyboard.down[Keyboard.KEY_LSHIFT]
                    ? node.camera.sprintSpeed
                    : node.camera.normalSpeed;
            amount *= dt;

            if (keyboard.down[Keyboard.KEY_W])
            {
                moveForward(node.view, dir, amount);
            } else if (keyboard.down[Keyboard.KEY_S])
            {
                moveForward(node.view, dir, -amount);
            }

            if (keyboard.down[Keyboard.KEY_D])
            {
                moveRight(node.view, right, amount);
            } else if (keyboard.down[Keyboard.KEY_A])
            {
                moveRight(node.view, right, -amount);
            }
        }
    }
    
    public void moveForward(View camera, Vec3 dir, float amount)
    {
        Vec3 step = dir.multiply(amount);
        
        camera.eye = camera.eye.add(step);
        camera.at = camera.at.add(step);
    }
    
    public void moveRight(View camera, Vec3 right, float amount)
    {
        Vec3 step = right.multiply(amount);
        
        camera.eye = camera.eye.add(step);
        camera.at = camera.at.add(step);
    }
    
    public void rotate(View camera, Vec3 dir, Vec3 right, float lateral, float vertical)
    {
        Vec4 d = new Vec4(dir.getX(), dir.getY(), dir.getZ(), 0.0f);

        Mat4 rot = new Mat4(1.0f);
        rot = Matrices.rotate(rot, vertical, right);
        rot = Matrices.rotate(rot, lateral, camera.up);

        d = rot.multiply(d);

        camera.at = camera.eye.add(new Vec3(d.getX(),d.getY(),d.getZ()));
    }

    @Override
    public void destroy()
    {
    }
    
}
