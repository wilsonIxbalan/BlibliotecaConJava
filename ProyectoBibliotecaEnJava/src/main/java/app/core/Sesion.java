package app.core;

import app.model.Usuario;

public class Sesion {
    private static Usuario usuario;

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    public static boolean hasRole(String rol) {
        return usuario != null && usuario.getRol().equalsIgnoreCase(rol);
    }

    public static void login(Usuario user) {
        usuario = user;
    }

    // Método para cerrar sesión
    public static void cerrarSesion() {
        usuario = null;
    }
}
