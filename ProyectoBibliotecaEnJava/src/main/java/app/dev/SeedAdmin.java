package app.dev;

import app.dao.UsuarioDAO;

//Se ejecuta una sola vez para crear el usuario ADMIN
public class SeedAdmin {
    public static void main(String[] args) throws Exception {
        UsuarioDAO dao = new UsuarioDAO();
        int id = dao.crearUsuario("admin","Administrador", "admin123",  "ADMIN", 1);
        System.out.println("Admin creado id=" + id);
    }
}
