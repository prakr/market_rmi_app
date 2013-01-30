package se.kth.id2212.marketrmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MyServer extends UnicastRemoteObject implements ServerInterface {
    
        public static final String Client_Table = "ClientDetails";
        public static final String Item_Table = "ItemDetails";
        
        private PreparedStatement createAccountStatement;
        private PreparedStatement findAccountStatement;
        private PreparedStatement deleteAccountStatement;
        private PreparedStatement updateStatement;
	private List<ClientInterface> clientTable = new ArrayList<ClientInterface>();
        
        private List<ObjMap> objMapList = new ArrayList<ObjMap>();
        private Map<String, ClientInterface> objectMapList = new HashMap<>();
        
        private List<ItemMap> wishList = new ArrayList<ItemMap>();
        
        private List<Item> itemList = null;
        
        
        
        Bank marketBankobj;  
        
        Connection connection;
        
	public MyServer() throws RemoteException, MalformedURLException, NotBoundException, ClassNotFoundException, SQLException {
		super();
                Registry registry = LocateRegistry.getRegistry();
		registry.rebind("marketplace", this);
                marketBankobj = (Bank)Naming.lookup("bank");
                System.out.println("before getconnection1");
                Class.forName("org.apache.derby.jdbc.ClientXADataSource");
                connection = DriverManager.getConnection("jdbc:derby://localhost:1527/" + "Banks" + ";create=true");
                System.out.println("aftergetconnetionsuccess");
                Statement statement = connection.createStatement();
                
                boolean exist = false;
                boolean itemExist =  false;
                int tableNameColumn = 3;
                DatabaseMetaData dbm = connection.getMetaData();
               
              // statement.executeUpdate("DROP TABLE ItemDetails"); 
              // statement.executeUpdate("DROP TABLE ClientDetails"); 
                for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
               System.out.println(rs.getString(tableNameColumn));
                    if (rs.getString(tableNameColumn).equalsIgnoreCase(Client_Table)) {
                    exist = true;
                    rs.close();
                    break;
               }
              }
                  if (!exist) {
                      System.out.println("creating clienttable");
                      statement.executeUpdate("CREATE TABLE " + Client_Table
                    + "(id VARCHAR(32) PRIMARY KEY,password VARCHAR(32),sold INT,bought INT)");
               }
                  
                  for (ResultSet rs = dbm.getTables(null, null, null,null); rs.next();) {
                   System.out.println(rs.getString(tableNameColumn));
                    if (rs.getString(tableNameColumn).equalsIgnoreCase(Item_Table)) {
                    itemExist = true;
                    rs.close();
                    break;
               }
              } 
                 if(!itemExist){
                     System.out.println("creating itemtable");
                     statement.executeUpdate("CREATE TABLE " + Item_Table
                    + "(itemName VARCHAR(32) PRIMARY KEY,id VARCHAR(32),price FLOAT)"); 
                 }
                 
                
	}
        
        
        
      

	public void registerClient(ClientInterface client) throws RemoteException {
	System.out.println("registeringclienttomarketplace");
                
        try {
            findAccountStatement = connection.prepareStatement("SELECT * from "
                + Client_Table + " WHERE id = ?");  
            findAccountStatement.setString(1, client.getID());
            ResultSet result = findAccountStatement.executeQuery();
             System.out.println("after find from the server");
            if (result.next()) {
                     client.receiveMsg("user name already exist.please give another name");
                     return;
                } 
            String password = client.getPassword();
            if(password.length()<8){
               client.receiveMsg("please enter password of atleast 8 characters");
               return; 
            }
            createAccountStatement = connection.prepareStatement("INSERT INTO "
            + Client_Table + " VALUES (?,?,0,0)");
            createAccountStatement.setString(1,client.getID());
            createAccountStatement.setString(2,password);
             int rows = createAccountStatement.executeUpdate();
             if (rows == 1) {
                // objMapList.add(new ObjMap(client,client.getID()));
                 client.receiveMsg("registered to the marketplace");
             }
             else{
                 client.receiveMsg("unable to register with the marketplace");
             }
                 
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
	
	}

	public void unregisterClient(ClientInterface client) throws RemoteException {
	System.out.println("removingclientfromthemarketplace"); 
        try {
            deleteAccountStatement = connection.prepareStatement("DELETE FROM "
                + Client_Table + " WHERE id = ?");
            deleteAccountStatement.setString(1, client.getID());
            int rows = deleteAccountStatement.executeUpdate();
            if (rows == 1) {
                 client.receiveMsg("removed from the market place");
             }
             else{
                 client.receiveMsg("unable to unregister from market place");
             }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
            
      }
        
        public void login(ClientInterface obj)throws RemoteException {
            if(!obj.getLogin()) {
           try {
            findAccountStatement = connection.prepareStatement("SELECT * from "
                + Client_Table + " WHERE id = ?");  
            findAccountStatement.setString(1, obj.getID());
            ResultSet result = findAccountStatement.executeQuery();
             if (result.next()) {
                  String passWord = result.getString("password");
                  if(passWord.equalsIgnoreCase(obj.getPassword())){
                      obj.receiveMsg("successfully login to the server");
                      objectMapList.put(obj.getID(),obj);
                     // objMapList.add(new ObjMap(client,client.getID()));
                      obj.setLogin(true);
                  }
                  else {
                      obj.receiveMsg("incorrect password");
                  }
                } 
             else {
                obj.receiveMsg("client is not registered with the market place"); 
             }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       }  
        else {     
             obj.receiveMsg("client is already in logged in ");  
            }
     }
    public void logout(ClientInterface obj)throws RemoteException {
        if(obj.getLogin()) {
                objectMapList.remove(obj.getID());
                obj.setLogin(false);
     }
    }
    public void getDetails(ClientInterface obj)throws RemoteException{
        if (!obj.getLogin()) {
               obj.receiveMsg("client not loggedin");
               return;
            }
        
        try {
            System.out.println("in get details");
            findAccountStatement = connection.prepareStatement("SELECT * from "
                    + Client_Table + " WHERE id = ?");
            findAccountStatement.setString(1, obj.getID());
            ResultSet result = findAccountStatement.executeQuery();
             System.out.println("after find from the server");
            if (result.next()) {
                      System.out.println("inside result next");
                      obj.itemInfo(result.getInt("sold"), result.getInt("bought"));
              } else {
                obj.receiveMsg("unable to retrieve the details");
              }
           } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
        public List<Item> getItems() {
            // iterate through the itemmap and create a itemlist and send item obj
	    itemList = new ArrayList<Item>();
        try {
            findAccountStatement = connection.prepareStatement("SELECT * from "
                + Item_Table );  
           // findAccountStatement.setString(1,"camera");
            ResultSet result = findAccountStatement.executeQuery();
            while (result.next()) {
                 System.out.println("after result next in getitems");
                 Item item = new Item();
                 item.setName(result.getString("itemName"));
                 item.setPrice(result.getFloat("price"));
                itemList.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       return itemList;
           
	}
        
        public void wishlist(Item item ,ClientInterface obj) throws RemoteException {
        System.out.println("adding an item to wishlist"); 
        if(obj.getLogin()) {
        wishList.add(new ItemMap(item,obj));
        }
        else {
            obj.receiveMsg("client is not logged in to server");
        }
        }
        
       
       public void sell(Item item ,ClientInterface obj) throws RemoteException {
           System.out.println("selling an item"); 
           int itemSoldCount = 0;
           if (!obj.getLogin()) {
               obj.receiveMsg("client not loggedin");
               return;
            }
          String[] bankList = marketBankobj.listAccounts(); 
          System.out.println(bankList.length);
         for(int i=0;i<bankList.length;i++){
            System.out.println("bank iteration"); 
            System.out.println(bankList[i]);
            System.out.println(obj.getID());
            if(bankList[i] == null){
               obj.receiveMsg("client dont have an account");
               return; 
            }
            if(!bankList[i].equalsIgnoreCase(obj.getID())){
                if(i == bankList.length-1){
                 obj.receiveMsg("client dont have an account");
                 return;   
                }
            }else {
                break;
            }
        }
        try {
            createAccountStatement = connection.prepareStatement("INSERT INTO "
                + Item_Table + " VALUES (?,?,?)");
            createAccountStatement.setString(1,item.getName());
            createAccountStatement.setString(2,obj.getID());
            createAccountStatement.setFloat(3,item.getPrice());
             int rows = createAccountStatement.executeUpdate();
             if (rows == 1) {
               obj.receiveMsg("item placed successful in marketplace");   
              findAccountStatement = connection.prepareStatement("SELECT * from "
                    + Client_Table + " WHERE id = ?");
              System.out.println(obj.getID());
            findAccountStatement.setString(1, obj.getID());
            ResultSet result = findAccountStatement.executeQuery();
             System.out.println("after find query ");
            if (result.next()) {
                     System.out.println("inside result next");
                     itemSoldCount = result.getInt("sold");
                     System.out.println(itemSoldCount);
              } else {
                obj.receiveMsg("unable to retrieve the details client details");
              }   
              
              updateStatement = connection.prepareStatement("UPDATE "
                + Client_Table + " SET sold = ? WHERE id = ?");
              updateStatement.setInt(1,itemSoldCount+1);
              updateStatement.setString(2,obj.getID());
              int row = updateStatement.executeUpdate();
            if (row == 1) {
                obj.receiveMsg("item  stored in the clienttable");
            } else {
                obj.receiveMsg("item  cannot stored in the clienttable");
            }
             
             }
             else{
                 obj.receiveMsg("unable to place an item at market place");
                 return;
             }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
            
       // itemMapList.add(new ItemMap(item,obj));
        for (ItemMap aMap : wishList) {
             System.out.println("insidesellwishlistforloop");
             System.out.println(aMap.getItem().getName());
             System.out.println(item.getName());
             System.out.println(item.getPrice());
             System.out.println(aMap.getItem().getPrice());
             if(aMap.getItem().getName().equalsIgnoreCase(item.getName()) && item.getPrice()<= aMap.getItem().getPrice()){
                 System.out.println("wish list callback"); 
                 aMap.getClientInterface().itemAvailable(item);
             }
	}
        
      }
       
       public void buy(Item item ,ClientInterface obj)throws RemoteException {
           System.out.println("buying an item");
           ClientInterface tradeInterface = null;
           int itemBoughtCount = 0;
           /*if (!clientTable.contains(obj)) {
               obj.receiveMsg("client not registered");
               return;
		}*/
        if (!obj.getLogin()) {
               obj.receiveMsg("client not logged in");
               return;
            }   
        String[] bankList = marketBankobj.listAccounts(); 
        System.out.println(bankList.length);
        for(int i=0;i<bankList.length;i++){
            System.out.println("bank iteration"); 
            System.out.println(bankList[i]);
            System.out.println(obj.getID());
            if(bankList[i] == null){
               obj.receiveMsg("client dont have an account");
               return; 
            }
            if(!bankList[i].equalsIgnoreCase(obj.getID())){
                if(i == bankList.length-1){
                 obj.receiveMsg("client dont have an account");
                 return;   
                }
            }else {
             break; 
            }
            
        }
       try {
            findAccountStatement = connection.prepareStatement("SELECT * from "
                + Item_Table + " WHERE itemName = ?");  
            findAccountStatement.setString(1,item.getName());
            ResultSet result = findAccountStatement.executeQuery();
            while (result.next()) {
               // String itemName = result.getString("itemName");
                float itemPrice = result.getFloat("price");
                String ClientID = result.getString("id");
                String itemName = result.getString("itemName");
          // System.out.println(itemName);
           //  System.out.println(itemPrice);
            // objectMapList.get(result.getString("id"));
             if(itemPrice== item.getPrice()){
                 System.out.println("item match at table"); 
                 Account accDeb = marketBankobj.getAccount(obj.getID());  
                 Account acc = marketBankobj.getAccount(ClientID);
                 if(item.getPrice() <= accDeb.getBalance()){
                     obj.receiveMsg("item bought successfully"); 
                     try {
                         accDeb.withdraw(item.getPrice());
                         acc.deposit(item.getPrice());
                     } catch (RejectedException ex) {
                         Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
                     }
                }
                else {
                  obj.receiveMsg("client dont have enough money in this account"); 
                  return;
                }
                 
             if(objectMapList.get(ClientID)!= null){
                 (objectMapList.get(ClientID)).getItem(item);
             }
             findAccountStatement = connection.prepareStatement("SELECT * from "
                    + Client_Table + " WHERE id = ?");
              findAccountStatement.setString(1, obj.getID());
            ResultSet boughtResult = findAccountStatement.executeQuery();
            if (boughtResult.next()) {
                     System.out.println("inside result next");
                     itemBoughtCount = boughtResult.getInt("bought");
                     System.out.println(itemBoughtCount);
              } else {
                obj.receiveMsg("unable to retrieve the bought details");
              }   
              
              updateStatement = connection.prepareStatement("UPDATE "
                + Client_Table + " SET bought = ? WHERE id = ?");
              updateStatement.setInt(1,itemBoughtCount+1);
              updateStatement.setString(2,obj.getID());
              int row = updateStatement.executeUpdate();
            if (row == 1) {
                obj.receiveMsg("item  bought details stored in the clienttable");
            } else {
                obj.receiveMsg("item  bought details cannot stored in the clienttable");
            } 
             
            deleteAccountStatement = connection.prepareStatement("DELETE FROM "
                + Item_Table + " WHERE itemName = ? AND price = ?");
            deleteAccountStatement.setString(1,itemName);
            deleteAccountStatement.setFloat(2,itemPrice);
            int rows = deleteAccountStatement.executeUpdate();
            if (rows == 1) {
                 System.out.println("sold item removed from database");
             }
             else{
                 System.out.println("unable to remove the sold item from database");
                 return;
             } 
            for (ItemMap Map : wishList) {
             System.out.println("insidesellwishlistforloop");
             if(Map.getItem().getName().equalsIgnoreCase(itemName) && Map.getItem().getPrice()== itemPrice ){
                 wishList.remove(Map);
                 break;
             }
            }
            break; 
            }    
           
        }
	}catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    

    public static void main(String[] args) throws NotBoundException, ClassNotFoundException, SQLException {
		try {
			new MyServer();
		} catch (RemoteException re) {
			System.out.println(re);
			System.exit(1);
		} catch (MalformedURLException me) {
			System.out.println(me);
			System.exit(1);
		}
	}
}





