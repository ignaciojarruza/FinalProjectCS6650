import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.*;
import java.util.concurrent.*;
import java.rmi.registry.*;

public class Database extends UnicastRemoteObject implements DatabaseI {
	private ConcurrentHashMap<String, String> items;
	private ConcurrentHashMap<String, Double> priceList;
	private ConcurrentHashMap<String, Integer> stockList;

	public boolean mutualExclusive;

	//Database constructor
	public Database() throws RemoteException {
		super();
		this.items = new ConcurrentHashMap<String, String>();
		this.priceList = new ConcurrentHashMap<String, Double>();
		this.stockList = new ConcurrentHashMap<String, Integer>();
		this.mutualExclusive = false;
	}

	//Used to add new items into the items hashmap
	public void putItem(String key, String value) throws RemoteException {
		if (!this.mutualExclusive) {
			this.mutualExclusive = !this.mutualExclusive;
			this.items.put(key, value);
			System.out.println(String.format("Key value pair <%s, %s> successfully added.", key, value));
			this.mutualExclusive = !this.mutualExclusive;
		} else {
			System.out.println("Database critical resources are currently locked by another transaction. Please try again.");
		}
		
	}

	//Used to add new item prices into the priceList hashmap
	public void putPrice(String key, Double price) throws RemoteException {
		if (!this.mutualExclusive) {
			this.mutualExclusive = !this.mutualExclusive;
			this.priceList.put(key, price);
			System.out.println(String.format("Key value pair <%s, %.2f> successfully added.", key, price));
			this.mutualExclusive = !this.mutualExclusive;
		} else {
			System.out.println("Database critical resources are currently locked by another transaction. Please try again.");
		}
	}

	//Used to add new item quantities into the stockList hashmap
	public void putStock(String key, int quantity) throws RemoteException {
		if (!this.mutualExclusive) {
			this.mutualExclusive = !this.mutualExclusive;
			this.stockList.put(key, quantity);
			System.out.println(String.format("Key value pair <%s, %d> successfully added.", key, quantity));
			this.mutualExclusive = !this.mutualExclusive;
		} else {
			System.out.println("Database critical resources are currently locked by another transaction. Please try again.");
		}
		
	}

	//Used to get data from the items hashTable
	public String getItem(String key) throws RemoteException {
		return this.items.get(key);
	}

	//Used to get the price of an item
	public Double getPrice(String key) throws RemoteException {
		return this.priceList.get(key);
	}

	//Used to get the stock of an item
	public int getStock(String key) throws RemoteException {
		return this.stockList.get(key);
	}

	//Used to get all items (and their image url strings)
	public ConcurrentHashMap<String, String> getAllItems() throws RemoteException {
		try {
			ConcurrentHashMap<String, String> copyItems = new ConcurrentHashMap<String, String>();
			copyItems.putAll(this.items);
			return copyItems;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ConcurrentHashMap<String, String>();
	}

	//Used to get all item prices
	public ConcurrentHashMap<String, Double> getAllPrices() throws RemoteException {
		ConcurrentHashMap<String, Double> copyPrices = new ConcurrentHashMap<String, Double>();
		copyPrices.putAll(this.priceList);
		return copyPrices;
	}

	//Used to get all item quantities
	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException {
		ConcurrentHashMap<String, Integer> copyStock = new ConcurrentHashMap<String, Integer>();
		copyStock.putAll(this.stockList);
		return copyStock;
	}

	//Used to decrease item stock by 1
	public String reduceStock(String item) throws RemoteException {
		int currentStock = this.stockList.get(item);
		if (currentStock == 0) {
			return "Item stock is already zero.";
		}
		this.stockList.put(item, currentStock - 1);
		return String.format("Item quantity for %s is now %d", item, currentStock - 1);
	}

	//Returns status if critical resources are locked
	public boolean isAvailable() throws RemoteException {
		return this.mutualExclusive;
	}
}