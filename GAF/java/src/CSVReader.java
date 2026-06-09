import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    
    private String filePath;
    
    public CSVReader(String filePath) {
        this.filePath = filePath;
    }
    
    public DataHandler readCSV() throws Exception {
        System.out.println("Reading CSV file: " + filePath);
        
        List<Float> voltageList = new ArrayList<>();
        List<Float> currentList = new ArrayList<>();
        
        int voltageIndex = -1;
        int currentIndex = -1;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(GramConfig.CSV_DELIMITER);
                
                if (isHeader) {
                    // Find column indices
                    for (int i = 0; i < values.length; i++) {
                        String header = values[i].trim();
                        if (header.equals(GramConfig.VOLTAGE_HEADER)) {
                            voltageIndex = i;
                        } else if (header.equals(GramConfig.CURRENT_HEADER)) {
                            currentIndex = i;
                        }
                    }
                    
                    if (voltageIndex == -1 || currentIndex == -1) {
                        throw new Exception("CSV must contain '" + GramConfig.VOLTAGE_HEADER + 
                                          "' and '" + GramConfig.CURRENT_HEADER + "' columns");
                    }
                    isHeader = false;
                    System.out.println("  Headers found: V at column " + voltageIndex + 
                                     ", I at column " + currentIndex);
                    continue;
                }
                
                try {
                    float voltage = Float.parseFloat(values[voltageIndex].trim());
                    float current = Float.parseFloat(values[currentIndex].trim());
                    
                    voltageList.add(voltage);
                    currentList.add(current);
                } catch (NumberFormatException e) {
                    System.out.println("  Warning: Skipping invalid row: " + line);
                }
            }
        }
        
        if (voltageList.isEmpty()) {
            throw new Exception("No valid data points found in CSV");
        }
        
        // Convert to arrays
        float[] voltageArray = new float[voltageList.size()];
        float[] currentArray = new float[currentList.size()];
        
        for (int i = 0; i < voltageList.size(); i++) {
            voltageArray[i] = voltageList.get(i);
            currentArray[i] = currentList.get(i);
        }
        
        System.out.println("  Successfully loaded " + voltageArray.length + " data points");
        
        return new DataHandler(voltageArray, currentArray);
    }
}
