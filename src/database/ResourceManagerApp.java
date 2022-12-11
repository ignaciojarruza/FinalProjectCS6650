package database;

import database.ResourceManager;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject; 
import java.rmi.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

public class ResourceManagerApp {
	public static void main(String[] args) {
		try {
			//Argument Handling
			String server = args[0];
			String port = args[1];
			String n = args[2];

			//Create Resource Manager object and bind to registry
			ResourceManager manager = new ResourceManager(server, port, Integer.parseInt(n));
			LocateRegistry.createRegistry(Integer.parseInt(port));
			Naming.rebind(String.format("rmi://%s:%s/ResourceManager", server, port), manager);
			System.out.println("ResourceManager was successfully bound with rmi.");
			manager.createReplicas();
			System.out.println("Replicas successfully created and bound with rmi.");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please follow ReadMe instructions on how to run. The program must have server and port as arguments (in that order) and the number of replicas.");
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}