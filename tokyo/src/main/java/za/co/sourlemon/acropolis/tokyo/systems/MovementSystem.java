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
 package za.co.sourlemon.acropolis.tokyo.systems;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import java.util.List;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.tokyo.components.State;
import za.co.sourlemon.acropolis.tokyo.components.Velocity;
import za.co.sourlemon.acropolis.tokyo.nodes.MovementNode;

/**
 *
 * @author daniel
 */
public class MovementSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double t, double dt)
    {
        List<MovementNode> nodes = engine.getNodeList(MovementNode.class);
        
        for (MovementNode node : nodes)
        {
            node.state.prevPos = node.state.pos;
            node.state.prevRot = node.state.rot;
            //node.state.prevScale = node.state.scale; // <- NOT NEEDED AS SYSTEM DOES NOT MODIFY SCALE
            integrate(node.state, node.velocity, (float)dt);
        }
    }

    @Override
    public void destroy()
    {
    }
    
    // helper "struct" for use in integration
    private static class Derivative
    {
        Vec3 dx = Vec3.VEC3_ZERO;
        Vec3 dv = Vec3.VEC3_ZERO;
        
        // angular
        Vec4 dax = Vec4.VEC4_ZERO;
        Vec4 dav = Vec4.VEC4_ZERO;
    }
    
    /**
     * This method performs RK4 integration to approximate the position of an
     * entityState after a time-step. this method takes advantage of the fact
     * that a better approximation of a function can be reached if we use its
     * higher order derivatives. This results in much faster convergence than
     * Euler integration.
     *
     * @see <a
     * href="http://gafferongames.com/game-physics/integration-basics/">RK4
     * source 1<a/>
     * @see <a
     * href="http://stackoverflow.com/questions/1668098/runge-kutta-rk4-integration-for-game-physics">RK4
     * source 2<a/>
     *
     * @param state
     * @param velocity
     * @param dt
     */
     private static void integrate(State state, final Velocity velocity, final float dt)
    {
        Derivative a = evaluate(state, velocity, 0.0f, new Derivative());
        Derivative b = evaluate(state, velocity, dt * 0.5f, a);
        Derivative c = evaluate(state, velocity, dt * 0.5f, b);
        Derivative d = evaluate(state, velocity, dt, c);
        
        state.pos = state.pos.add(d.dx.add(a.dx.add((b.dx.add(c.dx)).multiply(2))).multiply(dt / 6.0f));
        state.rot = state.rot.add(d.dax.add(a.dax.add((b.dax.add(c.dax)).multiply(2))).multiply(dt / 6.0f));
        velocity.velocity = velocity.velocity.add(d.dv.add(a.dv.add((b.dv.add(c.dv)).multiply(2))).multiply(dt / 6.0f));
        velocity.angularVelocity = velocity.angularVelocity.add(d.dav.add(a.dav.add((b.dav.add(c.dav)).multiply(2))).multiply(dt / 6.0f));
    }

    /**
     * this function evaluates a state in terms of the current time t and the
     * length of time for one time step dt. this function returns a derivative
     * object.
     *
     * @param state
     * @param velocity
     * @param dt <i>time-step length</i>
     * @param derivative
     * @return
     */
    private static Derivative evaluate(final State state, final Velocity velocity,
            final float dt, final Derivative derivative)
    {
        //Vec3 pos = state.pos.add(derivative.dx.multiply(dt));
        Vec3 v = velocity.velocity.add(derivative.dv.multiply(dt));
        Vec4 av = velocity.angularVelocity.add(derivative.dav.multiply(dt));
        
        Derivative output = new Derivative();
        output.dx = v;
        output.dv = velocity.acceleration;
        output.dax = av;
        output.dav = velocity.angularAcceleration;
        
        return output;
    }
    
}
