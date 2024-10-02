#!/bin/bash

set -e

# Update package list and upgrade existing packages
sudo apt update && sudo apt upgrade -y

# Check if Java is already installed
if ! command -v java &> /dev/null; then
    echo "Java not found. Installing OpenJDK 17..."
    sudo apt install openjdk-17-jdk -y
fi

# Set ANDROID_HOME if not already set
if [ -z "$ANDROID_HOME" ]; then
    echo 'export ANDROID_HOME=$HOME/Android/Sdk' >> ~/.bashrc
    echo 'export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools' >> ~/.bashrc
fi

# Source .bashrc to apply changes immediately
source ~/.bashrc

# Install Android SDK command line tools if not already installed
if [ ! -d "$ANDROID_HOME/cmdline-tools" ]; then
    echo "Installing Android SDK command line tools..."
    wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip
    unzip commandlinetools-linux-8512546_latest.zip -d $ANDROID_HOME
    mv $ANDROID_HOME/cmdline-tools $ANDROID_HOME/latest
    mkdir $ANDROID_HOME/cmdline-tools
    mv $ANDROID_HOME/latest $ANDROID_HOME/cmdline-tools/
    rm commandlinetools-linux-8512546_latest.zip
fi

# Accept licenses
yes | sdkmanager --licenses

# Install necessary SDK components
sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"

echo "Setup complete. Please restart your terminal or run 'source ~/.bashrc' to apply all changes."