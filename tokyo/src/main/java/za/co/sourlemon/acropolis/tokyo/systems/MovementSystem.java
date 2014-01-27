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

import java.util.List;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.tokyo.Integrator;
import za.co.sourlemon.acropolis.tokyo.components.Acceleration;
import za.co.sourlemon.acropolis.tokyo.nodes.MovementNode;

/**
 *
 * @author daniel
 */
public class MovementSystem extends AbstractSystem
{
    private final Integrator integrator;

    public MovementSystem(Integrator integrator)
    {
        this.integrator = integrator;
    }

    @Override
    public void update(Engine engine, double t, double dt)
    {
        List<MovementNode> nodes = engine.getNodeList(MovementNode.class);

        for (MovementNode node : nodes)
        {
            node.state.prevPos = node.state.pos;
            node.state.prevRot = node.state.rot;
            //node.state.prevScale = node.state.scale; // <- NOT NEEDED AS SYSTEM DOES NOT MODIFY SCALE
            integrator.integrate(node.state, node.velocity,
                    node.getEntity().getComponent(Acceleration.class),
                    (float) dt);
        }
    }

}
