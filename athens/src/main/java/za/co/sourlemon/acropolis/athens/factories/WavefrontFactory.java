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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import za.co.sourlemon.acropolis.athens.components.MeshComponent;
import za.co.sourlemon.acropolis.ems.ComponentFactory;

/**
 *
 * @author Daniel
 */
public class WavefrontFactory implements ComponentFactory<MeshComponent, WavefrontFactoryRequest>
{

    public static final String MESH_DIR = "meshes";
    public static final String MESH_EXT = "obj";

    @Override
    public MeshComponent create(WavefrontFactoryRequest request)
    {
        ArrayList<Vec3> vertices = new ArrayList<>();
        ArrayList<float[]> texcoords = new ArrayList<>();
        ArrayList<Vec3> normals = new ArrayList<>();
        ArrayList<int[][]> indices = new ArrayList<>();

        try
        {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(MESH_DIR + "/" + request.mesh + "." + MESH_EXT))))
            {
                String line;
                String[] list;
                while ((line = br.readLine()) != null)
                {
                    if (line.startsWith("v "))
                    {
                        list = line.split(" ");
                        // read X Y Z into vertex array
                        vertices.add(new Vec3(
                                Float.parseFloat(list[1]),
                                Float.parseFloat(list[2]),
                                Float.parseFloat(list[3])
                        ));
                    } else if (line.startsWith("vt "))
                    {
                        list = line.split(" ");
                        // Read X Y Z into normal array
                        texcoords.add(new float[]
                        {
                            Float.parseFloat(list[1]),
                            Float.parseFloat(list[2])
                        });
                    } else if (line.startsWith("vn "))
                    {
                        list = line.split(" ");
                        // Read X Y Z into normal array
                        Vec3 normal = new Vec3(
                                Float.parseFloat(list[1]),
                                Float.parseFloat(list[2]),
                                Float.parseFloat(list[3])
                        ).getUnitVector();
                        normals.add(new Vec3(
                                normal.getX(),
                                normal.getY(),
                                normal.getZ()
                        ));
                    } else if (line.startsWith("f "))
                    {
                        list = line.split(" ");

                        int[][] primitive = new int[list.length - 1][];

                        for (int i = 1; i < list.length; i++)
                        {
                            primitive[i - 1] = parseVertex(list[i].trim());
                        }

                        indices.add(primitive);
                    }
                }
            }
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return new MeshComponent(vertices, texcoords, normals, indices);
    }

    /**
     * Parses a wavefront vertex (i.e. "face point") by extracting the relevant
     * indices.
     *
     * @param string
     * @return
     */
    public static int[] parseVertex(String string)
    {
        int v, vt = 0, vn = 0;

        int a = string.indexOf('/');
        int b = string.lastIndexOf('/');
        if (b == a)
        {
            b = -1; // if there is no second "/"
        }
        if (a == -1) //                 "v"
        {
            v = Integer.parseInt(string);
        } else //                         "v/..."
        {
            v = Integer.parseInt(string.substring(0, a));
            if (b == -1) //             "v/vt"
            {
                vt = Integer.parseInt(string.substring(a + 1));
            } else if (b == a + 1) //       "v//vn"
            {
                vn = Integer.parseInt(string.substring(b + 1));
            } else //                     "v/vt/vn"
            {
                vt = Integer.parseInt(string.substring(a + 1, b));
                vn = Integer.parseInt(string.substring(b + 1));
            }
        }

        return new int[]
        {
            v - 1, vt - 1, vn - 1
        };
    }

}
