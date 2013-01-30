/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id2212.marketrmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author prakashRajagopalan
 */
@SuppressWarnings("serial")
public class ClientInterfaceImpl extends UnicastRemoteObject implements ClientInterface{
    
    private String id;
    private String password;
    private boolean login ;
    
    public ClientInterfaceImpl(String id,String password) throws RemoteException {
		super();
		this.id = id;
                this.password = password;
                this.login = false ;
	}

	public String getID() {
		return id;
	}
        
        public void getItem(Item item) {
           System.out.println("The item is sold out and money is credited"); 
           System.out.println(item.getName());
           System.out.println(item.getPrice());
	}

	public void itemAvailable(Item item) {
           System.out.println("wishlist item available to buy"); 
           System.out.println(item.getName());
           System.out.println(item.getPrice());
	}
        
        public void receiveMsg (String msg) {
            System.out.println(msg);
        }
        
        public  String getPassword() {
           return password;
        }   
        
       public void itemInfo(int sold ,int bought){
           System.out.println("items sold "+ sold);
           System.out.println("items bought "+ bought);
       } 
       public void setLogin(boolean set) {
           this.login = set;
        } 
       
       public boolean getLogin() {
           return login;
        }
       
        
}
