package app.model;

public class Usuario {
    private int id;
    private final String username;
    private final String nombre;
    private String password; // Se almacena el hash BCrypt
    private final String rol;
    private final int estado;

    // Constructor para la creación de un nuevo usuario
    public Usuario(String username, String nombre, String password, String rol, int estado) {
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
    }

    // Constructor para cuando se recupera un usuario de la base de datos
    public Usuario(int id, String username, String nombre, String password, String rol, int estado) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
    }

    // Constructor para la actualización sin cambiar la contraseña
    public Usuario(int id, String username, String nombre, String rol, int estado) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ... (El resto de los getters para username, nombre, password, rol, estado)
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public int getEstado() { return estado; }
}