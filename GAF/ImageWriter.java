import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageWriter {
    
    public static void savePNG(byte[] imageData, String outputPath, int imageSize) throws IOException {
        System.out.println("Saving PNG image to: " + outputPath);
        
        // Create DataBuffer from byte array
        DataBuffer buffer = new DataBufferByte(imageData, imageData.length);
        
        // Create WritableRaster with RGB data
        WritableRaster raster = Raster.createInterleavedRaster(
            buffer, 
            imageSize, 
            imageSize, 
            3 * imageSize,  // scanline stride
            3,              // pixel stride
            new int[]{0, 1, 2},  // band offsets for R, G, B
            null
        );
        
        // Create ColorModel
        ColorModel colorModel = new ComponentColorModel(
            ColorModel.getRGBdefault().getColorSpace(),
            false,
            true,
            Transparency.OPAQUE,
            DataBuffer.TYPE_BYTE
        );
        
        // Create BufferedImage
        BufferedImage image = new BufferedImage(colorModel, raster, true, null);
        
        // Ensure directory exists
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();
        
        // Write PNG
        boolean success = ImageIO.write(image, "png", outputFile);
        
        if (success) {
            System.out.println("  PNG image saved successfully");
        } else {
            throw new IOException("Failed to write PNG image");
        }
    }
}
