# Inicia la copia de SaludCerca PC2 en esta PC, usando el JDK 21 verificado.
# No importa SQL, no cambia el JDK global y no crea usuarios de base de datos.
$ErrorActionPreference = 'Stop'
$pc2Url = 'http://127.0.0.1:8082/'
$pc2Cliente = New-Object System.Net.Sockets.TcpClient
try {
    $pc2Conexion = $pc2Cliente.ConnectAsync('127.0.0.1', 8082)
    if ($pc2Conexion.Wait(2000) -and $pc2Cliente.Connected) {
        Write-Host "El puerto 8082 ya esta ocupado. Abre $pc2Url para ver la aplicacion existente."
        return
    }
} catch { } finally { $pc2Cliente.Dispose() }
$pc2Jdk = 'C:\Users\Anderson\Downloads\SALUDCERCA\.tools\java21\jdk-21.0.12.1+1'
$pc2MavenHome = 'C:\Users\Anderson\Downloads\SALUDCERCA\.tools\maven-home'
$pc2MavenRepo = 'C:\Users\Anderson\Downloads\SALUDCERCA\.tools\m2'
if (-not (Test-Path -LiteralPath (Join-Path $pc2Jdk 'bin\javac.exe'))) {
    throw "No se encuentra el JDK verificado en $pc2Jdk. Ajusta esta ruta antes de iniciar."
}
$pc2AnteriorJava = $env:JAVA_HOME
$pc2AnteriorPath = $env:Path
$pc2AnteriorMaven = $env:MAVEN_USER_HOME
Push-Location $PSScriptRoot
try {
    $env:JAVA_HOME = $pc2Jdk
    $env:Path = (Join-Path $pc2Jdk 'bin') + ';' + $env:Path
    $env:MAVEN_USER_HOME = $pc2MavenHome
    Write-Host "MySQL de XAMPP debe estar encendido. Aplicacion: $pc2Url"
    & (Join-Path $PSScriptRoot 'mvnw.cmd') "-Dmaven.repo.local=$pc2MavenRepo" '-Djava.version=21' '-Dspring-boot.run.jvmArguments=-Dspring.devtools.restart.enabled=false' '-Dspring-boot.run.arguments=--server.address=127.0.0.1 --spring.jpa.show-sql=false' spring-boot:run
    if ($LASTEXITCODE -ne 0) { throw "El inicio finalizo con codigo $LASTEXITCODE. Revisa el mensaje anterior." }
} finally {
    Pop-Location
    $env:JAVA_HOME = $pc2AnteriorJava
    $env:Path = $pc2AnteriorPath
    $env:MAVEN_USER_HOME = $pc2AnteriorMaven
}
