#!/usr/bin/env pwsh
# =====================================================================
# seed_mane.ps1 — Poblar 2+ semanas de datos para mane@gmail.com
# =====================================================================
$ErrorActionPreference = "Continue"
$BASE = "http://localhost:3036/api/v1/FWBBD"

# ─── 1. Login ─────────────────────────────────────────────────────────────────
Write-Host "`n[1/7] Login..." -ForegroundColor Cyan
$loginResp = Invoke-RestMethod -Uri "$BASE/usuarios/login" -Method POST -Body '{"correoElectronico":"mane@gmail.com","password":"Mane123"}' -ContentType "application/json" -ErrorAction Stop
$TOKEN = $loginResp.token; $USER_ID = $loginResp.usuarioId
$H = @{ Authorization = "Bearer $TOKEN"; "Content-Type" = "application/json" }
Write-Host "  OK - usuarioId=$USER_ID" -ForegroundColor Green

# ─── 2. Mediciones corporales (16 dias) ────────────────────────────────────────
Write-Host "`n[2/7] Mediciones corporales (16 dias)..." -ForegroundColor Cyan
$today = Get-Date; $okM = 0

for ($i = 15; $i -ge 0; $i--) {
    $f = ($today.AddDays(-$i)).ToString("yyyy-MM-dd")
    $p = (15 - $i) / 15.0
    $jit = { param($v,$j) [math]::Round($v + (Get-Random -Min (-$j*10) -Max ($j*10))/100.0, 1) }
    $peso = & $jit (82.0 - $p*1.5) 3; $grasa = & $jit (18.5 - $p*1.2) 2
    $magra = [math]::Round(66.8+$p*1.0,1); $pecho = [math]::Round(102.0+$p*1.5,1)
    $espalda = [math]::Round(110.0+$p*1.0,1); $hombro = [math]::Round(120.0+$p*1.2,1)
    $brazo = [math]::Round(36.0+$p*0.8,1); $muslo = [math]::Round(58.0+$p*0.5,1)
    $cintura = [math]::Round(82.0-$p*1.0,1)
    $json = "{`"usuarioId`":$USER_ID,`"fecha`":`"$f`",`"peso`":$peso,`"porcentajeGrasa`":$grasa,`"masaMagra`":$magra,`"pecho`":$pecho,`"espalda`":$espalda,`"hombro`":$hombro,`"brazo`":$brazo,`"muslo`":$muslo,`"cintura`":$cintura}"
    try {
        Invoke-RestMethod -Uri "$BASE/mediciones/save" -Method POST -Body $json -Headers $H -ErrorAction Stop | Out-Null
        $okM++; Write-Host "  + $f peso=${peso}kg" -ForegroundColor DarkGray
    } catch {
        Write-Host "  ~ $f ya existe" -ForegroundColor DarkYellow
    }
}
Write-Host "  $okM mediciones creadas" -ForegroundColor Green

# ─── 3. Comidas ────────────────────────────────────────────────────────────────
Write-Host "`n[3/7] Comidas (14 dias)..." -ForegroundColor Cyan
$meals = @(
    @{n="Avena con platano y whey";cal=450;prot=35;carb=55;fat=8;tipo="DESAYUNO";cant=350;u="GRAMOS"},
    @{n="Huevos revueltos tostada";cal=380;prot=28;carb=30;fat=15;tipo="DESAYUNO";cant=300;u="GRAMOS"},
    @{n="Yogur griego granola";cal=320;prot=22;carb=40;fat=10;tipo="DESAYUNO";cant=250;u="GRAMOS"},
    @{n="Pollo plancha arroz";cal=620;prot=48;carb=65;fat=12;tipo="ALMUERZO";cant=400;u="GRAMOS"},
    @{n="Ensalada cesar pollo";cal=480;prot=38;carb=25;fat=18;tipo="ALMUERZO";cant=350;u="GRAMOS"},
    @{n="Pasta bolonesa";cal=580;prot=32;carb=70;fat=16;tipo="ALMUERZO";cant=400;u="GRAMOS"},
    @{n="Salmon con verduras";cal=520;prot=42;carb=20;fat=22;tipo="CENA";cant=350;u="GRAMOS"},
    @{n="Tortilla con ensalada";cal=380;prot=25;carb=15;fat=20;tipo="CENA";cant=300;u="GRAMOS"},
    @{n="Batido proteico";cal=280;prot=40;carb=30;fat=4;tipo="SNACK";cant=400;u="MILILITROS"},
    @{n="Almendras y fruta";cal=220;prot=8;carb=20;fat=14;tipo="SNACK";cant=60;u="GRAMOS"}
)
$okC = 0
for ($i = 13; $i -ge 0; $i--) {
    $f = ($today.AddDays(-$i)).ToString("yyyy-MM-dd")
    $num = 3 + (Get-Random -Min 0 -Max 2)
    for ($c = 0; $c -lt $num; $c++) {
        $m = $meals[($i*3+$c) % $meals.Count]
        $json = "{`"usuarioId`":$USER_ID,`"nombre`":`"$($m.n)`",`"calorias`":$($m.cal),`"proteinas`":$($m.prot),`"carbohidratos`":$($m.carb),`"grasasSaturadas`":$($m.fat),`"tipoComida`":`"$($m.tipo)`",`"cantidad`":$($m.cant),`"unidad`":`"$($m.u)`",`"fecha`":`"$f`"}"
        try { Invoke-RestMethod -Uri "$BASE/comidas/save" -Method POST -Body $json -Headers $H -ErrorAction Stop | Out-Null; $okC++ } catch {}
    }
}
Write-Host "  $okC comidas creadas" -ForegroundColor Green

