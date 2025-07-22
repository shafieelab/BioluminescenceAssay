# LUCAS Bioluminescence Assay - Core Server Components

This directory contains the essential files for the LUCAS bioluminescence detection system.

## 📁 Files Included

### **Core Flask Server:**
- **`app.py`** - Main Flask web server with API endpoints
- **`requirements.txt`** - Python package dependencies

### **Analysis Scripts:**
- **`IntensityCalcCode.py`** - Original image intensity analysis script (also used by Flask server)

## 🚀 Quick Setup Guide

### 1. **Install Dependencies**
```bash
pip install -r requirements.txt
```

### 2. **Connect Hardware**
- Raspberry Pi Camera Module
- Optional: Servo motor via GPIO (see project's servo/ directory for examples)

### 3. **Run the Server**
```bash
python app.py
```

Server will start on port 5000: `http://[your-pi-ip]:5000`


## 🔧 Configuration

### Hardware Control
For servo motor control, refer to the project's `servo/` directory which contains GPIO-based control examples.

### Camera Settings (in app.py)
```python
libcamera-still --lens-position 50 --shutter 45000000 --gain 80 --vflip --hflip -n -o image.png
```

## 🧪 Testing Individual Components

### Test Image Analysis
```bash
python IntensityCalcCode.py sample_image.jpg
```

### Test Basic Server
```bash
curl http://localhost:5000/
```

### Test Image Analysis
```bash
python IntensityCalcCode.py
```

## 🔍 How It Works

1. **Client** connects to Flask server via HTTP  
2. **Flask Server** processes requests and captures images
3. **Pi Camera** captures bioluminescence image
4. **OpenCV Analysis** detects yellow fluorescence intensity
5. **Results** returned via API
