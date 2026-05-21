#!/bin/bash
set -e

BASE="http://api:3036/api/v1/FWBBD"

echo "==> Esperando a que la API esté lista..."
until curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/usuarios/login" \
    -H "Content-Type: application/json" -d '{}' | grep -qE '^[0-9]'; do
  sleep 3
done
echo "==> API lista."

# ─── PASO 1: Registro ───────────────────────────────────────────────────────
echo "==> Registrando usuario demo..."
REG=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/usuarios/save" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Usuario",
    "apellidos": "Prueba",
    "correoElectronico": "prueba@fitwin.com",
    "password": "fitwin123",
    "fechaNacimiento": "2000-05-15",
    "altura": 178.0,
    "genero": "MASCULINO",
    "nivelActividad": "MODERADO",
    "pesoActual": 80.0,
    "objetivo": "GANANCIA_MUSCULAR",
    "idioma": "es"
  }')
echo "==> Registro: HTTP $REG"

# ─── PASO 2: Login ──────────────────────────────────────────────────────────
echo "==> Login..."
LOGIN=$(curl -s -X POST "$BASE/usuarios/login" \
  -H "Content-Type: application/json" \
  -d '{"correoElectronico":"prueba@fitwin.com","password":"fitwin123"}')

TOKEN=$(echo "$LOGIN" | jq -r '.token')
USUARIO_ID=$(echo "$LOGIN" | jq -r '.usuarioId')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "ERROR: Login fallido. Respuesta: $LOGIN"
  exit 1
fi
echo "==> Token obtenido. usuarioId=$USUARIO_ID"

AUTH="Authorization: Bearer $TOKEN"

# ─── PASO 3: Onboarding completo ────────────────────────────────────────────
echo "==> Completando onboarding..."
curl -s -o /dev/null -X PUT "$BASE/usuarios/actualizar/$USUARIO_ID" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d '{"estrategia":"SUPERAVIT","ajusteCalorico":15,"onboardingCompleto":true}'

# ─── PASO 4: Ejercicios globales ────────────────────────────────────────────
echo "==> Creando catálogo de ejercicios globales..."

create_ejercicio() {
  curl -s -o /dev/null -X POST "$BASE/ejercicios-globales/save" \
    -H "Content-Type: application/json" -H "$AUTH" \
    -d "$1"
}

