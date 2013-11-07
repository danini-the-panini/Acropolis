/*
 * The MIT License
 *
 * Copyright 2013 sour Lemon.
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

package za.co.sourlemon.acropolis.athens;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import za.co.sourlemon.acropolis.athens.mesh.Mesh;
import za.co.sourlemon.acropolis.athens.mesh.WavefrontMesh;
import za.co.sourlemon.acropolis.athens.shader.FragmentShader;
import za.co.sourlemon.acropolis.athens.shader.Program;
import za.co.sourlemon.acropolis.athens.shader.VertexShader;

/**
 *
 * @author Daniel
 */
public class ResourceManager
{
    public static final String SHADER_DIR = "shaders";
    public static final String MESH_DIR = "meshes";
    public static final String VERTEX_NAME = "vertex";
    public static final String FRAGMENT_NAME = "fragment";
    public static final String SHADER_EXT = "glsl";
    public static final String MESH_EXT = "obj";    
    private final Map<String, Mesh> meshes = new HashMap<>();
    private final Map<String, Program> programs = new HashMap<>();
    
    public Program getProgram(String name)
    {
        Program program = programs.get(name);
        if (program == null)
        {
            VertexShader vertexShader = new VertexShader(new File(SHADER_DIR+"/"+name+"/"+VERTEX_NAME+"."+SHADER_EXT));
            FragmentShader fragmentShader = new FragmentShader(new File(SHADER_DIR+"/"+name+"/"+FRAGMENT_NAME+"."+SHADER_EXT));
            program = new Program(vertexShader, fragmentShader);
            try
            {
                program.load();
            } catch (IOException ex)
            {
                ex.printStackTrace(System.err);
            }
            programs.put(name,program);
        }
        return program;
    }
    
    public Mesh getMesh(String name)
    {
        Mesh mesh = meshes.get(name);
        if (mesh == null)
        {
            mesh = new WavefrontMesh(new File(MESH_DIR+"/"+name+"."+MESH_EXT));
            try
            {
                mesh.load();
            } catch (IOException ex)
            {
                ex.printStackTrace(System.err);
            }
            meshes.put(name,mesh);
        }
        return mesh;
    }
    
    public void unloadAll()
    {
        for (Mesh mesh : meshes.values())
        {
            mesh.unload();
        }
        for (Program program : programs.values())
        {
            program.unload();
        }
    }
}
