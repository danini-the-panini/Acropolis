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
package za.co.sourlemon.acropolis.athens.test;

import com.hackoeur.jglm.Quaternion;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.support.FastMath;
import javax.swing.JOptionPane;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.components.PerspectiveProjection;
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.components.Sun;
import za.co.sourlemon.acropolis.athens.components.Camera;
import za.co.sourlemon.acropolis.athens.components.Heightmap;
import za.co.sourlemon.acropolis.athens.components.NoClipCamControl;
import za.co.sourlemon.acropolis.athens.components.RTSCamControl;
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
import za.co.sourlemon.acropolis.athens.systems.ExitOnCloseSystem;
import za.co.sourlemon.acropolis.athens.systems.NoClipCamControlSystem;
import za.co.sourlemon.acropolis.athens.systems.PerspectiveProjectionSystem;
import za.co.sourlemon.acropolis.athens.systems.RTSCamControlSystem;
import za.co.sourlemon.acropolis.athens.systems.RenderSystem;
import za.co.sourlemon.acropolis.athens.systems.SnapToTerrainSystem;
import za.co.sourlemon.acropolis.athens.systems.SpinnyCamControlSystem;
import za.co.sourlemon.acropolis.athens.systems.StereoSystem;
import za.co.sourlemon.acropolis.athens.systems.TankMaker;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.FixedTimingThread;
import za.co.sourlemon.acropolis.ems.SystemThread;
import za.co.sourlemon.acropolis.ems.VariableTimingThread;
import za.co.sourlemon.acropolis.ems.components.EngineInfo;
import za.co.sourlemon.acropolis.tokyo.EulerIntegrator;
import za.co.sourlemon.acropolis.tokyo.RK4Integrator;
import za.co.sourlemon.acropolis.tokyo.components.State;
import za.co.sourlemon.acropolis.tokyo.components.Velocity;
import za.co.sourlemon.acropolis.tokyo.systems.BBoxSystem;
import za.co.sourlemon.acropolis.tokyo.systems.MovementSystem;

/**
 *
 * @author Daniel
 */
public class TestAthens
{

    public static final Vec3 SUN_VEC = new Vec3(-2.47511f, 3.87557f, 3.17864f);
    public static final View STARTING_VIEW = new View(
            new Vec3(0, 6, -10), // eye
            new Vec3(0f, 0, 0f), // at
            new Vec3(0f, 1f, 0f) // up
    );
    public static final Camera CAMERA = new Camera();
    final static Engine engine = new Engine();
    public static final PerspectiveProjection PERSPECTIVE = new PerspectiveProjection(
                                 45.0f, 0.01f, 200.0f);
    public static final Viewport LEFT_VIEWPORT_LR = new Viewport(0, 0, 0.5f, 1);
    public static final Viewport RIGHT_VIEWPORT_LR = new Viewport(0.5f, 0, 0.5f, 1);
    public static final Viewport LEFT_VIEWPORT_TB = new Viewport(0, 0.5f, 1.0f, 0.5f);
    public static final Viewport RIGHT_VIEWPORT_TB = new Viewport(0, 0, 1.0f, 0.5f);
    public static final float STEREO_DIST = 1.0f;
    public static final NoClipCamControl NO_CLIP_CAM_CONTROL = new NoClipCamControl(5, 25, 0.1f);
    public static final RTSCamControl RTS_CAM_CONTROL = new RTSCamControl(0, 45, 1, Vec3.VEC3_ZERO, 1);

    public static void main(String[] args)
    {

        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                
                int option = JOptionPane.showOptionDialog(null,
                        "Choose stereo type", "3D Options",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"None","Top-Bottom","Left-Right"}, "None");
                
                engine.setGlobal(CAMERA);
                
                switch (option)
                {
                    default: case 0:
                        engine.addEntity(createCamera()); break;
                    case 1:
                        setupStereo(LEFT_VIEWPORT_TB, RIGHT_VIEWPORT_TB); break;
                    case 2:
                        setupStereo(LEFT_VIEWPORT_LR, RIGHT_VIEWPORT_LR); break;
                        
                }
                
                engine.addEntity(createLand());
                engine.addEntity(createMonkey(new State()));

                engine.setGlobal(new Sun(SUN_VEC));
                
                Window window = engine.getGlobal(Window.class);
                window.width = 1920;
                window.height = 1080;
                
                EngineInfo info = engine.getGlobal(EngineInfo.class);
                
                setupThreads();

                while (!info.shuttingDown)
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
    
    private static void setupStereo(Viewport left, Viewport right)
    {
        Entity leftVP = createStereoViewport(left);
        Entity rightVP = createStereoViewport(right);
        engine.addEntity(createStereoCamera(leftVP.getComponent(View.class),rightVP.getComponent(View.class)));
        engine.addEntity(leftVP);
        engine.addEntity(rightVP);
    }

    private static Entity createMonkey(State state)
    {
        WavefrontFactory wavefrontFactory = new WavefrontFactory();
        Entity entity = new Entity();
        entity.addComponent(state);
        entity.addComponent(new Velocity(Vec3.VEC3_ZERO,
                new Quaternion((float)FastMath.toRadians(45), 0, 1, 0)));
        entity.addComponent(new Renderable("pvlighting", new Vec3(1, 0, 1), 1.0f));
        entity.addComponent(wavefrontFactory.create(new WavefrontFactoryRequest("monkey")));
        return entity;
    }

    private static Entity createCamera()
    {
        Entity entity = new Entity();
        entity.addComponent(CAMERA);
        entity.addComponent(PERSPECTIVE);
        entity.addComponent(STARTING_VIEW);
        entity.addComponent(new Viewport());
        entity.addComponent(RTS_CAM_CONTROL);
        return entity;
    }
    
    private static Entity createStereoCamera(View left, View right)
    {
        Entity entity = new Entity();
        entity.addComponent(STARTING_VIEW);
        entity.addComponent(new Stereo(left, right, STEREO_DIST));
        entity.addComponent(CAMERA);
        entity.addComponent(RTS_CAM_CONTROL);
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
        entity.addComponent(new State(Vec3.VEC3_ZERO, Quaternion.QUAT_IDENT, new Vec3(128,128,128)));
        Heightmap hm = hmFactory.create(new HeightmapFactoryRequest("hm2",0.1f));
        entity.addComponent(hm);
        entity.addComponent(hmMeshFactory.create(new HeightmapMeshFactoryRequest(hm)));
        entity.addComponent(new Renderable("pvlighting", new Vec3(0,0.7f,0), 1.0f));
        return entity;
    }

    private static void setupThreads()
    {
        SystemThread logicThread;
        logicThread = new FixedTimingThread(1.0 / 60.0, 1.0 / 25.0);
//        logicThread = new VariableTimingThread();

        logicThread.addSystem(new MovementSystem(new EulerIntegrator()));
        logicThread.addSystem(new SnapToTerrainSystem());
        logicThread.addSystem(new BBoxSystem());

        SystemThread renderThread;
        renderThread = new FixedTimingThread(1.0 / 60, 1.0 / 25.0);
//        renderThread = new VariableTimingThread();

        renderThread.addSystem(new RenderSystem());
        renderThread.addSystem(new NoClipCamControlSystem());
        renderThread.addSystem(new SpinnyCamControlSystem());
        renderThread.addSystem(new RTSCamControlSystem());
        renderThread.addSystem(new StereoSystem());
        renderThread.addSystem(new PerspectiveProjectionSystem());
        renderThread.addSystem(new TankMaker());
        renderThread.addSystem(new ExitOnCloseSystem());

        engine.addThread(logicThread);
        engine.addThread(renderThread);
    }
}
