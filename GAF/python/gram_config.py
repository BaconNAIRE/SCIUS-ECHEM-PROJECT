class GramConfig:
    # Image settings
    IMAGE_SIZE = 224
    OUTPUT_FILE_TYPE = 'png'

    # Data split options (only active when ENABLE_AUTO_SPLIT is true)
    ENABLE_AUTO_SPLIT = False
    TRAIN_FRACTION = 0.60
    TEST_FRACTION = 0.20
    VALIDATE_FRACTION = 0.20

    # CSV Headers
    VOLTAGE_HEADER = "Potential (V)"
    CURRENT_HEADER = "Current (A)"
    CSV_DELIMITER = ","

    # Logging
    VERBOSE = True
