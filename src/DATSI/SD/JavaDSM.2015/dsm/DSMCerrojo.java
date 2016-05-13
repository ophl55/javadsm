package dsm;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.Naming;
import java.util.*;
import java.util.List;
import Java.util.ArrayList;


public class DSMCerrojo {

    List<ObjetoCompartido> listofobjects = new ArrayList<ObjetosCompartido>();//saves all objects in list
    FabricaCerrojos factory;// factory of locks
    String name;// name of lock
    Cerrojo lock;//normal lock on which dsmlock is based
    String server   = System.getenv("SERVIDOR");//get server from environment variables
    String port     = System.getenv("PUERTO");// get port from environment variables

    public DSMCerrojo (String nom) throws RemoteException {

        this.name = nom;//set name
        this.almacen = (Almacen) Naming.lookup("rmi://" + servidor + ":"
                + puerto + "/DSM_almacen");// get memory from server
        this.factory = (FabricaCerrojos) Naming.lookup("rmi://" + servidor
                + ":" + puerto + "/DSM_cerrojos");//get factory from server

        this.lock = this.factory.iniciar(nom);// look up lock from given factory
    }

    /**
     * adds object o to global list of objects
     * @param o
     */
    public void asociar(ObjetoCompartido o) {
        listofobjects.add(o);//adds object to list
    }

    /**
     * removes object o from global list of objects
     * @param o
     */
    public void desasociar(ObjetoCompartido o) {
        //iterates through list of objects
        for(ObjetoCompartido obj : listofobjects)
            if (obj.getCabecera().getNombre().equals(o.getCabecera().getNombre()))
                listofobjects.remove(obj);//remove object which same name from list

    }

    /**
     *
     * @param exc
     * @return
     * @throws RemoteException
     */
    public boolean adquirir(boolean exc) throws RemoteException {
        this.lock.adquirir(exc);
        /**
         * TODO
         */
        return true;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean liberar() throws RemoteException {
        this.lock.liberar();
        /**
         * TODO
         */
        return true;
   }
}
