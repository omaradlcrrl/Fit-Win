package org.example.fitwinkmp.features.stats.data

import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.*
import org.example.fitwinkmp.features.stats.data.api.StatsApi
import org.example.fitwinkmp.features.stats.data.dto.*

class StatsRepository(private val api: StatsApi) {

    // Fisionomía

    suspend fun getPhysiqueData(usuarioId: Int): Result<PhysiqueData> = runCatching {
        val usuario = api.getUsuario(usuarioId)
        val ultimaMedicion = runCatching { api.getUltimaMedicion(usuarioId) }.getOrNull()

        // Rango último mes para historial
        val hoy = getCurrentDateString()
        val hace30Dias = getDateMinus30DaysString()
        val historial = runCatching {
            api.getMedicionesByRange(usuarioId, hace30Dias, hoy)
        }.getOrElse { emptyList() }

        val altura = usuario.altura

        // IMC: peso / (altura²)
        val imc: Double? = if (ultimaMedicion?.peso != null && altura != null && altura > 0) {
            ultimaMedicion.peso / (altura * altura)
        } else null

        // FFMI: masaMagra / altura² + 6.1 × (1.8 - altura)
        val ffmi: Double? = if (ultimaMedicion?.masaMagra != null && altura != null && altura > 0) {
            (ultimaMedicion.masaMagra / (altura * altura)) + 6.1 * (1.8 - altura)
        } else null

        // Ratio cintura-hombro
        val ratioCinturaHombro: Double? = if (ultimaMedicion?.cintura != null && ultimaMedicion.hombro != null && ultimaMedicion.hombro > 0) {
            ultimaMedicion.cintura / ultimaMedicion.hombro
        } else null

        // Tendencia peso último mes
        val tendenciaPeso: Double? = if (historial.size >= 2) {
            val primera = historial.first().peso
            val ultima = historial.last().peso
            if (primera != null && primera > 0 && ultima != null) {
                ((ultima - primera) / primera) * 100.0
            } else null
        } else null

        // Tendencia grasa
        val tendenciaGrasa: Double? = if (historial.size >= 2) {
            val primera = historial.first().porcentajeGrasa
            val ultima = historial.last().porcentajeGrasa
            if (primera != null && primera > 0 && ultima != null) {
                ((ultima - primera) / primera) * 100.0
            } else null
        } else null

        PhysiqueData(
            ultimaMedicion = ultimaMedicion,
            historialMediciones = historial,
            imc = imc,
            ffmi = ffmi,
            ratioCinturaHombro = ratioCinturaHombro,
            tendenciaPeso = tendenciaPeso,
            tendenciaGrasa = tendenciaGrasa,
            alturaUsuario = altura
        )
    }

    /** Si ya existe medición de hoy (409), hace PUT en su lugar. */
    suspend fun saveOrUpdateMedicion(dto: MedicionCorporalDTO): Result<MedicionCorporalDTO> = runCatching {
        try {
            api.saveMedicion(dto)
        } catch (e: ResponseException) {
            if (e.response.status == HttpStatusCode.Conflict) {
                val usuarioId = dto.usuarioId ?: throw e
                val medicionHoy = getMedicionDeHoy(usuarioId)
                    ?: throw Exception("No se pudo localizar la medición de hoy para actualizar")
                api.updateMedicion(medicionHoy.medicionId!!, dto)
            } else {
                throw e
            }
        }
    }

    /** Alias para compatibilidad con ViewModel anterior */
    suspend fun saveMedicion(dto: MedicionCorporalDTO): Result<MedicionCorporalDTO> =
        saveOrUpdateMedicion(dto)

    /** Obtiene la medición registrada hoy si existe, null si no */
    suspend fun getMedicionDeHoy(usuarioId: Int): MedicionCorporalDTO? {
        val hoy = getCurrentDateString()
        return runCatching {
            val todasHoy = api.getMedicionesByRange(usuarioId, hoy, hoy)
            todasHoy.firstOrNull()
        }.getOrNull()
    }

    /** Comprueba si el usuario ya tiene medición registrada hoy */
    suspend fun checkTieneMedicionHoy(usuarioId: Int): Boolean {
        return getMedicionDeHoy(usuarioId) != null
    }

    /** Borra la medición de hoy */
    suspend fun deleteMedicionHoy(usuarioId: Int): Result<Unit> = runCatching {
        api.deleteMedicionHoy(usuarioId)
        Unit
    }

    // Gráficas

