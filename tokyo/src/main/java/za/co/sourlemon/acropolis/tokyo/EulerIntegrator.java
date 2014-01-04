/*
 * The MIT License
 *
 * Copyright 2014 Sour Lemon.
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

package za.co.sourlemon.acropolis.tokyo;

import za.co.sourlemon.acropolis.tokyo.components.Acceleration;
import za.co.sourlemon.acropolis.tokyo.components.State;
import za.co.sourlemon.acropolis.tokyo.components.Velocity;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class EulerIntegrator implements Integrator
{

    @Override
    public void integrate(State state, Velocity velocity, Acceleration acceleration, float dt)
    {
        if (acceleration != null)
        {
            velocity.v = velocity.v.add(acceleration.a.multiply(dt));
            velocity.av = velocity.av.slerp(velocity.av.multiply(acceleration.aa), dt);
        }
        
        state.prevPos = state.pos;
        state.prevRot = state.rot;
        
        state.pos = state.pos.add(velocity.v.multiply(dt));
        state.rot = state.rot.slerp(state.rot.multiply(velocity.av), dt);
    }
    
}
