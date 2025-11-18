package consultorio;

public class Rol {

    private static Rol instance;   // instancia única
    private int rol;

    private Rol() { } // constructor privado para evitar que otros creen instancias

    // Obtener la instancia única
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

    // Opcional: limpiar sesión
    public static void clear() {
        instance = null;
    }
}
