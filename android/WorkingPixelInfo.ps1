# Working Pixel 4a Info Script
# ADB commands to get device information from PC

# Set ADB path
$AdbPath = "C:\Users\shin5\AppData\Local\Android\Sdk\platform-tools\adb.exe"

Write-Host "=== Pixel 4a Info Script ===" -ForegroundColor Green
Write-Host "Time: $(Get-Date)" -ForegroundColor Gray

# Check device connection
Write-Host "`nChecking device connection..." -ForegroundColor Cyan
$devices = & $AdbPath devices
Write-Host "Connected devices:" -ForegroundColor Yellow
$devices

if ($devices -notcontains "08171JEC207725	device") {
    Write-Host "Pixel 4a detected!" -ForegroundColor Green
} else {
    Write-Host "Device status check..." -ForegroundColor Yellow
}

# Get device info
Write-Host "`nDevice Information:" -ForegroundColor Cyan
$model = & $AdbPath shell getprop ro.product.model
Write-Host "Model: $model" -ForegroundColor White

$android = & $AdbPath shell getprop ro.build.version.release
Write-Host "Android: $android" -ForegroundColor White

$cpu = & $AdbPath shell getprop ro.product.cpu.abi
Write-Host "CPU: $cpu" -ForegroundColor White

# Get battery info
Write-Host "`nBattery Information:" -ForegroundColor Cyan
$batteryOutput = & $AdbPath shell dumpsys battery

# Parse battery information
$batteryLines = $batteryOutput -split "`n"

foreach ($line in $batteryLines) {
    if ($line -match "level: (\d+)") {
        $level = $Matches[1]
        Write-Host "Battery Level: $level%" -ForegroundColor Green
    }
    elseif ($line -match "status: (\d+)") {
        $statusCode = $Matches[1]
        $status = switch ($statusCode) {
            "1" { "Unknown" }
            "2" { "Charging" }
            "3" { "Discharging" }
            "4" { "Not Charging" }
            "5" { "Full" }
            default { "Unknown($statusCode)" }
        }
        Write-Host "Status: $status" -ForegroundColor Yellow
    }
    elseif ($line -match "temperature: (\d+)") {
        $tempRaw = [int]$Matches[1]
        $temp = $tempRaw / 10.0
        Write-Host "Temperature: ${temp}°C" -ForegroundColor Blue
    }
    elseif ($line -match "voltage: (\d+)") {
        $voltage = $Matches[1]
        Write-Host "Voltage: ${voltage}mV" -ForegroundColor Magenta
    }
    elseif ($line -match "health: (\d+)") {
        $healthCode = $Matches[1]
        $health = switch ($healthCode) {
            "1" { "Unknown" }
            "2" { "Good" }
            "3" { "Overheat" }
            "4" { "Dead" }
            "5" { "Over Voltage" }
            "6" { "Unspecified Failure" }
            "7" { "Cold" }
            default { "Unknown($healthCode)" }
        }
        Write-Host "Health: $health" -ForegroundColor Cyan
    }
}

# Additional device information
Write-Host "`nAdditional Information:" -ForegroundColor Cyan

# Screen status
$screenState = & $AdbPath shell dumpsys power | Select-String "mWakefulness"
Write-Host "Screen: $screenState" -ForegroundColor Gray

# Storage info
$storage = & $AdbPath shell df /data | Select-String "/data"
if ($storage) {
    Write-Host "Storage: $storage" -ForegroundColor Gray
}

Write-Host "`nScript completed successfully!" -ForegroundColor Green

# Ask for continuous monitoring
$monitor = Read-Host "`nStart continuous monitoring? (y/n)"
if ($monitor -eq "y" -or $monitor -eq "Y") {
    Write-Host "Starting continuous monitoring (Press Ctrl+C to stop)..." -ForegroundColor Yellow
    
    while ($true) {
        Start-Sleep -Seconds 5
        
        # Get current battery level
        $batteryNow = & $AdbPath shell dumpsys battery
        $batteryLevelLine = $batteryNow | Select-String "level: (\d+)"
        $batteryStatusLine = $batteryNow | Select-String "status: (\d+)"
        $batteryTempLine = $batteryNow | Select-String "temperature: (\d+)"
        
        if ($batteryLevelLine -match "level: (\d+)") {
            $currentLevel = $Matches[1]
        }
        if ($batteryStatusLine -match "status: (\d+)") {
            $currentStatus = switch ($Matches[1]) {
                "2" { "Charging" }
                "3" { "Discharging" }
                "5" { "Full" }
                default { "Unknown" }
            }
        }
        if ($batteryTempLine -match "temperature: (\d+)") {
            $currentTemp = [int]$Matches[1] / 10.0
        }
        
        $timestamp = Get-Date -Format "HH:mm:ss"
        Write-Host "[$timestamp] Battery: $currentLevel% ($currentStatus) - Temp: ${currentTemp}°C" -ForegroundColor Cyan
    }
}