package app.model;

import java.sql.Date;

public class PrestamoConEntidades {
    private final int id;
    private final String clienteNombre;
    private final String libroNombre;
    private final Date fecha;
    private final String usuarioNombre;
    private final int estado;

    public PrestamoConEntidades(int id, String clienteNombre, String libroNombre, Date fecha, String usuarioNombre, int estado) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.libroNombre = libroNombre;
        this.fecha = fecha;
        this.usuarioNombre = usuarioNombre;
        this.estado = estado;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public String getLibroNombre() {
        return libroNombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public int getEstado() {
        return estado;
    }
}