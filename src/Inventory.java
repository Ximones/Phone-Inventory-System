import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Inventory {
    Branch[] branches = new Branch[20];
    private static int branchCount;

    public Inventory(){
        branchCount = 0;
    }

    public static int getBranchCount() {
        return branchCount;
    }

    public static void setBranchCount(int branchCount) {
        Inventory.branchCount = branchCount;
    }

    public Branch[] createBranchArray() {
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/branch_txt/branch.txt"))) {
            String line = br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if(data[0].startsWith("W")){
                    branches[branchCount++] = new Warehouse(data[0],data[1],data[2],data[3]);
                }else{
                    branches[branchCount++] = new Store(data[0],data[1],data[2],data[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public Branch createCurrentBranchObject(String siteID) {
        Branch currentBranch = null;
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/branch_txt/branch.txt"))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if(data[0].equals(siteID)){
                    if(data[0].startsWith("W")) {
                        currentBranch = new Warehouse(data[0], data[1], data[2], data[3]);
                    }else{
                        currentBranch = new Store(data[0], data[1], data[2], data[3]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentBranch;
    }
}
