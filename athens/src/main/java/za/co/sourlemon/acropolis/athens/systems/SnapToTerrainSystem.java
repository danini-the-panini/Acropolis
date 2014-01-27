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
package za.co.sourlemon.acropolis.athens.systems;

import com.hackoeur.jglm.Quaternion;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.support.Compare;
import com.hackoeur.jglm.support.FastMath;
import za.co.sourlemon.acropolis.athens.components.Heightmap;
import za.co.sourlemon.acropolis.athens.nodes.HeightmapNode;
import za.co.sourlemon.acropolis.athens.nodes.SnapToTerrainNode;
import za.co.sourlemon.acropolis.ems.AbstractSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.tokyo.utils.StateUtils;
import static za.co.sourlemon.acropolis.athens.utils.HeightmapUtils.*;

/**
 *
 * @author Daniel
 */
public class SnapToTerrainSystem extends AbstractSystem
{

    @Override
    public void update(Engine engine, double t, double dt)
    {
        Heightmap hm = engine.getGlobal(Heightmap.class);

        for (HeightmapNode hnode : engine.getNodeList(HeightmapNode.class))
        {
            for (SnapToTerrainNode node : engine.getNodeList(SnapToTerrainNode.class))
            {
                float x = node.state.pos.getX();
                float z = node.state.pos.getZ();
                float y = getHeight(x, z, hnode);
                node.state.pos = new Vec3(x, y, z);

                Vec3 normal = StateUtils.getRotMatrix(node.state).getInverse().rotateVec3(getNormal(x, z, hnode));
                Vec3 tankNX = new Vec3(normal.getX(), normal.getY(), 0.0f).getUnitVector();
                Vec3 tankNZ = new Vec3(0.0f, normal.getY(), normal.getZ()).getUnitVector();

                float xRot = (float) (Math.asin(tankNZ.getZ()));
                float zRot = -(float) (Math.asin(tankNX.getX()));

                node.state.rot = node.state.rot.multiply(Quaternion.createFromAxisAngle(StateUtils.X_AXIS, xRot)
                        .multiply(Quaternion.createFromAxisAngle(StateUtils.Z_AXIS, zRot)));
            }
        }
    }
}
