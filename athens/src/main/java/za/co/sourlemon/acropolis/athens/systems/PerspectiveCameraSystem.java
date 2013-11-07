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

import com.hackoeur.jglm.Matrices;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.nodes.PerspectiveCameraNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel
 */
public class PerspectiveCameraSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        View activeView = engine.getGlobal(View.class);

        for (PerspectiveCameraNode node : engine.getNodeList(PerspectiveCameraNode.class))
        {
            if (node.view != activeView)
            {
                continue;
            }
            
//            dir = at.subtract(eye).getUnitVector();
//            right = dir.cross(up).getUnitVector();

            node.view.view = Matrices.lookAt(node.camera.eye, node.camera.at,
                    node.camera.up);
            float aspect = 1.0f; //(float) w / (float) h; // <- TODO!!!
            node.view.projection = Matrices.perspective(node.camera.fovY,
                    aspect, node.camera.near, node.camera.far);
        }
    }

}
