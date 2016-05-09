package dsm;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * Guarda objetos existentes (colección ObjetoCompartido)
 * Ofrece métodos para leer/escribir múltiples objetos
 */
public class AlmacenImpl extends UnicastRemoteObject implements dsm.Almacen {
    private Map<String, ObjetoCompartido> storage;


    public AlmacenImpl() throws RemoteException {
        storage = new HashMap<String, ObjetoCompartido>();
    }


    /**
     * leerObjetos:
     *
     * Por cada objeto
     *      Si versión cliente < actual: incluye ObjetoCompartido en lista retornada
     *      Si versión cliente >= actual || objeto no existe en gestor: nada
     *
     * @param lcab lista de cabeceras de objetos en cliente
     * @return lista de objetos obsoletos en el cliente
     * @throws RemoteException
     */
    public synchronized	List<ObjetoCompartido> leerObjetos(List<CabeceraObjetoCompartido> lcab)
      throws RemoteException {
        List<ObjetoCompartido> loc = new LinkedList<ObjetoCompartido>();
        Iterator<CabeceraObjetoCompartido> iterator = lcab.iterator();

        if (storage.size() == 0)
            return null;

        while (iterator.hasNext()) {
            CabeceraObjetoCompartido cab = iterator.next();
            System.out.println("+++ Read cab +++\n " +
                    "Name: " + cab.getNombre() + "\n" +
                    "Version: " + cab.getVersion() + "\n"
            );
            if (storage.containsKey(cab.getNombre())) {
                ObjetoCompartido oc = storage.get(cab.getNombre());
                if(cab.getVersion() < oc.getCabecera().getVersion())
                    loc.add(oc);
            }
        }

        if (loc.size() > 0)
            return loc;
        else
            return null;
    }


    /**
     * escribirObjetos:
     *
     * Los incluye directamente en el almacén
     * Descarga dinámica de clases si objeto de clase de usuario
     * Sin operaciones de destrucción explícitas o implícitas
     *
     * @param loc recibe lista de objetos compartidos del cliente
     * @throws RemoteException
     */
    public synchronized void escribirObjetos(List<ObjetoCompartido> loc)
     throws RemoteException  {
        Iterator<ObjetoCompartido> iterator = loc.iterator();
        while (iterator.hasNext()) {
            ObjetoCompartido oc = iterator.next();
            System.out.println("+++ Store oc +++ \n" +
                    "Name: " + oc.getCabecera().getNombre() + "\n" +
                    "Version: " + oc.getCabecera().getVersion() +"\n");
            storage.put(oc.getCabecera().getNombre(), oc);
        }
    }
}

