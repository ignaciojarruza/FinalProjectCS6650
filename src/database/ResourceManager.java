package database;

import Paxos.Acceptor;
import Paxos.Learner;
import Paxos.Proposer;
import database.Database;
import database.DatabaseI;

import java.net.SocketTimeoutException;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.*;
import java.util.concurrent.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

public class ResourceManager extends UnicastRemoteObject implements ResourceManagerI, Runnable {
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
				Database replica = new Database(server, port, numReplicas);
				Naming.rebind(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, portInt + i), replica);

				//Thread serverThread=new Thread();
				//serverThread.start();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

	}

	//Populates all replicas with item and item image url
	public void populateItemInReplicas(String item, String value) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
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
	public void populatePriceInReplicas(String item, Integer price) throws RemoteException {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
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
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
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
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getItem(key);
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "Error occured while retrieving item.";
	}

	//Retrieves item image url from a replica
	public Integer getPrice(String key) throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getPrice(key);
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	//Retrieves item image url from a replica
	public int getStock(String key) throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
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
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getAllItems();
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, String>();
	}

	//Retrieves all prices from a replica
	public ConcurrentHashMap<String, Integer> getAllPrices() throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
			return replica.getAllPrices();
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, Integer>();
	}

	//Retrieves all stock from a replica
	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException {
		try {
			int randomIndex = new Random().nextInt(this.numReplicas);
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(this.port) + 1 + randomIndex));
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
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				System.out.println(replica.reduceStock(item));
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	//add to cart in all replicas
	public void addToCart(String userId, HashMap<String, Integer> ItemIdAndCount) {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				System.out.println(replica.addToCart(userId, ItemIdAndCount));
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException | RemoteException e) {
			e.printStackTrace();
		}
	}

	//update cart in all replicas
	//add to cart in all replicas
	public void updateCart(String userId, HashMap<String, Integer> ItemIdAndCount) {
		try {
			for (int i = 0; i < this.numReplicas; i++) {
				LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
				DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + i));
				System.out.println(replica.updateCart(userId, ItemIdAndCount));
			}
		} catch (NotBoundException nbe) {
			nbe.printStackTrace();
		} catch (MalformedURLException | RemoteException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Integer> getCartItems(String userId) {
		int randomIndex = new Random().nextInt(this.numReplicas);
		//get cart from db and check whether all items are still available..
		try {
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + randomIndex));
			return replica.getCartItems(userId);
		} catch (Exception e) {

		}
		return null;
	}

	//checkout
	public String checkout(String userId) throws RemoteException, MalformedURLException, NotBoundException {

		int randomIndex = new Random().nextInt(this.numReplicas);
		//get cart from db and check whether all items are still available.
		LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
		DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + randomIndex));
		String msg = replica.checkout(userId);

		return msg;

	}

	public HashMap<String, Integer> getUserOrderList(String userId) throws RemoteException {
		int randomIndex = new Random().nextInt(this.numReplicas);
		//get cart from db and check whether all items are still available..
		try {
			LocateRegistry.getRegistry(this.server, Integer.parseInt(this.port));
			DatabaseI replica = (DatabaseI) Naming.lookup(String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, Integer.parseInt(port) + 1 + randomIndex));
			return replica.getUserOrderList(userId);
		} catch (Exception e) {

		}
		return null;
	}
}