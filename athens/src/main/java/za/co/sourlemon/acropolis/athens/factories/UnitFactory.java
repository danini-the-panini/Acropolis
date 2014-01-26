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
package za.co.sourlemon.acropolis.athens.factories;

import com.hackoeur.jglm.Quaternion;
import com.hackoeur.jglm.Vec3;
import za.co.sourlemon.acropolis.athens.components.MeshComponent;
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.components.SnapToTerrain;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.EntityFactory;
import za.co.sourlemon.acropolis.tokyo.components.BBox;
import za.co.sourlemon.acropolis.tokyo.components.State;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class UnitFactory implements EntityFactory<UnitFactoryRequest>
{

    private final WavefrontFactory factory = new WavefrontFactory();

    @Override
    public Entity create(UnitFactoryRequest request)
    {
        Entity entity = new Entity();

        State state = new State(request.position, Quaternion.QUAT_IDENT, new Vec3(request.size, request.size, request.size));
        entity.addComponent(state);
        entity.addComponent(new Renderable("pplighting", new Vec3(1, 1, 1), 1));
        MeshComponent mesh = factory.create(new WavefrontFactoryRequest(request.mesh));
        entity.addComponent(mesh);
        entity.addComponent(new SnapToTerrain());
        entity.addComponent(new BBox(state, mesh.getExtents(), mesh.getCenter()));

        return entity;
    }

}
