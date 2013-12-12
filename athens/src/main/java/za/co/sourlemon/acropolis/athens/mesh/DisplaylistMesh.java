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
package za.co.sourlemon.acropolis.athens.mesh;

import java.io.IOException;
import static org.lwjgl.opengl.GL11.*;
import za.co.sourlemon.acropolis.athens.components.MeshComponent;

/**
 *
 * @author Daniel
 */
public class DisplaylistMesh implements Mesh
{
    private MeshComponent mesh;
    private int handle = 0;

    public DisplaylistMesh(MeshComponent mesh)
    {
        this.mesh = mesh;
    }

    @Override
    public void load() throws IOException
    {
        handle = glGenLists(1);

        glNewList(handle, GL_COMPILE);
        {
            for (int[][] v : mesh.inds)
            {
                glBegin(v.length == 3 ? GL_TRIANGLES : GL_QUADS);
                {
                    for (int[] v1 : v)
                    {
                        float[] p;
                        if (v1[1] != -1)
                        {
                            p = mesh.tex.get(v1[1] - 1);
                            glTexCoord2f(p[0], p[1]);
                        }
                        if (v1[2] != -1)
                        {
                            p = mesh.norm.get(v1[2] - 1);
                            glNormal3f(p[0], p[1], p[2]);
                        }
                        p = mesh.pos.get(v1[0] - 1);
                        glVertex3f(p[0], p[1], p[2]); // emit vertex and all attributes
                    }
                }
                glEnd();
            }
        }
        glEndList();
    }

    @Override
    public void unload()
    {
        glDeleteLists(handle, 1);
    }

    @Override
    public void draw()
    {
        glCallList(handle);
    }

}
