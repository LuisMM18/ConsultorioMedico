package consultorio;

public class Rol {

    private static Rol instance;
    private int rol;
    private int idUsuario;

    private Rol() {

    }

    public static Rol getInstance() {
        if (instance == null) {
            instance = new Rol();
        }
        return instance;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public static void clear() {
        instance = null;
    }
}