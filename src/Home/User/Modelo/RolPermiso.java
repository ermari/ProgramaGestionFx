package Home.User.Modelo;


public class RolPermiso {
    private int rolId;
    private int permisoId;
    private Permiso permiso; // útil para mostrar en la UI

    public RolPermiso() {}

    public RolPermiso(int rolId, int permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    public RolPermiso(int rolId, Permiso permiso) {
        this.rolId = rolId;
        this.permiso = permiso;
        if (permiso != null) {
            this.permisoId = permiso.getPermisoId();
        }
    }

    // Getters y Setters

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public int getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(int permisoId) {
        this.permisoId = permisoId;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
        if (permiso != null) {
            this.permisoId = permiso.getPermisoId();
        }
    }

    @Override
    public String toString() {
        return permiso != null ? permiso.getDescripcion() : "Permiso ID: " + permisoId;
    }
}