# ─── 4. Ejercicios globales ────────────────────────────────────────────────────
Write-Host "`n[4/7] Ejercicios globales..." -ForegroundColor Cyan
$ejNames = @("Press Banca Seed","Sentadilla Seed","Peso Muerto Seed","Press Militar Seed","Curl Biceps Seed","Remo Barra Seed")
$ejCats = @("FUERZA","FUERZA","FUERZA","FUERZA","HIPERTROFIA","FUERZA")
$ejMusc = @("Pectoral","Cuadriceps","Espalda Baja","Deltoides","Biceps","Dorsal")
$ejEquip = @("BARRA","BARRA","BARRA","BARRA","MANCUERNAS","BARRA")
$ejIds = @()

for ($i = 0; $i -lt $ejNames.Count; $i++) {
    $json = "{`"nombre`":`"$($ejNames[$i])`",`"categoria`":`"$($ejCats[$i])`",`"musculoPrimario`":`"$($ejMusc[$i])`",`"equipamiento`":`"$($ejEquip[$i])`"}"
    try {
        $created = Invoke-RestMethod -Uri "$BASE/ejercicios-globales/save" -Method POST -Body $json -Headers $H -ErrorAction Stop
        $ejIds += $created.ejercicioGlobalId
        Write-Host "  + $($created.nombre) (ID=$($created.ejercicioGlobalId))" -ForegroundColor DarkGray
    } catch {}
}
if ($ejIds.Count -eq 0) {
    try {
        $existing = Invoke-RestMethod -Uri "$BASE/ejercicios-globales" -Method GET -Headers $H -ErrorAction Stop
        $ejIds = @($existing | Select-Object -First 6 | ForEach-Object { $_.ejercicioGlobalId })
        Write-Host "  Usando $($ejIds.Count) existentes" -ForegroundColor DarkGray
    } catch {}
}
Write-Host "  IDs: $($ejIds -join ', ')" -ForegroundColor Green

# ─── 5. Rutina ─────────────────────────────────────────────────────────────────
Write-Host "`n[5/7] Rutina..." -ForegroundColor Cyan
$rutinaId = $null
try {
    $r = Invoke-RestMethod -Uri "$BASE/rutinas/save" -Method POST -Body "{`"nombre`":`"Push Pull Legs Seed`",`"etiqueta`":`"PPL`",`"diasActivos`":`"LUNES,MARTES,MIERCOLES,JUEVES,VIERNES`",`"duracionEstimadaMin`":60,`"usuarioId`":$USER_ID}" -Headers $H -ErrorAction Stop
    $rutinaId = $r.rutinaId
    Write-Host "  OK rutinaId=$rutinaId" -ForegroundColor Green
} catch {
    try {
        $rutinas = Invoke-RestMethod -Uri "$BASE/rutinas?usuarioId=$USER_ID" -Method GET -Headers $H -ErrorAction Stop
        if ($rutinas.Count -gt 0) { $rutinaId = $rutinas[0].rutinaId; Write-Host "  Usando existente ID=$rutinaId" -ForegroundColor DarkGray }
    } catch {}
}

# ─── 6. Sesiones de entrenamiento ──────────────────────────────────────────────
Write-Host "`n[6/7] Sesiones de entrenamiento..." -ForegroundColor Cyan
$diasEnt = @(1,2,3,5,6,7,8,10,11,13)
$sesionIds = @()

