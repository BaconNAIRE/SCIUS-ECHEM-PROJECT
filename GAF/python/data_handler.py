import numpy as np

class DataHandler:
    def __init__(self, voltage, current):
        self.voltage = np.asarray(voltage, dtype=np.float32)
        self.current = np.asarray(current, dtype=np.float32)
        self.dataPoints = len(self.voltage)

        self._calculateMinMax()
    
    def _calculateMinMax(self):
        self.maxVoltage = float(np.max(self.voltage))
        self.minVoltage = float(np.min(self.voltage))
        self.maxCurrent = float(np.max(self.current))
        self.minCurrent = float(np.min(self.current))
    
    def getVoltage(self):
        return self.voltage
    
    def getCurrent(self):
        return self.current
    
    def getDataPoints(self):
        return self.dataPoints
    
    def getMaxVoltage(self):
        return self.maxVoltage
    
    def getMinVoltage(self):
        return self.minVoltage
    
    def getMaxCurrent(self):
        return self.maxCurrent
    
    def getMinCurrent(self):
        return self.minCurrent
    
    def printDataInfo(self):
        print(f"  Data Points: {self.dataPoints}")
        print(f"  Voltage Range: [{self.minVoltage} , {self.maxVoltage}]")
        print(f"  Current Range: [{self.minCurrent} , {self.maxCurrent}]")