pipeline {
 
    agent any
 
    options {
        skipDefaultCheckout(true)
    }
 
    parameters {
 
        string(name: 'GIT_URL', defaultValue: 'https://github.com/Ajeetkhot/RBC.git', description: 'Git repository URL')
        string(name: 'GIT_BRANCH', defaultValue: 'main', description: 'Git branch to build')
        string(name: 'PROJECT_DIR', defaultValue: 'D:/SoftwarePath/apache-maven-3.8.5/bin//mvn.cmd', description: 'Maven project directory')
        string(name: 'JAVA_HOME', defaultValue: 'C:/Program Files/Java/dk-17.0.2', description: 'JDK home on the Jenkins agent')
        string(name: 'BACKEND_PORT', defaultValue: '8000', description: 'Spring Boot port')
        string(name: 'TOMCAT_PORT', defaultValue: '8080', description: 'Tomcat port')
        string(name: 'APPZ_HOME', defaultValue: 'D:/SoftwarePath/apache-tomcat-9.0.53', description: 'Tomcat installation directory')
        string(name: 'APPZ_ARTIFACTS', defaultValue: 'D:/Jenkinss', description: 'Directory containing the WAR file')
        string(name: 'WAR_NAME', defaultValue: 'Checker_Maker.war', description: 'WAR file name')
        string(name: 'APP_CONTEXT_PATH', defaultValue: 'Checker_Maker', description: 'Tomcat application context path')
        string(name: 'BACKEND_URL', defaultValue: 'http://localhost:8000/api', description: 'Backend health-check URL')
        string(name: 'APPZILLON_URL', defaultValue: 'http://localhost:8080/Checker__Maker/', description: 'Tomcat application health-check URL')
        string(name: 'PLAYWRIGHT_BASE_URL', defaultValue: 'http://localhost:8080/Checker__Maker/', description: 'Playwright application URL')
        choice(name: 'PLAYWRIGHT_HEADLESS', choices: ['false', 'true'], description: 'Run Playwright headless')
        string(name: 'PLAYWRIGHT_BROWSERS_PATH', defaultValue: 'C:/jenkins/playwright-browsers', description: 'Playwright browser cache directory')
    }
 
    environment {
 
        // ============================================================
        // JAVA
        // ============================================================
 
        JAVA_HOME = "${params.JAVA_HOME}"
 
        // ============================================================
        // SPRING BOOT
        // ============================================================
 
        PROJECT_DIR = "${params.PROJECT_DIR}"
 
        APP_JAR = "${params.PROJECT_DIR}/target/checker_maker.jar"
 
        WAR_NAME = "${params.WAR_NAME}"
 
        APP_CONTEXT_PATH = "${params.APP_CONTEXT_PATH}"
 
        BACKEND_PORT = "${params.BACKEND_PORT}"
 
        BACKEND_URL = "${params.BACKEND_URL}"
 
        // ============================================================
        // TOMCAT / APPZILLON
        // ============================================================
 
        APPZ_HOME = "${params.APPZ_HOME}"
 
        APPZ_ARTIFACTS = "${params.APPZ_ARTIFACTS}"
 
        TOMCAT_PORT = "${params.TOMCAT_PORT}"
 
        APPZILLON_URL = "${params.APPZILLON_URL}"
 
        // ============================================================
        // PLAYWRIGHT CI
        // ============================================================
 
        PLAYWRIGHT_BASE_URL = "${params.PLAYWRIGHT_BASE_URL}"
 
        PLAYWRIGHT_HEADLESS = "${params.PLAYWRIGHT_HEADLESS}"
 
        PLAYWRIGHT_BROWSERS_PATH = "${params.PLAYWRIGHT_BROWSERS_PATH}"
 
        CI = 'true'
    }
 
 
    stages {
 
        // ============================================================
        // CHECKOUT
        // ============================================================
 
        stage('Checkout') {
 
            steps {
 
                echo '=========================================='
                echo 'CHECKING OUT CHECKER_MAKER'
                echo '=========================================='
 
                git branch: params.GIT_BRANCH,
                    url: params.GIT_URL
 
                echo 'CHECKER_MAKER CHECKOUT SUCCESSFUL'
            }
        }
 
        // ============================================================
        // BUILD BACKEND
        // ============================================================
 
        stage('Build Backend Jar') {
 
            steps {
 
                echo '=========================================='
                echo 'KILLING OLD PROCESSES'
                echo '=========================================='
 
                bat '''
                    @echo off
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Killing process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )
                    ping 127.0.0.1 -n 3 >nul
                '''
 
                echo '=========================================='
                echo 'STARTING MAVEN BUILD'
                echo '=========================================='
 
                bat 'mvn -f "%PROJECT_DIR%\\pom.xml" clean package -DskipTests'
 
                echo '=========================================='
                echo 'CHECKING JAR'
                echo '=========================================='
 
                bat 'dir "%PROJECT_DIR%\\target\\*.jar"'
            }
        }
 
        // ============================================================
        // DEPLOY BACKEND
        // ============================================================
 
        stage('Deploy Backend') {
 
            steps {
 
                bat 'if not exist "%APP_JAR%" (echo ERROR: JAR NOT FOUND && exit /b 1)'
 
                echo 'Checker_Maker JAR found'
 
                bat '''
                    @echo off
 
                    echo ==========================================
                    echo DEPLOYING CHECKER_MAKER BACKEND
                    echo ==========================================
 
                    echo.
                    echo CHECKING PORT %BACKEND_PORT%
                    echo ==========================================
 
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )
 
                    echo WAITING FOR PORT %BACKEND_PORT%
                    ping 127.0.0.1 -n 4 >nul
 
                    REM START BACKEND
 
                    echo.
                    echo STARTING CHECKER_MAKER
                    echo ==========================================
 
                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"
 
                    start "Checker_Maker-Backend" /B cmd /c ^
                    "set JENKINS_NODE_COOKIE=dontKillMe && java -jar %APP_JAR% > backend.log 2>&1"
 
                    echo CHECKER_MAKER START COMMAND EXECUTED
                    echo WAITING FOR APPLICATION TO START
 
                    ping 127.0.0.1 -n 6 >nul
 
                    echo.
                    echo BACKEND LOG:
                    if exist backend.log (
                        powershell -Command "Get-Content backend.log -Tail 20"
                    ) else (
                        echo backend.log not found
                    )
                '''
            }
        }
 
        // ============================================================
        // BACKEND HEALTH CHECK
        // ============================================================
 
        stage('Backend Health Check') {
 
            steps {
 
                bat '''
                    @echo off
 
                    echo ==========================================
                    echo CHECKING CHECKER_MAKER BACKEND
                    echo ==========================================
 
                    echo.
                    echo Backend URL:
                    echo %BACKEND_URL%
 
                    echo.
 
                    set RETRIES=20
 
                    :CHECK_BACKEND
 
                    echo Checking backend...
                    echo Remaining attempts: %RETRIES%
 
                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"
 
                    if not errorlevel 1 (
 
                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================
 
                        echo Backend URL:
                        echo %BACKEND_URL%
 
                        exit /b 0
                    )
 
                    echo.
                    echo Backend not ready.
 
                    set /a RETRIES-=1
 
                    if %RETRIES% LEQ 0 (
 
                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================
 
                        echo.
                        echo BACKEND LOG
                        echo ==========================================
 
                        if exist backend.log (
 
                            type backend.log
 
                        ) else (
 
                            echo backend.log not found
 
                        )
 
                        exit /b 1
                    )
 
                    echo Waiting 3 seconds before retry...
 
                    ping 127.0.0.1 -n 4 >nul
 
                    goto CHECK_BACKEND
                '''
            }
        }
 
        // ============================================================
        // DEPLOY APPZILLON
        // ============================================================
 
        stage('Deploy Appzillon') {
 
            steps {
 
                bat '''
                    @echo off
 
                    echo ==========================================
                    echo DEPLOYING APPZILLON CHECKER_MAKER
                    echo ==========================================
 
                    REM CHECK WAR
                    echo CHECKING CHECKER_MAKER WAR
                    if not exist "%APPZ_ARTIFACTS%\\%WAR_NAME%" (
                        echo ERROR: %WAR_NAME% not found at %APPZ_ARTIFACTS%\\%WAR_NAME%
                        exit /b 1
                    )
                    echo Checker_Maker.war found.
 
                    REM CHECK TOMCAT
                    echo.
                    echo TOMCAT HOME: %APPZ_HOME%
                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (
                        echo ERROR: catalina.bat not found
                        exit /b 1
                    )
 
                    REM STOP TOMCAT on port %TOMCAT_PORT%
                    echo.
                    echo STOPPING TOMCAT
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING') do (
                        echo Killing PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )
                    ping 127.0.0.1 -n 4 >nul
 
                    REM REMOVE OLD APP
                    echo.
                    echo REMOVING OLD CHECKER_MAKER
                    rmdir /S /Q "%APPZ_HOME%\\webapps\\%APP_CONTEXT_PATH%" >nul 2>&1
                    del /F /Q "%APPZ_HOME%\\webapps\\%WAR_NAME%" >nul 2>&1
 
                    REM COPY WAR
                    echo.
                    echo COPYING CHECKER_MAKER.WAR
                    copy /Y "%APPZ_ARTIFACTS%\\%WAR_NAME%" "%APPZ_HOME%\\webapps\\%WAR_NAME%"
                    if errorlevel 1 (
                        echo ERROR COPYING Checker_Maker.war
                        exit /b 1
                    )
                    echo Checker_Maker.war copied.
 
                    REM START TOMCAT
                    echo.
                    echo STARTING TOMCAT
                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"
 
                    echo Running: "%APPZ_HOME%\\bin\\catalina.bat" start
                    "%APPZ_HOME%\\bin\\catalina.bat" start
 
                    echo TOMCAT START COMMAND EXECUTED
                    echo WAITING 15 SECONDS FOR TOMCAT TO BOOT
                    ping 127.0.0.1 -n 16 >nul
 
                    echo.
                    echo CHECKING PORT %TOMCAT_PORT%
                    netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING
                    if errorlevel 1 (
                        echo WARNING: Port %TOMCAT_PORT% not listening yet
                    ) else (
                        echo Port %TOMCAT_PORT% is listening
                    )
 
                    echo.
                    echo TOMCAT LOG (last 30 lines):
                    if exist "%APPZ_HOME%\\logs\\catalina.out" (
                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 30"
                    ) else if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (
                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\jenkins-run.log' -Tail 30"
                    ) else (
                        echo No Tomcat log found
                        dir "%APPZ_HOME%\\logs\\" 2>nul
                    )
                '''
            }
        }
 
        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================
 
        stage('Appzillon Health Check') {
 
            steps {
 
                bat '''
                    @echo off
 
                    echo ==========================================
                    echo CHECKING APPZILLON
                    echo ==========================================
                    echo URL: %APPZILLON_URL%
 
                    set RETRIES=30
 
                    :CHECK_APPZILLON
 
                    echo.
                    echo Checking... attempts left: %RETRIES%
 
                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302 404"
 
                    if not errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================
                        echo URL: %APPZILLON_URL%
                        exit /b 0
                    )
 
                    set /a RETRIES-=1
 
                    if %RETRIES% LEQ 0 (
                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED TO START
                        echo ==========================================
 
                        echo PORT %TOMCAT_PORT% STATUS:
                        netstat -ano | findstr :%TOMCAT_PORT%
 
                        echo.
                        echo TOMCAT LOG:
                        if exist "%APPZ_HOME%\\logs\\catalina.out" (
                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 30"
                        ) else if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (
                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\jenkins-run.log' -Tail 30"
                        ) else (
                            echo No log found
                        )
 
                        exit /b 1
                    )
 
                    ping 127.0.0.1 -n 6 >nul
                    goto CHECK_APPZILLON
                '''
            }
        }
 
        // ============================================================
        // PLAYWRIGHT UI TESTS
        // ============================================================
 
        stage('Playwright Chromium Tests') {
 
            steps {
 
                echo 'CHECKING PLAYWRIGHT CHROMIUM'
 
                bat '''
                    @echo off
 
                    if not exist "%PLAYWRIGHT_BROWSERS_PATH%" mkdir "%PLAYWRIGHT_BROWSERS_PATH%"
 
                    set "CHROMIUM_FOUND="
                    for /d %%D in ("%PLAYWRIGHT_BROWSERS_PATH%\\chromium-*") do (
                        if exist "%%~fD\\chrome-win\\chrome.exe" set "CHROMIUM_FOUND=1"
                    )
 
                    if not defined CHROMIUM_FOUND (
                        echo INSTALLING PLAYWRIGHT CHROMIUM
                        mvn -f "%PROJECT_DIR%\\pom.xml" "-DskipTests" exec:java "-Dexec.classpathScope=test" "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium"
                    ) else (
                        echo PLAYWRIGHT CHROMIUM ALREADY INSTALLED
                    )
                '''
 
                echo 'RUNNING PLAYWRIGHT TESTS HEADLESS'
 
                bat 'mvn -f "%PROJECT_DIR%\\pom.xml" -Dtest=ExampleTest test'
            }
        }
    }
 
    // ============================================================
    // POST ACTIONS
    // ============================================================
 
    post {
 
        always {
 
            junit allowEmptyResults: true,
                testResults: "${params.PROJECT_DIR}/target/surefire-reports/*.xml"
 
            archiveArtifacts allowEmptyArchive: true,
                artifacts: 'test-results/**, playwright-report/**'
        }
 
        success {
 
            echo '=========================================='
            echo 'CHECKER_MAKER DEPLOYMENT SUCCESSFUL'
            echo '=========================================='
 
            echo 'Backend:'
            echo "${params.BACKEND_URL}"
 
            echo 'Appzillon:'
            echo "${params.APPZILLON_URL}"
 
            echo '=========================================='
        }
 
        failure {
 
            echo '=========================================='
            echo 'CHECKER_MAKER DEPLOYMENT FAILED'
            echo '=========================================='
 
            echo 'Check the stage that failed.'
 
            echo '=========================================='
        }
    }
}
 