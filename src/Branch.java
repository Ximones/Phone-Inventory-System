import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Branch {
    JFrame frame;

    private String id;
    private String name;
    private String address;
    private String state;

    public Branch(String id, String name, String address, String state) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.state = state;
    }

    public Branch(Menu menu, Staff loggedInStaff) {
        if (loggedInStaff.getSiteID().charAt(0) == 'W') {
            frame = new JFrame("Warehouse Stock");
        } else {
            frame = new JFrame("Store Stock");
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        // Header Image
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Stock list for " + loggedInStaff.getSiteID());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Amount");

        Map<String, String[]> productDetails = Branch.mapProductDetails();

        String siteFileName = "aux_files/branchStock_txt/" + loggedInStaff.getSiteID() + ".txt";
        // Read and populate table data
        try (BufferedReader br = new BufferedReader(new FileReader(siteFileName))) {
            String line = br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String[] productInfo = productDetails.get(data[0]);
                if (productInfo != null) {
                    model.addRow(new Object[]{data[0], productInfo[1], productInfo[2], productInfo[3], productInfo[4], productInfo[5], productInfo[6], data[1]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading warehouse stock data.");
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void ReportMenu(Menu menu, Staff loggedInStaff) {
        JFrame frame = new JFrame("Report");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(0, 0, frame.getWidth(), 200);
        frame.add(imageLabel);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel(loggedInStaff.getSiteID() + " " + getSiteType(loggedInStaff.getSiteID()));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        headerPanel.setBounds(0, 210, frame.getWidth(), 30);
        frame.add(headerPanel);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(4, 2, 10, 10));

        // Brand Selection
        JLabel brandLabel = new JLabel("Select Brand:");
        JComboBox<String> brandComboBox = new JComboBox<>(new String[]{"All", "Apple", "Samsung", "Nothing", "Xiaomi", "POCO"});
        selectionPanel.add(brandLabel);
        selectionPanel.add(brandComboBox);

        // Report Type Selection
        JLabel reportLabel = new JLabel("Select Report Type:");
        JComboBox<String> reportComboBox = new JComboBox<>(new String[]{"Current Stock", "Low Stock", "Stock Value"});
        selectionPanel.add(reportLabel);
        selectionPanel.add(reportComboBox);

        selectionPanel.setBounds(50, 230, 700, 150); // Adjust position and size as needed
        frame.add(selectionPanel);

        JButton generateButton = new JButton("Generate Report");
        generateButton.setBounds(300, 450, 200, 30); // Adjust position and size as needed
        frame.add(generateButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(300, 500, 200, 30); // Adjust position and size as needed
        frame.add(backButton);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBrand = (String) brandComboBox.getSelectedItem();
                String selectedReportType = (String) reportComboBox.getSelectedItem();
                frame.dispose();
                generateReport(menu, loggedInStaff, selectedBrand, selectedReportType);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    private static String getSiteType(String siteID) {
        if (siteID.startsWith("W")) {
            return "Warehouse";
        } else if (siteID.startsWith("S")) {
            return "Store";
        } else {
            return "Unknown";
        }
    }

    private static void generateReport(Menu menu, Staff loggedInStaff, String brand, String reportType) {
        JFrame frame = new JFrame("Report");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel(loggedInStaff.getSiteID() + " " + getSiteType(loggedInStaff.getSiteID()) + " " + reportType + " Report");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Amount");

        if (reportType.equals("Stock Value")) {
            model.addColumn("Stock Value");  // Add a column for stock value
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        double totalStockValue = 0.0;

        // Read and populate table data based on brand and report type
        Map<String, String[]> productDetails = new HashMap<>(mapProductDetails(brand));

        String siteFileName = "aux_files/branchStock_txt/" + loggedInStaff.getSiteID() + ".txt";

        // Read and populate table data
        try (BufferedReader br = new BufferedReader(new FileReader(siteFileName))) {
            String line = br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String[] productInfo = productDetails.get(data[0]);
                if (productInfo != null) {
                    int quantity = Integer.parseInt(data[1]);
                    double price = Double.parseDouble(productInfo[5]);

                    if (reportType.equals("Low Stock") && quantity >= 10) {
                        continue; // Skip items with quantity 10 or more
                    }
                    if (reportType.equals("Stock Value")) {
                        double stockValue = price * quantity;
                        totalStockValue += stockValue;
                        model.addRow(new Object[]{productInfo[0], productInfo[1], productInfo[2], productInfo[3], productInfo[4], decimalFormat.format(price), productInfo[6], quantity, decimalFormat.format(stockValue)});
                    } else {
                        model.addRow(new Object[]{productInfo[0], productInfo[1], productInfo[2], productInfo[3], productInfo[4], decimalFormat.format(price), productInfo[6], quantity});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading warehouse stock data.");
        }

        if (reportType.equals("Stock Value")) {
            model.addRow(new Object[]{"", "", "", "", "", "", "", "Total", decimalFormat.format(totalStockValue)});

            TableCellRenderer totalRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == model.getRowCount() - 1) {
                        component.setFont(component.getFont().deriveFont(Font.BOLD));
                    }
                    return component;
                }
            };

            for (int i = 0; i < model.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(totalRenderer);
            }
        }

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Branch.ReportMenu(menu, loggedInStaff);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static Map<String, String[]> mapProductDetails(String... brands) {
        Map<String, String[]> phoneDetails = new HashMap<>();

        // If not passing any brands it will display all the phone
        if (brands.length == 0 || (brands.length == 1 && brands[0].equals("All"))) {
            brands = new String[]{"POCO", "Apple", "Xiaomi", "Samsung", "Nothing"};
        }

        for (String brand : brands) {
            String fileName = "aux_files/all_txt/" + brand + ".txt";
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line = br.readLine();//skip the header line

                while ((line = br.readLine()) != null) {
                    String[] details = line.split("\\|");
                    phoneDetails.put(details[0], details);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Phone file not founded");
            }
        }

        return phoneDetails;
    }

    public static Branch matchWorkingBranch(Staff loggedInStaff){
        Inventory inventory = new Inventory();
        Branch[] branches = inventory.createBranchArray();

        for (Branch branch : branches) {
            if (branch != null) {
                Branch curentWorkingBranch = (Branch) branch;

                // Check if the entered ID and password match
                if (curentWorkingBranch.getId().equals(loggedInStaff.getSiteID())) {
                    return curentWorkingBranch;
                }
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}