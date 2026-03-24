package org.example.fitwin.util;

public class SessionControl {
    private static SessionControl instance;
    private Integer usuarioId;
    private SessionControl() {
    }

    public static SessionControl getInstance() {
        if (instance == null) {
            instance = new SessionControl();
        }
        return instance;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }




public void cerrar() {
        this.usuarioId = null;
    }
}
