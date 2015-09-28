package objenome.solution.dependency;

import objenome.Prototainer;
import objenome.solution.dependency.ClassBuilder.DependencyKey;

import java.util.Collection;

public class SingletonBuilder implements Builder {

    public Object instance;

    public Class<?> type;

    public SingletonBuilder() {

    }

    public SingletonBuilder(final Object instance) {

        this.instance = instance;

        this.type = instance.getClass();
    }

    @Override
    public <T> T instance(Prototainer context, Collection<DependencyKey> simulateAndAddExtraProblemsHere) {
 
        if (simulateAndAddExtraProblemsHere==null) {
            return (T) instance;
        }
        
        return null;
    }

    @Override
    public Class<?> type() {

        return type;
    }
}
