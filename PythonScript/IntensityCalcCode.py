import os
import cv2
import numpy as np

from numpy.linalg import norm

def brightness(img):
    top = 250
    right = 1150
    height = 900
    width = 1600
    img = img[top:(top + height), right: (right + width)]

    # convert to hsv colorspace
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    # lower bound and upper bound for Yellow color
    lower_bound = np.array([20, 50, 50])
    upper_bound = np.array([50, 255, 255])

    # find the colors within the boundaries
    mask = cv2.inRange(hsv, lower_bound, upper_bound)

    # define kernel size
    kernel = np.ones((7, 7), np.uint8)

    # Remove unnecessary noise from mask
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)
    mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)

    # Segment only the detected region
    segmented_img = cv2.bitwise_and(hsv, hsv, mask=mask)
    # cv2.imshow("seg", segmented_img[:, :, 2])

    yellow_brightness = np.sum(segmented_img[:, :, 2])
    return yellow_brightness


if __name__ == '__main__':

    sample_dir = './SampleData/'

    for image in os.listdir(sample_dir):
        if ".jpg" in image or ".png" in image:
            img = cv2.imread(sample_dir + "/" + image)
            bri = brightness(img)
            print(image, bri,  sep='\t')
