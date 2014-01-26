/*
 * The MIT License
 *
 * Copyright 2013 Daniel Smith <jellymann@gmail.com>.
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
package za.co.sourlemon.acropolis.athens.components;

import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import za.co.sourlemon.acropolis.ems.Component;

/**
 *
 * @author Daniel
 */
public class MeshComponent extends Component
{

    private ArrayList<Vec3> vertices = new ArrayList<>();
    private ArrayList<float[]> texcoords = new ArrayList<>();
    private ArrayList<Vec3> normals = new ArrayList<>(); // NOTE: these do not necessarily match 1:1 to vertices.
    private ArrayList<int[][]> indices = new ArrayList<>();

    private Vec3 extents = Vec3.VEC3_ZERO, center = Vec3.VEC3_ZERO;

    public MeshComponent()
    {
    }

    public MeshComponent(ArrayList<Vec3> vertices, ArrayList<float[]> texcoords, ArrayList<Vec3> normals, ArrayList<int[][]> indices)
    {
        this.vertices = vertices;
        this.texcoords = texcoords;
        this.normals = normals;
        this.indices = indices;

        float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;

        for (Vec3 v : vertices)
        {
            minX = minX < v.getX() ? minX : v.getX();
            minY = minY < v.getY() ? minY : v.getY();
            minZ = minZ < v.getZ() ? minZ : v.getZ();

            maxX = maxX > v.getX() ? maxX : v.getX();
            maxY = maxY > v.getY() ? maxY : v.getY();
            maxZ = maxZ > v.getZ() ? maxZ : v.getZ();
        }

        extents = new Vec3(
                (maxX - minX) * 0.5f,
                (maxY - minY) * 0.5f,
                (maxZ - minZ) * 0.5f
        );
        center = new Vec3(
                (minX + maxX) * 0.5f,
                (minY + maxY) * 0.5f,
                (minZ + maxZ) * 0.5f
        );
    }

    public ArrayList<int[][]> getIndices()
    {
        return indices;
    }

    public ArrayList<Vec3> getVertices()
    {
        return vertices;
    }

    public ArrayList<Vec3> getNormals()
    {
        return normals;
    }

    public ArrayList<float[]> getTexcoords()
    {
        return texcoords;
    }

    public Vec3 getCenter()
    {
        return center;
    }

    public Vec3 getExtents()
    {
        return extents;
    }

}
