import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Shopping {

    public static String[] convertToComboList(int stock) {
        String[] comboList = new String[stock + 1];
        for (int i = 0; i <= stock; i++) {
            comboList[i] = String.valueOf(i);
        }
        return comboList;
    }

    public void addComponentsToPane(Container pane, JFrame frame) {
        JScrollPane scrollPane = new JScrollPane(pane);
        frame.add(scrollPane);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        HashMap<String, ImageIcon> demoItems = new HashMap<>();
        HashMap<String, Integer> demoStockList = new HashMap<>();
        HashMap<String, Integer> demoPriceList = new HashMap<>();

        demoItems.put("Nike Air Max 90", new ImageIcon(Shopping.class.getResource("./images/image1.png")));
        demoStockList.put("Nike Air Max 90", 5);
        demoPriceList.put("Nike Air Max 90", 200);
        demoItems.put("Jordan Retro 12", new ImageIcon(Shopping.class.getResource("./images/image2.png")));
        demoStockList.put("Jordan Retro 12", 4);
        demoPriceList.put("Jordan Retro 12", 231);
        demoItems.put("Nike Air Force 1 '07 LV8", new ImageIcon(Shopping.class.getResource("./images/image3.png")));
        demoStockList.put("Nike Air Force 1 '07 LV8", 10);
        demoPriceList.put("Nike Air Force 1 '07 LV8", 180);
        demoItems.put("New Balance 574 Core", new ImageIcon(Shopping.class.getResource("./images/image4.png")));
        demoStockList.put("New Balance 574 Core", 6);
        demoPriceList.put("New Balance 574 Core", 199);
        demoItems.put("Nike Air Huarache", new ImageIcon(Shopping.class.getResource("./images/image5.png")));
        demoStockList.put("Nike Air Huarache", 20);
        demoPriceList.put("Nike Air Huarache", 99);


        constraints.gridwidth = 1;
        int index = 1;
        for (String key : demoItems.keySet()) {
            ImageIcon image = demoItems.get(key);
            Image smaller = image.getImage().getScaledInstance(200,200,Image.SCALE_SMOOTH);
            ImageIcon smallIcon = new ImageIcon(smaller);
            constraints.gridy = index + 1;
            constraints.gridx = 0;
            JLabel productTitle = new JLabel(key);
            JLabel productImage = new JLabel(smallIcon);
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

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("DistributedShopping.com");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        Shopping shop = new Shopping();
        shop.addComponentsToPane(panel, frame);
        frame.pack();
        frame.setVisible(true);
    }
}
