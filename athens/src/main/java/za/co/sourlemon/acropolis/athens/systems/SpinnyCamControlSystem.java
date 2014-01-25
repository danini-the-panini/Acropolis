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

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.support.FastMath;
import za.co.sourlemon.acropolis.athens.nodes.SpinnyCamControlNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel
 */
public class SpinnyCamControlSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double t, double dt)
    {

        for (SpinnyCamControlNode node : engine.getNodeList(SpinnyCamControlNode.class))
        {
            final float angleRad = (float) FastMath.toRadians(node.camera.angle);
            final float pitchRad = (float) FastMath.toRadians(node.camera.pitch);

            node.camera.angle += dt * node.camera.angularVelocity;
            node.view.at = node.camera.target;
            node.view.up = new Vec3(0, 1, 0);
            float y = (float) FastMath.sin(pitchRad) * node.camera.distance;
            float r = (float) FastMath.cos(pitchRad) * node.camera.distance;
            float x = node.camera.target.getX() + (float) FastMath.sin(angleRad) * r;
            float z = node.camera.target.getZ() + (float) FastMath.cos(angleRad) * r;
            node.view.eye = new Vec3(x, y, z);
        }
    }

    @Override
    public void destroy()
    {
    }
}
