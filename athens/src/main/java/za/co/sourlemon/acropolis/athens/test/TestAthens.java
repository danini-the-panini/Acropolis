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
import za.co.sourlemon.acropolis.athens.components.Renderable;
import za.co.sourlemon.acropolis.athens.systems.RenderSystem;
import za.co.sourlemon.acropolis.ems.Engine;
import za.co.sourlemon.acropolis.ems.Entity;
import za.co.sourlemon.acropolis.ems.FixedTimingThread;
import za.co.sourlemon.acropolis.ems.SystemThread;
import za.co.sourlemon.acropolis.tokyo.components.State;
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

                renderThread.addSystem(new RenderSystem(800, 800));

                engine.addThread(logicThread);
                engine.addThread(renderThread);

                Entity entity = new Entity();
                entity.addComponent(new State());
                entity.addComponent(new Renderable("monkey", "monkey", new Vec3(1, 0, 1), 1.0f));
                engine.addEntity(entity);

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
