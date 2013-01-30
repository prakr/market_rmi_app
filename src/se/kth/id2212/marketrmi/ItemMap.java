/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id2212.marketrmi;

/**
 *
 * @author prakashRajagopalan
 */
public class ItemMap {
    
    Item item;
    ClientInterface Trader;
    
   public ItemMap(Item aItem ,ClientInterface aTrader ){
       this.item = aItem;
       this.Trader = aTrader;
   }
   
   Item getItem(){
       return item;
   }
   
   ClientInterface getClientInterface(){
       return Trader;
   }
   
   
}




