package se.kth.id2212.marketrmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {    
	private static final String USAGE = "java bankrmi.Server <bank_rmi_url>";
	private static final String BANK = "Nordea";

	public Server(String bankName) {
		try {
			Bank bankobj = new BankImpl(bankName);
			// Register the newly created object at rmiregistry.
			//java.rmi.Naming.rebind(bankName, bankobj);
                       Registry registry = LocateRegistry.createRegistry(1099);
		       registry.rebind("bank", bankobj);
                        
			System.out.println(bankobj + " is ready.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
			System.out.println(USAGE);
			System.exit(1);
		}

		String bankName = null;
		if (args.length > 0) {
			bankName = args[0];
		} else {
			bankName = BANK;
		}

		new Server(bankName);
	}
}
