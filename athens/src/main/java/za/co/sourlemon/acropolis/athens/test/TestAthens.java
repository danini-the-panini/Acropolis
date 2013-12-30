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
package za.co.sourlemon.acropolis.athens.test;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.components.Perspective;
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.components.Sun;
import za.co.sourlemon.acropolis.athens.components.Camera;
import za.co.sourlemon.acropolis.athens.components.Heightmap;
import za.co.sourlemon.acropolis.athens.components.NoClipCamera;
import za.co.sourlemon.acropolis.athens.components.Stereo;
import za.co.sourlemon.acropolis.athens.components.Viewport;
import za.co.sourlemon.acropolis.athens.components.Window;
import za.co.sourlemon.acropolis.athens.factories.HeightmapFactory;
import za.co.sourlemon.acropolis.athens.factories.HeightmapFactoryRequest;
import za.co.sourlemon.acropolis.athens.factories.HeightmapMeshFactory;
import za.co.sourlemon.acropolis.athens.factories.HeightmapMeshFactoryRequest;
import za.co.sourlemon.acropolis.athens.factories.RelaxedHeightmapFactory;
import za.co.sourlemon.acropolis.athens.factories.WavefrontFactory;
import za.co.sourlemon.acropolis.athens.factories.WavefrontFactoryRequest;
import za.co.sourlemon.acropolis.athens.systems.NoClipCameraSystem;
import za.co.sourlemon.acropolis.athens.systems.PerspectiveCameraSystem;
import za.co.sourlemon.acropolis.athens.systems.RenderSystem;
import za.co.sourlemon.acropolis.athens.systems.StereoSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.FixedTimingThread;
import za.co.sourlemon.acropolis.ems.SystemThread;
import za.co.sourlemon.acropolis.ems.VariableTimingThread;
import za.co.sourlemon.acropolis.tokyo.components.State;
import za.co.sourlemon.acropolis.tokyo.components.Velocity;
import za.co.sourlemon.acropolis.tokyo.systems.MovementSystem;

/**
 *
 * @author Daniel
 */
public class TestAthens
{

    public static final Vec3 SUN_VEC = new Vec3(-2.47511f, 3.87557f, 3.17864f);
    public static final View STARTING_VIEW = new View(
            new Vec3(3, 3, 2), // eye
            new Vec3(0f, 0, 0f), // at
            new Vec3(0f, 1f, 0f) // up
    );
    public static final Camera CAMERA = new Camera();
    final static Engine engine = new Engine();
    public static final Perspective PERSPECTIVE = new Perspective(
                                 45.0f, 0.01f, 200.0f);
    public static final Viewport LEFT_VIEWPORT = new Viewport(0, 0, 0.5f, 1.0f);
    public static final Viewport RIGHT_VIEWPORT = new Viewport(0.5f, 0, 0.5f, 1.0f);
    public static final float STEREO_DIST = 1.0f;

    public static void main(String[] args)
    {

        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Window window = engine.getGlobal(Window.class);
                window.width = 1920;
                window.height = 1080;
                
                setupThreads();
                
                Entity leftVP = createStereoViewport(LEFT_VIEWPORT);
                Entity rightVP = createStereoViewport(RIGHT_VIEWPORT);
                engine.addEntity(createStereoCamera(leftVP.getComponent(View.class),rightVP.getComponent(View.class)));
                engine.addEntity(leftVP);
                engine.addEntity(rightVP);
//                engine.addEntity(createCamera());
                engine.addEntity(createLand());
                engine.addEntity(createMonkey(new State()));

                engine.setGlobal(new Sun(SUN_VEC));

                while (!window.closing)
                {
                    engine.update();
                }
                engine.shutDown();
            }

        });

        t.start();
        try
        {
            t.join();
        } catch (InterruptedException ex)
        {
        }

        engine.shutDown();
    }

    private static Entity createMonkey(State state)
    {
        WavefrontFactory wavefrontFactory = new WavefrontFactory();
        Entity entity = new Entity();
        entity.addComponent(state);
        entity.addComponent(new Velocity(Vec3.VEC3_ZERO, Vec3.VEC3_ZERO, new Vec4(0, 45, 0, 0), Vec4.VEC4_ZERO));
        entity.addComponent(new Renderable("pvlighting", new Vec3(1, 0, 1), 1.0f));
        entity.addComponent(wavefrontFactory.create(new WavefrontFactoryRequest("monkey")));
        return entity;
    }

    private static Entity createCamera()
    {
        Entity entity = new Entity();
        entity.addComponent(new Camera());
        entity.addComponent(PERSPECTIVE);
        entity.addComponent(STARTING_VIEW);
        entity.addComponent(new Viewport());
        entity.addComponent(new NoClipCamera(5, 25, 0.1f));
        return entity;
    }
    
    private static Entity createStereoCamera(View left, View right)
    {
        Entity entity = new Entity();
        entity.addComponent(STARTING_VIEW);
        entity.addComponent(new Stereo(left, right, STEREO_DIST));
        entity.addComponent(new Camera());
        entity.addComponent(new NoClipCamera(5, 25, 0.1f));
        return entity;
    }
    
    private static Entity createStereoViewport(Viewport vp)
    {
        Entity entity = new Entity();
        entity.addComponent(new Camera());
        entity.addComponent(PERSPECTIVE);
        entity.addComponent(new View());
        entity.addComponent(vp);
        return entity;
    }
    
    private static Entity createLand()
    {
        HeightmapFactory hmFactory = new RelaxedHeightmapFactory();
        HeightmapMeshFactory hmMeshFactory = new HeightmapMeshFactory();
        Entity entity = new Entity();
        entity.addComponent(new State(Vec3.VEC3_ZERO, Vec4.VEC4_ZERO, new Vec3(128,128,128)));
        Heightmap hm = hmFactory.create(new HeightmapFactoryRequest("hm2",0.1f));
        entity.addComponent(hm);
        entity.addComponent(hmMeshFactory.create(new HeightmapMeshFactoryRequest(hm)));
        entity.addComponent(new Renderable("pvlighting", new Vec3(0,0.7f,0), 1.0f));
        return entity;
    }

    private static void setupThreads()
    {
        SystemThread logicThread = new FixedTimingThread(1.0 / 60.0, 1.0 / 25.0);

        logicThread.addSystem(new MovementSystem());

        SystemThread renderThread;
        //renderThread = new FixedTimingThread(1.0 / 60, 1.0 / 25.0);
        renderThread = new VariableTimingThread();

        renderThread.addSystem(new RenderSystem());
        renderThread.addSystem(new NoClipCameraSystem());
        renderThread.addSystem(new StereoSystem());
        renderThread.addSystem(new PerspectiveCameraSystem());

        engine.addThread(logicThread);
        engine.addThread(renderThread);
    }
}
