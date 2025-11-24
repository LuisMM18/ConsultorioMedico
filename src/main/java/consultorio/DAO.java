package consultorio;

import consultorio.controlador.PacientesViewController;
import consultorio.model.CitaCalendario;
import consultorio.model.Nota;
import consultorio.model.Paciente;
import consultorio.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

                rolUsuario(usuario);

                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void rolUsuario(String usuarioLogeado) {
        String sql = "SELECT idUsuario, rol FROM usuarios WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuarioLogeado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int rol = rs.getInt("rol");
                    int id = rs.getInt("idUsuario");

                    Rol.getInstance().setRol(rol);
                    Rol.getInstance().setIdUsuario(id);

                    System.out.println("Usuario ID: " + id + " Rol: " + rol);
                } else {
                    System.out.println("No se encontró el usuario: " + usuarioLogeado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario getUsuarioPorId(int idUsuario) {
        String sql = "SELECT idUsuario, nombre, apellidoUNO, apellidoDOS, email, telefono, contrasena FROM usuarios WHERE idUsuario = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellidoUNO(rs.getString("apellidoUNO"));
                    u.setApellidoDOS(rs.getString("apellidoDOS"));
                    u.setCorreo(rs.getString("email"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setContrasena(rs.getString("contrasena"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarUsuario(int idUsuario, String nombreCompleto, String correo, String telefono, String nuevaContrasena) {
        boolean cambiarPass = (nuevaContrasena != null && !nuevaContrasena.isEmpty());

        String[] partes = (nombreCompleto != null) ? nombreCompleto.split(" ") : new String[0];
        String nombre = (partes.length > 0) ? partes[0] : "";
        String ap1 = (partes.length > 1) ? partes[1] : "";
        String ap2 = (partes.length > 2) ? String.join(" ", java.util.Arrays.copyOfRange(partes, 2, partes.length)) : "";

        String sql;
        if (cambiarPass) {
            sql = "UPDATE usuarios SET nombre=?, apellidoUNO=?, apellidoDOS=?, email=?, telefono=?, contrasena=? WHERE idUsuario=?";
        } else {
            sql = "UPDATE usuarios SET nombre=?, apellidoUNO=?, apellidoDOS=?, email=?, telefono=? WHERE idUsuario=?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, ap1);
            ps.setString(3, ap2);
            ps.setString(4, correo);
            ps.setString(5, telefono);

            if (cambiarPass) {
                ps.setString(6, nuevaContrasena);
                ps.setInt(7, idUsuario);
            } else {
                ps.setInt(6, idUsuario);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Usuario> getAllUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT idUsuario, nombre, apellidoUNO, apellidoDOS, email FROM usuarios WHERE activo = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidoUNO(rs.getString("apellidoUNO"));
                u.setApellidoDOS(rs.getString("apellidoDOS"));
                u.setCorreo(rs.getString("email"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Código de Calendario
    public List<CitaCalendario> getCitasForDate(LocalDate fecha) {
        String sql = "SELECT c.idCitas, c.idUsuarioRef, c.idPacienteRef, c.fechaHora, c.tipoConsulta, c.activo, " +
                "p.nombre AS pnombre, p.apellidoUNO AS pap1, p.apellidoDOS AS pap2, " +
                "u.nombre AS unombre, u.apellidoUNO AS uap1 " +
                "FROM citas c " +
                "LEFT JOIN pacientes p ON c.idPacienteRef = p.idPaciente " +
                "LEFT JOIN usuarios u ON c.idUsuarioRef = u.idUsuario " +
                "WHERE c.fechaHora >= ? AND c.fechaHora < ? AND c.activo = 1 " +
                "ORDER BY c.fechaHora";

        List<CitaCalendario> lista = new ArrayList<>();
        ZoneId zone = ZoneId.of("America/Hermosillo");
        ZonedDateTime zInicio = fecha.atStartOfDay(zone);
        ZonedDateTime zFin = fecha.plusDays(1).atStartOfDay(zone);
        Timestamp tsInicio = Timestamp.from(zInicio.toInstant());
        Timestamp tsFin = Timestamp.from(zFin.toInstant());

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, tsInicio);
            ps.setTimestamp(2, tsFin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CitaCalendario c = new CitaCalendario();
                    c.setIdCitas(rs.getInt("idCitas"));
                    c.setIdUsuarioRef(rs.getInt("idUsuarioRef"));
                    c.setIdPacienteRef(rs.getInt("idPacienteRef"));
                    Timestamp ts = rs.getTimestamp("fechaHora");
                    if (ts != null) c.setFechaHora(ts.toLocalDateTime());
                    c.setActivo(rs.getBoolean("activo"));

                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
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
                    c.setActivo(rs.getBoolean("activo"));
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

    // CITAS
    public boolean actualizarCita(int idCitas, int idPaciente, LocalDateTime nuevaFechaHora, String nuevoTipo) {
        // 1. Validación (Opcional, si quieres evitar citas en el pasado)
        if (nuevaFechaHora.toLocalDate().isBefore(LocalDate.now())) {
            System.out.println("Error: No se puede mover una cita al pasado.");
            return false;
        }

        String sql = "UPDATE citas SET idPacienteRef = ?, fechaHora = ?, tipoConsulta = ? WHERE idCitas = ? AND activo = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // --- CORRECCIÓN DE ÍNDICES AQUÍ ---
            ps.setInt(1, idPaciente);                           // 1er interrogación
            ps.setTimestamp(2, Timestamp.valueOf(nuevaFechaHora)); // 2da interrogación
            ps.setString(3, nuevoTipo);                         // 3ra interrogación
            ps.setInt(4, idCitas);                              // 4ta interrogación

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Cita ID " + idCitas + " actualizada correctamente.");
                return true;
            } else {
                System.out.println("Advertencia: No se encontró la cita ID " + idCitas + ".");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar cita: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean crearCita(int idUsuarioRef, int idPacienteRef, LocalDateTime fechaHora, String tipoConsulta) {
        if (fechaHora.toLocalDate().isBefore(LocalDate.now())) {
            System.out.println("Intento de Agendar Cita en una fecha anterior (" + fechaHora + ")");
            return false;
        }

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

    // Reportes
    public boolean crearReporte(int idUsuarioRef, String titulo, String contenido, LocalDate fecha) {
        String sql = "INSERT INTO reportes (idUsuarioRef, titulo, contenido, fecha) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuarioRef);
            ps.setString(2, titulo);
            ps.setString(3, contenido);

            LocalDate fechaFinal = (fecha != null) ? fecha : LocalDate.now();
            ps.setDate(4, java.sql.Date.valueOf(fechaFinal));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Notas
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

    public boolean eliminarNota(int idNotas) {
        final String sql = "DELETE FROM notas WHERE idNotas=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idNotas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

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

    public List<CitaCalendario> getCitasCalendarioForDate(LocalDate fecha) {
        String sql = """
        SELECT 
            c.idCitas, 
            c.idUsuarioRef, 
            c.idPacienteRef, 
            c.fechaHora, 
            c.tipoConsulta, 
            c.activo,
            CONCAT_WS(' ', p.nombre, p.apellidoUNO, p.apellidoDOS) AS pacienteNombre,
            CONCAT_WS(' ', u.nombre, u.apellidoUNO) AS usuarioNombre
        FROM citas c
        LEFT JOIN pacientes p ON c.idPacienteRef = p.idPaciente
        LEFT JOIN usuarios u ON c.idUsuarioRef = u.idUsuario
        WHERE c.fechaHora >= ? AND c.fechaHora < ? AND c.activo = 1
        ORDER BY c.fechaHora
    """;

        List<CitaCalendario> citas = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, fecha.atStartOfDay());
            ps.setObject(2, fecha.plusDays(1).atStartOfDay());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CitaCalendario cita = new CitaCalendario();
                    cita.setIdCitas(rs.getInt("idCitas"));
                    cita.setIdUsuarioRef(rs.getInt("idUsuarioRef"));
                    cita.setIdPacienteRef(rs.getInt("idPacienteRef"));
                    cita.setFechaHora(rs.getTimestamp("fechaHora").toLocalDateTime());
                    cita.setTipoConsulta(rs.getString("tipoConsulta"));
                    cita.setActivo(rs.getBoolean("activo"));
                    cita.setPacienteNombre(rs.getString("pacienteNombre"));
                    cita.setUsuarioNombre(rs.getString("usuarioNombre"));
                    citas.add(cita);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }

    // Pacientes - Métodos de la primera clase
    public List<PacientesViewController.Paciente> getAllPacientes() {
        List<PacientesViewController.Paciente> lista = new ArrayList<>();
        String sql = "SELECT idPaciente, nombre, apellidoUNO, apellidoDOS, fechaNacimiento, telefono, correo FROM pacientes WHERE activo = 1";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("idPaciente");
                String nombre = rs.getString("nombre");
                String ap1 = rs.getString("apellidoUNO");
                String ap2 = rs.getString("apellidoDOS");
                String nombreCompleto = buildFullName(nombre, ap1, ap2);

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

    public boolean crearPaciente(String nombreCompleto, LocalDate fechaNacimiento, String telefono, String correo) {
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

    // PACIENTES - Métodos adicionales de la segunda clase
    public List<Paciente> getPacientesActivosBasico() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT idPaciente, nombre, apellidoUNO, apellidoDOS " +
                "FROM pacientes WHERE activo = 1 " +
                "ORDER BY nombre, apellidoUNO, apellidoDOS";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(rs.getInt("idPaciente"));
                p.setNombre(rs.getString("nombre"));
                p.setApellidoUNO(rs.getString("apellidoUNO"));
                p.setApellidoDOS(rs.getString("apellidoDOS"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Generación de Reporte (Excel / PDF) - Método adicional de la primera clase
    public List<CitaCalendario> getCitasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
            SELECT 
                c.idCitas, 
                c.idUsuarioRef, 
                c.idPacienteRef, 
                c.fechaHora, 
                c.tipoConsulta, 
                c.activo,
                CONCAT_WS(p.nombre, ' ', p.apellidoUNO, ' ', p.apellidoDOS) AS pacienteNombre,
                CONCAT_WS(u.nombre, ' ', u.apellidoUNO) AS usuarioNombre
            FROM citas c
            LEFT JOIN pacientes p ON c.idPacienteRef = p.idPaciente
            LEFT JOIN usuarios u ON c.idUsuarioRef = u.idUsuario
            WHERE c.fechaHora >= ? AND c.fechaHora < ? 
            ORDER BY c.fechaHora
        """;

        List<CitaCalendario> citas = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, fechaInicio.atStartOfDay());
            ps.setObject(2, fechaFin.plusDays(1).atStartOfDay());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CitaCalendario cita = new CitaCalendario();
                    cita.setIdCitas(rs.getInt("idCitas"));
                    cita.setIdUsuarioRef(rs.getInt("idUsuarioRef"));
                    cita.setIdPacienteRef(rs.getInt("idPacienteRef"));

                    java.sql.Timestamp ts = rs.getTimestamp("fechaHora");
                    if (ts != null) {
                        cita.setFechaHora(ts.toLocalDateTime());
                    }

                    cita.setTipoConsulta(rs.getString("tipoConsulta"));
                    cita.setActivo(rs.getBoolean("activo"));
                    cita.setPacienteNombre(rs.getString("pacienteNombre"));
                    cita.setUsuarioNombre(rs.getString("usuarioNombre"));
                    citas.add(cita);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
}//DAO