/*
 * The MIT License
 *
 * Copyright 2014 Daniel Smith <jellymann@gmail.com>.
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
import org.lwjgl.input.Keyboard;
import za.co.sourlemon.acropolis.athens.components.KeyboardComponent;
import za.co.sourlemon.acropolis.athens.nodes.RTSCamControlNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.tokyo.utils.StateUtils;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class RTSCamControlSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        KeyboardComponent keyboard = engine.getGlobal(KeyboardComponent.class);
        
        for (RTSCamControlNode node : engine.getNodeList(RTSCamControlNode.class))
        {
            float x = 0, y = 0;

            if (keyboard.down[Keyboard.KEY_UP])
            {
                y += 1;
            }
            if (keyboard.down[Keyboard.KEY_DOWN])
            {
                y -= 1;
            }
            if (keyboard.down[Keyboard.KEY_LEFT])
            {
                x += 1;
            }
            if (keyboard.down[Keyboard.KEY_RIGHT])
            {
                x -= 1;
            }
            
            Mat4 mat = Matrices.rotate(Mat4.MAT4_IDENTITY, node.camera.angle, StateUtils.Y_AXIS);
            
            Vec3 translation = mat.multiply(new Vec3(x,0,y).getUnitVector().toDirection()).getXYZ().multiply(node.camera.speed);
            
            node.view.eye = node.view.eye.add(translation);
            node.view.at = node.view.at.add(translation);
        }
    }
    
}
