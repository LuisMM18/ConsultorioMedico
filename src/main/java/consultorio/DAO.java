package consultorio;

import consultorio.model.CitaCalendario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<CitaCalendario> getCitasForMonth(int year, int month) {
        String sql = "SELECT c.idCitas, c.idUsuarioRef, c.idPacienteRef, c.fechaHora, c.tipoConsulta, c.activo, " +
                "p.nombre AS pnombre, p.apellidoUNO AS pap1, p.apellidoDOS AS pap2, " +
                "u.nombre AS unombre, u.apellidoUNO AS uap1 " +
                "FROM citas c " +
                "LEFT JOIN pacientes p ON c.idPacienteRef = p.idPaciente " +
                "LEFT JOIN usuarios u ON c.idUsuarioRef = u.idUsuario " +
                "WHERE YEAR(c.fechaHora)=? AND MONTH(c.fechaHora)=? AND c.activo=1 " +
                "ORDER BY c.fechaHora";
        List<CitaCalendario> lista = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CitaCalendario c = new CitaCalendario();
                    c.setIdCitas(rs.getInt("idCitas"));
                    c.setIdUsuarioRef(rs.getInt("idUsuarioRef"));
                    c.setIdPacienteRef(rs.getInt("idPacienteRef"));
                    Timestamp ts = rs.getTimestamp("fechaHora");
                    if (ts != null) c.setFechaHora(ts.toLocalDateTime());
                    c.setTipoConsulta(rs.getString("tipoConsulta"));
                    c.setActivo(rs.getBoolean("activo"));

                    String pnombre = rs.getString("pnombre");
                    String pap1 = rs.getString("pap1");
                    String pap2 = rs.getString("pap2");
                    c.setPacienteNombre(buildFullName(pnombre, pap1, pap2));

                    String unombre = rs.getString("unombre");
                    String uap1 = rs.getString("uap1");
                    c.setUsuarioNombre(buildFullName(unombre, uap1, null));

                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    private String buildFullName(String a, String b, String c) {
        StringBuilder sb = new StringBuilder();
        if (a != null && !a.isEmpty()) sb.append(a);
        if (b != null && !b.isEmpty()) { if (sb.length()>0) sb.append(" "); sb.append(b); }
        if (c != null && !c.isEmpty()) { if (sb.length()>0) sb.append(" "); sb.append(c); }
        return sb.toString().trim();
    }
    public boolean actualizarCita(int idCitas, LocalDateTime nuevaFechaHora, String nuevoTipo) {
        String sql = "UPDATE citas SET fechaHora = ?, tipoConsulta = ? WHERE idCitas = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(nuevaFechaHora));
            ps.setString(2, nuevoTipo);
            ps.setInt(3, idCitas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
