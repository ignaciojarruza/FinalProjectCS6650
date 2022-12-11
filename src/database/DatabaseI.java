package database;

import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public interface DatabaseI extends Remote {
	public void putItem(String key, String value) throws RemoteException;
	public void putPrice(String key, Integer price) throws RemoteException;
	public void putStock(String key, int quantity) throws RemoteException;
	public String getItem(String key) throws RemoteException;
	public Integer getPrice(String key) throws RemoteException;
	public int getStock(String key) throws RemoteException;
	public ConcurrentHashMap<String, String> getAllItems() throws RemoteException;
	public ConcurrentHashMap<String, Integer> getAllPrices() throws RemoteException;
	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException;
	public String reduceStock(String item) throws RemoteException;
	public boolean isAvailable() throws RemoteException;

	public String addToCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException;

	public String updateCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException;

	public HashMap<String, Integer> getCartItems(String userId) throws RemoteException;

	boolean prepare(int myProposalId, HashMap<String, Integer> items) throws SocketTimeoutException, RemoteException, InterruptedException;

	boolean accept(int myProposalId, HashMap<String, Integer> items) throws InterruptedException, SocketTimeoutException, RemoteException;

	String commit(String userId, HashMap<String, Integer> items, DatabaseI databaseI) throws RemoteException;

	void setProposerProposalId(int myProposalId) throws RemoteException;

	String checkout(String userId) throws RemoteException;

	void updateUserOrders(String userId, String item, int count) throws RemoteException;

	HashMap<String, Integer> getUserOrderList(String userId) throws RemoteException;

	void removeItemsFromCart(String userId, String item) throws RemoteException;
}