public class GramianImageBuilder {
    
    private float[] normalizedVoltage;
    private float[] normalizedCurrent;
    private int imageSize;
    private byte[] imageData;
    
    public GramianImageBuilder(float[] normalizedVoltage, float[] normalizedCurrent, int imageSize) {
        this.normalizedVoltage = normalizedVoltage;
        this.normalizedCurrent = normalizedCurrent;
        this.imageSize = imageSize;
        this.imageData = new byte[imageSize * imageSize * 3]; // RGB
    }
    
    public byte[] generateGAFImage() {
        System.out.println("Generating GAF image (" + imageSize + "x" + imageSize + ")...");
        
        int dataPoints = normalizedVoltage.length;
        
        // Resample or pad data to fit image size
        float[] resampledVoltage = resampleData(normalizedVoltage, imageSize);
        float[] resampledCurrent = resampleData(normalizedCurrent, imageSize);
        
        // Generate image pixel by pixel
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                // Calculate RGB values using Gramian Angular Field formulas
                // r: cos(V_i + V_j) + 1
                // g: cos(I_i + I_j) + 1
                // b: sin(I_i - I_j) + 1
                
                float r = (float) Math.cos(resampledVoltage[i] + resampledVoltage[j]) + 1;
                float g = (float) Math.cos(resampledCurrent[i] + resampledCurrent[j]) + 1;
                float b = (float) Math.sin(resampledCurrent[i] - resampledCurrent[j]) + 1;
                
                // Convert to 0-255 range
                byte rByte = (byte) Math.round((255 * r / 2));
                byte gByte = (byte) Math.round((255 * g / 2));
                byte bByte = (byte) Math.round((255 * b / 2));
                
                // Store in byte array (RGB interleaved)
                int pixelIndex = (i * imageSize + j) * 3;
                imageData[pixelIndex] = rByte;           // Red
                imageData[pixelIndex + 1] = gByte;       // Green
                imageData[pixelIndex + 2] = bByte;       // Blue
            }
        }
        
        System.out.println("  GAF image generated successfully");
        return imageData;
    }
    
    private float[] resampleData(float[] data, int targetSize) {
        float[] resampled = new float[targetSize];
        
        if (data.length == targetSize) {
            System.arraycopy(data, 0, resampled, 0, targetSize);
            return resampled;
        }
        
        // Linear interpolation for resampling
        float ratio = (float) (data.length - 1) / (targetSize - 1);
        
        for (int i = 0; i < targetSize; i++) {
            float position = i * ratio;
            int index = (int) position;
            float fraction = position - index;
            
            if (index >= data.length - 1) {
                resampled[i] = data[data.length - 1];
            } else {
                // Linear interpolation
                resampled[i] = data[index] * (1 - fraction) + data[index + 1] * fraction;
            }
        }
        
        return resampled;
    }
    
    public byte[] getImageData() {
        return imageData;
    }
}
