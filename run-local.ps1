# Sobe o back-end (monolito) carregando as variaveis do arquivo .env desta pasta.
# O Spring Boot NAO le o .env sozinho: sem isto, falta JWT_SECRET e o servidor nao inicia.
#
# Uso (na raiz do repositorio back-end):
#   powershell -ExecutionPolicy Bypass -File .\run-local.ps1
#   powershell -ExecutionPolicy Bypass -File .\run-local.ps1 -JavaHome "C:\caminho\do\jdk-21"
param([string]$JavaHome = "")

$ErrorActionPreference = "Stop"

# --- Java 21 (o projeto exige 21) ---
if ($JavaHome) {
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$JavaHome\bin;$env:Path"
}
$javaOut = (cmd /c "java -version 2>&1") -join "`n"
$m = [regex]::Match($javaOut, 'version "(\d+)')
if (-not $m.Success -or [int]$m.Groups[1].Value -lt 21) {
    Write-Error "Java 21 ou superior e necessario (encontrado: $($m.Groups[1].Value)). Use -JavaHome com o caminho do JDK 21."
}

# --- carrega o .env (somente nesta janela do PowerShell) ---
$envFile = Join-Path $PSScriptRoot ".env"
if (-not (Test-Path $envFile)) {
    Write-Error "Arquivo .env nao encontrado em $PSScriptRoot. Copie o .env.example para .env e preencha."
}
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
        $name, $value = $line.Split("=", 2)
        Set-Item -Path "Env:$($name.Trim())" -Value $value.Trim()
    }
}

# --- variaveis obrigatorias (sem valor padrao no application.properties) ---
foreach ($n in "DB_PASSWORD", "JWT_SECRET", "OCR_API_KEY", "RABBITMQ_PASSWORD") {
    if (-not (Get-Item "Env:$n" -ErrorAction SilentlyContinue) -or -not (Get-Item "Env:$n").Value) {
        Write-Error "Variavel obrigatoria ausente ou vazia no .env: $n"
    }
}
if ($env:JWT_SECRET.Length -lt 32) {
    Write-Error "JWT_SECRET precisa ter 32 caracteres ou mais."
}

Write-Host "Variaveis carregadas do .env (valores nao exibidos). Iniciando o back-end..."

# Grava tambem o log em arquivo (usado por notification-service\scripts\test-e2e.ps1). A pasta target/ ja e ignorada pelo Git.
$logDir = Join-Path $PSScriptRoot "Intera\adegaunderground\adegaunderground\target\e2e"
New-Item -ItemType Directory -Force -Path $logDir | Out-Null
$env:LOGGING_FILE_NAME = Join-Path $logDir "monolith.log"
Set-Location (Join-Path $PSScriptRoot "Intera\adegaunderground\adegaunderground")
& .\mvnw.cmd spring-boot:run
