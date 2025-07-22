import csv
import json
import os
from os.path import isdir
import subprocess
import time
from flask import Flask, request, jsonify, send_file

# from serial_com import start_servo  # Optional hardware control
from flask_cors import CORS
from werkzeug.utils import secure_filename

from IntensityCalcCode import get_brightness
app = Flask(__name__)
cors = CORS(app)


ALLOWED_EXTENSIONS = set([ 'png', 'jpg', 'jpeg', 'gif', 'bmp', 'tif', 'tiff'])


@app.route('/')
def hello_world():  # put application's code here
    return 'Hello World!'
    
@app.route('/run_servo',  methods=['POST', 'GET'])    
def run_servo():
    # Basic servo control endpoint - implement as needed
    # Could integrate with GPIO control from servo/ directory
    return "servo control available"



@app.route('/capture_image',  methods=['POST', 'GET']) 
def capture_image():
    if os.path.exists("image.png"):
        os.remove("image.png")

    try:
        output = subprocess.check_output("libcamera-still --lens-position 50 --shutter 45000000 --gain 80 --vflip --hflip -n -o image.png", shell=True)                       
        print(output)
        print("Images captured")
        return 'image captured'


    except subprocess.CalledProcessError as grepexc:                                                                                                   
        print("error code", grepexc.returncode, grepexc.output)
        print("Failed!!! Image capture")
        return 'failed'


@app.route('/calculate_intensity',  methods=['POST', 'GET']) 
def calculate_intensity():
    brightness = get_brightness("image.png")
    
    return "Brightness:" + str(brightness)
    


        

    
@app.route("/download_image", methods=['POST', 'GET']) 
def download_image():
    #input_data = request.json
    #image_path = input_data['image_path']


    return send_file("image.png", as_attachment=True)
    
if __name__ == '__main__':
    app.run(debug=False, port=5000, host="0.0.0.0") 