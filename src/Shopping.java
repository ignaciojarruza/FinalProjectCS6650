import database.ResourceManager;
import database.ResourceManagerI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Shopping {
    ConcurrentHashMap<String, String> itemList;
    ConcurrentHashMap<String, Integer> stockList;
    ConcurrentHashMap<String, Integer> priceList;

    String userId;
    ResourceManagerI thisServer;
    String host;
    String port;

    public Shopping(String host, String port, String userId) throws NotBoundException, RemoteException, MalformedURLException {
        this.userId = userId;
        this.host = host;
        this.port = port;
        this.thisServer = connectToOneAvailableServer(host, port);
        this.itemList = thisServer.getAllItems();
        this.stockList = thisServer.getAllStock();
        this.priceList = thisServer.getAllPrices();
    }

    public static ResourceManagerI connectToOneAvailableServer(String host, String port) throws RemoteException, NotBoundException, MalformedURLException {

        Registry registry = LocateRegistry.getRegistry(Integer.parseInt(port));
        String key = String.format("rmi://%s:%s/ResourceManager", host, port);
        ResourceManagerI resourceManagerI = (ResourceManagerI) Naming.lookup(key);
        System.out.println("connected to server on port : " + port);

        return resourceManagerI;
    }

    public static String[] convertToComboList(int stock) {
        String[] comboList = new String[stock + 1];
        for (int i = 0; i <= stock; i++) {
            comboList[i] = String.valueOf(i);
        }
        return comboList;
    }

    public void addComponentsToShoppingPane(Container pane, JFrame frame) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;


        constraints.gridwidth = 1;
        int index = 1;
        HashMap<String, JComboBox> selectMap = new HashMap<>();
        for (String key : itemList.keySet()) {
            Image image = new ImageIcon(Shopping.class.getResource(itemList.get(key))).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(image);
            constraints.gridy = index + 1;
            constraints.gridx = 0;
            JLabel productTitle = new JLabel(key);
            JLabel productImage = new JLabel(imageIcon);
            JLabel productPrice = new JLabel("$" + priceList.get(key));
            productTitle.setVerticalAlignment(JLabel.CENTER);
            productTitle.setHorizontalAlignment(JLabel.CENTER);
            constraints.weightx = 0.25;
            pane.add(productTitle, constraints);
            constraints.gridx = 1;

            pane.add(productPrice, constraints);
            constraints.gridx = 2;

            constraints.weightx = 0.5;
            pane.add(productImage, constraints);
            JComboBox<String> select = new JComboBox<>(convertToComboList(stockList.get(key)));
            selectMap.put(key, select);
            constraints.gridx = 3;
            constraints.weightx = 0.25;
            pane.add(select, constraints);
            index++;
        }

        JButton addToCart = new JButton("Add To Cart");
        constraints.anchor = GridBagConstraints.PAGE_END;
        pane.add(addToCart, constraints);

        addToCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Added to cart");
                HashMap<String, Integer> ItemIdAndCount = new HashMap<>();
                for (String key : selectMap.keySet()) {
                    int itemQty = Integer.parseInt(selectMap.get(key).getSelectedItem().toString());
                    if (itemQty > 0) {
                        ItemIdAndCount.put(key, itemQty);
                    }
                }
                try {
                    System.out.println(ItemIdAndCount.size());
                    addToCart(userId, ItemIdAndCount);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }

                JPanel cartPanel = new JPanel(new GridBagLayout());
                JScrollPane cartScrollPane = new JScrollPane(cartPanel);

                try {
                    addComponentsToCartPane(cartPanel);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
                frame.getContentPane().removeAll();
                frame.add(cartScrollPane);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }

    public int calculateCartTotal(HashMap<String, Integer> cart) {
        int total = 0;
        if (cart == null) {
            return total;
        }
        for (String item : cart.keySet()) {
            total += cart.get(item) * priceList.get(item);
        }
        return total;
    }


    public void addComponentsToCartPane(Container pane) throws RemoteException {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        HashMap<String, Integer> cart = getCartItems(userId);


        int index = 0;
        HashMap<String, JComboBox> selectMap = new HashMap<>();

        if (cart == null) {
            JLabel emptyCart = new JLabel("Your cart is empty. Return to the Shop All page to get something!");
            emptyCart.setHorizontalAlignment(JLabel.CENTER);
            constraints.weighty = 1;
            constraints.gridwidth = 4;
            pane.add(emptyCart, constraints);
        } else {
            JLabel cartTitle = new JLabel("These items are in your cart. Time to checkout!");
            cartTitle.setHorizontalAlignment(JLabel.CENTER);
            constraints.weighty = 1;
            constraints.gridwidth = 4;
            pane.add(cartTitle, constraints);

            constraints.gridwidth = 1;

            for (String key : cart.keySet()) {
                Image image = new ImageIcon(Shopping.class.getResource(itemList.get(key))).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(image);
                constraints.gridy = index + 1;
                constraints.gridx = 0;
                JLabel productTitle = new JLabel(key);
                JLabel productImage = new JLabel(imageIcon);
                JLabel productPrice = new JLabel("$" + priceList.get(key));
                productTitle.setVerticalAlignment(JLabel.CENTER);
                productTitle.setHorizontalAlignment(JLabel.CENTER);
                constraints.weightx = 0.25;
                pane.add(productTitle, constraints);
                constraints.gridx = 1;

                pane.add(productPrice, constraints);
                constraints.gridx = 2;

                constraints.weightx = 0.5;
                pane.add(productImage, constraints);
                JComboBox<String> select = new JComboBox<>(convertToComboList(stockList.get(key)));
                select.setSelectedIndex(cart.get(key));
                selectMap.put(key, select);
                constraints.gridx = 3;
                constraints.weightx = 0.25;
                pane.add(select, constraints);

                index++;
            }
        }

        JLabel total = new JLabel("Cart Total: $" + calculateCartTotal(cart));
        constraints.gridy = ++index;
        pane.add(total, constraints);

        JButton updateCart = new JButton("Update Cart");
        constraints.gridy = ++index;
        pane.add(updateCart, constraints);

        updateCart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (String key : selectMap.keySet()) {
                    JComboBox select = selectMap.get(key);
                    int newQty = Integer.parseInt(select.getSelectedItem().toString());
                    cart.put(key, newQty);
                }
                total.setText("Cart Total: $" + calculateCartTotal(cart));
                try {
                    updateCart(userId, cart);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        index ++;

        JButton checkout = new JButton("Checkout");
        constraints.gridy = index;
        constraints.anchor = GridBagConstraints.PAGE_END;
        pane.add(checkout, constraints);

        checkout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Checking out");
                try {
                    checkout(userId);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    public void addToCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException {
        thisServer.addToCart(userId, ItemIdAndCount);
    }

    public void updateCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException {
        thisServer.updateCart(userId, ItemIdAndCount);
    }

    public HashMap<String, Integer> getCartItems(String userId) throws RemoteException {
        return thisServer.getCartItems(userId);
    }

    public void checkout(String userId) throws RemoteException {
        thisServer.checkout(userId);
    }

    public void getUserOrderList(String userId) throws RemoteException{
        HashMap<String, Integer> orderList = thisServer.getUserOrderList(userId);
    }

    public static void main(String[] args) throws NotBoundException, RemoteException, MalformedURLException {

        if(args.length < 3) {

            // example Java Shopping localhost 1111 rao@gmail.com
            System.out.println( " Required host, port no, User-emailId ");
            System.exit(0);
        }

        JFrame frame = new JFrame("DistributedShopping.com");
//        JTabbedPane tabbedPane = new JTabbedPane();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel shoppingPanel = new JPanel(new GridBagLayout());
        JPanel cartPanel = new JPanel(new GridBagLayout());
        Shopping shop = new Shopping(args[0], args[1], args[2]);

        JScrollPane shopScrollPane = new JScrollPane(shoppingPanel);

        shop.addComponentsToShoppingPane(shoppingPanel, frame);
//        shop.addComponentsToCartPane(cartPanel);

//        tabbedPane.addTab("Shop All", shoppingPanel);
//        tabbedPane.addTab("Cart", cartPanel);
//        frame.add(tabbedPane, BorderLayout.CENTER);

//        frame.add(cartScrollPane);
        frame.add(shopScrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
