import os
import sys
import random
from pathlib import Path

from gram_config import GramConfig
from CSVReader import CSVReader
from normalizer import Normalizer
from gram_image_builder import GramImageBuilder
from image_writer import ImageWriter


class VoltammotramConverter:
    def __init__ (self, input_path, output_path, substance_name):
        """     
        Args:
            input_path: Path to CSV file or folder with CSV files
            output_directory: Base directory for output
            substance_name: Name of the substance
        """

        self.input_path = input_path
        self.output_path = output_path
        self.substance_name = substance_name
        self.csv_file_list = []

    def run(self):
        # Detect input type and process accordingly

        input_file = Path(self.input_path)

        if input_file.is_file():
            # Single file processing
            self._process_single_file(str(input_file))

        elif input_file.is_dir():
            # Folder processing
            self._process_multi_files(str(input_file))
        
        else:
            print(f"Error: PATH does not exist: {self.input_path}")

    def _process_single_file(self, file_path):
        try:
            # Step 1: Read CSV
            print("[1/5] Reading CSV file...")
            csv_reader = CSVReader(file_path)
            voltammogram_data = csv_reader.read_csv()
            voltammogram_data.printDataInfo()
            print()
            
            # Step 2: Normalize data
            print("[2/5] Normalizing data...")
            normalizer = Normalizer(voltammogram_data)
            print()
            
            # Step 3: Generate GAF image
            print("[3/5] Generating GAF image...")
            image_builder = GramImageBuilder(
                normalizer.getNormalizedVoltage(),
                normalizer.getNormalizedCurrent(),
                GramConfig.IMAGE_SIZE
            )
            image_array = image_builder.generate_gaf_image()
            print()
            
            # Step 4: Determine output path
            print("[4/5] Determining output path (Train/Test/Validate split)...")
            output_file_path = self._determine_output_path(file_path)
            print()
            
            # Step 5: Save image
            print("[5/5] Saving GAF image...")
            ImageWriter.savePNG(image_array, output_file_path, GramConfig.IMAGE_SIZE)
            print()
            
            print("=" * 70)
            print("CONVERSION COMPLETE!")
            print("Output: " + output_file_path)
            print("=" * 70)
        
        except Exception as e:
            print("ERROR: " + str(e))

    def _process_multi_files(self, folder_path):
        # Find all CSV files
        print("Scanning folder: " + folder_path)
        folder = Path(folder_path)

        if not folder:
            print("ERROR: Path is not a folder: " + folder_path)

        csv_files = sorted(folder.glob('*.csv'))

        if not csv_files:
            print("Error: No CSV files found in" + folder_path)
            return

        print("Found " + str(len(csv_files)) + " CSV file(s) to process")
        print()

        # Processing each file
        success_count = 0
        error_count = 0

        for i, csv_file in enumerate(csv_files, 1):
            fileName = csv_file.name

            try:
                self._process_single_file(str(csv_file))
                success_count += 1
            except Exception as e:
                print("Error processing " + fileName + ": " + str(e))
                error_count += 1
                print()

        print("=" * 70)
        print("BATCH PROCESSING COMPLETE!")
        print("=" * 70)
        print("Successful: " + str(success_count))
        print("Failed: " + str(error_count))
        print("=" * 70)

    def _determine_output_path(self, csv_file_path):
        csv_filename = os.path.basename(csv_file_path)
        filename_without_extension = os.path.splitext(csv_filename)[0]

        output_filename = "GAF_" + filename_without_extension + f".{GramConfig.OUTPUT_FILE_TYPE}"

        if GramConfig.ENABLE_AUTO_SPLIT:
            # Auto split into Train/Test/Eval folders
            random_value = random.random()

            if random_value < GramConfig.TRAIN_FRACTION:
                folder_name = "TRAIN"
            elif random_value < (GramConfig.TRAIN_FRACTION + GramConfig.TEST_FRACTION):
                folder_name = "TEST"
            else:
                folder_name = "VALIDATE"

        
            full_path = os.path.join(
                self.output_path,
                self.substance_name,
                folder_name,
                output_filename
            )

            print("  Folder: " + folder_name + " (" + str(random_value * 100)[:4] + '%)')
            print("  Filename:" + output_filename)

        else:
             # Save to single folder without splitting
            full_path = os.path.join(
                self.output_path,
                self.substance_name,
                output_filename
            )
            
            print("  Folder: " + self.substance_name + " (no auto-split)")
            print("  Filename: " + output_filename)
        
        return full_path
    
def print_usage():
    # Print usage instructions
    print("Voltammogram to GAF Image Converter")
    print()
    print("Usage: python main.py <input_path> <output_directory> <substance_name>")
    print()
    print("Arguments:")
    print("  input_path         - Path to CSV file OR folder with CSV files")
    print("  output_directory   - Directory where GAF images will be saved")
    print("  substance_name     - Name of the substance (for folder organization)")
    print()
    print("Examples:")
    print("  Single file:  python main.py data.csv ./output MySubstance")
    print("  Batch folder: python main.py ./data_folder ./output MySubstance")
    print()
    print("CSV files must have headers: 'Potential (V)' and 'Current (A)'")

def main():
    if len(sys.argv) < 4:
        print_usage()

    input_path = sys.argv[1]
    output_path = sys.argv[2]
    substance_name = sys.argv[3]

    converter = VoltammotramConverter(input_path, output_path, substance_name)
    converter.run()


if __name__ == "__main__":
    main()

