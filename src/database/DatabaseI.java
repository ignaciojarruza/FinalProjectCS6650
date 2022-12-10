import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public interface DatabaseI extends Remote {
	public void putItem(String key, String value) throws RemoteException;
	public void putPrice(String key, Double price) throws RemoteException;
	public void putStock(String key, int quantity) throws RemoteException;
	public String getItem(String key) throws RemoteException;
	public Double getPrice(String key) throws RemoteException;
	public int getStock(String key) throws RemoteException;
	public ConcurrentHashMap<String, String> getAllItems() throws RemoteException;
	public ConcurrentHashMap<String, Double> getAllPrices() throws RemoteException;
	public ConcurrentHashMap<String, Integer> getAllStock() throws RemoteException;
	public String reduceStock(String item) throws RemoteException;
	public boolean isAvailable() throws RemoteException;
}