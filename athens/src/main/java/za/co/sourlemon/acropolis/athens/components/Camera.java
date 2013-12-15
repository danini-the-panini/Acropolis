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

package za.co.sourlemon.acropolis.athens.components;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import za.co.sourlemon.acropolis.ems.Component;

/**
 *
 * @author Daniel
 */
public class Camera extends Component
{
    public Mat4 viewMatrix = Mat4.MAT4_IDENTITY;
    public Mat4 projection = Mat4.MAT4_IDENTITY;
    
    public Vec3 eye = Vec3.VEC3_ZERO;

    public Camera()
    {
    }

    public Camera(Mat4 view, Mat4 projection)
    {
        this.viewMatrix = view;
        this.projection = projection;
    }
    
}