    /** Carga las mediciones filtradas por rango y construye estadísticas para la métrica. */
    suspend fun getChartData(
        usuarioId: Int,
        metrica: ChartMetrica,
        rango: ChartRango
    ): Result<ChartData> = runCatching {
        val todasMediciones = runCatching {
            api.getAllMediciones(usuarioId)
        }.getOrElse {
            val hace365 = getDateMinusDaysString(365)
            api.getMedicionesByRange(usuarioId, hace365, getCurrentDateString())
        }

        // Filtrar por rango temporal
        val cutoff = rango.dias?.let { getDateMinusDaysString(it) }
        val medicionesFiltradas = if (cutoff != null) {
            todasMediciones.filter { m ->
                m.fecha?.take(10)?.let { isAfterDate(it, cutoff) } == true
            }
        } else {
            todasMediciones
        }.sortedBy { it.fecha }

        // Construir puntos del chart
        val puntos = medicionesFiltradas
            .mapNotNull { m ->
                val valor = metrica.extractValue(m)
                if (m.fecha != null && valor != null && valor > 0) {
                    ChartPoint(fecha = m.fecha.take(10), valor = valor)
                } else null
            }

        if (puntos.isEmpty()) {
            return@runCatching ChartData(
                puntos = emptyList(),
                metrica = metrica,
                min = 0.0, max = 0.0, media = 0.0,
                primero = null, ultimo = null,
                cambioAbsoluto = null, cambioPorcentaje = null
            )
        }

        val valores = puntos.map { it.valor }
        val minVal = valores.min()
        val maxVal = valores.max()
        val mediaVal = valores.average()
        val primero = puntos.first().valor
        val ultimo = puntos.last().valor
        val cambioAbs = ultimo - primero
        val cambioPct = if (primero > 0) (cambioAbs / primero) * 100.0 else null

        ChartData(
            puntos = puntos,
            metrica = metrica,
            min = minVal,
            max = maxVal,
            media = mediaVal,
            primero = primero,
            ultimo = ultimo,
            cambioAbsoluto = cambioAbs,
            cambioPorcentaje = cambioPct
        )
    }

    // Fotos de progreso

    suspend fun getFotosProgreso(usuarioId: Int): Result<List<FotoProgresoDTO>> =
        runCatching { api.getFotosProgreso(usuarioId) }

    suspend fun deleteFotoProgreso(fotoId: Int): Result<Unit> = runCatching {
        api.deleteFotoProgreso(fotoId)
        Unit
    }

    suspend fun saveRecord(dto: RecordPersonalDTO): Result<RecordPersonalDTO> =
        runCatching { api.saveRecord(dto) }


    // Rendimiento

