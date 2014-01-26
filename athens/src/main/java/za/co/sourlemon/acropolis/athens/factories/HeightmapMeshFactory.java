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
package za.co.sourlemon.acropolis.athens.factories;

import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import za.co.sourlemon.acropolis.athens.components.MeshComponent;
import za.co.sourlemon.acropolis.ems.ComponentFactory;

/**
 *
 * @author Daniel
 */
public class HeightmapMeshFactory implements ComponentFactory<MeshComponent, HeightmapMeshFactoryRequest>
{

    public static final float OFF = -0.5f;

    @Override
    public MeshComponent create(HeightmapMeshFactoryRequest request)
    {
        ArrayList<Vec3> vertices = new ArrayList<>();
        ArrayList<float[]> texcoords = new ArrayList<>();
        ArrayList<Vec3> normals = new ArrayList<>();
        ArrayList<int[][]> indices = new ArrayList<>();

        final int w = request.heightmap.heights.length;
        final int l = request.heightmap.heights[0].length;

        final float xStep = request.heightmap.xStep();
        final float zStep = request.heightmap.yStep();

        final float[][] hm = request.heightmap.heights;

        float x, y, z, u, v;
        for (int j = 0; j < l; j++)
        {
            for (int i = 0; i < w; i++)
            {
                x = i * xStep + OFF;
                z = j * zStep + OFF;
                y = hm[i][j];
                vertices.add(new Vec3(x, y, z));
                u = i * xStep;
                v = j * zStep;
                texcoords.add(new float[]
                {
                    u, v
                });
                float Hx = hm[i < w - 1 ? i + 1 : i][j] - hm[i > 0 ? i - 1 : i][j];
                if (i == 0 || i == w - 1)
                {
                    Hx *= 2;
                }
                Hx /= xStep;

                float Hz = hm[i][j < l - 1 ? j + 1 : j] - hm[i][j > 0 ? j - 1 : j];
                if (j == 0 || j == l - 1)
                {
                    Hz *= 2;
                }
                Hz /= zStep;

                Vec3 n = new Vec3(-Hx, 1.0f, -Hz).getUnitVector();

                normals.add(new Vec3(n.getX(), n.getY(), n.getZ()));
            }
        }
        int a, b, c, d;
        for (int j = 0; j < l - 1; j++)
        {
            for (int i = 0; i < w - 1; i++)
            {
                a = i + j * w;
                b = a + 1;
                c = b + w;
                d = a + w;
                indices.add(new int[][]
                {
                    {
                        a, a, a
                    }, 
                    {
                        c, c, c
                    }, 
                    {
                        b, b, b
                    }
                //{a, a, a},{b, b, b},{d, d, d}
                });
                indices.add(new int[][]
                {
                    {
                        a, a, a
                    }, 
                    {
                        d, d, d
                    }, 
                    {
                        c, c, c
                    }
                });
            }
        }

        return new MeshComponent(vertices, texcoords, normals, indices);
    }
}