create_ejercicio '{"nombre":"Press banca","categoria":"FUERZA","musculoPrimario":"Pectorales","musculosSecundarios":"Tríceps, Deltoides anterior","equipamiento":"BARRA","cueCoaching":"Escápulas retraídas, pies en el suelo, baja la barra al pecho de forma controlada."}'
create_ejercicio '{"nombre":"Sentadilla","categoria":"FUERZA","musculoPrimario":"Cuádriceps","musculosSecundarios":"Glúteos, Isquiotibiales","equipamiento":"BARRA","cueCoaching":"Rodillas en línea con los pies, pecho arriba, baja hasta paralelo o más."}'
create_ejercicio '{"nombre":"Dominadas","categoria":"FUERZA","musculoPrimario":"Dorsales","musculosSecundarios":"Bíceps, Romboides","equipamiento":"PESO_CORPORAL","cueCoaching":"Agarre prono, parte de colgado con brazos extendidos, sube hasta barbilla sobre la barra."}'
create_ejercicio '{"nombre":"Peso muerto","categoria":"FUERZA","musculoPrimario":"Isquiotibiales","musculosSecundarios":"Glúteos, Erector espinal, Trapecio","equipamiento":"BARRA","cueCoaching":"Espalda neutra, barra pegada al cuerpo, empuja el suelo con los pies."}'
create_ejercicio '{"nombre":"Press militar","categoria":"HIPERTROFIA","musculoPrimario":"Deltoides anterior","musculosSecundarios":"Tríceps, Trapecio","equipamiento":"BARRA","cueCoaching":"Core activado, empuja la barra verticalmente sobre la cabeza sin arquear la espalda."}'
create_ejercicio '{"nombre":"Remo con barra","categoria":"HIPERTROFIA","musculoPrimario":"Dorsales","musculosSecundarios":"Romboides, Bíceps, Trapecio medio","equipamiento":"BARRA","cueCoaching":"Torso inclinado 45°, tira la barra hacia el ombligo, aprieta la espalda en el recorrido."}'
create_ejercicio '{"nombre":"Hip thrust","categoria":"HIPERTROFIA","musculoPrimario":"Glúteos","musculosSecundarios":"Isquiotibiales, Core","equipamiento":"BARRA","cueCoaching":"Barra sobre las caderas, empuja hacia el techo, aprieta glúteos en la posición alta."}'
create_ejercicio '{"nombre":"Curl de bíceps","categoria":"HIPERTROFIA","musculoPrimario":"Bíceps","musculosSecundarios":"Braquial, Braquiorradial","equipamiento":"MANCUERNAS","cueCoaching":"Codos fijos a los costados, sube de forma controlada, baja lentamente."}'
create_ejercicio '{"nombre":"Press de banca con mancuernas","categoria":"HIPERTROFIA","musculoPrimario":"Pectorales","musculosSecundarios":"Tríceps, Deltoides anterior","equipamiento":"MANCUERNAS","cueCoaching":"Mayor rango de movimiento que con barra, baja las mancuernas hasta sentir estiramiento en el pecho."}'
create_ejercicio '{"nombre":"Remo unilateral","categoria":"HIPERTROFIA","musculoPrimario":"Dorsales","musculosSecundarios":"Bíceps, Romboides","equipamiento":"MANCUERNAS","cueCoaching":"Apoya rodilla y mano en el banco, tira del codo hacia atrás y arriba."}'
create_ejercicio '{"nombre":"Elevaciones laterales","categoria":"HIPERTROFIA","musculoPrimario":"Deltoides lateral","musculosSecundarios":"Trapecio superior","equipamiento":"MANCUERNAS","cueCoaching":"Codos ligeramente flexionados, sube hasta la altura de los hombros, controla la bajada."}'
create_ejercicio '{"nombre":"Extensión de tríceps con mancuerna","categoria":"HIPERTROFIA","musculoPrimario":"Tríceps","musculosSecundarios":"Ninguno","equipamiento":"MANCUERNAS","cueCoaching":"Codo apuntando al techo, baja la mancuerna detrás de la cabeza y extiende."}'
create_ejercicio '{"nombre":"Jalón al pecho","categoria":"HIPERTROFIA","musculoPrimario":"Dorsales","musculosSecundarios":"Bíceps, Romboides","equipamiento":"MAQUINA","cueCoaching":"Agarre ancho, tira hacia el pecho superior, aprieta las escápulas al bajar."}'
create_ejercicio '{"nombre":"Prensa de pierna","categoria":"HIPERTROFIA","musculoPrimario":"Cuádriceps","musculosSecundarios":"Glúteos, Isquiotibiales","equipamiento":"MAQUINA","cueCoaching":"Pies a anchura de cadera, baja hasta 90° sin despegar la espalda del respaldo."}'
create_ejercicio '{"nombre":"Curl femoral","categoria":"HIPERTROFIA","musculoPrimario":"Isquiotibiales","musculosSecundarios":"Gemelos","equipamiento":"MAQUINA","cueCoaching":"Caderas pegadas al pad, flexiona de forma controlada, no uses impulso."}'
create_ejercicio '{"nombre":"Extensión de cuádriceps","categoria":"HIPERTROFIA","musculoPrimario":"Cuádriceps","musculosSecundarios":"Ninguno","equipamiento":"MAQUINA","cueCoaching":"Extiende hasta bloqueo completo, mantén 1 segundo y baja lentamente."}'
create_ejercicio '{"nombre":"Cruce de poleas","categoria":"HIPERTROFIA","musculoPrimario":"Pectorales","musculosSecundarios":"Deltoides anterior","equipamiento":"CABLE","cueCoaching":"Ligera inclinación hacia delante, cruza las manos en el centro, aprieta el pecho."}'
create_ejercicio '{"nombre":"Extensión de tríceps en polea","categoria":"HIPERTROFIA","musculoPrimario":"Tríceps","musculosSecundarios":"Ninguno","equipamiento":"CABLE","cueCoaching":"Codos fijos, extiende hasta bloqueo completo, controla la vuelta."}'
create_ejercicio '{"nombre":"Fondos en paralelas","categoria":"HIPERTROFIA","musculoPrimario":"Pectorales","musculosSecundarios":"Tríceps, Deltoides anterior","equipamiento":"PESO_CORPORAL","cueCoaching":"Inclínate hacia delante para mayor activación pectoral, baja hasta 90°."}'
create_ejercicio '{"nombre":"Plancha","categoria":"RESISTENCIA","musculoPrimario":"Core","musculosSecundarios":"Deltoides, Glúteos","equipamiento":"PESO_CORPORAL","cueCoaching":"Cuerpo recto de cabeza a talones, activa el core, no dejes caer las caderas."}'

