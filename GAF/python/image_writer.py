import os
from gram_config import GramConfig
from PIL import Image

class ImageWriter:
    @staticmethod
    def savePNG(image_data, output_path, image_size):
        """
         Args:
            image_data: Numpy array of image
            output_path: Path where to save the PNG file
            image_size: Size of the image
        """
        print(f"Saving PNG image to: {output_path}")

        try:
            # Create parent directory if they don't exist
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            image = Image.fromarray(image_data, mode='RGB')
            image.save(output_path, GramConfig.OUTPUT_FILE_TYPE)

            print("Image saved successfully")
            
        except Exception as e:
            raise IOError(f"Failed to write {GramConfig.OUTPUT_FILE_TYPE} image: {e}")
