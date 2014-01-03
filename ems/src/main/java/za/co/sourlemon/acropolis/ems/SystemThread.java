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
package za.co.sourlemon.acropolis.ems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import za.co.sourlemon.acropolis.ems.id.Identifiable;
import za.co.sourlemon.acropolis.ems.id.ID;

/**
 *
 * @author Daniel
 */
public abstract class SystemThread implements Identifiable<ID<Thread>>
{

    protected final Map<ID<System>, ISystem> systems = new LinkedHashMap<>();
    private final Collection<ISystem> toAdd = new ArrayList<>();
    private final Collection<ID<System>> toRemove = new ArrayList<>();
    protected final AtomicBoolean updating = new AtomicBoolean(false);
    private boolean shuttingDown = false;
    private final ID<Thread> id = new ID<Thread>();

    @Override
    public final ID<Thread> getId()
    {
        return id;
    }

    public final void addSystem(ISystem system)
    {
        if (updating.get())
        {
            toAdd.add(system);
            return;
        }
        addSystemUnsafe(system);
    }

    private void addSystemUnsafe(ISystem system)
    {
        systems.put(system.getId(), system);
    }

    public final void removeSystem(ISystem system)
    {
        if (updating.get())
        {
            toRemove.add(system.getId());
            return;
        }
        removeSystemUnsafe(system);
    }

    private void removeSystemUnsafe(ISystem system)
    {
        if (systems.remove(system.getId()) != null)
        {
            system.destroy();
        }
    }

    public final void updateFromEngine(Engine engine)
    {
        update(engine);
        postUpdate();
    }

    public final boolean initFromEngine(Engine engine)
    {
        for (ISystem system : systems.values())
        {
            if (!system.init(engine))
            {
                return false;
            }
        }

        return init();
    }

    public abstract boolean init();

    public abstract void update(Engine engine);

    public abstract double getAlpha();

    public abstract double getTicksPerSecond();

    private void postUpdate()
    {
        for (ISystem s : toAdd)
        {
            addSystemUnsafe(s);
        }
        toAdd.clear();

        for (ID<System> s : toRemove)
        {
            removeSystemUnsafe(systems.get(s));
        }
        toRemove.clear();

        if (shuttingDown)
        {
            shutDown();
        }
    }

    public final void shutDown()
    {
        if (updating.get())
        {
            shuttingDown = true;
        }

        for (ISystem s : systems.values())
        {
            s.destroy();
        }

        systems.clear();
    }
}
