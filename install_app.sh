#!/bin/bash

echo "Installing the PixelChef app on the emulator..."
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo "Installation successful!"
else
    echo "Installation failed. Please check the error messages above."
    exit 1
fi