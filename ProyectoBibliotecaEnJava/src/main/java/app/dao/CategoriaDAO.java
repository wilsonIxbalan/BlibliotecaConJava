// Archivo: src/main/java/app/dao/CategoriaDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public int insertar(Categoria c) throws SQLException {
        String sql = "INSERT INTO Categoria (nombre, estado) VALUES (?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<Categoria> listar() throws SQLException {
        String sql = "SELECT id, nombre, estado FROM Categoria ORDER BY id DESC";
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapCategoria(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE Categoria SET nombre = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getEstado());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Categoria SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Categoria> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, estado FROM Categoria WHERE nombre LIKE ? ORDER BY id DESC";
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCategoria(rs));
                }
            }
        }
        return lista;
    }
//Mapear Categoria
    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getInt("estado")
        );
    }
}