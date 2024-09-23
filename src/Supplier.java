import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Supplier extends Person {
    private String state;
    private String productBrand;

    public Supplier(String id, String state, String productBrand, String name, String email, String birthDay, String phoneNum) {
        super(id, name, email, birthDay, phoneNum);
        this.state = state;
        this.productBrand = productBrand;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public static void displaySupplier(Menu menu, String siteID, String brand, HashMap<String, Integer> orderDetails, String supplierID) {
        Map<String, Supplier> suppliers = readSuppliers(); // Get the map of suppliers
        Supplier supplier = suppliers.get(supplierID); // Retrieve the supplier by ID
        if (supplier != null) {
            JFrame frame = new JFrame("Supplier Details");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);

            // Header image
            ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBounds(0, 0, frame.getWidth(), 200);
            frame.add(imageLabel);

            // Info Panel with editable fields
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns, spacing
            infoPanel.setBounds(250, 250, 300, 200); // Adjust as needed

            // Labels for displaying supplier information
            infoPanel.add(new JLabel("Supplier ID:"));
            infoPanel.add(new JLabel(supplier.getId()));

            infoPanel.add(new JLabel("Name:"));
            infoPanel.add(new JLabel(supplier.getName()));

            infoPanel.add(new JLabel("Email:"));
            infoPanel.add(new JLabel(supplier.getEmail()));

            infoPanel.add(new JLabel("Date Added:"));
            infoPanel.add(new JLabel(supplier.getBirthDay()));

            infoPanel.add(new JLabel("Phone:"));
            infoPanel.add(new JLabel(supplier.getPhoneNum()));

            infoPanel.add(new JLabel("State:"));
            infoPanel.add(new JLabel(supplier.getState()));

            frame.add(infoPanel);

            JButton backButton = new JButton("Back");
            backButton.setBounds(350, 500, 100, 30);
            frame.add(backButton);

            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    PurchaseOrder.chooseSupplierForOrder(menu, siteID, brand, orderDetails);
                }
            });

            frame.setVisible(true);
        }

    }

    public static Map<String, Supplier> readSuppliers() {
        Map<String, Supplier> supplierMap = new HashMap<>();
        String filePath = "aux_files/person_txt/Person.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data[0].equals("Supplier")) {
                    Supplier supplier = new Supplier(data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                    supplierMap.put(supplier.getId(), supplier);
                }
            }
        } catch (IOException ex) {
            System.err.println("Error reading supplier data: " + ex.getMessage());
        }
        return supplierMap;
    }
}