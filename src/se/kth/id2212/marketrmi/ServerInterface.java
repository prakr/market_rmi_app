package se.kth.id2212.marketrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    //have to enter create table id , password, num item sold,purchased as 0
    //unique id and pass of min 8 char
    // on remote object as the user to provide another name and passwrd
    void registerClient(ClientInterface obj) throws RemoteException;
    //remove the item from database
    void unregisterClient(ClientInterface obj) throws RemoteException;
    // have an another table table id, item, price .add an item 
    void sell(Item item ,ClientInterface obj)throws RemoteException;
    //remove item from item table 
    void buy(Item item ,ClientInterface obj)throws RemoteException;//some other expection
    // retain the same arraylist
    void wishlist(Item item ,ClientInterface obj) throws RemoteException;
    //
    List<Item> getItems() throws RemoteException;
    // user has to verify passwrd with the server
    void login(ClientInterface obj)throws RemoteException;
    void logout(ClientInterface obj)throws RemoteException;
    void getDetails(ClientInterface obj)throws RemoteException;
}
