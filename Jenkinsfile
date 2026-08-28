
pipeline {

    agent any

    options {
        skipDefaultCheckout(true)
        retry(conditions: [nonresumable()], count: 2)
        durabilityHint('PERFORMANCE_OPTIMIZED')
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    // ================================================================
    // GENERIC CONFIGURATION
    // ================================================================
    // CHANGE THESE VALUES FOR ANOTHER PROJECT.
    // DO NOT CHANGE THE STAGES BELOW UNLESS REQUIRED.
    // ================================================================

    environment {

        // ------------------------------------------------------------
        // GIT
        // ------------------------------------------------------------

        GIT_URL = 'https://github.com/Ajeetkhot/RBC.git'
        GIT_BRANCH = 'main'


        // ------------------------------------------------------------
        // JAVA
        // ------------------------------------------------------------
        // Your previous Java installation

        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17.0.2'


        // ------------------------------------------------------------
        // MAVEN
        // ------------------------------------------------------------
        // IMPORTANT:
        // MAVEN_HOME must point to Maven installation.
        // NOT to mvn.cmd.
        //
        // Correct:
        // D:\\SoftwarePath\\apache-maven-3.8.5
        //
        // Incorrect:
        // D:\\SoftwarePath\\apache-maven-3.8.5\\bin\\mvn.cmd

       MAVEN_HOME = 'D:\\SoftwarePath\\apache-maven-3.8.5'
       MAVEN_CMD = 'D:\\SoftwarePath\\apache-maven-3.8.5\\bin\\mvn.cmd'


        // ------------------------------------------------------------
        // BACKEND
        // ------------------------------------------------------------
        //
        // Put the backend Maven project relative to Jenkins workspace.
        //
        // Example:
        // '.'                       -> pom.xml in repository root
        // 'backend'                 -> backend/pom.xml
        // 'Checker_Maker/backend'   -> Checker_Maker/backend/pom.xml
        //
        // CHANGE THIS IF YOUR pom.xml IS INSIDE A SUBDIRECTORY.

        PROJECT_DIR = 'Maker-Checker'
        BACKEND_PROJECT_DIR = 'Maker-Checker'

        BACKEND_PORT = '8000'

        BACKEND_URL = 'http://localhost:8000/api/health'

        // Expected Spring Boot JAR
        //
        // CHANGE THIS if your generated JAR has another name.
        //
        // Example:
        // target/checker_maker.jar
        // target/my-app.jar

        APP_JAR = 'target\\maker-checker-banking-0.0.1-SNAPSHOT.jar'


        // ------------------------------------------------------------
        // TOMCAT
        // ------------------------------------------------------------

        APPZ_HOME = 'D:\\SoftwarePath\\apache-tomcat-9.0.53'

        TOMCAT_PORT = '8080'


        // ------------------------------------------------------------
        // APPZILLON WAR
        // ------------------------------------------------------------
        //
        // This is where your WAR is currently available.
        //
        // D:\\Jenkinss\\Checker_Maker.war

        APPZ_ARTIFACTS = 'D:\\Jenkinss'

        WAR_NAME = 'Checker__Maker.war'

        APP_CONTEXT_PATH = 'Checker__Maker'

        APPZILLON_URL = 'http://localhost:8080/Checker__Maker/'


        // ------------------------------------------------------------
        // PLAYWRIGHT
        // ------------------------------------------------------------

        PLAYWRIGHT_JAVA_DIR = 'Maker-Checker'
    PLAYWRIGHT_BASE_URL = 'http://localhost:8080/Checker__Maker/'
    PLAYWRIGHT_TEST = 'PWTest'

        // ------------------------------------------------------------
        // CI
        // ------------------------------------------------------------

        CI = 'true'
    }


    stages {


        // ============================================================
        // CHECKOUT
        // ============================================================

        stage('Checkout') {

            steps {

                echo '=========================================='
                echo 'CHECKING OUT PROJECT'
                echo '=========================================='

                deleteDir()

                git(
                    branch: "${GIT_BRANCH}",
                    url: "${GIT_URL}"
                )

                echo ''
                echo 'CHECKOUT SUCCESSFUL'
                echo "Repository: ${GIT_URL}"
                echo "Branch: ${GIT_BRANCH}"
                echo ''
            }
        }


        // ============================================================
        // ENVIRONMENT CHECK
        // ============================================================

        stage('Environment Check') {

            steps {

                echo '=========================================='
                echo 'CHECKING JAVA AND MAVEN'
                echo '=========================================='

                bat '''
                    @echo off

                    echo.
                    echo ==========================================
                    echo JAVA
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    echo.
                    echo JAVA VERSION:
                    java -version

                    if errorlevel 1 (
                        echo ERROR: Java is not working.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN
                    echo ==========================================

                    echo MAVEN_HOME:
                    echo %MAVEN_HOME%

                    echo Maven executable:
                    echo %MAVEN_HOME%\\bin\\mvn.cmd

                    if not exist "%MAVEN_HOME%\\bin\\mvn.cmd" (
                        echo.
                        echo ERROR: Maven not found.
                        echo Expected:
                        echo %MAVEN_HOME%\\bin\\mvn.cmd
                        exit /b 1
                    )

                    echo.
                    echo MAVEN VERSION:
                    "%MAVEN_HOME%\\bin\\mvn.cmd" -version

                    if errorlevel 1 (
                        echo ERROR: Maven is not working.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo JAVA AND MAVEN OK
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // BUILD BACKEND
        // ============================================================

        stage('Build Backend') {

            steps {

                echo '=========================================='
                echo 'BUILDING BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo BACKEND PROJECT:
                    echo %BACKEND_PROJECT_DIR%

                    if not exist "%BACKEND_PROJECT_DIR%\\pom.xml" (
                        echo.
                        echo ==========================================
                        echo ERROR: pom.xml NOT FOUND
                        echo ==========================================

                        echo Expected:
                        echo %WORKSPACE%\\%BACKEND_PROJECT_DIR%\\pom.xml

                        echo.
                        echo Workspace contents:
                        dir /s /b "%WORKSPACE%\\pom.xml"

                        exit /b 1
                    )

                    echo.
                    echo pom.xml FOUND.

                    echo.
                    echo ==========================================
                    echo KILLING OLD BACKEND
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Killing PID %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul

                    echo.
                    echo ==========================================
                    echo MAVEN CLEAN PACKAGE
                    echo ==========================================

                    cd /d "%WORKSPACE%\\%BACKEND_PROJECT_DIR%"

                    "%MAVEN_HOME%\\bin\\mvn.cmd" clean package -DskipTests

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo MAVEN BUILD FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD SUCCESSFUL
                    echo ==========================================

                    echo.
                    echo TARGET CONTENTS:
                    dir target
                '''
            }
        }


        // ============================================================
        // CHECK JAR
        // ============================================================

        stage('Check Backend Jar') {

            steps {

                echo '=========================================='
                echo 'CHECKING BACKEND JAR'
                echo '=========================================='

                bat '''
                    @echo off

                    if not exist "%WORKSPACE%\\%BACKEND_PROJECT_DIR%\\%APP_JAR%" (
                        echo.
                        echo ==========================================
                        echo ERROR: JAR NOT FOUND
                        echo ==========================================

                        echo Expected:
                        echo %WORKSPACE%\\%BACKEND_PROJECT_DIR%\\%APP_JAR%

                        echo.
                        echo Available JAR files:

                        if exist "%WORKSPACE%\\%BACKEND_PROJECT_DIR%\\target" (
                            dir "%WORKSPACE%\\%BACKEND_PROJECT_DIR%\\target\\*.jar"
                        )

                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo BACKEND JAR FOUND
                    echo ==========================================

                    dir "%WORKSPACE%\\%BACKEND_PROJECT_DIR%\\target\\*.jar"
                '''
            }
        }


        // ============================================================
        // DEPLOY BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    set "JAR_PATH=%WORKSPACE%\\%BACKEND_PROJECT_DIR%\\%APP_JAR%"

                    echo JAR:
                    echo %JAR_PATH%

                    if not exist "%JAR_PATH%" (
                        echo ERROR: JAR NOT FOUND
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STOPPING OLD BACKEND
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul

                    echo.
                    echo ==========================================
                    echo STARTING BACKEND
                    echo ==========================================

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe && set JAVA_HOME=%JAVA_HOME% && java -jar "%JAR_PATH%" > "%WORKSPACE%\\backend.log" 2>&1"

                    echo Backend start command executed.

                    echo.
                    echo Waiting for backend...
                    ping 127.0.0.1 -n 8 >nul

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist "%WORKSPACE%\\backend.log" (
                        powershell -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 40"
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

                echo '=========================================='
                echo 'BACKEND HEALTH CHECK'
                echo '=========================================='

                bat '''
                    @echo off

                    set RETRIES=20

                    :CHECK_BACKEND

                    echo.
                    echo Checking:
                    echo %BACKEND_URL%

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr /c:"200" /c:"201" /c:"204" >nul

                    if not errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================
                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED
                        echo ==========================================

                        echo.
                        echo PORT STATUS:
                        netstat -ano | findstr :%BACKEND_PORT%

                        echo.
                        echo BACKEND LOG:

                        if exist "%WORKSPACE%\\backend.log" (
                            type "%WORKSPACE%\\backend.log"
                        ) else (
                            echo backend.log not found
                        )

                        exit /b 1
                    )

                    echo Backend not ready.
                    echo Waiting 3 seconds...

                    ping 127.0.0.1 -n 4 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // CHECK APPZILLON WAR
        // ============================================================

        stage('Check Appzillon WAR') {

            steps {

                echo '=========================================='
                echo 'CHECKING APPZILLON WAR'
                echo '=========================================='

                bat '''
                    @echo off

                    echo WAR SOURCE:
                    echo %APPZ_ARTIFACTS%\\%WAR_NAME%

                    if not exist "%APPZ_ARTIFACTS%\\%WAR_NAME%" (

                        echo.
                        echo ==========================================
                        echo ERROR: WAR NOT FOUND
                        echo ==========================================

                        echo Expected:
                        echo %APPZ_ARTIFACTS%\\%WAR_NAME%

                        echo.
                        echo Files in artifact directory:
                        dir "%APPZ_ARTIFACTS%"

                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo WAR FOUND
                    echo ==========================================

                    dir "%APPZ_ARTIFACTS%\\%WAR_NAME%"
                '''
            }
        }


        // ============================================================
        // DEPLOY APPZILLON
        // ============================================================

        stage('Deploy Appzillon') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING APPZILLON'
                echo '=========================================='

                bat '''
                    @echo off

                    echo.
                    echo TOMCAT:
                    echo %APPZ_HOME%

                    echo.
                    echo PORT:
                    echo %TOMCAT_PORT%

                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (
                        echo.
                        echo ERROR: catalina.bat not found.
                        echo Expected:
                        echo %APPZ_HOME%\\bin\\catalina.bat
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    call "%APPZ_HOME%\\bin\\shutdown.bat"

                    ping 127.0.0.1 -n 5 >nul

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING') do (
                        echo Killing Tomcat PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul


                    echo.
                    echo ==========================================
                    echo REMOVING OLD APPLICATION
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\%APP_CONTEXT_PATH%" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\%WAR_NAME%" >nul 2>&1


                    echo.
                    echo ==========================================
                    echo COPYING WAR
                    echo ==========================================

                    copy /Y "%APPZ_ARTIFACTS%\\%WAR_NAME%" "%APPZ_HOME%\\webapps\\%WAR_NAME%"

                    if errorlevel 1 (
                        echo ERROR: WAR COPY FAILED
                        exit /b 1
                    )

                    echo WAR copied successfully.


                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    call "%APPZ_HOME%\\bin\\catalina.bat" start

                    if errorlevel 1 (
                        echo ERROR: Tomcat failed to start
                        exit /b 1
                    )

                    echo.
                    echo Tomcat start command executed.

                    echo.
                    echo Waiting for Tomcat...
                    ping 127.0.0.1 -n 16 >nul

                    echo.
                    echo ==========================================
                    echo TOMCAT PORT
                    echo ==========================================

                    netstat -ano | findstr :%TOMCAT_PORT%

                    echo.
                    echo ==========================================
                    echo TOMCAT LOG
                    echo ==========================================

                    if exist "%APPZ_HOME%\\logs\\catalina.out" (
                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 40"
                    ) else (
                        echo catalina.out not found.
                        dir "%APPZ_HOME%\\logs\\"
                    )
                '''
            }
        }


        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================

        stage('Appzillon Health Check') {

            steps {

                echo '=========================================='
                echo 'APPZILLON HEALTH CHECK'
                echo '=========================================='

                bat '''
                    @echo off

                    set RETRIES=30

                    :CHECK_APP

                    echo.
                    echo Checking:
                    echo %APPZILLON_URL%

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 301 302 404"

                    if not errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================
                        echo URL:
                        echo %APPZILLON_URL%
                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED
                        echo ==========================================

                        echo.
                        echo TOMCAT PORT:
                        netstat -ano | findstr :%TOMCAT_PORT%

                        echo.
                        echo TOMCAT LOG:

                        dir "%APPZ_HOME%\\logs\\" 2>nul

                        exit /b 1
                    )

                    echo Appzillon not ready.
                    echo Waiting 5 seconds...

                    ping 127.0.0.1 -n 6 >nul

                    goto CHECK_APP
                '''
            }
        }


        // ============================================================
        // OPEN APPZILLON
        // ============================================================

        stage('Open Appzillon') {

            steps {

                echo '=========================================='
                echo 'OPENING APPZILLON'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Opening:
                    echo %APPZILLON_URL%

                    start "" "%APPZILLON_URL%"

                    ping 127.0.0.1 -n 5 >nul
                '''
            }
        }


        // ============================================================
        // PLAYWRIGHT
        // ============================================================

       stage('Playwright Tests') {

    steps {

        echo '=========================================='
        echo 'RUNNING PLAYWRIGHT TESTS'
        echo '=========================================='

        bat '''
            @echo off

            echo.
            echo ==========================================
            echo PLAYWRIGHT PROJECT
            echo ==========================================

            echo Jenkins Workspace:
            echo %WORKSPACE%

            echo Playwright Directory:
            echo %PLAYWRIGHT_JAVA_DIR%

            if not exist "%WORKSPACE%\\%PLAYWRIGHT_JAVA_DIR%" (
                echo.
                echo ERROR: Playwright directory not found:
                echo %WORKSPACE%\\%PLAYWRIGHT_JAVA_DIR%
                exit /b 1
            )

            if not exist "%WORKSPACE%\\%PLAYWRIGHT_JAVA_DIR%\\pom.xml" (
                echo.
                echo ERROR: Playwright pom.xml not found:
                echo %WORKSPACE%\\%PLAYWRIGHT_JAVA_DIR%\\pom.xml
                exit /b 1
            )

            cd /d "%WORKSPACE%\\%PLAYWRIGHT_JAVA_DIR%"

            echo.
            echo Current Directory:
            cd

            set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

            echo.
            echo ==========================================
            echo JAVA
            echo ==========================================

            java -version

            echo.
            echo ==========================================
            echo MAVEN
            echo ==========================================

            "%MAVEN_HOME%\\bin\\mvn.cmd" -version

            echo.
            echo ==========================================
            echo PLAYWRIGHT URL
            echo ==========================================

            echo %PLAYWRIGHT_BASE_URL%

            echo.
            echo ==========================================
            echo PLAYWRIGHT TEST
            echo ==========================================

            echo %PLAYWRIGHT_TEST%

            echo.
            echo ==========================================
            echo RUNNING PLAYWRIGHT
            echo ==========================================

            "%MAVEN_HOME%\\bin\\mvn.cmd" com.microsoft.playwright:playwright-maven-plugin:install

            if errorlevel 1 (
                echo.
                echo ==========================================
                echo PLAYWRIGHT INSTALL FAILED
                echo ==========================================
                exit /b 1
            )

            "%MAVEN_HOME%\\bin\\mvn.cmd" test -Dtest=%PLAYWRIGHT_TEST% -Dplaywright.headless=true

            if errorlevel 1 (
                echo.
                echo ==========================================
                echo PLAYWRIGHT TEST FAILED
                echo ==========================================
                exit /b 1
            )

            echo.
            echo ==========================================
            echo PLAYWRIGHT TEST PASSED
            echo ==========================================
        '''
    }
}
    }


    // ================================================================
    // POST ACTIONS
    // ================================================================

    post {

        always {

            echo '=========================================='
            echo 'JENKINS BUILD FINISHED'
            echo '=========================================='

            echo "Workspace: ${env.WORKSPACE}"

            echo "Backend URL: ${env.BACKEND_URL}"

            echo "Appzillon URL: ${env.APPZILLON_URL}"

            echo '=========================================='
        }


        success {

            echo '=========================================='
            echo 'CHECKER MAKER DEPLOYMENT SUCCESSFUL'
            echo '=========================================='

            echo "Backend:"
            echo "${env.BACKEND_URL}"

            echo "Appzillon:"
            echo "${env.APPZILLON_URL}"

            echo '=========================================='
        }


        failure {

            echo '=========================================='
            echo 'CHECKER MAKER DEPLOYMENT FAILED'
            echo '=========================================='

            echo 'Check the failed Jenkins stage.'

            echo 'Backend log: backend.log'

            echo "Tomcat logs: ${env.APPZ_HOME}\\logs\\"

            echo '=========================================='
        }
    }
}

