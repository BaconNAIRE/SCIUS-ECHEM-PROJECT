public class GramConfig {
    
    // Image settings
    public static final int IMAGE_SIZE = 224;
    
    // Data split options (only used if ENABLE_AUTO_SPLIT is true)
    public static final boolean ENABLE_AUTO_SPLIT = false;
    public static final float TRAIN_FRACTION = 0.60f;
    public static final float TEST_FRACTION = 0.20f;
    public static final float VALIDATE_FRACTION = 0.20f;
    
    // CSV Headers
    public static final String VOLTAGE_HEADER = "Potential (V)";
    public static final String CURRENT_HEADER = "Current (A)";
    public static final String CSV_DELIMITER = ",";
    
    // Logging
    public static final boolean VERBOSE = true;
}
