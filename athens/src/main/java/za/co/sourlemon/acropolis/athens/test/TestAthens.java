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
import za.co.sourlemon.acropolis.athens.components.PerspectiveCamera;
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.components.Sun;
import za.co.sourlemon.acropolis.athens.components.View;
import za.co.sourlemon.acropolis.athens.systems.PerspectiveCameraSystem;
import za.co.sourlemon.acropolis.athens.systems.RenderSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.FixedTimingThread;
import za.co.sourlemon.acropolis.ems.SystemThread;
import za.co.sourlemon.acropolis.tokyo.components.State;
import za.co.sourlemon.acropolis.tokyo.components.Velocity;
import za.co.sourlemon.acropolis.tokyo.systems.MovementSystem;

/**
 *
 * @author Daniel
 */
public class TestAthens
{

    public static void main(String[] args)
    {
        final Engine engine = new Engine();

        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                SystemThread logicThread = new FixedTimingThread(1.0 / 60.0, 1.0 / 25.0);

                logicThread.addSystem(new MovementSystem());

                SystemThread renderThread = new FixedTimingThread(1.0 / 60, 1.0 / 25.0);

                renderThread.addSystem(new PerspectiveCameraSystem());
                renderThread.addSystem(new RenderSystem(800, 800));

                engine.addThread(logicThread);
                engine.addThread(renderThread);

                Entity entity = new Entity();
                entity.addComponent(new State());
                entity.addComponent(new Velocity(Vec3.VEC3_ZERO, Vec3.VEC3_ZERO, new Vec4(0,45,0,0), Vec4.VEC4_ZERO));
                entity.addComponent(new Renderable("monkey", "monkey", new Vec3(1, 0, 1), 1.0f));
                engine.addEntity(entity);
                
                Entity cameraEntity = new Entity();
                PerspectiveCamera camera = new PerspectiveCamera(
                        new Vec3(3, 3, 2), // eye
                        new Vec3(0f, 0, 0f), // at
                        new Vec3(0f, 1f, 0f), // up
                        45.0f, 0.1f, 100.0f);
                cameraEntity.addComponent(camera);
                View view = new View();
                cameraEntity.addComponent(view);
                engine.addEntity(cameraEntity);
                engine.addGlobal(view);
                engine.addGlobal(new Sun(new Vec3(-2.47511f, 3.87557f, 3.17864f)));

                while (true)
                {
                    engine.update();
                }
            }

        });
        
        t.start();
        try
        {
            t.join();
        } catch (InterruptedException ex)
        {}
        
        engine.shutDown();
    }
}
