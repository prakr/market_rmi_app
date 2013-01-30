package se.kth.id2212.marketrmi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class MyClient {    
	Account account;
	Bank bankobj;
	private String bankname;
	String clientname;
        ClientInterface trader;
        ServerInterface marketServer;
	
        public MyClient(String clientID,String passWord) throws RemoteException, NotBoundException, MalformedURLException {
                this.clientname = clientID;
                trader = new ClientInterfaceImpl(clientID,passWord);
                bankobj = (Bank)Naming.lookup("bank");
                marketServer=(ServerInterface)Naming.lookup("marketplace");
		
	}
        
       public void run() throws RemoteException, RejectedException {        
		BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println(clientname + "@" + "marketplace" + ">");
			try {
				String userInput = consoleIn.readLine();
                                if (userInput == null) {
                                        break;
                                  }
                                switch (userInput) {
                                    case "register":
                                        System.out.println("callingregister");
                                        register();
                                        break;
                                    case "unregister":
                                        System.out.println("callingunregister");
                                        unregister();
                                        break;
                                    case "sell":
                                        System.out.println("sell");
                                        sell();
                                        break;
                                    case "buy":
                                        System.out.println("buy");
                                        buy();
                                        break;
                                    case "wishlist":
                                        System.out.println("wishlist");
                                        wishlist();
                                        break;
                                    case "listItems": 
                                        System.out.println("listItems");
                                        listItems();
                                        break;
                                    case "getDetails":
                                        System.out.println("getDetails");
                                        getDetails();
                                        break;
                                    case "login":
                                        System.out.println("login");
                                        login();
                                        break;
                                    case "logout":
                                        System.out.println("logout");
                                        logout();
                                        break;
                                    case "newAccount":
                                        account = bankobj.newAccount(clientname);
                                        System.out.println("deposit or withdrawl or balance"); 
                                        break;
                                    case "deleteAccount":
                                        bankobj.deleteAccount(clientname);
                                        break;
                                    case "getAccount":
                                        account = bankobj.getAccount(clientname);
                                        System.out.println("deposit or withdrawl or balance"); 
                                        break;
                                    case "deposit" :
                                        System.out.println("enter the deposit money:"); 
                                        String depo = consoleIn.readLine();
                                        float depoAmount=Float.parseFloat(depo);
                                        account.deposit(depoAmount);
                                        break;
                                    case "withdraw":
                                        System.out.println("enter the withdrawl money:"); 
                                        String wthdrawl = consoleIn.readLine();
                                        float withAmount=Float.parseFloat(wthdrawl);
                                        account.withdraw(withAmount);
                                        break;
                                    case "balance" :
                                        System.out.println("account balance:"); 
                                        float f = account.getBalance();
                                        System.out.println(f);
                                        break;
                                    case "help":
                                        System.out.println("register");
                                        System.out.println("unregister");
                                        System.out.println("sell");
                                        System.out.println("buy");
                                        System.out.println("wishlist");
                                        System.out.println("listItems");
                                        System.out.println("newAccount");
                                        System.out.println("deleteAccount");
                                        break;
                                    case "quit":
                                        System.exit(1);
                                }
                                
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
       
       public void register() throws RemoteException{
           System.out.println("register"); 
           marketServer.registerClient(trader);
       }
       
       public void login() throws RemoteException{
           System.out.println("login"); 
           marketServer.login(trader);
       }
       
       public void logout() throws RemoteException{
           System.out.println("logout"); 
           marketServer.logout(trader);
       }
       
       public void getDetails() throws RemoteException{
           System.out.println("getDetails"); 
           marketServer.getDetails(trader);
       }
         public void unregister() throws RemoteException{
           System.out.println("unregister"); 
           marketServer.unregisterClient(trader);
       }
         
       public void sell() throws RemoteException, IOException{
           System.out.println("enter the item name:"); 
           BufferedReader sellInput = new BufferedReader(new InputStreamReader(System.in));
           String name = sellInput.readLine();
           System.out.println("enter the item price:"); 
           String price = sellInput.readLine();
           float amount=Float.parseFloat(price);
           Item itemSell = new Item();
           itemSell.setName(name);
           itemSell.setPrice(amount);
           marketServer.sell(itemSell,trader);
       }  
       
       public void buy() throws RemoteException, IOException{
           System.out.println("enter the item name:"); 
           BufferedReader sellInput = new BufferedReader(new InputStreamReader(System.in));
           String name = sellInput.readLine();
           System.out.println("enter the item price:"); 
           String price = sellInput.readLine();
           float amount=Float.parseFloat(price);
           Item itemBought = new Item();
           itemBought.setName(name);
           itemBought.setPrice(amount);
           marketServer.buy(itemBought,trader);
       } 
       
       public void wishlist() throws RemoteException, IOException{
           System.out.println("enter the item name:"); 
           BufferedReader sellInput = new BufferedReader(new InputStreamReader(System.in));
           String name = sellInput.readLine();
           System.out.println("enter the item price:"); 
           String price = sellInput.readLine();
           float amount=Float.parseFloat(price);
           Item itemWish = new Item();
           itemWish.setName(name);
           itemWish.setPrice(amount);
           marketServer.wishlist(itemWish,trader);
       } 

	
      public void listItems() throws RemoteException{
           System.out.println("listItems"); 
            List<Item> itemList= marketServer.getItems();
		if(!itemList.isEmpty()) {
			System.out.println("\nitems available ***********");
			for (Item aItem : itemList) {
				System.out.println(aItem.getName());
                                System.out.println(aItem.getPrice());
			}
			System.out.println("******************************\n");
		}
         
       }

      public static void main(String[] args) throws IOException, RemoteException, NotBoundException, RejectedException {
            System.out.println("Enter the client");
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            System.out.println(str);
            String passWrd = br.readLine();
            MyClient myClient = new MyClient(str,passWrd);
            myClient.run();
        }
}



