echo "==> Ejercicios globales creados."

# ─── PASO 5: Rutina PPL ─────────────────────────────────────────────────────
echo "==> Creando rutina..."
RUTINA=$(curl -s -X POST "$BASE/rutinas/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"nombre\":\"Push Pull Legs\",\"etiqueta\":\"Hipertrofia\",\"diasActivos\":\"LUNES,MARTES,MIERCOLES,JUEVES,VIERNES\",\"duracionEstimadaMin\":75}")
RUTINA_ID=$(echo "$RUTINA" | jq -r '.rutinaId')
echo "==> Rutina creada: rutinaId=$RUTINA_ID"

# ─── PASO 6: Ejercicios en rutina ───────────────────────────────────────────
echo "==> Añadiendo ejercicios a la rutina..."

curl -s -o /dev/null -X POST "$BASE/ejercicios/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"rutinaId\":$RUTINA_ID,\"ejercicioGlobalId\":1,\"diaSemana\":\"LUNES\",\"series\":4,\"repeticionesMin\":8,\"repeticionesMax\":12,\"descansoSegundos\":90,\"pesoKg\":80.0,\"posicion\":1}"

curl -s -o /dev/null -X POST "$BASE/ejercicios/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"rutinaId\":$RUTINA_ID,\"ejercicioGlobalId\":2,\"diaSemana\":\"MIERCOLES\",\"series\":4,\"repeticionesMin\":6,\"repeticionesMax\":10,\"descansoSegundos\":120,\"pesoKg\":100.0,\"posicion\":1}"

curl -s -o /dev/null -X POST "$BASE/ejercicios/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"rutinaId\":$RUTINA_ID,\"ejercicioGlobalId\":3,\"diaSemana\":\"MARTES\",\"series\":3,\"repeticionesMin\":6,\"repeticionesMax\":10,\"descansoSegundos\":90,\"pesoKg\":0.0,\"posicion\":1}"

# ─── PASO 7: Sesión 1 — Press banca ─────────────────────────────────────────
echo "==> Creando sesión 1 (press banca)..."
SES1=$(curl -s -X POST "$BASE/sesiones/iniciar" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"rutinaId\":$RUTINA_ID}")
SES1_ID=$(echo "$SES1" | jq -r '.sesionId')

EJERCICIO_ID=1
for serie in \
  '{"pesoKg":80.0,"repeticionesRealizadas":12,"orden":1}' \
  '{"pesoKg":82.5,"repeticionesRealizadas":10,"orden":2}' \
  '{"pesoKg":85.0,"repeticionesRealizadas":8,"orden":3}' \
  '{"pesoKg":85.0,"repeticionesRealizadas":7,"orden":4}'; do
  curl -s -o /dev/null -X POST "$BASE/series/save" \
    -H "Content-Type: application/json" -H "$AUTH" \
    -d "{\"sesionId\":$SES1_ID,\"ejercicioId\":$EJERCICIO_ID,\"completado\":true,$(echo $serie | sed 's/^{//')}"