    suspend fun getPerformanceData(usuarioId: Int): Result<PerformanceData> = runCatching {
        val sesiones = runCatching { api.getSesiones(usuarioId) }.getOrElse { emptyList() }
        val records = runCatching { api.getRecords(usuarioId) }.getOrElse { emptyList() }

        val hoy = getCurrentDateString()
        val hace7 = getDateMinusDaysString(7)
        val hace30 = getDateMinusDaysString(30)

        // Frecuencia: sesiones en los últimos 7 y 30 días
        val frecuenciaSemana = sesiones.count { s ->
            s.fechaInicio?.let { isAfterDate(it, hace7) } == true
        }
        val frecuenciaMes = sesiones.count { s ->
            s.fechaInicio?.let { isAfterDate(it, hace30) } == true
        }

        // Duración media de sesiones completadas
        val sesionesCompletadas = sesiones.filter { it.duracionMinutos != null && it.duracionMinutos > 0 }
        val duracionMedia = if (sesionesCompletadas.isNotEmpty()) {
            sesionesCompletadas.mapNotNull { it.duracionMinutos }.average()
        } else 0.0

        // Intensidad y recuperación medias (último mes)
        val sesionesRecientes = sesiones.filter { s ->
            s.fechaInicio?.let { isAfterDate(it, hace30) } == true
        }
        val intensidadMedia = sesionesRecientes.mapNotNull { it.nivelIntensidad }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }
        val recuperacionMedia = sesionesRecientes.mapNotNull { it.nivelRecuperacion }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }

        // Racha: días consecutivos con sesión hasta hoy
        val rachaActual = calcularRacha(sesiones)

        // Tonelaje mensual: para las sesiones del mes actual cargamos sus series
        val sesionesDelMes = sesiones.filter { s ->
            s.fechaInicio?.let { isAfterDate(it, hace30) } == true
        }.take(20) // Limitar a 20 sesiones para no saturar la red

        var tonelajeMensual = 0.0
        for (sesion in sesionesDelMes) {
            if (sesion.sesionId != null) {
                val series = runCatching { api.getSeriesBySesion(sesion.sesionId) }.getOrElse { emptyList() }
                val tonelajeSesion = series
                    .filter { it.completado == true && it.pesoKg != null && it.repeticionesRealizadas != null }
                    .sumOf { (it.pesoKg ?: 0.0) * (it.repeticionesRealizadas ?: 0) }
                tonelajeMensual += tonelajeSesion
            }
        }

        // Mejores records con 1RM estimado (Epley)
        val mejoresRecords = records
            .groupBy { it.nombreEjercicio ?: "Desconocido" }
            .mapValues { (_, recs) -> recs.maxByOrNull { it.pesoKg ?: 0.0 } }
            .values
            .filterNotNull()
            .map { r ->
                val peso = r.pesoKg ?: 0.0
                val reps = r.repeticiones ?: 1
                val rm = if (reps > 0) peso * (1.0 + reps / 30.0) else peso
                RecordConRm(
                    nombreEjercicio = r.nombreEjercicio ?: "Desconocido",
                    pesoKg = peso,
                    repeticiones = r.repeticiones,
                    rmEstimado = rm,
                    fecha = r.fecha
                )
            }
            .sortedByDescending { it.rmEstimado }

        PerformanceData(
            sesiones = sesiones,
            records = records,
            tonelajeMensual = tonelajeMensual,
            frecuenciaUltimaSemana = frecuenciaSemana,
            frecuenciaUltimoMes = frecuenciaMes,
            duracionMediaMin = duracionMedia,
            intensidadMedia = intensidadMedia,
            recuperacionMedia = recuperacionMedia,
            rachaActual = rachaActual,
            mejoresRecords = mejoresRecords
        )
    }

    // Nutrición

    suspend fun getNutritionData(usuarioId: Int, pesoActual: Double?): Result<NutritionData> = runCatching {
        val objetivo = runCatching { api.getObjetivoActual(usuarioId) }.getOrNull()
        val comidasHoy = runCatching { api.getComidasHoy(usuarioId) }.getOrElse { emptyList() }

        val hoy = getCurrentDateString()
        val hace7 = getDateMinusDaysString(7)
        val comidasSemana = runCatching {
            api.getComidasByRange(usuarioId, hace7, hoy)
        }.getOrElse { emptyList() }

        val calHoy = comidasHoy.sumOf { it.calorias ?: 0.0 }
        val protHoy = comidasHoy.sumOf { it.proteinas ?: 0.0 }
        val carbsHoy = comidasHoy.sumOf { it.carbohidratos ?: 0.0 }
        val grasHoy = comidasHoy.sumOf { it.grasasSaturadas ?: 0.0 }

        val adherenciaCal = objetivo?.caloriasObjetivo?.let { obj ->
            if (obj > 0) (calHoy / obj) * 100.0 else null
        }
        val adherenciaProt = objetivo?.proteinasObjetivo?.let { obj ->
            if (obj > 0) (protHoy / obj) * 100.0 else null
        }
        val adherenciaCarbs = objetivo?.carbohidratosObjetivo?.let { obj ->
            if (obj > 0) (carbsHoy / obj) * 100.0 else null
        }
        val adherenciaGras = objetivo?.grasasObjetivo?.let { obj ->
            if (obj > 0) (grasHoy / obj) * 100.0 else null
        }

        // Calorías medias última semana
        val calPorDia = comidasSemana.groupBy { it.fecha?.take(10) ?: "" }
            .values.map { dia -> dia.sumOf { it.calorias ?: 0.0 } }
        val calMediasSemana = if (calPorDia.isEmpty()) 0.0 else calPorDia.average()

        // Proteína por kg
        val proteinaPorKg = if (pesoActual != null && pesoActual > 0) protHoy / pesoActual else null

        NutritionData(
            adherenciaCaloricaHoy = adherenciaCal,
            adherenciaProteinasHoy = adherenciaProt,
            adherenciaCarbosHoy = adherenciaCarbs,
            adherenciaGrasasHoy = adherenciaGras,
            proteinaPorKg = proteinaPorKg,
            caloriasMediasSemana = calMediasSemana
        )
    }

    // Helpers de fecha

    private fun getCurrentDateString(): String {
        return Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
    }

    private fun getDateMinus30DaysString(): String = getDateMinusDaysString(30)

    private fun getDateMinusDaysString(days: Int): String {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return today.minus(DatePeriod(days = days)).toString()
    }

    private fun isAfterDate(fechaStr: String, cutoffStr: String): Boolean {
        val fecha = fechaStr.take(10)
        val cutoff = cutoffStr.take(10)
        return fecha >= cutoff
    }

    private fun calcularRacha(sesiones: List<SesionStatsDTO>): Int {
        val diasConSesion = sesiones
            .mapNotNull { it.fechaInicio?.take(10)?.let(LocalDate::parse) }
            .toSet()
        if (diasConSesion.isEmpty()) return 0

        var dia = Clock.System.todayIn(TimeZone.currentSystemDefault())
        var racha = 0
        while (dia in diasConSesion) {
            racha++
            dia = dia.minus(1, DateTimeUnit.DAY)
        }
        return racha
    }
}
