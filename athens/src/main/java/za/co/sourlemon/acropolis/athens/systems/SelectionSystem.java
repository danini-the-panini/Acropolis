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

import za.co.sourlemon.acropolis.athens.components.MouseComponent;
import za.co.sourlemon.acropolis.athens.nodes.SelectableNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class SelectionSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double time, double dt)
    {
        MouseComponent mouse = engine.getGlobal(MouseComponent.class);

        if (mouse.pressed[0])
        {
            SelectableNode choice = null;
            float closest = Float.POSITIVE_INFINITY;
            for (SelectableNode node : engine.getNodeList(SelectableNode.class))
            {
                node.selectable.selected = false;
                float f = node.bbox.getEntrancePointDistance(mouse.near, mouse.far.subtract(mouse.near));
                if (!Float.isInfinite(f) && f < closest)
                {
                    choice = node;
                    closest = f;
                }
            }
            if (choice != null)
            {
                choice.selectable.selected = true;
            }
        }
    }

}
