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

package za.co.sourlemon.acropolis.athens.utils;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import za.co.sourlemon.acropolis.athens.components.Camera;
import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.components.Window;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class MouseUtils
{
    
    public static Vec3[] mouseToWorld(Window display, Camera camera, MouseComponent mouse)
    {
        Vec3[] mousePoints = new Vec3[2];

        Mat4 camInv = camera.projection.multiply(camera.viewMatrix).getInverse();
        
        System.out.println("x: " + mouse.nx + ", y: " + mouse.ny);
        Vec4 mouseFar4 = camInv.multiply(new Vec4(mouse.nx, mouse.ny, 1, 1));
        Vec4 mouseNear4 = camInv.multiply(new Vec4(mouse.nx, mouse.ny, -1, 1));
        
        mousePoints[0] = mouseNear4.getXYZ().scale(1.0f / mouseNear4.getW());
        mousePoints[1] = mouseFar4.getXYZ().scale(1.0f / mouseFar4.getW());
        
        System.out.println("M3D: " + mousePoints[0]);
        
        return mousePoints;
    }
}
