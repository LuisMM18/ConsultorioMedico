package consultorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//documento para agregar funciones sql

public class DAO {
    public boolean DAOautenticarUsuario(String usuario, String contrasena) {
        String sql = "SELECT 1 FROM usuarios WHERE email = ? AND contrasena = ? AND activo = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // use a logger in real projects
            return false;
        }
    }
}
