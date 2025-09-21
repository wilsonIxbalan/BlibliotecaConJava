package app.dao;

import app.core.PasswordUtil;
import app.db.Conexion;
import app.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Valida un usuario y su contraseña para el login usando BCrypt.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto Usuario si las credenciales son válidas, de lo contrario, null.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Usuario validarLogin(String username, String password) throws SQLException {
        String sql = "SELECT id, username, nombre, password, rol, estado FROM Usuario WHERE username = ? AND estado = 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    // Verifica si la contraseña ingresada coincide con el hash almacenado
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return mapUsuario(rs);
                    }
                }
            }
        }
        return null;
    }

    public int insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO Usuario (username, nombre, password, rol, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombre());
            // Hashea la contraseña antes de guardarla
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
            ps.setString(3, hashedPassword);
            ps.setString(4, u.getRol());
            ps.setInt(5, u.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    u.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza un usuario. Si la contraseña es null, no la actualiza.
     */
    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE Usuario SET username = ?, nombre = ?, rol = ?, estado = ? WHERE id = ?";
        // Si el usuario proporcionó una nueva contraseña, la incluimos en la consulta
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            sql = "UPDATE Usuario SET username = ?, nombre = ?, password = ?, rol = ?, estado = ? WHERE id = ?";
        }

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombre());
            int index = 3;

            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                // Hashea la nueva contraseña y la añade al PreparedStatement
                String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
                ps.setString(index++, hashedPassword);
            }

            ps.setString(index++, u.getRol());
            ps.setInt(index++, u.getEstado());
            ps.setInt(index, u.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, username, nombre, password, rol, estado FROM Usuario ORDER BY id DESC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }
        }
        return lista;
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Usuario SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Usuario> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, username, nombre, rol, estado FROM Usuario WHERE nombre LIKE ? ORDER BY id DESC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapUsuario(rs));
                }
            }
        }
        return lista;
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        // En el map, no hay necesidad de mapear el password
        return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("nombre"),
                rs.getString("password"),
                rs.getString("rol"),
                rs.getInt("estado")
        );
    }

    // ---- Crear el primer Usuario ----
    public int crearUsuario(String username, String plainPassword, String nombre, String rol) throws SQLException {
        return crearUsuario(username, plainPassword, nombre, rol, 1);
    }

    public int crearUsuario(String username, String plainPassword, String nombre, String rol, int estado) throws SQLException {
        String sql = "INSERT INTO usuario (username, password, nombre, rol, estado) VALUES (?,?,?,?,?)";
        String hash = PasswordUtil.hash(plainPassword);
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, nombre);
            ps.setString(4, rol);
            ps.setInt(5, estado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

}