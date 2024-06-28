
# Android App

## System Requirements

### Software Dependencies
- Android Studio 4.1 or later
- Android SDK 30 or later
- OpenCV 4.8.0
- Flask server hosted on Raspberry Pi
- Python 3.7 or later

### Operating Systems
- Android 6.0 (Marshmallow) or later

### Versions Tested
- Android  12.0

### Required Non-standard Hardware
- Raspberry Pi 4
- Arduino Nano
- Servo Motor
- CMOS Image Sensor
- 3D Printed Microfluidic Cartridge

## Installation Guide

### Instructions
1. **Set up the Flask Server on Raspberry Pi:**
   - Install Flask on Raspberry Pi: `pip install Flask`
   - Navigate to the server directory and run: `python app.py`

2. **Set up the Android Application:**
   - Clone the repository: `git clone https://github.com/shafieelab/BioluminescenceAssasy`
   - Open the project in Android Studio.
   - Build the project to ensure all dependencies are resolved.
   - Connect your Android device and run the application.

3. **Configure the Arduino Nano:**
   - Install the Arduino IDE on your computer.
   - Open the Arduino sketch from the repository.
   - Upload the sketch to the Arduino Nano.

4. **Set up the CMOS Image Sensor:**
   - Connect the CMOS sensor to the Raspberry Pi as per the wiring diagram provided in the documentation.
   - Ensure the sensor is correctly positioned to capture images from the microfluidic cartridge.

### Typical Install Time
- Approximately 2 hours.

## Demo

### Instructions to Run on Data
1. Prepare the sample and load it into the microfluidic cartridge as described in the documentation.
2. Insert the cartridge into the detection module.
3. Open the Android application and connect to the Raspberry Pi server.
4. Press the "Start" button on the app to begin the assay.

### Expected Output
- The application will display the bioluminescence intensity and determine the presence of viral pathogens.

### Expected Run Time for Demo
- Approximately 23 minutes on a "normal" desktop computer.

## Instructions for Use

### How to Run the Software on Your Data
1. **Sample Preparation:**
   - Collect the sample and prepare it using the provided protocol.
   - Load the prepared sample into the microfluidic cartridge.

2. **Running the Assay:**
   - Insert the cartridge into the detection module.
   - Open the Android application.
   - Connect the application to the Raspberry Pi server.
   - Press the "Start" button to initiate the automated assay.

### Reproduction Instructions
We encourage you to follow these steps to reproduce the quantitative results in the manuscript:
1. **Set up the Environment:**
   - Ensure all hardware components are correctly assembled and connected.
   - Install all necessary software dependencies.

2. **Data Collection:**
   - Follow the detailed protocol in the manuscript to prepare and analyze samples.
   - Use the same concentrations and conditions as described.

3. **Data Analysis:**
   - Use the provided OpenCV script to analyze the bioluminescence images. [text](../PythonScript/IntensityCalcCode.py) 
   - Compare the results with the expected outputs mentioned in the manuscript.

For detailed information, refer to the supplementary information provided in the manuscript.

