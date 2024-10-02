#!/bin/bash

echo "Building the PixelChef app..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful!"
else
    echo "Build failed. Please check the error messages above."
    exit 1
fi