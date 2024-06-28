# BioluminescenceAssay

This repository contains the code and resources for the paper "Ultrasensitive and Long-lasting Luminescence Cascade Sensor for Point of Care Viral Pathogen Detection".

## Abstract

Bioluminescence holds notable promise as a modality in diagnostics due to its high signal-to-noise ratio and absence of incident radiation. However, challenges arise from rapid signal decay and reduced enzyme activity when linked to targeting molecules, limiting its reliability in point-of-care diagnostic applications. Here, we introduce LUCAS, an enzyme cascade system capable of detecting analytes with ultrahigh sensitivity and prolonged bioluminescence. By employing an enzyme that retains its activity when conjugated to an antibody, our assay achieves more than a 500-fold increase in bioluminescence signal and maintains an 8-fold improvement in signal persistence compared to conventional bioluminescence assays. Implemented on the fully automated LUCAS, our system facilitates rapid (< 23 min) sample-to-answer analysis of viruses without an external power supply. Its accuracy surpasses 94% in the qualitative classification of 177 viral-infected patient samples and 50 viral-spiked serum samples, various pathogens including the respiratory virus SARS-CoV-2, and blood-borne pathogens such as HIV, HBV, and HCV as clinical models. The decentralized, rapid, sensitive, specific, and cost-effective nature of LUCAS positions it as a viable diagnostic tool for low-resource environments.

## System Requirements

### Software Dependencies
- **Python**: Version 3.7 or higher
- **Libraries**:
  - `opencv-python`
  - `numpy`
  - `scipy`
  - `matplotlib`
  - `pandas`
  - `flask`
  - `serial`

### Operating Systems
- **Windows**: Tested on Windows 10
- **Linux**: Tested on Ubuntu 20.04
- **macOS**: Tested on macOS Big Sur

### Hardware Requirements
- **Standard Desktop or Laptop**: No special hardware requirements
- **Optional**: Raspberry Pi 4 for portable operation
- **Optional**: CMOS sensor for bioluminescence detection

## Installation Guide

### Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/shafieelab/BioluminescenceAssay.git
   cd BioluminescenceAssay
   ```

2. **Set Up Python Environment**:
   - Create and activate a virtual environment (optional but recommended):
     ```bash
     python3 -m venv env
     source env/bin/activate  # On Windows use `env\Scripts\activate`
     ```

3. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

### Typical Install Time
- Approximately 5-10 minutes on a standard desktop computer.

## Python 

### Instructions to Run on Sample Data using Python
1. **Navigate to the PythonScript Directory**:
   ```bash
   cd PythonScript
   ```

2. **Run the Demo Script**:
   ```bash
   python IntensityCalcCode.py
   ```

### Expected Output
- The demo script will process the sample data and display bioluminescence intensity values.

### Expected Run Time
- Approximately 2-3 seconds on a standard desktop computer for ~20 images. Runtime varies with number of images.

