package EntityComponentSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A singleton class, representing a registry for storing and managing all 
 * {@code Component} instances associated with {@code entity}'s.
 * 
 * <P>The {@code Component} instances are stored in a {@code HashMap}, with the
 * {@code Component} class type as key. Each {@code Component} class type has a
 * {@code HashMap} mapped to it, with the actual instance of the {@code Component}
 * mapped to an{@code entity}.
 * 
 * <P>This way, the {@code Component}'s can be retrieved and processed very quickly,
 * by retrieving the {@code Component}'s in bulk from memory, as the {@code Component}'s
 * are stored contingently.
 * 
 * @author Marcel Lenterman
 */
public final class ComponentRegistry{
    private static ComponentRegistry instance = null;
    private HashMap<Class<?>, HashMap<Integer, ? extends Component>> componentStores;
    
    private ComponentRegistry(){
        componentStores = new HashMap();
    }
    
    /**
     * Returns the instance of the singleton {@code ComponentRegistry} class.
     * 
     * @return The {@code ComponentRegistry} instance.
     */
    public static ComponentRegistry getInstance(){
        if(instance == null)
            instance = new ComponentRegistry();
        return instance;
    }
    
    /**
     * Get the instance of the {@code Component} associated with the {@code entity}.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param entity The id of the {@code entity}.
     * @param componentType The {@code Component} class.
     * @return The instance of the {@code Component} associated with the {@code entity}. 
     * @throws  IllegalArgumentException When the {@code entity} does not posses
     *                                   the provided {@code Component}.
     */
    public <T extends Component> T getComponent(int entity, Class<T> componentType){      
        HashMap<Integer, ? extends Component> store = componentStores.get(componentType);
        
        T result = (T)store.get(entity);
        if(result == null)
            throw new IllegalArgumentException("Entry with entity id: " + entity + " does not posses the component: " + componentType.getSimpleName());
        
        return result;
    }
    
    /**
     * Returns a {@code ArrayList} with all the registered {@code Component}
     * instances of the provided {@code Component}.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param componentType The {@code Component} class.
     * @return A {@code List} with all the registered instances of the provided
     *         {@code Component} class.
     */
    public <T extends Component> List<T> getAllComponentsOfType(Class<T> componentType){
	HashMap<Integer, ? extends Component> store = componentStores.get(componentType);
		
	if(store == null)
            return new ArrayList();
        
        return new ArrayList((Collection<T>)store.values());
    }
    
    /**
     * Returns an {@code ArrayList} with all the registered {@code entity}'s, 
     * associated with the provided {@code Component}. Returns an empty {@code ArrayList}
     * when no {@code entity}'s are associated with the {@code Component}.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param componentType The {@code Component} class.
     * @return An {@code ArrayList} with all {@code entity}'s.
     */
    public <T extends Component> List<Integer> getAllEntitiesPossessingComponent(Class<T> componentType){
	HashMap<Integer, ? extends Component> store = componentStores.get(componentType);
		
	if(store == null)
            return new ArrayList();

	return new ArrayList(store.keySet());
    }
    
    /**
     * Returns a {@code HashMap} with all the instances of the {@code Component}'s
     * associated with the provided {@code entity}. Returns an empty {@code HashMap}
     * when no {@code Component}'s are associated with the {@code entity}.
     * 
     * <P>The key of the {@code HashMap} is the class of the {@code Component}.
     * The value is the instance of the {@code Component}.
     * 
     * @param entity The id of the {@code entity}.
     * @return A {@code HashMap} with all {@code Component} instances, mapped to
     *         the {@code Component} class.
     */
    public Map<Class<? extends Component>, Component> getAllComponentsOnEntity(int entity){
        HashMap<Class<? extends Component>, Component> result = new HashMap();  
        
        for(Entry entry : componentStores.entrySet())
            if(((HashMap<Integer, ? extends Component>)entry.getValue()).containsKey(entity))
                result.put((Class)entry.getKey(), ((HashMap<Integer, ? extends Component>)entry.getValue()).get(entity));
        
        return result;
    }
    
    /**
     * Returns wether the {@code entity} is associated with the provided
     * {@code Component} class.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param entity The id of the {@code entity}.
     * @param componentType The {@code Component} class.
     * @return {@code true} when the {@code entity} is associated with the
     *         {@code Component}. {@code false} otherwise.
     */
    public <T extends Component> boolean hasComponent(int entity, Class<T> componentType){
        HashMap<Integer, ? extends Component> store = componentStores.get(componentType);
        
        if(store == null)
            return false;
        
        return store.containsKey(entity);
    }
    
    /**
     * Add's  a {@code Component} instance to the provided {@code entity}. 
     * Overwrites the current {@code Component}, if one is already associated
     * with the {@code entity}.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param entity The id of the {@code entity}.
     * @param component An instance of {@code Component}.
     */
    public synchronized <T extends Component> void addComponent(int entity, T component){       
        HashMap<Integer, ? extends Component> store = componentStores.get(component.getClass());
        
        if(store == null){
            store = new HashMap();
            componentStores.put(component.getClass(), store);
        }
        
        ((HashMap<Integer, T>)store).put(entity, component);    
    }
    
    /**
     * Removes the {@code Component} instance associated with the provided
     * {@code entity}, if one is present.
     * 
     * @param entity The id of the {@code entity}.
     * @param component An instance of {@code Component}.
     */
    public synchronized void removeComponent(int entity, Component component){
        HashMap<Integer, ? extends Component> store = componentStores.get(component.getClass());
        
        if(store != null)
            store.remove(entity);
    }
    
    /**
     * Removes the {@code Component} instance associated with the provided
     * {@code entity}, if one is present.
     * 
     * @param <T> {@code Class} that implements the {@code Component} interface.
     * @param entity The id of the {@code entity}.
     * @param componentType The {@code Component} class.
     */
    public synchronized <T extends Component> void removeComponent(int entity, Class<T> componentType){
        HashMap<Integer, ? extends Component> store = componentStores.get(componentType);
        
        if(store != null)
            store.remove(entity);
    }
    
    /**
     * Removes all {@code Component}'s associated with the provided {@code entity}.
     * 
     * @param entity The id of the {@code entity}.
     */
    public synchronized void removeAllComponentsOnEntity(int entity){        
        for(Entry entry : componentStores.entrySet())
            ((HashMap<Integer, ? extends Component>)entry.getValue()).remove(entity);
    }

    @Override
    public String toString(){
        return "ComponentRegistry{" + "componentStores=" + componentStores + '}';
    }
}
