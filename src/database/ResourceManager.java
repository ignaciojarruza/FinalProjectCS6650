import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.*;
import java.util.concurrent.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

public class ResourceManager extends UnicastRemoteObject implements ResourceManagerI {
	private int numReplicas;
	private String server;
	private String port;
	private String[] replicas;

	//ResourceManager Constructor, creates n replicas and bings all remote objects to the registry
	public ResourceManager(String server, String port, int n) throws RemoteException {
		super();
		this.server = server;
		this.port = port;
		this.numReplicas = n;
	}

	public void createReplicas() {
		//Create replicas + bind to registry
		try {
			//Create n replicas and bind to register
			this.replicas = new String[this.numReplicas];
			int portInt = Integer.parseInt(this.port) + 1;
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			for (int i = 0; i < this.numReplicas; i++) {
				this.replicas[i] = String.format("%d", portInt + i);
				Database replica = new Database();
				Naming.rebind(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, portInt + i), replica);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Populates all replicas with item and item image url
	public void populateItemInReplicas(String item, String value) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				replica.putItem(item, value);
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Populates all replicas with price of item
	public void populatePriceInReplicas(String item, Double price) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				replica.putPrice(item, price);
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	//Populates all replicas with stock of item
	public void populateStockInReplicas(String item, int stock) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				replica.putStock(item, stock);
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	//Retrieves item image url from a replica
	public String getItem(String key) throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getItem(key);
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "Error occured while retrieving item.";
	}

	//Retrieves item image url from a replica
	public Double getPrice(String key) throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getPrice(key);
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	//Retrieves item image url from a replica
	public int getStock(String key) throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getStock(key);
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	//Resource for random index: https://stackoverflow.com/questions/4023137/generating-a-random-index-for-an-array
	//Retrieves all items from a replica
	public ConcurrentHashMap<String, String> getAllItems() throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getAllItems();
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, String>();
	}

	//Retrieves all prices from a replica
	public ConcurrentHashMap<String, Double> getAllPrices() throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getAllPrices();
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, Double>();
	}

	//Retrieves all stock from a replica
	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getAllStock();
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, Integer>();
	}

	//Decrements stock for an item in all replicas
	public void decrementStock(String item) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI)Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				System.out.println(replica.reduceStock(item));
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}