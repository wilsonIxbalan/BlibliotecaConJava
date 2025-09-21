package app.dao;

import app.db.Conexion;
import app.model.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    // INSERT: crea un autor y devuelve el id generado
    public int insertar(Autor a) throws SQLException {
        String sql = "INSERT INTO autor (nombre, biografia, estado) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getNombre());
            ps.setString(2, a.getBiografia());
            ps.setInt(3, a.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        }
        return -1; // no se obtuvo id
    }

    // SELECT *: lista todos los autores (últimos primero)
    public List<Autor> listar() throws SQLException {
        String sql = "SELECT id, nombre, biografia, estado FROM autor ORDER BY id DESC";
        List<Autor> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapAutor(rs));
            }
        }
        return lista;
    }

    // SELECT WHERE id = ?
    public Autor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, biografia, estado FROM autor WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAutor(rs);
                }
            }
        }
        return null;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Autor a) throws SQLException {
        String sql = "UPDATE autor SET nombre = ?, biografia = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, a.getNombre());
            ps.setString(2, a.getBiografia());
            ps.setInt(3, a.getEstado());
            ps.setInt(4, a.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // DELETE físico (si prefieres baja lógica, cambia a: UPDATE autor SET estado=0 WHERE id=?)
    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Autor SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca autores por nombre.
     * @param nombre El nombre o parte del nombre a buscar.
     * @return Una lista de autores que coinciden con la búsqueda.
     * @throws SQLException Si ocurre un error de base de datos.
     */

    public List<Autor> buscarPorNombre(String nombre) throws SQLException {
        // La consulta usa LIKE para buscar coincidencias parciales y el % lo hace dinámico.
        String sql = "SELECT id, nombre, biografia, estado FROM Autor WHERE nombre LIKE ? ORDER BY id DESC";
        List<Autor> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se usa el signo % para que la búsqueda sea parcial (ej. "a%" encuentra "Ana", "Alex", etc.)
            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAutor(rs));
                }
            }
        }
        return lista;
    }


    // Metodo helper para mapear el resultado de la consulta a un objeto Autor
    private Autor mapAutor(ResultSet rs) throws SQLException {
        return new Autor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("biografia"),
                rs.getInt("estado")
        );
    }
}
