/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id2212.marketrmi;

/**
 *
 * @author prakashRajagopalan
 */
public class ObjMap {
    
    private String clientID;
    ClientInterface Trader;
    
   public ObjMap(ClientInterface aTrader,String ClientID ){
       this.clientID = ClientID;
       this.Trader = aTrader;
   }
   
   String getClientID(){
       return clientID;
   }
   
  ClientInterface getClientInterface(){
       return Trader;
   }
   
   
}
