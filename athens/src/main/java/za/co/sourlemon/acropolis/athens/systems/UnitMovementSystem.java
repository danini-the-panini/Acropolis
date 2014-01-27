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
package za.co.sourlemon.acropolis.athens.systems;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Quaternion;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.support.Compare;
import com.hackoeur.jglm.support.FastMath;
import za.co.sourlemon.acropolis.athens.components.MoveCommand;
import za.co.sourlemon.acropolis.athens.nodes.MoveCommandNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.tokyo.utils.StateUtils;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class UnitMovementSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        for (MoveCommandNode node : engine.getNodeList(MoveCommandNode.class))
        {
            Mat4 rot = StateUtils.getRotMatrix(node.state);
            Vec3 forward = rot.rotateVec3(StateUtils.Z_AXIS);

            Vec3 command = node.command.position.subtract(node.state.pos);

            if (command.getLengthSquared() < 0.1f)
            {
                node.getEntity().removeComponent(MoveCommand.class);
                node.velocity.av = Quaternion.QUAT_IDENT;
                node.velocity.v = Vec3.VEC3_ZERO;

                continue;
            }

            float speed = node.movement.speed;

            if (command.getLengthSquared() < speed * speed)
            {
                speed = command.getLength();
            }

            node.velocity.av = forward.quaterntionTo(command).getNormalised();
            node.velocity.v = forward.scale(speed);
        }
    }

}
