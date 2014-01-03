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

package za.co.sourlemon.acropolis.tokyo.utils;

import com.hackoeur.jglm.Mat4;
import static com.hackoeur.jglm.Matrices.rotate;
import static com.hackoeur.jglm.Matrices.scale;
import static com.hackoeur.jglm.Matrices.translate;
import com.hackoeur.jglm.Vec3;
import za.co.sourlemon.acropolis.tokyo.components.State;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public final class StateUtils
{
    public static final Vec3 X_AXIS = new Vec3(1, 0, 0);
    public static final Vec3 Y_AXIS = new Vec3(0, 1, 0);
    public static final Vec3 Z_AXIS = new Vec3(0, 0, 1);
    
    private StateUtils()
    {
    }
    
    public static Mat4 getMatrix(State state)
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
    
    public static Mat4 getRotMatrix(State state)
    {
        Mat4 monkeyWorld = new Mat4(1f);

        monkeyWorld = rotate(monkeyWorld, state.rot.getX(), X_AXIS);
        monkeyWorld = rotate(monkeyWorld, state.rot.getZ(), Z_AXIS);
        monkeyWorld = rotate(monkeyWorld, state.rot.getY(), Y_AXIS);
        // don't ask...
        monkeyWorld = rotate(monkeyWorld, state.rot.getW(), X_AXIS);

        return monkeyWorld;
    }
}