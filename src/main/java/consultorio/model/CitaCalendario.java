package consultorio.model;

import java.time.LocalDateTime;

public class CitaCalendario {
    private int idCitas;
    private int idUsuarioRef;
    private int idPacienteRef;
    private LocalDateTime fechaHora;
    private String tipoConsulta;
    private boolean activo;

    // Campos extra para UI
    private String pacienteNombre;
    private String usuarioNombre;

    public CitaCalendario() {}

    public int getIdCitas() { return idCitas; }
    public void setIdCitas(int idCitas) { this.idCitas = idCitas; }

    public int getIdUsuarioRef() { return idUsuarioRef; }
    public void setIdUsuarioRef(int idUsuarioRef) { this.idUsuarioRef = idUsuarioRef; }

    public int getIdPacienteRef() { return idPacienteRef; }
    public void setIdPacienteRef(int idPacienteRef) { this.idPacienteRef = idPacienteRef; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

}
