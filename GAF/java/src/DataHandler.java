public class DataHandler {
    
    private float[] voltage;  // Potential (V)
    private float[] current;  // Current (A)
    private int dataPoints;
    
    // Min/Max values for normalization
    private float maxVoltage;
    private float minVoltage;
    private float maxCurrent;
    private float minCurrent;
    
    public DataHandler(float[] voltage, float[] current) {
        this.voltage = voltage;
        this.current = current;
        this.dataPoints = voltage.length;
        
        // Calculate min/max values
        calculateMinMax();
    }
    
    private void calculateMinMax() {
        maxVoltage = Float.MIN_VALUE;
        minVoltage = Float.MAX_VALUE;
        maxCurrent = Float.MIN_VALUE;
        minCurrent = Float.MAX_VALUE;
        
        for (int i = 0; i < dataPoints; i++) {
            if (voltage[i] > maxVoltage) maxVoltage = voltage[i];
            if (voltage[i] < minVoltage) minVoltage = voltage[i];
            if (current[i] > maxCurrent) maxCurrent = current[i];
            if (current[i] < minCurrent) minCurrent = current[i];
        }
    }
    
    // Getters
    public float[] getVoltage() {
        return voltage;
    }
    
    public float[] getCurrent() {
        return current;
    }
    
    public int getDataPoints() {
        return dataPoints;
    }
    
    public float getMaxVoltage() {
        return maxVoltage;
    }
    
    public float getMinVoltage() {
        return minVoltage;
    }
    
    public float getMaxCurrent() {
        return maxCurrent;
    }
    
    public float getMinCurrent() {
        return minCurrent;
    }
    
    public void printDataInfo() {
        System.out.println("  Data Points: " + dataPoints);
        System.out.println("  Voltage Range: [" + minVoltage + ", " + maxVoltage + "]");
        System.out.println("  Current Range: [" + minCurrent + ", " + maxCurrent + "]");
    }
}
