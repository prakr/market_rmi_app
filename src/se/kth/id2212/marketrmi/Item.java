/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id2212.marketrmi;

import java.io.Serializable;

/**
 *
 * @author prakashRajagopalan
 */
public class Item implements Serializable{
    private String itemName;
    private float itemPrice;
    
    void setName(String name){
        this.itemName = name;
    }
    
    public String getName(){
        return this.itemName;
    }
    
    void setPrice(float price){
        this.itemPrice=price;
    }
    
    public float getPrice(){
        return this.itemPrice;
    }
            
    
}
