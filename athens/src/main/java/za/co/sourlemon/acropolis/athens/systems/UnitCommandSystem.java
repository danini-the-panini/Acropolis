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

import com.hackoeur.jglm.Vec3;
import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.components.MoveCommand;
import za.co.sourlemon.acropolis.athens.factories.UnitFactory;
import za.co.sourlemon.acropolis.athens.factories.UnitFactoryRequest;
import za.co.sourlemon.acropolis.athens.nodes.MoveCommandNode;
import za.co.sourlemon.acropolis.athens.nodes.HeightmapNode;
import za.co.sourlemon.acropolis.athens.nodes.SelectableMoveableNode;
import za.co.sourlemon.acropolis.athens.utils.HeightmapUtils;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class UnitCommandSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {

        MouseComponent mouse = engine.getGlobal(MouseComponent.class);

        for (HeightmapNode node : engine.getNodeList(HeightmapNode.class))
        {
            if (mouse.pressed[1])
            {

                Vec3 p = HeightmapUtils.getIntersection(mouse.near, mouse.far, 0.1f, node);

                if (p != null)
                {
                    for (SelectableMoveableNode snode : engine.getNodeList(SelectableMoveableNode.class))
                    {
                        if (snode.selectable.selected)
                        {
                            if (snode.getEntity().hasComponent(MoveCommand.class))
                            {
                                snode.getEntity().getComponent(MoveCommand.class).position = p;
                            } else
                            {
                                snode.getEntity().addComponent(new MoveCommand(p));
                            }
                        }
                    }
                }
            }
        }
    }

}
