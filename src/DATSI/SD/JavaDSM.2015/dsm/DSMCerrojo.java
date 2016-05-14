package dsm;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.Naming;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.MalformedURLException;


public class DSMCerrojo {

    List<ObjetoCompartido> listofobjects = new ArrayList<ObjetoCompartido>();//saves all objects in list
    FabricaCerrojos factory;// factory of locks
    String name;// name of lock
    Almacen memory; //memory system which is used
    Cerrojo lock;//normal lock on which dsmlock is based
    String server   = System.getenv("SERVIDOR");//get server from environment variables
    String port     = System.getenv("PUERTO");// get port from environment variables
    boolean exclusive; // indicates whether the access is exclusive (write) or not (read)

    public DSMCerrojo (String nom) throws RemoteException,
    					MalformedURLException, NotBoundException {

        this.name = nom;//set name
        this.memory = (Almacen) Naming.lookup("rmi://" + server + ":"
                + port + "/DSM_almacen");// get memory from server
        this.factory = (FabricaCerrojos) Naming.lookup("rmi://" + server
                + ":" + port + "/DSM_cerrojos");//get factory from server

        this.lock = this.factory.iniciar(nom);// look up lock from given factory
    }

    /**
     * adds object o to global list of objects
     * @param o object to be added
     */
    public void asociar(ObjetoCompartido o) {
        listofobjects.add(o);//adds object to list
    }

    /**
     * removes object o from global list of objects
     * @param o object to be removed
     */
    public void desasociar(ObjetoCompartido o) {
        //iterates through list of objects
        for(ObjetoCompartido obj : listofobjects)
            if (obj.getCabecera().getNombre().equals(o.getCabecera().getNombre()))
                listofobjects.remove(obj);//remove object which same name from list

    }

    /**
     *adquieres the lock, reads the remote memory and brings all objects up to date
     * @param exc - indicates whether the access is exclusive(write) or not (read)
     * @return true
     * @throws RemoteException
     */
    public boolean adquirir(boolean exc) throws RemoteException {
        this.lock.adquirir(exc);//use function from Cerrojo
        this.exclusive = exc;//set global variable

        /*get header objects*/
        List<CabeceraObjetoCompartido> listofheaders = new ArrayList<CabeceraObjetoCompartido>();
        for (ObjetoCompartido o : listofobjects)
            listofheaders.add(o.getCabecera());

        //check whether list is empty
        if(listofheaders.size() > 0 ){
            List <ObjetoCompartido> newVersionObjects = memory.leerObjetos(listofheaders);//get latest versions of objects

            //check whether list is empty
            if(newVersionObjects != null)

                for(ObjetoCompartido newObject : newVersionObjects){
                    //get object through name
                    ObjetoCompartido object = getObjectfromName(newObject.getCabecera().getNombre());

                    /*update object on new version*/
                    if(object != null){
                        object.setObjeto(newObject.getObjeto());
                        object.setVersion(newObject.getCabecera().getVersion());
                    }
                }

        }

        return true;
    }

    /**
     * saves the latest versions of the objects
     * on the remote memory if the access was exclusive
     * and frees the lock with the free function from Cerrojo
     * @return value of function liberar() of Cerrojo
     * @throws RemoteException
     */
    public boolean liberar() throws RemoteException {
        if(exclusive){
            List <ObjetoCompartido> objects = new ArrayList<ObjetoCompartido>();

            //increment the version of the objects
            for (ObjetoCompartido obj : listofobjects){
                obj.incVersion();
                objects.add(obj);
            }
            //save the new versions in memory
            memory.escribirObjetos(objects);
        }

        return this.lock.liberar();//frees object using function from Cerrojo
   }

    /**
     * returns object from list of objects with name: objectname
     * @param objectname - name of the object
     * @return ObjetoCompartido with the objectname as name
     */
    private ObjetoCompartido getObjectfromName(String objectname){
        for (int i = 0 ; i < listofobjects.size();i++)
            if (objectname.equals(listofobjects.get(i).getCabecera().getNombre()))
                return listofobjects.get(i);
        return null;
    }
}

