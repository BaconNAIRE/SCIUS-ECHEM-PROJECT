import java.io.File;

public class Main {
    
    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        String outputBaseDir = args[1];
        String substanceName = args[2];
        
        File inputFile = new File(inputPath);
        
        // Check if input is a folder or a file
        if (inputFile.isDirectory()) {
            // Process folder in bulk
            System.out.println();
            BulkProcessor bulkProcessor = new BulkProcessor(inputPath, outputBaseDir, substanceName);
            bulkProcessor.processBulk();
        } else if (inputFile.isFile()) {
            // Process single file
            System.out.println();
            VoltammogramToGAF converter = new VoltammogramToGAF(inputPath, outputBaseDir, substanceName);
            converter.process();
        } else {
            System.err.println("ERROR: Input path does not exist: " + inputPath);
            System.exit(1);
        }
    }
}

class VoltammogramToGAF {
    
    private String inputCsvPath;
    private String outputBaseDir;
    private String substanceName;
    
    public VoltammogramToGAF(String inputCsvPath, String outputBaseDir, String substanceName) {
        this.inputCsvPath = inputCsvPath;
        this.outputBaseDir = outputBaseDir;
        this.substanceName = substanceName;
    }
    
    public void process() throws Exception {
        System.out.println("=".repeat(70));
        System.out.println("VOLTAMMOGRAM TO GAF IMAGE CONVERTER");
        System.out.println("=".repeat(70));
        System.out.println();
        
        System.out.println("[1/5] Reading CSV file...");
        CSVReader csvReader = new CSVReader(inputCsvPath);
        DataHandler voltammogramData = csvReader.readCSV();
        voltammogramData.printDataInfo();
        System.out.println();
        
        System.out.println("[2/5] Normalizing data...");
        Normalizer normalizer = new Normalizer(voltammogramData);
        System.out.println();
        
        System.out.println("[3/5] Generating GAF image...");
        GramianImageBuilder builder = new GramianImageBuilder(
            normalizer.getNormalizedVoltage(),
            normalizer.getNormalizedCurrent(),
            GramConfig.IMAGE_SIZE
        );
        byte[] imageData = builder.generateGAFImage();
        System.out.println();
        
        System.out.println("[4/5] Determining output path (Train/Test/Validate split)...");
        String outputPath = determineOutputPath(imageData);
        System.out.println();
        
        System.out.println("[5/5] Saving GAF image...");
        ImageWriter.savePNG(imageData, outputPath, GramConfig.IMAGE_SIZE);
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("CONVERSION COMPLETE!");
        System.out.println("Output: " + outputPath);
        System.out.println("=".repeat(70));
    }
    
    private String determineOutputPath(byte[] imageData) {
        File inputFile = new File(inputCsvPath);
        String csvFileName = inputFile.getName();
        String filenameWithoutExtension = csvFileName.substring(0, csvFileName.lastIndexOf('.'));
        
        String gafFileName = "GAF_" + filenameWithoutExtension + ".png";
        
        String fullPath;
        
        if (GramConfig.ENABLE_AUTO_SPLIT) {
            // Auto-split into Train/Test/Validate folders
            float randomVal = (float) Math.random();
            String folder;
            
            if (randomVal < GramConfig.TRAIN_FRACTION) {
                folder = "Train";
            } else if (randomVal < (GramConfig.TRAIN_FRACTION + GramConfig.TEST_FRACTION)) {
                folder = "Test";
            } else {
                folder = "Validate";
            }
            
            fullPath = outputBaseDir + File.separator + 
                             substanceName + File.separator + 
                             folder + File.separator + 
                             gafFileName;
            
            System.out.println("  Folder: " + folder + " (" + String.format("%.1f%%", randomVal * 100) + ")");
            System.out.println("  Filename: " + gafFileName);
        } else {
            // Save to single folder without splitting
            fullPath = outputBaseDir + File.separator + 
                             substanceName + File.separator + 
                             gafFileName;
            
            System.out.println("  Folder: " + substanceName + " (no auto-split)");
            System.out.println("  Filename: " + gafFileName);
        }
        
        return fullPath;
    }
}


class BulkProcessor {
    
    private String folderPath;
    private String outputBaseDir;
    private String substanceName;
    private java.util.List<File> csvFiles;
    
    public BulkProcessor(String folderPath, String outputBaseDir, String substanceName) {
        this.folderPath = folderPath;
        this.outputBaseDir = outputBaseDir;
        this.substanceName = substanceName;
        this.csvFiles = new java.util.ArrayList<>();
    }
    
    /**
     * Find all CSV files in the folder
     */
    private void findCSVFiles() {
        File folder = new File(folderPath);
        
        if (!folder.exists()) {
            System.err.println("ERROR: Folder does not exist: " + folderPath);
            return;
        }
        
        if (!folder.isDirectory()) {
            System.err.println("ERROR: Path is not a folder: " + folderPath);
            return;
        }
        
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        
        if (files == null || files.length == 0) {
            System.out.println("No CSV files found in: " + folderPath);
            return;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                csvFiles.add(file);
            }
        }
    }
    
    /**
     * Process all CSV files found in the folder
     */
    public void processBulk() throws Exception {
        // Find CSV files
        System.out.println("Scanning folder: " + folderPath);
        findCSVFiles();
        
        if (csvFiles.isEmpty()) {
            System.out.println("No CSV files found to process.");
            return;
        }
        
        System.out.println("Found " + csvFiles.size() + " CSV file(s) to process");
        System.out.println();
        
        // Process each file
        int successCount = 0;
        int errorCount = 0;
        
        for (int i = 0; i < csvFiles.size(); i++) {
            File csvFile = csvFiles.get(i);
            String filename = csvFile.getName();
            
            System.out.println("Processing file " + (i + 1) + "/" + csvFiles.size() + ": " + filename);
            
            try {
                // Process single file
                VoltammogramToGAF converter = new VoltammogramToGAF(
                    csvFile.getAbsolutePath(),
                    outputBaseDir,
                    substanceName
                );
                converter.process();
                successCount++;
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("ERROR processing " + filename + ": " + e.getMessage());
                e.printStackTrace();
                errorCount++;
                System.out.println();
            }
        }
        
        // Summary
        System.out.println("=".repeat(70));
        System.out.println("BULK PROCESSING COMPLETE!");
        System.out.println("=".repeat(70));
        System.out.println("Total files processed: " + csvFiles.size());
        System.out.println("Successful: " + successCount);
        System.out.println("Failed: " + errorCount);
        System.out.println("=".repeat(70));
    }
}
