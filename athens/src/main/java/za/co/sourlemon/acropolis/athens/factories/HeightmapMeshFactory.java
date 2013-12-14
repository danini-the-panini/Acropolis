/*
 * The MIT License
 *
 * Copyright 2013 Sour Lemon.
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
        MeshComponent mesh = new MeshComponent();

        int w = request.heightmap.heights.length;
        int h = request.heightmap.heights[0].length;
        
        float xStep = 1.0f/(float)w;
        float zStep = 1.0f/(float)h;
        
        float x, y, z, u, v, nx, ny, nz;
        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                x = i*xStep+OFF;
                z = j*zStep+OFF;
                y = request.heightmap.heights[i][j]+OFF;
                mesh.pos.add(new float[]
                {
                    x, y, z
                });
                u = i*xStep;
                v = j*zStep;
                mesh.tex.add(new float[]
                {
                    u, v
                });
                // TODO: normals
                nx = nz = 0;
                ny = 1;
                mesh.norm.add(new float[]
                {
                    nx, ny, nz
                });
            }
        }
        int a, b, c, d;
        for (int i = 0; i < w-1; i++)
        {
            for (int j = 0; j < h-1; j++)
            {
                a = i+w*j;
                b = a+1;
                c = a+w;
                d = c+1;
                mesh.inds.add(new int[][]
                {
                    {a,a,a},{b,b,b},{d,d,d},
                    {b,b,b},{c,c,c},{d,d,d}
                });
            }
        }

        return mesh;
    }
}
