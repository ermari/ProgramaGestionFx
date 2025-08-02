package Login.model;

import Home.User.Modelo.Usuario;
import CatalogoGestion.Empresas.Modelo.Empresa;
import CatalogoGestion.Empresas.Modelo.Sucursal;

import java.util.List;

public class Sesion {

    private static Usuario usuarioActual;
    private static List<Sucursal> sucursalesDisponibles;
    private static List<Empresa> empresasDisponibles;
    private static Empresa empresaSeleccionada;
    private static Sucursal sucursalSeleccionada;

    // Usuario actual
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // Sucursales disponibles
    public static void setSucursalesDisponibles(List<Sucursal> sucursales) {
        sucursalesDisponibles = sucursales;
    }

    public static List<Sucursal> getSucursalesDisponibles() {
        return sucursalesDisponibles;
    }

    // Empresas disponibles
    public static void setEmpresasDisponibles(List<Empresa> empresas) {
        empresasDisponibles = empresas;
    }

    public static List<Empresa> getEmpresasDisponibles() {
        return empresasDisponibles;
    }

    // Empresa seleccionada
    public static void setEmpresaSeleccionada(Empresa empresa) {
        empresaSeleccionada = empresa;
    }

    public static Empresa getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    // Sucursal seleccionada
    public static void setSucursalSeleccionada(Sucursal sucursal) {
        sucursalSeleccionada = sucursal;
    }

    public static Sucursal getSucursalSeleccionada() {
        return sucursalSeleccionada;
    }

    // Cerrar sesión (limpiar todo)
    public static void cerrarSesion() {
        usuarioActual = null;
        sucursalesDisponibles = null;
        empresasDisponibles = null;
        empresaSeleccionada = null;
        sucursalSeleccionada = null;
    }
}
