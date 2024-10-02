# Install Chocolatey
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))

# Install OpenJDK 11
choco install openjdk11 -y

# Install Android SDK
choco install android-sdk -y

# Set up environment variables
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Android\android-sdk", "User")
[Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools", "User")

# Refresh environment variables
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Accept licenses
echo y | sdkmanager --licenses

# Install necessary SDK components
sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Install Gradle
choco install gradle -y

Write-Host "Setup complete. Please restart your PowerShell session to apply changes."