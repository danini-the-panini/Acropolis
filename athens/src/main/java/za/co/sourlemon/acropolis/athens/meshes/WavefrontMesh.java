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

package za.co.sourlemon.acropolis.athens.meshes;

import com.hackoeur.jglm.Vec3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Daniel
 */
public class WavefrontMesh implements Mesh
{
    private final File file;
    private int handle = 0;

    public WavefrontMesh(File file)
    {
        this.file = file;
    }

    @Override
    public void load() throws IOException
    {
        handle = glGenLists(1);
        
        ArrayList<float[]> pos = new ArrayList<>(); // Read in vertices
        ArrayList<float[]> tex = new ArrayList<>(); // Read in texcoords
        ArrayList<float[]> norm = new ArrayList<>(); // Read in vertex normals. NOTE: these do not match 1:1 to vertices.
        ArrayList<int[][]> inds = new ArrayList<>(); // Vertex indices

        BufferedReader br = new BufferedReader(new FileReader(file));
        try
        {
            String line;
            String[] list;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("v "))
                {
                    list = line.split(" ");
		    // read X Y Z into vertex array
                    pos.add(new float[]{
                        Float.parseFloat(list[1]),
                        Float.parseFloat(list[2]),
                        Float.parseFloat(list[3])
                    });
                } else if (line.startsWith("vt "))
                {
                    list = line.split(" ");
		    // Read X Y Z into normal array
                    tex.add(new float[]{
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
                    norm.add(new float[]{
                        normal.getX(),
                        normal.getY(),
                        normal.getZ()
                    });
                } else if (line.startsWith("f "))
                {
                    list = line.split(" ");
                    
                    int[][] primitive = new int[list.length-1][];
                    
                    for (int i = 1; i < list.length; i++)
                    {
                        primitive[i-1] = parseVertex(list[i].trim());
                    }
                    
                    inds.add(primitive);
                }
            }
        }
        finally
        {
            br.close();
        }
        
        glNewList(handle, GL_COMPILE);
        {
            for (int[][] v :inds)
            {
                glBegin(v.length == 3 ? GL_TRIANGLES : GL_QUADS);
                {
                    for (int i = 0; i < v.length; i++)
                    {
                        float[] p;
                        if (v[i][1] != -1) // if there is a texcoord
                        {
                            p = tex.get(v[i][1]-1);
                            glTexCoord2f(p[0], p[1]);
                        }
                        if (v[i][2] != -1) // if there is a normal
                        {
                            p = norm.get(v[i][2]-1);
                            glNormal3f(p[0], p[1], p[2]);
                        }
                        p = pos.get(v[i][0]-1);
                        glVertex3f(p[0], p[1], p[2]); // emit vertex and all attributes
                    }
                }
                glEnd();
            }
        }
        glEndList();
    }
    
    /**
     * Parses a wavefront vertex (i.e. "face point") by extracting the relevant indices.
     * @param string
     * @return 
     */
    public static int[] parseVertex(String string)
    {
        int v, vt = -1, vn = -1;
        
        int a = string.indexOf('/');
        int b = string.lastIndexOf('/');
        if (b == a) b = -1; // if there is no second "/"
        if (a == -1) //                 "v"
            v = Integer.parseInt(string);
        else //                         "v/..."
        {
            v = Integer.parseInt(string.substring(0, a));
            if (b == -1) //             "v/vt"
                vt = Integer.parseInt(string.substring(a+1));
            else if (b == a+1) //       "v//vn"
                vn = Integer.parseInt(string.substring(b+1));
            else //                     "v/vt/vn"
            {
                vt = Integer.parseInt(string.substring(a+1,b));
                vn = Integer.parseInt(string.substring(b+1));
            }
        }
        
        return new int[]{v, vt, vn};
    }

    @Override
    public void unload()
    {
        glDeleteLists(handle, 1);
    }

    @Override
    public void draw()
    {
    }
    
}
