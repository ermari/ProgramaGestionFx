package Constantes;

import javafx.scene.effect.BoxBlur;

public class constantes {

    public static final String TITULO = "SISTEMA DE VENTA JJ";
    public static final Double ANCHO_MIN = 1000.00;
    public static final Double ALTURA_MIN = 600.00;

    public static final String PACKAGE_IMG = "/Imagenes/";
    public static final String PACKAGE_VISTA = "/Vista/";

    public static final String VIEW_LOGIN = PACKAGE_VISTA + "Login.fxml";
    public static final String VIEW_MENU = PACKAGE_VISTA + "menuView.fxml";

    public static final String ICONO_PROGRAMA = PACKAGE_IMG + "logoJJ.jpg";
    public static final String IMG_INFORMACION = PACKAGE_IMG + "information.png";
    public static final String IMG_ERROR = PACKAGE_IMG + "error.png";
    public static final String IMG_EXITO = PACKAGE_IMG + "success.png";
    
    public static final String MENSAJE_ERROR_CONEXION_MYSQL = "Se produjo un error al conectarse a MySQL.\nCompruebe su conexión a MySQL";
    public static final String MENSAJE_DATOS_INSUFICIENTES = "Datos insuficientes";
    public static final String MENSAJE_USUARIO_EXISTE = "Este usuario ya existe";
    public static final String MENSAJE_CONTRA_NO_COINCIDEN = "Las contraseñas no coinciden.";
    public static final String MENSAJE_INGRESAR_4_CARACTERES = "Ingrese al menos 4 caracteres";
    public static final String MENSAJE_NO_SELECCIONADO = "Seleccione un artículo de la tabla.";

    public static final String C_USUARIO_VACIO = "Campo usuario vacio";
    public static final String C_CONTRA_VACIO = "Campo contraseña vacio";
    public static final String C_TEL_VACIO = "Campo teléfono vacio";
    public static final String C_NOMBRE_VACIO = "Campo nombre vacio";
    public static final String C_DIR_VACIO = "Campo dirección vacio";
    public static final String C_CEDULA_VACIO = "Campo cédula vacio";
    public static final String C_TIPUS_VACIO = "Campo Tipo Usuario vacio";
    public static final String C_TIPCLIEN_VACIO = "Campo Tipo Cliente vacio";
    public static final String C_CORREO_VACIO = "Campo correo vacio";
    
    public static final String C_CEDULA_INVALIDO = "Campo cédula no válido";
    public static final String C_CORREO_INVALIDO = "Campo correo no válido";
    
    public static final String MENSAJE_AGREGADO = "Registro agregado exitosamente";
    public static final String MENSAJE_ACTUALIZADO = "Registro actualizado con éxito";
    public static final String MENSAJE_BORRADO = "Registro eliminado correctamente";

    public static final String MENSAJE_USUARIO_EXISTE_USUARIO = "Ya existe un usuario con ese usuario";
    public static final String MENSAJE_USUARIO_EXISTE_CORREO = "Ya existe un usuario con ese correo";
    public static final String MENSAJE_USUARIO_EXISTE_CEDULA = "Ya existe un usuario con esa cédula";
    
    public static final String MENSAJE_CLIENTE_EXISTE_CORREO = "Ya existe un cliente con ese correo";
    public static final String MENSAJE_CLIENTE_EXISTE_CEDULA = "Ya existe un cliente con esa cédula";

    public static final BoxBlur EFECTO_BOX_BLUR = new BoxBlur(3, 3, 3);
    
    public static final String CREDENCIAL_INVALIDO = "Lo sentimos, los credenciales no son correctos :(";
    
    public static final String PRODUCTO_EXISTE = "Ya existe un producto con este código de barras";
}
