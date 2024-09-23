import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stock{
    String productSKU;
    int quantityAvailable,quantityRequested;

    public Stock(String productSKU, int quantityAvailable, int quantityRequested) {
        this.productSKU = productSKU;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequested = quantityRequested;
    }

    public Stock(String productSKU, int quantityAvailable) {
        this.productSKU = productSKU;
        this.quantityAvailable = quantityAvailable;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    //done by fattsiong, for creating an array list for current available stock in branches
    public static List<Stock> createCurrentStockArrayList(String warehouseID) {
        List<Stock> currentStock = new ArrayList<>();

        String fileName = "aux_files/branchStock_txt/" + warehouseID + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip the header line

            while ((line = br.readLine()) != null) {
                String[] details = line.split("\\|");

                // Assuming the file contains productSKU and quantity
                String productSKU = details[0];
                int quantity = Integer.parseInt(details[1]);


                Stock stocks = new Stock(productSKU, quantity);

                // Add to the list
                currentStock.add(stocks);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "File not found.");
        }
        return currentStock;
    }

    public static void updateBranchStockFile(StockRequest currentStockRequest, Branch currentBranch) {
        String warehouseID = currentBranch.getId();// Get the warehouse ID from the current branch
        String storeID = currentStockRequest.getStoreID();// Get the store ID from the current stock request
        String warehouseFilePath = "aux_files/branchStock_txt/" + warehouseID + ".txt";
        String storeFilePath = "aux_files/branchStock_txt/" + storeID + ".txt";
        StringBuilder warehouseFileContent = new StringBuilder();
        StringBuilder storeFileContent = new StringBuilder();

        Map<String, Integer> requestedProducts = StockRequest.getRequestedProducts(currentStockRequest.getRequestID());

        // Read warehouse stock data and update it
        try (BufferedReader reader = new BufferedReader(new FileReader(warehouseFilePath))) {
            String line = reader.readLine(); // Read header line if necessary
            warehouseFileContent.append(line).append("\n"); // Keep header

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split("\\|");
                String sku = stockData[0]; // SKU of the product
                int availableStock = Integer.parseInt(stockData[1]);

                if (requestedProducts.containsKey(sku)) {
                    // Update stock if the request was approved
                    int requestedQty = requestedProducts.get(sku);
                    if (currentStockRequest.getRequestStatus().equals("Approved")) {
                        availableStock -= requestedQty; // Add requested quantity
                    }
                }

                // Append updated line
                warehouseFileContent.append(sku).append("|").append(availableStock).append("\n");
            }

        } catch (IOException ex) {
            System.err.println("Error reading stock data: " + ex.getMessage());
        }

        // Read store stock data and update it
        try (BufferedReader reader = new BufferedReader(new FileReader(storeFilePath))) {
            String line = reader.readLine(); // Read header line if necessary
            storeFileContent.append(line).append("\n"); // Keep header

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split("\\|");
                String sku = stockData[0]; // SKU of the product
                int availableStock = Integer.parseInt(stockData[1]);

                if (requestedProducts.containsKey(sku)) {
                    // Update stock if the request was approved
                    int requestedQty = requestedProducts.get(sku);
                    if (currentStockRequest.getRequestStatus().equals("Approved")) {
                        availableStock += requestedQty; // Add requested quantity
                    }
                }

                // Append updated line
                storeFileContent.append(sku).append("|").append(availableStock).append("\n");
            }

        } catch (IOException ex) {
            System.err.println("Error reading stock data: " + ex.getMessage());
        }

        // Write the updated content back to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(warehouseFilePath)) {
            writer.write(warehouseFileContent.toString());
        } catch (IOException ex) {
            System.err.println("Error writing updated stock data: " + ex.getMessage());
        }

        // Write the updated content back to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(storeFilePath)) {
            writer.write(storeFileContent.toString());
        } catch (IOException ex) {
            System.err.println("Error writing updated stock data: " + ex.getMessage());
        }



    }

}
