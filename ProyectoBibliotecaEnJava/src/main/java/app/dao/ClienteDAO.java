// Archivo: src/main/java/app/dao/ClienteDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    // INSERT: crea un Cliente y devuelve el id generado
    public int insertar(Cliente a) throws SQLException {
        String sql = "INSERT INTO Cliente (nombre, nit, telefono, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getNit());
            ps.setString(3, a.getTelefono());
            ps.setInt(4, a.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    // SELECT *: lista todos los Clientes
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM Cliente ORDER BY id DESC";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        }
        return lista;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Cliente a) throws SQLException {
        String sql = "UPDATE Cliente SET nombre = ?, nit = ?, telefono = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getNit());
            ps.setString(3, a.getTelefono());
            ps.setInt(4, a.getEstado());
            ps.setInt(5, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE físico (si prefieres baja lógica, cambia a: UPDATE Cliente SET estado=0 WHERE id=?)
    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Cliente SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Método para buscar clientes por nombre (similar a AutorDAO)
    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM Cliente WHERE nombre LIKE ? ORDER BY id DESC";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCliente(rs));
                }
            }
        }
        return lista;
    }

    // Helper: mapea un ResultSet a Cliente
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("nit"),
                rs.getString("telefono"),
                rs.getInt("estado")
        );
    }
}