package app.dao;

import app.db.Conexion;
import app.model.Prestamo;
import app.model.PrestamoConEntidades;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    // INSERT: crea un Prestamo y devuelve el id generado
    public int insertar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO Prestamo (idCliente, idLibro, fecha, idUsuario, estado) VALUES (?, ?, GETDATE(), ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setInt(3, p.getIdUsuario());
            ps.setInt(4, p.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    // UPDATE: actualiza el estado (devuelto o activo)
    public boolean actualizar(Prestamo p) throws SQLException {
        String sql = "UPDATE Prestamo SET idCliente = ?, idLibro = ?, idUsuario = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setInt(3, p.getIdUsuario());
            ps.setInt(4, p.getEstado());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // MÉTODO CORREGIDO PARA ELIMINACIÓN LÓGICA POR ID
    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Prestamo SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // SELECT con JOIN para obtener los nombres de cliente, libro y usuario
    public List<PrestamoConEntidades> listar() throws SQLException {
        String sql = """
                    SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, u.nombre AS usuarioNombre, p.estado
                    FROM Prestamo p
                    JOIN Cliente c ON c.id = p.idCliente
                    JOIN Libro l ON l.id = p.idLibro
                    JOIN Usuario u ON u.id = p.idUsuario
                    ORDER BY p.id DESC
                    """;
        List<PrestamoConEntidades> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new PrestamoConEntidades(
                        rs.getInt("id"),
                        rs.getString("clienteNombre"),
                        rs.getString("libroNombre"),
                        rs.getDate("fecha"),
                        rs.getString("usuarioNombre"),
                        rs.getInt("estado")
                ));
            }
        }
        return lista;
    }

    // Método para buscar préstamos por el nombre del cliente
    public List<PrestamoConEntidades> buscarPorNombreCliente(String nombre) throws SQLException {
        String sql = """
                    SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, p.fecha, u.nombre AS usuarioNombre, p.estado
                    FROM Prestamo p
                    JOIN Cliente c ON c.id = p.idCliente
                    JOIN Libro l ON l.id = p.idLibro
                    JOIN Usuario u ON u.id = p.idUsuario
                    WHERE c.nombre LIKE ?
                    ORDER BY p.id DESC
                    """;
        List<PrestamoConEntidades> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PrestamoConEntidades(
                            rs.getInt("id"),
                            rs.getString("clienteNombre"),
                            rs.getString("libroNombre"),
                            rs.getDate("fecha"),
                            rs.getString("usuarioNombre"),
                            rs.getInt("estado")
                    ));
                }
            }
        }
        return lista;
    }


    public boolean verificarLibroDisponible(int idLibro) throws SQLException {
        // Busca préstamos activos (estado = 1) para el libro
        String sql = "SELECT COUNT(*) AS count FROM Prestamo WHERE idLibro = ? AND estado = 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    // Retorna true si el libro está disponible (count es 0), false si está prestado
                    return count == 0;
                }
            }
        }
        return false; // En caso de error, asumimos que no está disponible por seguridad.
    }

}