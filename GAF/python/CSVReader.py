import pandas as pd
from gram_config import GramConfig
from data_handler import DataHandler

class CSVReader:

    def __init__(self, file_path):
        self.file_path = file_path

    def read_csv(self):
        print(f"Reading CSV file: {self.file_path}")

        try:
            df = pd.read_csv(self.file_path, delimiter=GramConfig.CSV_DELIMITER)
        except Exception as e:
            raise Exception(f"Failed to read CSV file: {e}")
        
        # Validate headers
        voltage_header = GramConfig.VOLTAGE_HEADER
        current_header  = GramConfig.CURRENT_HEADER

        if voltage_header not in df.columns or current_header not in df.columns:
            raise Exception(
                f"CSV must contain '{voltage_header}' and '{current_header}' columns. "
                f"Found columns: {list(df.columns)}"
            )
        
        # Extract and validate data
        voltage_list = []
        current_list = []

        for idx, row in df.iterrows():
            try:
                voltage_value = float(row[voltage_header])
                current_value = float(row[current_header])
                
                voltage_list.append(voltage_value)
                current_list.append(current_value)
            except (ValueError, TypeError):
                print("Warning: Skipping row "+ str(idx+2))

        if not voltage_list:
            raise Exception("No valid data points found in CSV")
        
        print("  Successfully loaded " + str(len(voltage_list)) + " data points")
        
        return DataHandler(voltage_list, current_list)
