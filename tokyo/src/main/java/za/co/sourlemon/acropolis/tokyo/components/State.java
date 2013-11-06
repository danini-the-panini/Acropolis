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
 package za.co.sourlemon.acropolis.tokyo.components;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import za.co.sourlemon.acropolis.ems.Component;

/**
 *
 * @author daniel
 */
public class State extends Component
{

    /// PREVIOUS STATE ///
    public Vec3 prevPos = Vec3.VEC3_ZERO;
    public Vec4 prevRot = Vec4.VEC4_ZERO;
    public Vec3 prevScale = new Vec3(1,1,1);
    /// CURRENT STATE ///
    public Vec3 pos = Vec3.VEC3_ZERO;
    public Vec4 rot = Vec4.VEC4_ZERO;
    public Vec3 scale = new Vec3(1,1,1);

    public State()
    {
    }

    public State(Vec3 pos, Vec4 rot, Vec3 scale)
    {
        this.prevPos = pos;
        this.prevRot = rot;
        this.prevScale = scale;
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
    }

    public State(Vec3 prevPos, Vec4 prevRot, Vec3 prevScale, Vec3 pos, Vec4 rot, Vec3 scale)
    {
        this.prevPos = prevPos;
        this.prevRot = prevRot;
        this.prevScale = prevScale;
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
    }
}
