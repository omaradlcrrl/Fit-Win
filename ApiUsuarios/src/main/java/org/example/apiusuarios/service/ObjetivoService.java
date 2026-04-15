package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.ObjetivoDTO;
import org.example.apiusuarios.model.Objetivo;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.ObjetivoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjetivoService {

    @Autowired
    private ObjetivoRepository objetivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ObjetivoDTO generarAutomatico(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Double altura = usuario.getAltura();
        if (altura == null || altura <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta altura del usuario");
        }

        Double peso = usuario.getPesoActual();
        if (peso == null || peso <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta peso actual del usuario");
        }

        double imc = peso / (altura * altura);

        int edad = 25;
        if (usuario.getFechaNacimiento() != null) {
            edad = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();
        }

        // Mifflin-St Jeor (Usando Strings)
        double tmb;
        String gen = (usuario.getGenero() != null) ? usuario.getGenero().toUpperCase() : "MASCULINO";
        if (gen.equals("FEMENINO")) {
            tmb = 10 * peso + 6.25 * (altura * 100) - 5 * edad - 161;
        } else {
            tmb = 10 * peso + 6.25 * (altura * 100) - 5 * edad + 5;
        }

        // Mapear String NivelActividad a valor double
        double actividad = 1.2; // Default SEDENTARIO
        if (usuario.getNivelActividad() != null) {
            String activ = usuario.getNivelActividad().toUpperCase();
            switch (activ) {
                case "LIGERO":
                    actividad = 1.375;
                    break;
                case "MODERADO":
                    actividad = 1.55;
                    break;
                case "ACTIVO":
                    actividad = 1.725;
                    break;
                case "MUY_ACTIVO":
                    actividad = 1.9;
                    break;
                default:
                    actividad = 1.2;
            }
        }

        double caloriasBase = tmb * actividad;

        String estrategia = (usuario.getEstrategia() != null)
                ? usuario.getEstrategia().trim().toUpperCase()
                : "MANTENIMIENTO";

        int ajuste = (usuario.getAjusteCalorico() != null)
                ? usuario.getAjusteCalorico()
                : 10;

        if (!estrategia.equals("MANTENIMIENTO")
                && !estrategia.equals("SUPERAVIT")
                && !estrategia.equals("DEFICIT")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estrategia inválida");
        }

        if (ajuste < 0 || ajuste > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ajusteCalorico debe estar entre 0 y 50");
        }

        double factor;
        switch (estrategia) {
            case "SUPERAVIT" -> factor = (ajuste / 100.0);
            case "DEFICIT" -> factor = -(ajuste / 100.0);
            default -> factor = 0.0;
        }

        double calorias = caloriasBase * (1.0 + factor);

        double proteinas = peso * 2.0;
        double grasas = calorias * 0.25 / 9;
        double carbohidratos = (calorias - (proteinas * 4 + grasas * 9)) / 4;

        LocalDate hoy = LocalDate.now();
        Objetivo objetivo;

        if (objetivoRepository.existsByUsuarioAndFechaInicio(usuario, hoy)) {
            objetivo = objetivoRepository
                    .findFirstByUsuarioAndFechaInicioOrderByObjetivoIdDesc(usuario, hoy)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "No se encontró el objetivo de hoy"));
        } else {
            objetivo = new Objetivo();
            objetivo.setUsuario(usuario);
            objetivo.setFechaInicio(hoy);
            objetivo.setFechaFin(hoy.plusDays(1));
            objetivo.setActivo(true);
        }

        objetivo.setImc(imc);
        objetivo.setTipo(estrategia);
        objetivo.setCaloriasObjetivo(calorias);
        objetivo.setProteinasObjetivo(proteinas);
        objetivo.setCarbohidratosObjetivo(carbohidratos);
        objetivo.setGrasasObjetivo(grasas);

        Objetivo guardado = objetivoRepository.save(objetivo);
        return new ObjetivoDTO(guardado);
    }

    public ObjetivoDTO getObjetivoActual(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Objetivo objetivo = objetivoRepository
                .findFirstByUsuarioOrderByFechaInicioDesc(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin objetivos para el usuario"));
        return new ObjetivoDTO(objetivo);
    }

    public ObjetivoDTO getObjetivoDeHoy(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        LocalDate hoy = LocalDate.now();

        Objetivo objetivo = objetivoRepository
                .findFirstByUsuarioAndFechaInicioOrderByObjetivoIdDesc(usuario, hoy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin objetivo para hoy"));

        return new ObjetivoDTO(objetivo);
    }

    public List<ObjetivoDTO> findAllByUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return objetivoRepository.findByUsuarioOrderByFechaInicioDesc(usuario).stream()
                .map(ObjetivoDTO::new)
                .collect(Collectors.toList());
    }

    public List<ObjetivoDTO> getByUsuarioAndFechaBetween(Integer usuarioId, LocalDate from, LocalDate to) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return objetivoRepository.findByUsuarioAndFechaInicioBetween(usuario, from, to).stream()
                .map(ObjetivoDTO::new)
                .collect(Collectors.toList());
    }

    public ObjetivoDTO findById(Integer id) {
        Objetivo o = objetivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado"));
        return new ObjetivoDTO(o);
    }

    public void deleteById(Integer id) {
        if (!objetivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado");
        }
        objetivoRepository.deleteById(id);
    }
}