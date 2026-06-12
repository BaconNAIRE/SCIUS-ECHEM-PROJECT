import numpy as np

class Normalizer:

    def __init__ (self, data_handler):
        self.normalizedVoltage = None
        self.normalizedCurrent = None

        self.normalize(data_handler)
    
    def normalize(self, data_handler):
        print("Normalizing data...")

        voltage = data_handler.getVoltage()
        current = data_handler.getCurrent()
        dataPoints = data_handler.getDataPoints()

        minV = data_handler.getMinVoltage()
        maxV = data_handler.getMaxVoltage()
        minI = data_handler.getMinCurrent()
        maxI = data_handler.getMaxCurrent()

        rangeV = maxV - minV
        rangeI = maxI - minI

        # Avoid Division by zero
        if rangeV == 0: rangeV = 1.0
        if rangeI == 0: rangeI = 1.0

        # Normalize to [-1,1]
        normalizedVoltage = 2 * (voltage - minV) / rangeV - 1
        normalizedVoltage = np.clip(normalizedVoltage, -1.0, 1.0)

        normalizedCurrent = 2 * (current - minI) / rangeI - 1
        normalizedCurrent = np.clip(normalizedCurrent, -1.0, 1.0)

        print(" Applying arccos transformation...")
        self.normalizedVoltage = np.arccos(normalizedVoltage).astype(np.float32)
        self.normalizedCurrent = np.arccos(normalizedCurrent).astype(np.float32)

    def getNormalizedVoltage(self):
        return self.normalizedVoltage
    
    def getNormalizedCurrent(self):
        return self.normalizedCurrent

