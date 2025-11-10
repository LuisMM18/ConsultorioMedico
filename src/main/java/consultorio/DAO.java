package consultorio;

import consultorio.controlador.PacientesViewController; // IMPORTANTE: Importar la clase Paciente
import consultorio.model.CitaCalendario;
import consultorio.model.Nota;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period; // Para calcular la edad
import java.util.ArrayList;
import java.util.List;

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

    //CITAS
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

    public boolean crearCita(int idUsuarioRef, int idPacienteRef, LocalDateTime fechaHora, String tipoConsulta) {
        String sql = "INSERT INTO citas (idUsuarioRef, idPacienteRef, fechaHora, tipoConsulta, activo) VALUES (?,?,?,?,1)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuarioRef);
            ps.setInt(2, idPacienteRef);
            ps.setTimestamp(3, Timestamp.valueOf(fechaHora));
            ps.setString(4, tipoConsulta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelarCita(int idCitas) {
        String sql = "UPDATE citas SET activo = 0 WHERE idCitas = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCitas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //crear Reporte-pendiente
    public boolean crearReporte(int idUsuarioRef, String titulo, String contenido, LocalDate fecha) {
        String sql = "INSERT INTO reportes (idUsuarioRef, titulo, contenido, fecha) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuarioRef);
            ps.setString(2, titulo);
            ps.setString(3, contenido);

            // Si no se pasa fecha, usa la actual
            LocalDate fechaFinal = (fecha != null) ? fecha : LocalDate.now();
            ps.setDate(4, java.sql.Date.valueOf(fechaFinal));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Notas-pendiente en BD
    //Crear Nota
    public Integer crearNota(int idCitasRef, String titulo, String textoNota, LocalDate fecha) {
        final String sql = "INSERT INTO notas (idCitasRef, titulo, textoNota, fechaNota) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCitasRef);
            ps.setString(2, titulo);
            ps.setString(3, textoNota);
            if (fecha != null) ps.setDate(4, java.sql.Date.valueOf(fecha));
            else ps.setNull(4, Types.DATE);

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    //act nota
    public boolean actualizarNota(int idNotas, String titulo, String textoNota, LocalDate fecha) {
        final String sql = "UPDATE notas SET titulo=?, textoNota=?, fechaNota=? WHERE idNotas=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, textoNota);
            if (fecha != null) ps.setDate(3, java.sql.Date.valueOf(fecha));
            else ps.setNull(3, Types.DATE);
            ps.setInt(4, idNotas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    //eliminar not
    public boolean eliminarNota(int idNotas) {
        final String sql = "DELETE FROM notas WHERE idNotas=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idNotas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    //obtener Notas por cita ID
    public List<Nota> getNotasPorCita(int idCitasRef) {
        final String sql = """
            SELECT idNotas, idCitasRef, titulo, textoNota, fechaNota
            FROM notas
            WHERE idCitasRef=?
            ORDER BY COALESCE(fechaNota, CURDATE()) DESC, idNotas DESC
        """;
        List<Nota> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCitasRef);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Nota n = new Nota();
                    n.setIdNotas(rs.getInt("idNotas"));
                    n.setIdCitasRef(rs.getInt("idCitasRef"));
                    n.setTitulo(rs.getString("titulo"));
                    n.setTextoNota(rs.getString("textoNota"));
                    Date d = rs.getDate("fechaNota");
                    n.setFechaNota(d != null ? d.toLocalDate() : null);
                    list.add(n);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // no termi
    public Integer getUltimaCitaIdPorPaciente(int idPaciente) {
        final String sql = "SELECT idCitas FROM citas WHERE idPacienteRef=? AND activo=1 ORDER BY fechaHora DESC LIMIT 1";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idCitas");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    //Pacientes

    public List<PacientesViewController.Paciente> getAllPacientes() {
        List<PacientesViewController.Paciente> lista = new ArrayList<>();
        // Asumo que tu tabla se llama 'pacientes' y tiene estas columnas. AJÚSTALAS SI ES NECESARIO.
        String sql = "SELECT idPaciente, nombre, apellidoUNO, apellidoDOS, fechaNacimiento, telefono, correo FROM pacientes WHERE activo = 1";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("idPaciente");
                String nombre = rs.getString("nombre");
                String ap1 = rs.getString("apellidoUNO");
                String ap2 = rs.getString("apellidoDOS");
                String nombreCompleto = buildFullName(nombre, ap1, ap2); // Reutilizamos tu método

                // Para calcular la edad, necesitamos la fecha de nacimiento
                Date fechaNacimientoSql = rs.getDate("fechaNacimiento");
                LocalDate fechaNacimiento = (fechaNacimientoSql != null) ? fechaNacimientoSql.toLocalDate() : null;

                String telefono = rs.getString("telefono");
                String correo = rs.getString("correo");

                lista.add(new PacientesViewController.Paciente(id, nombreCompleto, fechaNacimiento, telefono, correo));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarPaciente(int idPaciente) {
        String sql = "UPDATE pacientes SET activo = 0 WHERE idPaciente = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean crearPaciente (String nombreCompleto, LocalDate fechaNacimiento, String telefono, String correo) {
        String[] partesNombre = (nombreCompleto != null && !nombreCompleto.isEmpty()) ? nombreCompleto.split(" ") : new String[0];
        String nombre = (partesNombre.length > 0) ? partesNombre[0] : "";
        String ap1 = (partesNombre.length > 1) ? partesNombre[1] : null;
        String ap2 = null;
        if (partesNombre.length > 2) {
            ap2 = String.join(" ", java.util.Arrays.copyOfRange(partesNombre, 2, partesNombre.length));
    }
        String sql = "INSERT INTO pacientes (nombre, apellidoUNO, apellidoDOS, fechaNacimiento, telefono, correo, activo) VALUES (?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, nombre);
            ps.setString(2, ap1);
            ps.setString(3, ap2);

            if(fechaNacimiento != null) {
                ps.setDate(4, java.sql.Date.valueOf(fechaNacimiento));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, telefono);
            ps.setString(6, correo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}