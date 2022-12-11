package database;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public interface ResourceManagerI extends Remote {
	public void populateItemInReplicas(String item, String value) throws RemoteException;

	public void populatePriceInReplicas(String item, Integer price) throws RemoteException;

	public void populateStockInReplicas(String item, int stock) throws RemoteException;

	public String getItem(String key) throws RemoteException;

	public Integer getPrice(String key) throws RemoteException;

	public int getStock(String key) throws RemoteException;

	public ConcurrentHashMap<String, String> getAllItems() throws RemoteException;

	public ConcurrentHashMap<String, Integer> getAllPrices() throws RemoteException;

	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException;

	public void decrementStock(String item) throws RemoteException;

	public void addToCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException;

	public void updateCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException;

	public void checkout(String userId) throws RemoteException;

	public HashMap<String, Integer> getCartItems(String userId) throws RemoteException;

	public HashMap<String, Integer> getUserOrderList(String userId) throws RemoteException;
}