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

import com.hackoeur.jglm.Vec3;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.nodes.StereoNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author daniel
 */
public class StereoSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        for (StereoNode node : engine.getNodeList(StereoNode.class))
        {
            View left = node.stereo.left;
            View right = node.stereo.right;
            View center = node.center;
            
            left.up = right.up = center.up;
            Vec3 off = center.at.subtract(center.eye).cross(center.up).getUnitVector().multiply(node.stereo.dist * 0.5f);
            left.eye = center.eye.add(off.getNegated());
            left.at = center.at.add(off.getNegated());
            right.eye = center.eye.add(off);
            right.at = center.at.add(off);
        }
    }
    
}
