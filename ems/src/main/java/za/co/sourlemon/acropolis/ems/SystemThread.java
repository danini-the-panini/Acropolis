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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import za.co.sourlemon.acropolis.ems.id.Identifiable;
import za.co.sourlemon.acropolis.ems.id.SystemID;
import za.co.sourlemon.acropolis.ems.id.ThreadID;

/**
 *
 * @author Daniel
 */
public abstract class SystemThread implements Identifiable<ThreadID>
{
    protected final Map<SystemID, ISystem> systems = new HashMap<>();
    private final Collection<ISystem> toAdd = new ArrayList<>();
    private final Collection<SystemID> toRemove = new ArrayList<>();
    protected final AtomicBoolean updating = new AtomicBoolean(false);
    private boolean shuttingDown = false;
    private final ThreadID id = new ThreadID();

    public final void addSystem(ISystem system)
    {
        if (updating.get())
        {
            toAdd.add(system);
            return;
        }
        addSystemUnsafe(system);
    }

    @Override
    public final ThreadID getId()
    {
        return id;
    }

    private void addSystemUnsafe(ISystem system)
    {
        if (system.init())
        {
            systems.put(system.getId(), system);
        }
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

        for (SystemID s : toRemove)
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
