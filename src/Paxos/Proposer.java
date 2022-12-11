package Paxos;

import database.DatabaseI;
import database.ResourceManagerI;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Proposer {

    int myProposalId;
    
    String server;
    String port;
    int n;

    public Proposer() {
        this.myProposalId = 0;
    }

    public void updateAllServers(String server, String port, int n) {
        this.server = server;
        this.port = port;
        this.n = n;
    }

    public synchronized String propose(String userId, HashMap<String, Integer> items) {

        String response = "";
        try {

            //prepare
            log("***************** Started Paxos proposal functionality *******************");

            myProposalId++; int count =0;
            log("Sending prepare requests to all acceptors");
            for(int i=0; i<n; i++){
                try {
                    Registry registry = LocateRegistry.getRegistry(server, Integer.parseInt(port+i));
                    String key = String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, this.port + 1 + i);
                    DatabaseI databaseI = (DatabaseI) registry.lookup(key);
                    if(databaseI.prepare(myProposalId, items)){
                        count ++;
                    }
                }
                catch (SocketTimeoutException se){
                    continue;
                }
                catch(Exception e){
                    continue;
                }
            }
            log("Servers accepted prepare requests are " + count);

            //accept only if server positive response of more than half
            if(count > n/2) {
                count = 0;
                log("Sending accept requests to all acceptors");
                for(int i=0; i<n; i++){
                    try {
                        Registry registry = LocateRegistry.getRegistry(server, Integer.parseInt(port+i));
                        String key = String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, this.port + 1 + i);
                        DatabaseI databaseI = (DatabaseI) registry.lookup(key);
                        if(databaseI.accept(myProposalId, items)){
                            count ++;
                        }
                    }
                    catch (SocketTimeoutException se){
                        continue;
                    }
                    catch (Exception e) {
                        continue;
                    }
                }
                log("Servers accepted accept requests are " + count);
            }
            else {
                response = "Consensus could not be reached as only " + count +
                        "servers replied to the prepare request";
                log(response);
                return response;
            }

            //commit only if server positive response of more than half
            if(count > n/2) {
                count = 0;
                log("Sending commit requests to all Learners");
                for(int i=0; i<n; i++){
                    try {
                        Registry registry = LocateRegistry.getRegistry(server, Integer.parseInt(port+i));
                        String key = String.format("rmi://%s:%s/%s:%d/Replica", this.server, this.port, this.server, this.port + 1 + i);
                        DatabaseI databaseI = (DatabaseI) registry.lookup(key);
                        databaseI.commit(userId, items, databaseI);
                        databaseI.setProposerProposalId(myProposalId);
                    }
                    catch (Exception e) {
                        continue;
                    }
                }
            }
            else {
                response = "Consensus could not be reached as only " + count +
                        "servers replied to the accept request";
                log(response);
                return response;
            }
            log("****************** Completed Paxos proposal functionality **************************");

        }

        catch(Exception e){
            System.out.println("Remote Exception" + e.getMessage());
        }

        return "Successfully checkout";
    }

    public int getmyProposalId() {
        return myProposalId;
    }

    public void setmyProposalId(int id) {

        if(myProposalId < id) { myProposalId = id; }
    }

    public static String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
    }

    public static void log(String msg){
        System.out.println(getCurrentTimeStamp() +  " : " + msg);}
}



