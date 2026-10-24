# ==============================================================================
# SkillTrack - Automated Local Setup & Runner (Windows PowerShell)
# ==============================================================================

$ErrorActionPreference = "Stop"
$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $ProjectDir

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  SkillTrack - Starting Local Server      " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 1. Locate Java
$javaCmd = Get-Command "java" -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    if (Test-Path "C:\Program Files\Java\jdk-24\bin\java.exe") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
        $env:PATH = "C:\Program Files\Java\jdk-24\bin;$env:PATH"
    } elseif (Test-Path "C:\Program Files\Java\jdk-17\bin\java.exe") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
        $env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"
    } else {
        Write-Error "Java JDK not found. Please install Java 17+ or set JAVA_HOME."
        exit 1
    }
}

# 2. Locate or Download Maven
$mvnCmd = Get-Command "mvn" -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    $toolsDir = Join-Path $ProjectDir ".tools"
    $mvnDir = Join-Path $toolsDir "apache-maven-3.9.6"
    $mvnBin = Join-Path $mvnDir "bin\mvn.cmd"

    if (-not (Test-Path $mvnBin)) {
        Write-Host "Maven not found in PATH. Downloading portable Maven 3.9.6..." -ForegroundColor Yellow
        New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null
        $zipPath = Join-Path $toolsDir "maven.zip"
        
        $url = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
        Invoke-WebRequest -Uri $url -OutFile $zipPath
        
        Write-Host "Extracting Maven..." -ForegroundColor Yellow
        Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
        Remove-Item -Path $zipPath -Force
    }

    $env:PATH = "$(Join-Path $mvnDir 'bin');$env:PATH"
    Write-Host "Portable Maven configured successfully." -ForegroundColor Green
}

# 3. Build & Run embedded Tomcat 10
Write-Host "`nBuilding project and starting Tomcat on http://localhost:8080/ ..." -ForegroundColor Cyan
& mvn clean package cargo:run
