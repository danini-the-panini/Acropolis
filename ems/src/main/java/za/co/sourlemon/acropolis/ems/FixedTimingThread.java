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
package za.co.sourlemon.acropolis.ems;

/**
 *
 * @author Daniel
 */
public class FixedTimingThread extends SystemThread
{

    public static final long NANOS_PER_SECOND_LONG = 1000000000l;
    public static final double NANOS_PER_SECOND_DOUBLE = 1E9;

    private double delta, currentTime, accumulator, maxFrameTime;
    private double time = 0.0f;

    public FixedTimingThread(double delta, double maxFrameTime)
    {
        this.delta = delta;
        this.maxFrameTime = maxFrameTime;
    }

    public double getDelta()
    {
        return delta;
    }

    public void setDelta(double delta)
    {
        this.delta = delta;
    }

    @Override
    public boolean init()
    {
        currentTime = System.nanoTime();
        accumulator = 0;
        return true;
    }

    @Override
    public void update(Engine engine)
    {
        double newTime = System.nanoTime();
        double frameTime = (newTime - currentTime) / NANOS_PER_SECOND_DOUBLE;
        if (frameTime > maxFrameTime)
        {
            frameTime = maxFrameTime;
        }
        currentTime = newTime;

        accumulator += frameTime;

        //any function called in this block should run at the fixed "delta" rate
        while (accumulator >= delta)
        {
            updating.set(true);
            for (ISystem s : systems.values())
            {
                s.update(engine, time, delta);
            }
            updating.set(false);
            accumulator -= delta;
            time += delta;
        }
    }

    @Override
    public double getAlpha()
    {
        return((System.nanoTime()-currentTime)/delta)%1.0;
    }

    @Override
    public double getTicksPerSecond()
    {
        return 1.0/delta;
    }
}
