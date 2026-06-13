import numpy as np
from scipy import interpolate
from gram_config import GramConfig

class GramImageBuilder():

    def __init__(self, normalized_voltage, normalized_current, image_size):
        """
        Args:
            normalized_voltage: Normalized voltage array
            normalized_current: Normalized current array
            image_size: Size of output image (e.g., 224 for 224x224)
        """
        self.normalized_voltage = normalized_voltage
        self.normalized_current = normalized_current
        self.image_size = image_size
        self.image_data = None

    def generate_gaf_image(self):
        print(f"Generating GAF image ({self.image_size}x{self.image_size})...")

        resampled_voltage = self._resample_data(self.normalized_voltage, self.image_size)
        resampled_current = self._resample_data(self.normalized_current, self.image_size)

        # Create template image array
        image = np.zeros((self.image_size, self.image_size, 3), dtype=np.uint8)

        for i in range(self.image_size):
            for j in range(self.image_size):
                """
                  Calculate RGB values using Gramian Angular Field formulas
                  r: cos(V_i + V_j) + 1
                  g: cos(I_i + I_j) + 1
                  b: sin(I_i - I_j) + 1
                """

                r = np.cos(resampled_voltage[i] + resampled_voltage[j]) + 1
                g = np.cos(resampled_current[i] + resampled_current[j]) + 1
                b = np.sin(resampled_current[i] - resampled_current[j]) + 1

                image[i, j, 0] = int(np.round(255 * r / 2))
                image[i, j, 1] = int(np.round(255 * g / 2))
                image[i, j, 2] = int(np.round(255 * b / 2))


        self.image_data = image
        print("  GAF image generated successfully")
        return image
    

    def _resample_data(self, data, target_size):
        if len(data) == target_size:
            return data
        
        # Create interpolation functions
        x_old = np.linspace(0, 1, len(data))
        x_new = np.linspace(0, 1, target_size)

        # Linear interpolation
        f = interpolate.interp1d(x_old, data, kind='linear')
        resampled = f(x_new)

        return resampled.astype(np.float32)
    
    def get_image_data(self):
        # Fetch the generated image array
        return self.image_data

