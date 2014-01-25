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
import za.co.sourlemon.acropolis.athens.factories.UnitFactory;
import za.co.sourlemon.acropolis.athens.factories.UnitFactoryRequest;
import za.co.sourlemon.acropolis.athens.nodes.HeightmapNode;
import za.co.sourlemon.acropolis.athens.utils.HeightmapUtils;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class TankMaker extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        UnitFactory factory = new UnitFactory();
        UnitFactoryRequest request = new UnitFactoryRequest(Vec3.VEC3_ZERO, 0.3f, "tank");

        MouseComponent mouse = engine.getGlobal(MouseComponent.class);

        for (HeightmapNode node : engine.getNodeList(HeightmapNode.class))
        {
            if (mouse.pressed[0] || mouse.pressed[1] || mouse.pressed[2])
            {

                Vec3 p = HeightmapUtils.getIntersection(mouse.near, mouse.far, 0.1f, node);

                if (mouse.pressed[2] && p != null)
                {
                    engine.addEntity(factory.create(request.atPosition(p)));
                }
            }
        }
    }

}
