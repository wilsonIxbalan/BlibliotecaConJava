package app.model;

import java.sql.Date;

public class Prestamo {
    private int id;
    private int idCliente;
    private int idLibro;
    private Date fecha;
    private int idUsuario;
    private final int estado;

    // Constructor para insertar un nuevo préstamo
    public Prestamo(int idCliente, int idLibro, int idUsuario, int estado) {
        this.idCliente = idCliente;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.estado = estado;
    }

    // Constructor para recuperar un préstamo de la BD
    public Prestamo(int id, int idCliente, int idLibro, Date fecha, int idUsuario, int estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idLibro = idLibro;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.estado = estado;
    }

    // Constructor para la actualización
    public Prestamo(int id, int idCliente, int idLibro, int idUsuario, int estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.estado = estado;
    }

    // Constructor para la eliminación lógica
    public Prestamo(int id, int estado) {
        this.id = id;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdCliente() { return idCliente; }
    public int getIdLibro() { return idLibro; }
    public Date getFecha() { return fecha; }
    public int getIdUsuario() { return idUsuario; }
    public int getEstado() { return estado; }


    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", idCliente='" + idCliente + '\'' +
                ", idLibro='"+ idLibro + '\''+
                ", fecha='"+ fecha + '\''+
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }
}
