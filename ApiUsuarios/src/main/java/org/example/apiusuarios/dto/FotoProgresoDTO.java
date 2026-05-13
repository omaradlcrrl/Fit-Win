package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.FotoProgreso;
import org.example.apiusuarios.model.TipoFoto;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FotoProgresoDTO {

    private Integer fotoId;
    private String urlFoto;
    private TipoFoto tipoFoto;
    private LocalDate fecha;
    private Integer usuarioId;

    public FotoProgresoDTO(FotoProgreso f) {
        this.fotoId = f.getFotoId();
        this.urlFoto = f.getUrlFoto();
        this.tipoFoto = f.getTipoFoto();
        this.fecha = f.getFecha();
        this.usuarioId = f.getUsuario() != null ? f.getUsuario().getUsuarioId() : null;
    }
}
