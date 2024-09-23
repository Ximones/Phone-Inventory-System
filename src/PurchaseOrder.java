import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class PurchaseOrder {
    private String orderID;
    private String siteID;
    private String productSKU;
    private int quantity;
    private String orderStatus;
    private Date orderDate;

    public PurchaseOrder(String orderID,String siteID, String orderStatus, Date orderDate) {
        this.orderID = orderID;
        this.siteID = siteID;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }

    public PurchaseOrder(String productSKU, int quantity){
        this.productSKU = productSKU;
        this.quantity = quantity;
    }

    public static void displayOrderHistory(Menu menu, String siteID) {
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Order history for " + siteID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table setup
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Column headers
        model.addColumn("Select");
        model.addColumn("Order ID");
        model.addColumn("Supplier ID");
        model.addColumn("Status");
        model.addColumn("Order Date");

        // Read from file and populate table
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/order_txt/orderRequest.txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] orderData = line.split("\\|");

                if (orderData[1].equals(siteID)) {
                    model.addRow(new Object[]{
                            false, // Selection (radio button)
                            orderData[0], // Order ID
                            orderData[2], // Supplier ID
                            orderData[3], // Order Status
                            orderData[4]  // Order Date
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Radio button selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Update all radio buttons in the first column to false except the selected row
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (i != selectedRow) {
                            model.setValueAt(false, i, 0);
                        }
                    }
                }
            }
        });

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // View Details Button
        JButton viewDetailsButton = new JButton("View Order Details");
        buttonPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedOrderID = model.getValueAt(selectedRow, 1).toString();
                    frame.dispose();
                    showOrderDetails(menu, siteID, selectedOrderID);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an order to view details.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void showOrderDetails(Menu menu, String siteID, String orderID) {
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Order details for " + orderID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);



        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("No.");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Quantity");

        Map<String, String[]> productDetails = Branch.mapProductDetails();

        try(BufferedReader br = new BufferedReader(new FileReader("aux_files/order_txt/orderDetails.txt"))){
            String line = br.readLine(); //Skip the header line
            int numOfProduct = 0;
            while((line = br.readLine()) != null) {
                String[] orderDetails = line.split("\\|");
                if (orderDetails[0].equals(orderID)) {
                    String[] productInfo = productDetails.get(orderDetails[1]);
                    numOfProduct++;
                    if (productInfo != null) {
                        model.addRow(new Object[]{
                                numOfProduct, //product number
                                productInfo[0], //SKU
                                productInfo[1], //Model
                                productInfo[2], //RAM
                                productInfo[3], //ROM
                                productInfo[4], //Color
                                productInfo[5], //Price
                                productInfo[6], //Type
                                orderDetails[2], //Order Qty
                        });
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            PurchaseOrder.displayOrderHistory(menu, siteID);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void makeOrder(Menu menu, String siteID){
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Choose Phone Brand for Order");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 10));

        Dimension buttonSize = new Dimension(400, 40);
        Insets buttonMargin = new Insets(0, 25, 0, 25);

        String[] brandNames = { "POCO", "Samsung", "Xiaomi", "Apple", "Nothing" };

        // Loop through the buttons and assign the corresponding ActionListener
        for (int i = 0; i < brandNames.length; i++) {
            String brand = brandNames[i];

            JButton brandButton = new JButton(brand); // Create a button with the brand name
            brandButton.setPreferredSize(buttonSize); // Set custom button size

            // Simulate margins by wrapping the button in a JPanel with EmptyBorder
            JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonWrapper.setBorder(BorderFactory.createEmptyBorder(
                    buttonMargin.top, buttonMargin.left, buttonMargin.bottom, buttonMargin.right
            )); // Set the margin around the button
            buttonWrapper.add(brandButton); // Add the button to the wrapper

            // Add action listener dynamically based on brand
            brandButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose(); // Close the current Menu frame
                    displayProductForOrder(menu, siteID, brand); // Use the brand dynamically
                }
            });

            buttonPanel.add(buttonWrapper); // Add the wrapped button to the panel
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.add(backButton);
        bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH); // Add the combined panel to SOUTH

        frame.setVisible(true);
    }

    public static void displayProductForOrder(Menu menu, String siteID, String brand) {
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Make order for " + brand);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table setup
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("Select");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");

        String brandFileName = "aux_files/all_txt/" + brand + ".txt";

        ArrayList<Product> productList = DisplayAllSKU.readProductsFromFile(brandFileName);
        for (Product product : productList) {
            model.addRow(new Object[]{
                    false,
                    product.SKU,
                    product.Model,
                    product.RAM,
                    product.ROM,
                    product.Color,
                    product.Price,
                    product.Type
            });
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // View Details Button
        JButton continueOrderButton = new JButton("Continue Order");
        buttonPanel.add(continueOrderButton);

        continueOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> selectedSKUs = new ArrayList<>();

                // Loop through all rows and check if they are selected
                for (int i = 0; i < table.getRowCount(); i++) {
                    Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Check the checkbox column
                    if (isSelected != null && isSelected) {
                        String selectedSKU = model.getValueAt(i, 1).toString(); // Get the SKU of the selected row
                        selectedSKUs.add(selectedSKU); // Add the SKU to the list
                    }
                }

                if (!selectedSKUs.isEmpty()) {
                    frame.dispose(); // Close the current window
                    enterQuantityForSelectedStocks(menu, siteID, brand, selectedSKUs); // Pass the list of SKUs to the next method
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select at least one stock to continue.", "No Stock Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                makeOrder(menu, siteID);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void enterQuantityForSelectedStocks(Menu menu, String siteID, String brand, ArrayList<String> selectedSKUs){
        JFrame frame = new JFrame("Enter Stock Order Quantity");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Enter Quantity for selected stock");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table setup
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 8 ? Integer.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("No.");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Quantity");

        Map<String, String[]> productDetails = Branch.mapProductDetails();

        int rowCount = 1;
        for (String sku : selectedSKUs) {
            String[] productInfo = productDetails.get(sku);
            if (productInfo != null) {
                model.addRow(new Object[]{
                        rowCount++,
                        productInfo[0], //SKU
                        productInfo[1], //Model
                        productInfo[2], //RAM
                        productInfo[3], //ROM
                        productInfo[4], //Color
                        productInfo[5], //Price
                        productInfo[6], //Type
                        0 // Default Quantity is 0, user will enter the value
                });
            }
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Select Supplier Button
        JButton selectSupplierButton = new JButton("Confirm and Select Supplier");
        buttonPanel.add(selectSupplierButton);

        selectSupplierButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean allQuantitiesValid = true;
                HashMap<String, Integer> orderDetails = new HashMap<>();

                // Loop through all rows and validate the entered quantities
                for (int i = 0; i < table.getRowCount(); i++) {
                    String sku = model.getValueAt(i, 1).toString();
                    int quantity = (int) model.getValueAt(i, 8); // Get the quantity from the last column

                    if (quantity > 0) {
                        orderDetails.put(sku, quantity); // Add valid quantity to the map
                    } else {
                        allQuantitiesValid = false;
                        JOptionPane.showMessageDialog(frame, "Please enter a valid quantity for SKU: " + sku, "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                        break;
                    }
                }

                if (allQuantitiesValid) {
                    chooseSupplierForOrder(menu, siteID, brand, orderDetails);
                    frame.dispose(); // Close the window after successful order
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                displayProductForOrder(menu, siteID, brand);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void chooseSupplierForOrder(Menu menu, String siteID, String brand, HashMap<String, Integer> orderDetails) {
        JFrame frame = new JFrame("Select Supplier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Select a Supplier for your Order");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        model.addColumn("Select");
        model.addColumn("Supplier ID");
        model.addColumn("State");

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/person_txt/Person.txt"))) {
            String line = br.readLine(); //Skip the header line
            while ((line = br.readLine()) != null) {
                String[] supplierDetails = line.split("\\|");
                if (supplierDetails[3].equals(brand)) {
                    model.addRow(new Object[]{
                            false,
                            supplierDetails[1], //Supplier ID
                            supplierDetails[2] //state

                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Radio button selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Update all radio buttons in the first column to false except the selected row
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (i != selectedRow) {
                            model.setValueAt(false, i, 0);
                        }
                    }
                }
            }
        });


        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));


        // Select Supplier Button
        JButton confirmOrderButton = new JButton("Confirm Order");
        buttonPanel.add(confirmOrderButton);

        confirmOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedSupplierID = model.getValueAt(selectedRow, 1).toString();
                    JOptionPane.showMessageDialog(frame, "Order placed successfully!");
                    String newOrderID = getNextOrderID("aux_files/order_txt/orderDetails.txt");
                    addOrderToFile(newOrderID, selectedSupplierID, siteID, orderDetails);
                    frame.dispose();
                    menu.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a supplier to confirm order.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // View Details Button
        JButton viewDetailsButton = new JButton("View Supplier Details");
        buttonPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedSupplierID = model.getValueAt(selectedRow, 1).toString();
                    Supplier.displaySupplier(menu, siteID, brand, orderDetails, selectedSupplierID);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a supplier to view supplier details.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static String getNextOrderID(String fileName) {
        String lastOrderID = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String currentLine;

            // Loop through the file to find the last line
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.trim().isEmpty()) {
                    lastOrderID = currentLine.split("\\|")[0]; // Get OrderID from the first column
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If file is empty or no valid OrderID is found, return the first OrderID
        if (lastOrderID.isEmpty()) {
            return "O001";
        }

        // Extract the numeric part of the OrderID and increment it
        String orderNumberPart = lastOrderID.substring(1); // Get the numeric part (e.g., '001')
        int orderNumber = Integer.parseInt(orderNumberPart);
        orderNumber++; // Increment the numeric part

        // Format the new OrderID by keeping the 'O' prefix and ensuring it is three digits
        return String.format("O%03d", orderNumber);
    }

    public static void addOrderToFile(String orderID, String supplierID, String siteID, HashMap<String, Integer> orderDetails) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the formatter for DD/MM/YY format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the date to the desired pattern
        String formattedDate = currentDate.format(dateFormatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/order_txt/orderDetails.txt", true))) { // true to append to file
            for (Map.Entry<String, Integer> entry : orderDetails.entrySet()) {
                String sku = entry.getKey();
                int quantity = entry.getValue();

                // Write the order details in the format: OrderID|SKU|Qty
                writer.write(orderID + "|" + sku + "|" + quantity);
                writer.newLine(); // Add a new line after each entry
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/order_txt/orderRequest.txt", true))) { // true to append to file
                // Write the order details in the format: OrderID|SKU|Qty
                writer.write(orderID + "|" + siteID + "|" + supplierID + "|" + "Pending" + "|" + formattedDate);
                writer.newLine(); // Add a new line after each entry
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