foreach ($d in $diasEnt) {
    $fInicio = ($today.AddDays(-$d)).ToString("yyyy-MM-dd") + "T09:00:00"
    $dur = 45 + (Get-Random -Min 0 -Max 30)
    $body = "{`"usuarioId`":$USER_ID"
    if ($rutinaId) { $body += ",`"rutinaId`":$rutinaId" }
    $body += ",`"fechaInicio`":`"$fInicio`"}"
    try {
        $s = Invoke-RestMethod -Uri "$BASE/sesiones/iniciar" -Method POST -Body $body -Headers $H -ErrorAction Stop
        $sId = $s.sesionId
        # Finalizar
        $intens = Get-Random -Min 6 -Max 10; $recup = Get-Random -Min 5 -Max 9
        try { Invoke-RestMethod -Uri "$BASE/sesiones/finalizar/$sId" -Method PUT -Body "{`"duracionMinutos`":$dur,`"nivelIntensidad`":$intens,`"nivelRecuperacion`":$recup,`"notasUsuario`":`"Entreno dia $d`"}" -Headers $H -ErrorAction Stop | Out-Null } catch {}
        $sesionIds += $sId
        Write-Host "  + Sesion #$sId (dia -$d, ${dur}min)" -ForegroundColor DarkGray
    } catch {
        Write-Host "  ~ Error dia -$d" -ForegroundColor DarkYellow
    }
}
Write-Host "  $($sesionIds.Count) sesiones creadas" -ForegroundColor Green

# ─── 7. Series + Records ──────────────────────────────────────────────────────
Write-Host "`n[7/7] Series y records..." -ForegroundColor Cyan
$okS = 0; $okR = 0
$pesosBase = @(80,100,120,50,30,70)

if ($ejIds.Count -gt 0) {
    foreach ($sId in $sesionIds) {
        $numEj = [math]::Min(3, $ejIds.Count)
        for ($e = 0; $e -lt $numEj; $e++) {
            $ns = 3 + (Get-Random -Min 0 -Max 2)
            for ($s = 1; $s -le $ns; $s++) {
                $pk = $pesosBase[$e % 6] + (Get-Random -Min -5 -Max 10)
                $reps = Get-Random -Min 5 -Max 12
                try {
                    Invoke-RestMethod -Uri "$BASE/series/save" -Method POST -Body "{`"sesionId`":$sId,`"ejercicioId`":$($ejIds[$e]),`"pesoKg`":$pk,`"repeticionesRealizadas`":$reps,`"completado`":true,`"orden`":$(($e*5)+$s)}" -Headers $H -ErrorAction Stop | Out-Null
                    $okS++
                } catch {}
            }
        }
    }
    # Records
    $recs = @()
    if ($ejIds.Count -ge 1) { $recs += @{ej=$ejIds[0];p=100;r=5;d=14},@{ej=$ejIds[0];p=105;r=3;d=7},@{ej=$ejIds[0];p=110;r=1;d=2} }
    if ($ejIds.Count -ge 2) { $recs += @{ej=$ejIds[1];p=120;r=5;d=13},@{ej=$ejIds[1];p=130;r=3;d=5},@{ej=$ejIds[1];p=140;r=1;d=1} }
    if ($ejIds.Count -ge 3) { $recs += @{ej=$ejIds[2];p=140;r=5;d=12},@{ej=$ejIds[2];p=160;r=1;d=3} }
    foreach ($rc in $recs) {
        $f = ($today.AddDays(-$rc.d)).ToString("yyyy-MM-dd")
        try {
            Invoke-RestMethod -Uri "$BASE/records/save" -Method POST -Body "{`"usuarioId`":$USER_ID,`"ejercicioGlobalId`":$($rc.ej),`"pesoKg`":$($rc.p),`"repeticiones`":$($rc.r),`"fecha`":`"$f`"}" -Headers $H -ErrorAction Stop | Out-Null
            $okR++
        } catch {}
    }
}

Write-Host "  $okS series, $okR records" -ForegroundColor Green
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  SEED COMPLETADO - mane@gmail.com (ID=$USER_ID)" -ForegroundColor Green
Write-Host "  Mediciones=$okM  Comidas=$okC  Sesiones=$($sesionIds.Count)  Series=$okS  Records=$okR" -ForegroundColor White
Write-Host "========================================`n" -ForegroundColor Cyan
