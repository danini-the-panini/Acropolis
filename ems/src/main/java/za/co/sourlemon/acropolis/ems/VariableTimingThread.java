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

import static za.co.sourlemon.acropolis.ems.FixedTimingThread.NANOS_PER_SECOND_DOUBLE;

/**
 *
 * @author Daniel
 */
public class VariableTimingThread extends SystemThread
{
    private double delta, time;
    private long currentTimeNanos;

    @Override
    public boolean init()
    {
        currentTimeNanos = System.nanoTime();
        delta = 0;
        time = 0;
        return true;
    }

    @Override
    public void update(Engine engine)
    {
        long newTimeNanos = System.nanoTime();
        delta = (double)(newTimeNanos - currentTimeNanos) / NANOS_PER_SECOND_DOUBLE;
        currentTimeNanos = newTimeNanos;
        time += delta;
        updating.set(true);
        for (ISystem s : systems.values())
        {
            s.update(engine, time, delta);
        }
        updating.set(false);
    }

    @Override
    public double getAlpha()
    {
        return 0;
    }

    @Override
    public double getTicksPerSecond()
    {
        return 1.0/delta;
    }
    
}
