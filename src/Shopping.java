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

public class Shopping {
    HashMap<String, String> demoItems = new HashMap<>();
    HashMap<String, Integer> demoStockList = new HashMap<>();
    HashMap<String, Integer> demoPriceList = new HashMap<>();
    HashMap<String, Integer> demoCart = new HashMap<>();

    String userId;
    ResourceManagerI thisServer;
    String host;
    String port;

    public Shopping(String host, String port, String userId) throws NotBoundException, RemoteException, MalformedURLException {
        this.userId = userId;
        this.host = host;
        this.port = port;
        this.thisServer = connectToOneAvailableServer(host, port);
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

    public void addComponentsToShoppingPane(Container pane, JTabbedPane tabbedPane) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        demoItems.put("Nike Air Max 90", "./images/image1.png");
        demoStockList.put("Nike Air Max 90", 5);
        demoPriceList.put("Nike Air Max 90", 200);
        demoItems.put("Jordan Retro 12", "./images/image2.png");
        demoStockList.put("Jordan Retro 12", 4);
        demoPriceList.put("Jordan Retro 12", 231);
        demoItems.put("Nike Air Force 1 '07 LV8", "./images/image3.png");
        demoStockList.put("Nike Air Force 1 '07 LV8", 10);
        demoPriceList.put("Nike Air Force 1 '07 LV8", 180);
        demoItems.put("New Balance 574 Core", "./images/image4.png");
        demoStockList.put("New Balance 574 Core", 6);
        demoPriceList.put("New Balance 574 Core", 199);
        demoItems.put("Nike Air Huarache", "./images/image5.png");
        demoStockList.put("Nike Air Huarache", 20);
        demoPriceList.put("Nike Air Huarache", 99);


        constraints.gridwidth = 1;
        int index = 1;
        for (String key : demoItems.keySet()) {
            Image image = new ImageIcon(Shopping.class.getResource(demoItems.get(key))).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(image);
            constraints.gridy = index + 1;
            constraints.gridx = 0;
            JLabel productTitle = new JLabel(key);
            JLabel productImage = new JLabel(imageIcon);
            JLabel productPrice = new JLabel("$" + demoPriceList.get(key));
            productTitle.setVerticalAlignment(JLabel.CENTER);
            productTitle.setHorizontalAlignment(JLabel.CENTER);
            constraints.weightx = 0.25;
            pane.add(productTitle, constraints);
            constraints.gridx = 1;

            pane.add(productPrice, constraints);
            constraints.gridx = 2;

            constraints.weightx = 0.5;
            pane.add(productImage, constraints);
            JComboBox<String> select = new JComboBox<>(convertToComboList(demoStockList.get(key)));
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
                //TODO: add products to cart hashmap here
                System.out.println("Added to cart");
                tabbedPane.setSelectedIndex(1);
            }
        });

    }

    public int calculateCartTotal() {
        int total = 0;
        for (String item : demoCart.keySet()) {
            total += demoCart.get(item) * demoPriceList.get(item);
        }
        return total;
    }


    public void addComponentsToCartPane(Container pane) {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        demoCart.put("Nike Air Max 90", 2);
        demoCart.put("Nike Air Huarache", 1);

        JLabel cartTitle = new JLabel("These items are in your cart. Time to checkout!");
        cartTitle.setHorizontalAlignment(JLabel.CENTER);
        constraints.weighty = 1;
        constraints.gridwidth = 4;
        pane.add(cartTitle, constraints);

        constraints.gridwidth = 1;
        int index = 0;
        HashMap<String, JComboBox> selectMap = new HashMap<>();
        for (String key : demoCart.keySet()) {
            Image image = new ImageIcon(Shopping.class.getResource(demoItems.get(key))).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(image);
            constraints.gridy = index + 1;
            constraints.gridx = 0;
            JLabel productTitle = new JLabel(key);
            JLabel productImage = new JLabel(imageIcon);
            JLabel productPrice = new JLabel("$" + demoPriceList.get(key));
            productTitle.setVerticalAlignment(JLabel.CENTER);
            productTitle.setHorizontalAlignment(JLabel.CENTER);
            constraints.weightx = 0.25;
            pane.add(productTitle, constraints);
            constraints.gridx = 1;

            pane.add(productPrice, constraints);
            constraints.gridx = 2;

            constraints.weightx = 0.5;
            pane.add(productImage, constraints);
            JComboBox<String> select = new JComboBox<>(convertToComboList(demoStockList.get(key)));
            select.setSelectedIndex(demoCart.get(key));
            selectMap.put(key, select);
            constraints.gridx = 3;
            constraints.weightx = 0.25;
            pane.add(select, constraints);

            index++;
        }

        JLabel total = new JLabel("Cart Total: $" + calculateCartTotal());
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
                    demoCart.put(key, newQty);
                    total.setText("Cart Total: $" + calculateCartTotal());
                }
            }
        });


        JButton checkout = new JButton("Checkout");
        constraints.anchor = GridBagConstraints.PAGE_END;
        pane.add(checkout, constraints);

        checkout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Checking out");
                //TODO: checkout and remove products from user cart and stock list
            }
        });
    }

    public void addTOCart(String userId, HashMap<String, Integer> ItemIdAndCount) throws RemoteException {
        thisServer.addToCart(userId, ItemIdAndCount);
    }

    public void getCartItems(String userId) throws RemoteException {
        HashMap<String, Integer> cartList = thisServer.getCartItems(userId);
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
        JTabbedPane tabbedPane = new JTabbedPane();
        frame.setExtendedState(JFrame.MAXIMIZED_VERT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel shoppingPanel = new JPanel(new GridBagLayout());
        JPanel cartPanel = new JPanel(new GridBagLayout());
        Shopping shop = new Shopping(args[0], args[1], args[2]);
        shop.addComponentsToShoppingPane(shoppingPanel, tabbedPane);
        shop.addComponentsToCartPane(cartPanel);

        tabbedPane.addTab("Shop All", shoppingPanel);
        tabbedPane.addTab("Cart", cartPanel);
        frame.add(tabbedPane, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tabbedPane);
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
