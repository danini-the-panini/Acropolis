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
package za.co.sourlemon.acropolis.athens.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import za.co.sourlemon.acropolis.athens.Resource;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Daniel
 */
public class Shader implements Resource
{

    private int shader = 0;
    private final File file;
    private final int type;

    public Shader(File file, int type)
    {
        this.file = file;
        this.type = type;
    }

    @Override
    public void load()
            throws IOException
    {
        if (shader != 0)
        {
            return;
        }
        try
        {
            shader = glCreateShaderObjectARB(type);

            if (shader == 0)
            {
                return;
            }

            glShaderSourceARB(shader, readFileAsString(file));
            glCompileShaderARB(shader);

            if (glGetObjectParameteriARB(shader, GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE)
            {
                throw new IOException("Error creating shader: " + getLogInfo(shader));
            }

        } catch (IOException exc)
        {
            unload();
            throw exc;
        }
    }

    @Override
    public void unload()
    {
        glDeleteObjectARB(shader);
    }

    private static String getLogInfo(int obj)
    {
        return glGetInfoLogARB(obj, glGetObjectParameteriARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    static String readFileAsString(File file)
            throws IOException
    {
        String input = "";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                input += line + "\n";
            }
        }

        return input;
    }

    public void attachTo(int program)
    {
        glAttachObjectARB(program, shader);
    }
}
