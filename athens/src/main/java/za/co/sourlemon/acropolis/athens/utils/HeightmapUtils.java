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

package za.co.sourlemon.acropolis.athens.utils;

import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import za.co.sourlemon.acropolis.athens.components.Heightmap;
import za.co.sourlemon.acropolis.athens.nodes.HeightmapNode;

/**
 *
 * @author Daniel Smith <jellymann@gmail.com>
 */
public class HeightmapUtils
{
    public static float samplei(int u, int v, Heightmap hm)
    {
        if (u < 0)
        {
            u = 0;
        }
        if (v < 0)
        {
            v = 0;
        }
        if (u >= hm.getWidth())
        {
            u = hm.getWidth() - 1;
        }
        if (v >= hm.getLength())
        {
            v = hm.getLength() - 1;
        }
        return hm.heights[u][v];
    }

    public static float samplef(float u, float v, Heightmap hm)
    {
        if (u < 0)
        {
            u = 0;
        }
        if (u > 1)
        {
            u = 1;
        }
        if (v < 0)
        {
            v = 0;
        }
        if (v > 1)
        {
            v = 1;
        }

        u *= hm.getWidth();
        v *= hm.getLength();
        int x = (int) (u);
        int y = (int) (v);
        float u_ratio = u - x;
        float v_ratio = v - y;
        float u_opposite = 1 - u_ratio;
        float v_opposite = 1 - v_ratio;

        return (samplei(x, y, hm) * u_opposite + samplei(x + 1, y, hm) * u_ratio) * v_opposite
                + (samplei(x, y + 1, hm) * u_opposite + samplei(x + 1, y + 1, hm) * u_ratio) * v_ratio;
    }

    public static float getHeight(float x, float y, HeightmapNode hnode)
    {
        final float w = hnode.state.scale.getX();
        final float h = hnode.state.scale.getY();
        final float l = hnode.state.scale.getZ();
        
        x += w * 0.5f;
        y += l * 0.5f;
        return h * (samplef(x / w, y / l, hnode.hm));
    }

    public static Vec3 getNormal(float x, float y, HeightmapNode hnode)
    {
        final float w = hnode.state.scale.getX();
        final float l = hnode.state.scale.getZ();
        
        final float QUAD_WIDTH = w * hnode.hm.xStep();
        final float QUAD_LENGTH = l * hnode.hm.yStep();

        float Hx = getHeight(x + QUAD_WIDTH, y, hnode) - getHeight(x - QUAD_WIDTH, y, hnode);
        Hx /= QUAD_WIDTH * 2;

        float Hz = getHeight(x, y + QUAD_LENGTH, hnode) - getHeight(x, y - QUAD_LENGTH, hnode);
        Hz /= QUAD_LENGTH * 2;

        return new Vec3(-Hx, 1.0f, -Hz).getUnitVector();
    }

    /**
     * Checks if the given point lies below the terrain.
     * @param v the point.
     * @param hnode the heightmap node representing the terrain.
     * @return true if the point lies under the terrain, false otherwise.
     */
    public static boolean under(Vec3 v, HeightmapNode hnode)
    {
        return v.getY() < getHeight(v.getX(), v.getZ(), hnode);
    }

    /**
     * Finds the intersections between the given line segment and the heightmap using
     * a brute force raymarching algorithm.
     *
     * @param a
     * @param b
     * @param accuracy
     * @param hnode
     * @return an array containing all the intersections between the line segment and the heigtmap
     */
    public static Vec3[] getIntersections(Vec3 a, Vec3 b, float accuracy, HeightmapNode hnode)
    {
        Vec3 v = b.subtract(a);
        float len = v.getLength();
        int n = (int) (len / accuracy);
        v = v.multiply(accuracy / len);
        boolean under = under(a, hnode);
        Vec3 temp = a.add(v);
        ArrayList<Vec3> intersections = new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            boolean under2 = under(temp, hnode);
            if (under != under2)
            {
                intersections.add(temp);
            }
            under = under2;
            temp = temp.add(v);
        }
        if (under != under(b, hnode))
        {
            intersections.add(b);
        }
        return intersections.toArray(new Vec3[0]);
    }
    
    /**
     * Finds the first intersection of the given line segment to the heightmap using
     * a brute force raymarching algorithm. Faster than getIntersections but only returns a single result.
     *
     * @param a
     * @param b
     * @param accuracy
     * @param hnode
     * @return the first intersection between the segment at the heightmap, null if the segment does not intersect.
     */
    public static Vec3 getIntersection(Vec3 a, Vec3 b, float accuracy, HeightmapNode hnode)
    {
        Vec3 v = b.subtract(a);
        float len = v.getLength();
        int n = (int) (len / accuracy);
        v = v.multiply(accuracy / len);
        boolean under = under(a, hnode);
        if (under) return a;
        Vec3 temp = a.add(v);
        for (int i = 0; i < n; i++)
        {
            boolean under2 = under(temp, hnode);
            if (under != under2)
            {
                return temp;
            }
            under = under2;
            temp = temp.add(v);
        }
        if (under != under(b,hnode))
        {
            return b;
        }
        return null;
    }
}
