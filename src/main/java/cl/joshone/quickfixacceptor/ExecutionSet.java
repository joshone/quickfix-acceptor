package cl.joshone.quickfixacceptor;

import java.util.ArrayList;
import java.util.Iterator;

public class ExecutionSet {

    private ArrayList<Execution> executions = new ArrayList<Execution>();

    public ExecutionSet() {}

    public void add ( Execution execution ) {
        executions.add( execution );
        int limit = 50;
        /*try {
            limit = (int)FIXimulator.getApplication().getSettings()
                    .getLong("FIXimulatorCachedObjects");
        } catch ( Exception e ) {}*/
        while ( executions.size() > limit ) {
            executions.remove(0);
        }
    }

    public int getCount() {
        return executions.size();
    }

    public Execution getExecution( int i ) {
        return executions.get( i );
    }

    public Execution getExecution( String id ) {
        Iterator<Execution> iterator = executions.iterator();
        while ( iterator.hasNext() ){
            Execution execution = iterator.next();
            if ( execution.getID().equals(id) )
                return execution;
        }
        return null;
    }
    
}