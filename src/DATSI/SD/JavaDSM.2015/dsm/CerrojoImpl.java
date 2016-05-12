package dsm;
import java.rmi.*;
import java.rmi.server.*;

class CerrojoImpl extends UnicastRemoteObject implements Cerrojo {

    private static final long serialVersionUID = 1L;//for synchronized methods
    boolean writer;
    int readers;
    CerrojoImpl() throws RemoteException {
        this.writer     = false;
        this.readers    = 0;
    }

    /**
     * function to adquire the associated object to the lock
     * there can be more than one reader but just one writer
     * @param exc says if access is (exclusive) write access
     * @throws RemoteException
     */
    public synchronized void adquirir (boolean exc) throws RemoteException {

        while (true) {
            try {
                //write access
                if (exc) {
                    //if there are no writer and readers
                    if (!writer && readers == 0) {
                        writer = true;//set writer
                        return;
                    }
                    //else next writer has to wait
                    else
                        wait();

                }

                //read access with current writer writing
                else if (writer)
                    wait();//reader has to wait

                    //read access with just readers accessing on object
                else {
                    readers++;//increment readers
                    return;
                }

            }
            //exception for thread interrupts on waiting thread
            catch(InterruptedException e){
                e.printStackTrace();//print status
            }

        }
    }

    /**
     * frees the set lock
     * @return true if the free was successfull, false otherwise
     * @throws RemoteException
     */
    public synchronized boolean liberar() throws RemoteException {

        //if there are no current writer or reader there must had been an error
        if (!writer && readers == 0)
            return false;
        //if there are reader
        if (readers > 0) {
            readers--;//free a reader
            //if there are no reader anymore
            if (readers == 0) {
                notify();//notify the waiting writer process
                return true;
            }
        }
        //else there must be a writer
        else {
            writer = false;//free the writer
            notify();//notify the next writer or reader
            return true;
        }
        return true;//programm never comes to this point
    }