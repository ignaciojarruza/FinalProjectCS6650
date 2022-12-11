package Paxos;

import database.DatabaseI;

import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Learner {

    public Learner(){
    }

    public static String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
    }

    public static void log(String msg){
        System.out.println(getCurrentTimeStamp() +  " : " + msg);}

    public String commit(String userId, HashMap<String, Integer> items, DatabaseI databaseI) throws RemoteException {

        for (String item:items.keySet()) {
            for(int i=0; i<items.get(item); i++){
                databaseI.reduceStock(item);
            }
            databaseI.updateUserOrders(userId, item, items.get(item));
            databaseI.removeItemsFromCart(userId, item);
        }

        return "Commit success";
    }
}
