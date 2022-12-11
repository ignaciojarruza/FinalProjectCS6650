package database;

import Paxos.Acceptor;
import Paxos.Learner;
import Paxos.Proposer;

import java.net.SocketTimeoutException;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.*;
import java.util.concurrent.*;
import java.rmi.registry.*;

public class Database extends UnicastRemoteObject implements DatabaseI {
	private ConcurrentHashMap<String, String> items;
	private ConcurrentHashMap<String, Integer> priceList;
	private ConcurrentHashMap<String, Integer> stockList;

	private ConcurrentHashMap<String, HashMap<String, Integer>> userCart;

	private ConcurrentHashMap<String, HashMap<String, Integer>> userOrders;

	public boolean mutualExclusive;

	Proposer pro = null;
	Acceptor accept = null;
	Learner learn = null;

	//Database constructor
	public Database(String server, String port, int n) throws RemoteException {
		super();
		this.items = new ConcurrentHashMap<String, String>();
		this.priceList = new ConcurrentHashMap<String, Integer>();
		this.stockList = new ConcurrentHashMap<String, Integer>();
		this.mutualExclusive = false;
		this.userCart = new ConcurrentHashMap<String, HashMap<String, Integer>>();
		this.userOrders = new ConcurrentHashMap<>();
		this.addToDatabase();
		this.pro = new Proposer();
		pro.updateAllServers(server, port, n);
		this.accept = new Acceptor();
		this.learn = new Learner();
	}

	public void addToDatabase() {
		this.items.put("Nike Air Max 90", "./images/image1.png");
		this.stockList.put("Nike Air Max 90", 5);
		this.priceList.put("Nike Air Max 90", 200);
		this.items.put("Jordan Retro 12", "./images/image2.png");
		this.stockList.put("Jordan Retro 12", 4);
		this.priceList.put("Jordan Retro 12", 231);
		this.items.put("Nike Air Force 1 '07 LV8", "./images/image3.png");
		this.stockList.put("Nike Air Force 1 '07 LV8", 10);
		this.priceList.put("Nike Air Force 1 '07 LV8", 180);
		this.items.put("New Balance 574 Core", "./images/image4.png");
		this.stockList.put("New Balance 574 Core", 6);
		this.priceList.put("New Balance 574 Core", 199);
		this.items.put("Nike Air Huarache", "./images/image5.png");
		this.stockList.put("Nike Air Huarache", 20);
		this.priceList.put("Nike Air Huarache", 99);
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
	public void putPrice(String key, Integer price) throws RemoteException {
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
	public Integer getPrice(String key) throws RemoteException {
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
	public ConcurrentHashMap<String, Integer> getAllPrices() throws RemoteException {
		ConcurrentHashMap<String, Integer> copyPrices = new ConcurrentHashMap<String, Integer>();
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


	//add to cart
	public synchronized String addToCart(String userId, HashMap<String, Integer> ItemIdAndCount) {

		try {
			if (userCart.containsKey(userId)) {
				HashMap<String, Integer> tmp = userCart.get(userId);
				for (String itemId : ItemIdAndCount.keySet()) {
					if (tmp.containsKey(itemId)) {
						tmp.put(itemId, ItemIdAndCount.get(itemId) + tmp.get(itemId));
					} else {
						tmp.put(itemId, ItemIdAndCount.get(itemId));
					}
				}
				userCart.put(userId, tmp);
			} else {
				userCart.put(userId, ItemIdAndCount);
			}
		}
		catch (Exception e) {
			return "Exception while updating cart" + e.getMessage();
		}
		return "Successfully updated cart with " + ItemIdAndCount.size() + " products";
	}

	public synchronized String updateCart(String userId, HashMap<String, Integer> ItemIdAndCount) {
		try {
			userCart.put(userId, ItemIdAndCount);
		}
		catch (Exception e) {
			return "Exception while updating cart" + e.getMessage();
		}
		return "Successfully updated cart..";
	}

	public HashMap<String, Integer> getCartItems(String userId) throws RemoteException {

		if(userCart.containsKey(userId)) {
			HashMap<String, Integer> items = userCart.get(userId);

			for(String it:items.keySet()) {
				if(getStock(it) < items.get(it)) {
					items.remove(it);
				}
			}
			return items;
		}
		return null;
	}

	public String checkout(String userId) {

		String checkOutMsg = "";
		//get cart from db and check whether all items are still available..
		try {
			HashMap<String, Integer> items = getCartItems(userId);
			if(items == null) {
				System.out.println(" Cart is empty or Items are went out of stock ");
			}
			else {
				//checkOutMsg = pro.propose(userId, items);

				for (String item:items.keySet()) {
					for(int i=0; i<items.get(item); i++){
						this.reduceStock(item);
					}
					this.updateUserOrders(userId, item, items.get(item));
					this.removeItemsFromCart(userId, item);
				}

			}
		}
		catch (Exception e) {

		}
		return checkOutMsg;
	}

	public void removeItemsFromCart(String userId, String itemId) throws RemoteException {

		HashMap<String, Integer> items = getCartItems(userId);
		if(userCart.containsKey(userId)) {
			items.remove(itemId);
		}
	}


	public void updateUserOrders(String userId, String item, int count) throws RemoteException {
		try {
			if (userOrders.containsKey(userId)) {
				HashMap<String, Integer> tmp = userOrders.get(item);
				tmp.put(item, count);
			} else {
				HashMap<String, Integer> tmp = new HashMap<>();
				tmp.put(item, count);
				userOrders.put(userId, tmp);
			}

		}
		catch (Exception e) {
			System.out.println( "Exception while updating Order List" + e.getMessage() );
		}
	}

	public synchronized HashMap<String, Integer> getUserOrderList(String userId) throws RemoteException {

		if(userOrders.containsKey(userId)) {
			return userOrders.get(userId);
		}
		else {
			return null;
		}
	}

	public boolean prepare(int id, HashMap<String, Integer> items) throws SocketTimeoutException, RemoteException, InterruptedException {
		return accept.prepare(id, items);
	}

	public boolean accept(int id, HashMap<String, Integer> items) throws InterruptedException, SocketTimeoutException, RemoteException {
		return accept.accept(id, items);
	}

	public String commit(String userId, HashMap<String, Integer> items, DatabaseI databaseI) throws RemoteException {
		return learn.commit(userId, items, databaseI);
	}

	public void setProposerProposalId(int id) throws RemoteException {
		pro.setmyProposalId(id);
	}

}