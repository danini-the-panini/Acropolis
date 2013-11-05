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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author daniel
 */
public class Engine implements EntityListener
{

    private final HashMap<UUID, ISystem> systems = new HashMap<>();
    private final HashMap<UUID, Entity> entities = new HashMap<>();
    private final Collection<Entity> toAdd = new ArrayList<>();
    private final Collection<UUID> toRemove = new ArrayList<>();
    private final Collection<ISystem> systemsToAdd = new ArrayList<>();
    private final Collection<ISystem> systemsToRemove = new ArrayList<>();
    private final Collection<EntityComponent> componentsAdded = new ArrayList<>();
    private final Collection<EntityComponent> componentsRemoved = new ArrayList<>();
    private final Map<Class, Object> globals = new HashMap<>();
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
    
    public void removeEntity(UUID id)
    {
        removeEntity(entities.get(id));
    }

    public void addGlobal(Object global)
    {
        globals.put(global.getClass(), global);
    }

    public void removeGlobal(Class globalClass)
    {
        globals.remove(globalClass);
    }

    public <T> T getGlobal(Class<T> globalClass)
    {
        T global = (T) globals.get(globalClass);
        if (global == null)
        {
            try
            {
                global = globalClass.newInstance();
                addGlobal(global);
            } catch (InstantiationException | IllegalAccessException ex)
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

    public void addSystem(ISystem system)
    {
        if (updating.get())
        {
            systemsToAdd.add(system);
            return;
        }
        addSystemUnsafe(system);
    }

    private void addSystemUnsafe(ISystem system)
    {
        if (system.init(this))
        {
            systems.put(system.getId(), system);
        }
    }
    
    public void removeSystem(ISystem system)
    {
        if (updating.get())
        {
            systemsToRemove.add(system);
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

    public boolean containsSystem(ISystem system)
    {
        return systems.containsValue(system);
    }

    public void update(float t, float dt)
    {
        updating.set(true);
        for (ISystem s : systems.values())
        {
            s.update(t, dt);
        }
        updating.set(false);

        postUpdate();
    }

    private void postUpdate()
    {
        for (UUID e : toRemove)
        {
            removeEntityUnsafe(entities.get(e));
        }
        toRemove.clear();
        for (Entity e : toAdd)
        {
            addEntityUnsafe(e);
        }
        toAdd.clear();

        for (ISystem s : systemsToAdd)
        {
            addSystemUnsafe(s);
        }
        systemsToAdd.clear();

        for (ISystem s : systemsToRemove)
        {
            removeSystemUnsafe(s);
        }
        systemsToRemove.clear();

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
        
        for (ISystem s : systems.values())
        {
            s.destroy();
        }

        systems.clear();
    }
}
