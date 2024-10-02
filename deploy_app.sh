#!/bin/bash

set -e

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Using Java version: $java_version"

# Update Gradle wrapper
./gradlew wrapper --gradle-version 8.3

# Build the app
echo "Building the PixelChef app..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful!"
else
    echo "Build failed. Please check the error messages above."
    exit 1
fi

# Install the app
echo "Installing the PixelChef app on the emulator..."
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo "Installation successful!"
else
    echo "Installation failed. Please check the error messages above."
    exit 1
fi

# Run the app
echo "Launching the PixelChef app on the emulator..."
adb shell am start -n com.pixelchef/.MainActivity

if [ $? -eq 0 ]; then
    echo "App launched successfully!"
else
    echo "Failed to launch the app. Please check the error messages above."
    exit 1
fi

echo "PixelChef app has been built, installed, and launched on the emulator."