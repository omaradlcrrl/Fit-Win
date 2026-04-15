package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.UsuarioDTO;
import org.example.apiusuarios.dto.login.LoginRequest;
import org.example.apiusuarios.dto.login.LoginResponse;
import org.example.apiusuarios.exception.CredencialesInvalidasException;
import org.example.apiusuarios.exception.RecursoNoEncontradoException;
import org.example.apiusuarios.model.RefreshToken;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public UsuarioDTO save(UsuarioDTO usuarioDTO) {

        String correo = usuarioDTO.getCorreoElectronico() != null
                ? usuarioDTO.getCorreoElectronico().trim().toLowerCase()
                : null;

        if (correo == null || correo.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico es obligatorio");

        if (!correo.matches("^.+@.+\\..+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de correo inválido");

        if (usuarioRepository.findByCorreoElectronico(correo).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");

        String pass = usuarioDTO.getPassword();
        if (pass == null || pass.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        if (pass.length() < 6 || pass.length() > 60)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener entre 6 y 60 caracteres");

        String est = (usuarioDTO.getEstrategia() != null)
                ? usuarioDTO.getEstrategia().trim().toUpperCase()
                : "MANTENIMIENTO";
        if (!est.equals("MANTENIMIENTO") && !est.equals("SUPERAVIT") && !est.equals("DEFICIT"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estrategia inválida");
        usuarioDTO.setEstrategia(est);

        Integer ajuste = (usuarioDTO.getAjusteCalorico() != null) ? usuarioDTO.getAjusteCalorico() : 10;
        if (ajuste < 0 || ajuste > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ajusteCalorico debe estar entre 0 y 50");
        usuarioDTO.setAjusteCalorico(ajuste);

        usuarioDTO.setCorreoElectronico(correo);

        if (usuarioDTO.getGenero() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El género es obligatorio");
        }
        if (usuarioDTO.getNivelActividad() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nivel de actividad es obligatorio");
        }

        // CAMBIO: Usar passwordEncoder
        usuarioDTO.setPassword(passwordEncoder.encode(pass.trim()));

        // Mapeo manual DTO -> Entidad
        Usuario nuevoUsuario = convertirAEntidad(usuarioDTO);
        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        return new UsuarioDTO(guardado);
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public UsuarioDTO findById(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return new UsuarioDTO(u);
    }

    public void deleteById(Integer id) {
        if (!usuarioRepository.existsById(id))
            throw new RecursoNoEncontradoException("Usuario no encontrado");

        // Seguridad: Verificar si quien borra es ADMIN o el propio usuario
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario currentUser = usuarioRepository.findByCorreoElectronico(currentEmail)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario actual no encontrado"));

        boolean isAdmin = (currentUser.getRole() != null) && currentUser.getRole().name().equals("ADMIN");
        boolean isSelf = currentUser.getUsuarioId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("No tienes permiso para eliminar este usuario");
        }

        usuarioRepository.deleteById(id);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // CAMBIO: Lógica de login con Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreoElectronico(),
                            loginRequest.getPassword()));
        } catch (Exception e) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByCorreoElectronico(loginRequest.getCorreoElectronico())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        // CAMBIO: Generar Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
        String jwtToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.crear(usuario.getUsuarioId());

        LoginResponse response = new LoginResponse();
        response.setMensaje("Inicio de sesión exitoso");
        response.setToken(jwtToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUsuarioId(usuario.getUsuarioId());

        return response;
    }

    public UsuarioDTO update(Integer id, UsuarioDTO updates) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (updates.getNombre() != null)
            usuario.setNombre(updates.getNombre());

        if (updates.getApellidos() != null)
            usuario.setApellidos(updates.getApellidos());

        if (updates.getCorreoElectronico() != null) {
            String nuevoCorreo = updates.getCorreoElectronico().trim().toLowerCase();

            if (!nuevoCorreo.matches("^.+@.+\\..+$"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de correo inválido");

            if (!nuevoCorreo.equals(usuario.getCorreoElectronico())
                    && usuarioRepository.findByCorreoElectronico(nuevoCorreo).isPresent())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
            usuario.setCorreoElectronico(nuevoCorreo);
        }

        if (updates.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(updates.getFechaNacimiento());
        }

        if (updates.getAltura() != null) {
            usuario.setAltura(updates.getAltura());
        }

        if (updates.getPassword() != null && !updates.getPassword().trim().isEmpty()) {
            String nuevaPass = updates.getPassword().trim();
            if (nuevaPass.length() < 6 || nuevaPass.length() > 60)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contraseña debe tener entre 6 y 60 caracteres");
            // CAMBIO: Usar passwordEncoder
            usuario.setPassword(passwordEncoder.encode(nuevaPass));
        }

        if (updates.getEstrategia() != null) {
            String e = updates.getEstrategia().trim().toUpperCase();
            if (!e.equals("MANTENIMIENTO") && !e.equals("SUPERAVIT") && !e.equals("DEFICIT"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estrategia inválida");
            usuario.setEstrategia(e);
        }

        if (updates.getAjusteCalorico() != null) {
            Integer pct = updates.getAjusteCalorico();
            if (pct < 0 || pct > 50)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ajusteCalorico debe estar entre 0 y 50");
            usuario.setAjusteCalorico(pct);
        }

        if (updates.getGenero() != null) {
            usuario.setGenero(updates.getGenero());
        }

        if (updates.getNivelActividad() != null) {
            usuario.setNivelActividad(updates.getNivelActividad());
        }

        if (updates.getPesoActual() != null) {
            usuario.setPesoActual(updates.getPesoActual());
        }

        if (updates.getOnboardingCompleto() != null) {
            usuario.setOnboardingCompleto(updates.getOnboardingCompleto());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioDTO(actualizado);
    }

    // Método privado para mapear DTO -> Entidad (Antes estaba en la Fábrica)
    private Usuario convertirAEntidad(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setUsuarioId(dto.getUsuarioId());
        u.setNombre(dto.getNombre());
        u.setApellidos(dto.getApellidos());
        u.setCorreoElectronico(dto.getCorreoElectronico());
        u.setPassword(dto.getPassword());
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setFechaRegistro(dto.getFechaRegistro());
        u.setAltura(dto.getAltura());
        u.setIdioma(dto.getIdioma());
        u.setEstrategia(dto.getEstrategia());
        u.setAjusteCalorico(dto.getAjusteCalorico());
        u.setGenero(dto.getGenero());
        u.setNivelActividad(dto.getNivelActividad());
        u.setPesoActual(dto.getPesoActual());
        u.setOnboardingCompleto(dto.getOnboardingCompleto());
        return u;
    }
}