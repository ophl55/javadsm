package dsm;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class FabricaCerrojosImpl extends UnicastRemoteObject implements FabricaCerrojos {

    /*Hashmap, which maps locks to their names (given as Strings) as keys*/
    Map<String, Cerrojo> locks = new HashMap<String, Cerrojo> ();

    public FabricaCerrojosImpl() throws RemoteException {
    }

    /**
     * returns a lock with name s, if it already exists, it will be returned,
     * otherwise the function creates a new lock and returns it
     * @param s=name of the lock given as String
     * @return a lock (class Cerrojo) which has the name s
     * @throws RemoteException
     */
    public synchronized	Cerrojo iniciar(String s) throws RemoteException {

        //if the lock already exists, return it
	    if(locks.containsKey(s))
            return locks.get(s);

        Cerrojo newlock = new CerrojoImpl();//create new lock
        locks.put(s,newlock);//save lock in map

        return newlock;
    }
}

