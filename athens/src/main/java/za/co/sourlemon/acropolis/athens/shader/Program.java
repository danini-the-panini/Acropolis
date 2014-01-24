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

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import java.io.IOException;
import za.co.sourlemon.acropolis.athens.Resource;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Daniel
 */
public class Program implements Resource
{

    private final VertexShader vertexShader;
    private final FragmentShader fragmentShader;
    private int program, view, projection, world, sun, colour, opacity, eye;

    public Program(VertexShader vertexShader, FragmentShader fragmentShader)
    {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    @Override
    public void load() throws IOException
    {
        if (program != 0)
        {
            return;
        }
        vertexShader.load();
        fragmentShader.load();

        program = glCreateProgramObjectARB();

        if (program == 0)
        {
            return;
        }

        vertexShader.attachTo(program);
        fragmentShader.attachTo(program);

        glLinkProgramARB(program);
        if (glGetObjectParameteriARB(program, GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE)
        {
            System.err.println(getLogInfo(program));
            program = 0;
            return;
        }

        glValidateProgramARB(program);
        if (glGetObjectParameteriARB(program, GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE)
        {
            System.err.println(getLogInfo(program));
            program = 0;
        }
        
        view = findUniform("view");
        projection = findUniform("projection");
        world = findUniform("world");
        sun = findUniform("sun");
        eye = findUniform("eye");
        colour = findUniform("colour");
        opacity = findUniform("opacity");
    }

    private int findUniform(String name)
    {
        return glGetUniformLocationARB(program, name);
    }

    @Override
    public void unload()
    {
        vertexShader.unload();
        fragmentShader.unload();
    }
    
    public void use()
    {
        glUseProgramObjectARB(program);
    }

    public void setView(Mat4 mat)
    {
        glUniformMatrix4ARB(view, false, mat.getBuffer());
    }

    public void setProjection(Mat4 mat)
    {
        glUniformMatrix4ARB(projection, false, mat.getBuffer());
    }

    public void setWorld(Mat4 mat)
    {
        glUniformMatrix4ARB(world, false, mat.getBuffer());
    }

    public void setSun(Vec3 v)
    {
        glUniform3ARB(sun, v.getBuffer());
    }
    
    public void setEye(Vec3 v)
    {
        glUniform3ARB(eye, v.getBuffer());
    }

    public void setColour(Vec3 v)
    {
        glUniform3ARB(colour, v.getBuffer());
    }

    public void setOpacity(float f)
    {
        glUniform1fARB(opacity, f);
    }

    private static String getLogInfo(int obj)
    {
        return glGetInfoLogARB(obj, glGetObjectParameteriARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

}
