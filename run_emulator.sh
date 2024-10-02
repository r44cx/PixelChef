#!/bin/bash

set -e

# Check if ANDROID_HOME is set
if [ -z "$ANDROID_HOME" ]; then
    echo "Error: ANDROID_HOME environment variable is not set. Please set it to your Android SDK location."
    exit 1
fi

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for required tools
for tool in sdkmanager avdmanager adb gradle; do
    if ! command_exists $tool; then
        echo "Error: $tool is not installed or not in PATH. Please ensure Android SDK tools are properly installed."
        exit 1
    fi
done

# Set the path to the emulator executable
EMULATOR="$ANDROID_HOME/emulator/emulator"

if [ ! -f "$EMULATOR" ]; then
    echo "Error: Emulator not found at $EMULATOR"
    echo "Please ensure the Android Emulator is installed via Android Studio or sdkmanager."
    exit 1
fi

# Create project structure
mkdir -p app/src/main/java/com/pixelchef
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/drawable

# Create AndroidManifest.xml
cat > app/src/main/AndroidManifest.xml << EOL
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.pixelchef">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PixelChef">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".GameActivity" />
        <activity android:name=".RecipeBookActivity" />
    </application>

</manifest>
EOL

# Create strings.xml
mkdir -p app/src/main/res/values
cat > app/src/main/res/values/strings.xml << EOL
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">PixelChef</string>
</resources>
EOL

# Create themes.xml
cat > app/src/main/res/values/themes.xml << EOL
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.PixelChef" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>
EOL

# Create colors.xml
cat > app/src/main/res/values/colors.xml << EOL
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
EOL

# Create placeholder launcher icons
mkdir -p app/src/main/res/mipmap-hdpi
convert -size 72x72 xc:transparent png:app/src/main/res/mipmap-hdpi/ic_launcher.png
convert -size 72x72 xc:transparent png:app/src/main/res/mipmap-hdpi/ic_launcher_round.png

# Create Gradle files
cat > build.gradle << EOL
buildscript {
    ext.kotlin_version = "1.6.21"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.0.4"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlin_version"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
EOL

cat > app/build.gradle << EOL
plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {
    compileSdkVersion 31
    buildToolsVersion "30.0.3"

    defaultConfig {
        applicationId "com.pixelchef"
        minSdkVersion 21
        targetSdkVersion 31
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:\$kotlin_version"
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
EOL

cat > settings.gradle << EOL
rootProject.name = "PixelChef"
include ':app'
EOL

cat > gradle.properties << EOL
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.enableJetifier=true
EOL

# Create Gradle wrapper
gradle wrapper --gradle-version 7.3.3

# Install system image if not already installed
echo "Checking and installing system image..."
sdkmanager "system-images;android-31;google_apis;x86_64"

# Create Android Virtual Device (AVD) if it doesn't exist
if ! avdmanager list avd | grep -q "PixelChef"; then
    echo "Creating PixelChef AVD..."
    echo "no" | avdmanager create avd -n PixelChef -k "system-images;android-31;google_apis;x86_64" --force
else
    echo "PixelChef AVD already exists."
fi

# Start the emulator
echo "Starting the emulator..."
"$EMULATOR" -avd PixelChef -no-snapshot-load &

# Wait for the emulator to fully boot
echo "Waiting for the emulator to boot..."
adb wait-for-device
adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'

echo "Emulator is ready. You can now build and run the app using the deploy_app.sh script."