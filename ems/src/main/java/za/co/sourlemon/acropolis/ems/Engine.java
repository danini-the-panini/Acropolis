/*
 * Copyright (c) 2013 Triforce - in association with the University of Pretoria and Epi-Use <Advance/>
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import za.co.sourlemon.acropolis.ems.id.ID;

/**
 *
 * @author daniel
 */
public class Engine implements EntityListener
{

    private final HashMap<ID<Thread>, SystemThread> threads = new HashMap<>();
    private final Collection<SystemThread> threadsToAdd = new ArrayList<>();
    private final Collection<ID<Thread>> threadsToRemove = new ArrayList<>();
    private final HashMap<ID<Entity>, Entity> entities = new HashMap<>();
    private final Collection<Entity> toAdd = new ArrayList<>();
    private final Collection<ID<Entity>> toRemove = new ArrayList<>();
    private final Collection<EntityComponent> componentsAdded = new ArrayList<>();
    private final Collection<EntityComponent> componentsRemoved = new ArrayList<>();
    private final Map<Class, Component> globals = new HashMap<>();
    private final Map<Class, Family> families = new HashMap<>();
    private final AtomicBoolean updating = new AtomicBoolean(false);
    private boolean shuttingDown = false;

    private class EntityComponent
    {

        Entity entity;
        Class component;

        public EntityComponent(Entity entity, Class component)
        {
            this.entity = entity;
            this.component = component;
        }
    }

    public void addEntity(Entity entity)
    {
        if (updating.get())
        {
            toAdd.add(entity);
            toRemove.remove(entity.getId());
            return;
        }
        addEntityUnsafe(entity);
    }

    private void addEntityUnsafe(Entity entity)
    {
        entities.put(entity.getId(), entity);
        entity.addEntityListener(this);
        for (Family family : families.values())
        {
            family.newEntity(entity);
        }

        for (Entity dep : entity.dependents)
        {
            addEntityUnsafe(dep);
        }
    }

    public void removeEntity(Entity entity)
    {
        if (updating.get())
        {
            toRemove.add(entity.getId());
            toAdd.remove(entity);
            return;
        }
        removeEntityUnsafe(entity);
    }

    private void removeEntityUnsafe(Entity entity)
    {
        entity.removeEntityListener(this);
        
        for (Family family : families.values())
        {
            family.removeEntity(entity);
        }

        entities.remove(entity.getId());
        entity.returnToSource();

        for (Entity dep : entity.dependents)
        {
            removeEntityUnsafe(dep);
        }
    }
    
    public void removeEntity(ID<Entity> id)
    {
        removeEntity(entities.get(id));
    }

    public void setGlobal(Component global)
    {
        globals.put(global.getClass(), global);
    }

    public void removeGlobal(Class globalClass)
    {
        globals.remove(globalClass);
    }

    /**
     * Gets the global instance of the given component. Adds a new 
     * instance of the global if it does not already exist.
     * @param <T> the type of the global to retrieve
     * @param globalClass the class of the global to retrieve
     * @return the global instance of the global
     */
    public <T extends Component> T getGlobal(Class<T> globalClass)
    {
        T global = (T) globals.get(globalClass);
        if (global == null)
        {
            try
            {
                global = globalClass.getConstructor().newInstance();
                setGlobal(global);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex)
            {
                ex.printStackTrace(System.err);
            }
        }
        return global;
    }

    @Override
    public void componentAdded(Entity entity, Class componentClass)
    {
        if (updating.get())
        {
            componentsAdded.add(new EntityComponent(entity, componentClass));
            return;
        }
        componentAddedUnsafe(entity, componentClass);
    }

    private void componentAddedUnsafe(Entity entity, Class componentClass)
    {
        for (Family family : families.values())
        {
            family.componentAddedToEntity(entity, componentClass);
        }
    }

    @Override
    public void componentRemoved(Entity entity, Class componentClass)
    {
        if (updating.get())
        {
            componentsRemoved.add(new EntityComponent(entity, componentClass));
            return;
        }
        componentRemovedUnsafe(entity, componentClass);
    }

    private void componentRemovedUnsafe(Entity entity, Class componentClass)
    {
        for (Family family : families.values())
        {
            family.componentRemovedFromEntity(entity, componentClass);
        }
    }

    public <N extends Node> List<N> getNodeList(Class<N> nodeClass)
    {
        Family family = families.get(nodeClass);
        if (family == null)
        {
            family = new Family(nodeClass);
            for (Entity entity : entities.values())
            {
                family.newEntity(entity);
            }
            families.put(nodeClass, family);
        }
        return family.getNodeList();
    }
    

    public final void addThread(SystemThread thread)
    {
        if (updating.get())
        {
            threadsToAdd.add(thread);
            return;
        }
        addThreadUnsafe(thread);
    }

    private void addThreadUnsafe(SystemThread thread)
    {
        if (thread.initFromEngine(this))
        {
            threads.put(thread.getId(), thread);
        }
    }
    
    public final void removeThread(SystemThread thread)
    {
        if (updating.get())
        {
            threadsToRemove.add(thread.getId());
            return;
        }
        removeThreadUnsafe(thread);        
    }

    private void removeThreadUnsafe(SystemThread thread)
    {
        if (threads.remove(thread.getId()) != null)
        {
            thread.shutDown();
        }
    }
    
    public SystemThread getThread(ID<Thread> id)
    {
        return threads.get(id);
    }
    
    public Entity getEntity(ID<Entity> id)
    {
        return entities.get(id);
    }
    
    public void update()
    {
        updating.set(true);
        for (SystemThread s : threads.values())
        {
            s.update(this);
        }
        updating.set(false);

        postUpdate();
    }

    private void postUpdate()
    {
        for (ID<Entity> e : toRemove)
        {
            removeEntityUnsafe(entities.get(e));
        }
        toRemove.clear();
        for (Entity e : toAdd)
        {
            addEntityUnsafe(e);
        }
        toAdd.clear();
        
        for (ID<Thread> e : threadsToRemove)
        {
            removeThreadUnsafe(threads.get(e));
        }
        threadsToRemove.clear();
        for (SystemThread e : threadsToAdd)
        {
            addThreadUnsafe(e);
        }
        threadsToAdd.clear();

        for (EntityComponent ec : componentsAdded)
        {
            componentAddedUnsafe(ec.entity, ec.component);
        }
        componentsAdded.clear();

        for (EntityComponent ec : componentsRemoved)
        {
            componentRemovedUnsafe(ec.entity, ec.component);
        }
        componentsRemoved.clear();
        
        if (shuttingDown)
        {
            shutDown();
        }
    }

    public void shutDown()
    {
        if (updating.get())
        {
            shuttingDown = true;
        }
        
        for (SystemThread s : threads.values())
        {
            s.shutDown();
        }

        threads.clear();
    }
}
