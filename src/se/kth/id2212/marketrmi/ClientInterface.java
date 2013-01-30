package se.kth.id2212.marketrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
   // void receiveMsg (String msg) throws RemoteException;
    
    void itemAvailable(Item item)throws RemoteException;
    
    void getItem(Item item) throws RemoteException;
    
    String getID() throws RemoteException;
    
    void receiveMsg (String msg) throws RemoteException;
    
    String getPassword() throws RemoteException;
    
    void itemInfo(int sold ,int bought) throws RemoteException;
    
    void setLogin(boolean set) throws RemoteException;
    
    boolean getLogin() throws RemoteException;
}