done

curl -s -o /dev/null -X PUT "$BASE/sesiones/finalizar/$SES1_ID" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d '{"nivelIntensidad":8,"nivelRecuperacion":7,"notasUsuario":"Buena sesión, nuevo record en press banca. Hombro izquierdo algo tenso."}'

# ─── PASO 8: Sesión 2 — Dominadas ───────────────────────────────────────────
echo "==> Creando sesión 2 (dominadas)..."
SES2=$(curl -s -X POST "$BASE/sesiones/iniciar" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"rutinaId\":$RUTINA_ID}")
SES2_ID=$(echo "$SES2" | jq -r '.sesionId')

curl -s -o /dev/null -X POST "$BASE/series/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"sesionId\":$SES2_ID,\"ejercicioId\":3,\"pesoKg\":0.0,\"repeticionesRealizadas\":18,\"completado\":true,\"orden\":1}"

curl -s -o /dev/null -X PUT "$BASE/sesiones/finalizar/$SES2_ID" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d '{"nivelIntensidad":7,"nivelRecuperacion":8,"notasUsuario":"Dominadas bien, 18 reps sin lastre."}'

# ─── PASO 9: Nutrición ──────────────────────────────────────────────────────
echo "==> Registrando comidas..."
TODAY=$(date +%Y-%m-%d)

for comida in \
  "{\"nombre\":\"Avena con leche\",\"calorias\":380,\"proteinas\":14.0,\"carbohidratos\":58.0,\"grasasSaturadas\":3.0,\"tipoComida\":\"DESAYUNO\",\"cantidad\":100,\"unidad\":\"GRAMOS\",\"fecha\":\"$TODAY\"}" \
  "{\"nombre\":\"Pechuga de pollo con arroz\",\"calorias\":520,\"proteinas\":48.0,\"carbohidratos\":62.0,\"grasasSaturadas\":2.5,\"tipoComida\":\"ALMUERZO\",\"cantidad\":400,\"unidad\":\"GRAMOS\",\"fecha\":\"$TODAY\"}" \
  "{\"nombre\":\"Salmón al horno con verduras\",\"calorias\":450,\"proteinas\":38.0,\"carbohidratos\":20.0,\"grasasSaturadas\":6.0,\"tipoComida\":\"CENA\",\"cantidad\":350,\"unidad\":\"GRAMOS\",\"fecha\":\"$TODAY\"}" \
  "{\"nombre\":\"Batido de proteínas\",\"calorias\":160,\"proteinas\":30.0,\"carbohidratos\":6.0,\"grasasSaturadas\":1.0,\"tipoComida\":\"SNACK\",\"cantidad\":300,\"unidad\":\"ML\",\"fecha\":\"$TODAY\"}"; do
  curl -s -o /dev/null -X POST "$BASE/comidas/save" \
    -H "Content-Type: application/json" -H "$AUTH" \
    -d "{\"usuarioId\":$USUARIO_ID,$(echo $comida | sed 's/^{//')}"
done

# ─── PASO 10: Medición corporal ─────────────────────────────────────────────
echo "==> Registrando medición corporal..."
curl -s -o /dev/null -X POST "$BASE/mediciones/save" \
  -H "Content-Type: application/json" -H "$AUTH" \
  -d "{\"usuarioId\":$USUARIO_ID,\"peso\":80.0,\"porcentajeGrasa\":16.5,\"masaMagra\":66.8,\"cintura\":82.0,\"pecho\":102.0,\"brazo\":37.0,\"muslo\":58.0,\"hombro\":116.0,\"espalda\":109.0}"

# ─── PASO 11: Objetivo nutricional ──────────────────────────────────────────
echo "==> Generando objetivo calórico..."
curl -s -o /dev/null -X POST "$BASE/objetivos/generar/$USUARIO_ID" \
  -H "$AUTH"

echo ""
echo "======================================"
echo "  SEED COMPLETO"
echo "  Email:    prueba@fitwin.com"
echo "  Password: fitwin123"
echo "======================================"
