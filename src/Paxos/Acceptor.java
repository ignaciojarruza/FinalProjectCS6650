package Paxos;

import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Acceptor {

    int myOldProposalId = Integer.MIN_VALUE;
    public Acceptor() {}

    public boolean prepare(int id, HashMap<String, Integer> items) throws InterruptedException, RemoteException, SocketTimeoutException {
        log("Received a prepare request to the Paxos Acceptor");
        return conditionCheck(id, items);
    }

    public boolean accept(int id, HashMap<String, Integer> items) throws InterruptedException, RemoteException, SocketTimeoutException{
        log("Received a accept request to the Paxos Acceptor");
        return conditionCheck(id, items);
    }

    public boolean conditionCheck(int id, HashMap<String, Integer> items) {

        if(myOldProposalId > id) {
            return false;
        }
        else {
            this.myOldProposalId = id;
            return true;
        }
    }

    public static String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
    }

    public static void log(String msg){
        System.out.println(getCurrentTimeStamp() +  " : " + msg);}
}
