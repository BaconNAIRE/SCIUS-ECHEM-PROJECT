public class Normalizer {
    
    private float[] normalizedVoltage;
    private float[] normalizedCurrent;
    
    public Normalizer(DataHandler data) {
        this.normalizedVoltage = new float[data.getDataPoints()];
        this.normalizedCurrent = new float[data.getDataPoints()];
        
        normalize(data);
    }
    
    private void normalize(DataHandler data) {
        System.out.println("Normalizing data...");
        
        float[] voltage = data.getVoltage();
        float[] current = data.getCurrent();
        int dataPoints = data.getDataPoints();
        
        float minV = data.getMinVoltage();
        float maxV = data.getMaxVoltage();
        float minI = data.getMinCurrent();
        float maxI = data.getMaxCurrent();
        
        float rangeV = maxV - minV;
        float rangeI = maxI - minI;
        
        // Avoid division by zero
        if (rangeV == 0) rangeV = 1.0f;
        if (rangeI == 0) rangeI = 1.0f;
        
        // Step 1: Normalize to [-1, 1]
        for (int i = 0; i < dataPoints; i++) {
            // Normalize voltage to [-1, 1]
            normalizedVoltage[i] = 2 * (voltage[i] - minV) / rangeV - 1;
            normalizedVoltage[i] = Math.max(-1, Math.min(1, normalizedVoltage[i])); // Clamp
            
            // Normalize current to [-1, 1]
            normalizedCurrent[i] = 2 * (current[i] - minI) / rangeI - 1;
            normalizedCurrent[i] = Math.max(-1, Math.min(1, normalizedCurrent[i])); // Clamp
        }
        
        // Step 2: Apply arccos transformation
        System.out.println("  Applying arccos transformation...");
        for (int i = 0; i < dataPoints; i++) {
            normalizedVoltage[i] = (float) Math.acos(normalizedVoltage[i]);
            normalizedCurrent[i] = (float) Math.acos(normalizedCurrent[i]);
        }
        
        System.out.println("  Normalization complete");
    }
    
    public float[] getNormalizedVoltage() {
        return normalizedVoltage;
    }
    
    public float[] getNormalizedCurrent() {
        return normalizedCurrent;
    }
}
