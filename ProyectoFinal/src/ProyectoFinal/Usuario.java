package ProyectoFinal;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.*;

/**
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class Usuario {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;

    private int idUsuario;
    private String nombreUsuario;
    private String nombreCompleto;
    private String correoElectronico;
    private String telefono;
    private String contrasena;
    private String tipoUsuario;
    
    private static ArrayList<Usuario> usuarios = new ArrayList<>();
    
    private static Usuario usr;
    
    //Constructor con parametros
    public Usuario(int idUsuario, String nombreUsuario, String nombreCompleto, String correoElectronico, String telefono, String contrasena, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }
    
    //Constructor sin parametros
    public Usuario(){}
    
    //Metodo que crea un usuario y lo agrega con los datos del mismo a la base de datos
    public static boolean crearUsuario(String usuario, String nombre, String correo, String telefono, String contra, String tipoUsr){
        int newId = getNextIdUsuario(); //Último ID encontrado
        Connection conexion = null;
        int tusr = 0; //ID para asignar tipo de usuario al usuario
        String encript = encriptarContrasena(contra); //Variable para almacenar la contraseña encriptada
        
        //Validar el tipo de usuario para insertar el ID del usuario en la tabla
        if(tipoUsr.equals("Administrador") || tipoUsr.equals("administrador")){
            tusr = 1;
        } else if(tipoUsr.equals("Reclutador") || tipoUsr.equals("reclutador")){
            tusr = 2;
        } else if(tipoUsr.equals("Operador") || tipoUsr.equals("operador")){
            tusr = 3;
        } else {
            System.out.println("El tipo de usuario no coincide");
            return false;
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Usuario(id, nombre, nombreCompleto, correo, telefono, contraseña, tipoUsuario) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, newId);
            ps.setString(2, usuario);
            ps.setString(3, nombre);
            ps.setString(4, correo);
            ps.setString(5, telefono);
            ps.setString(6, encript);
            ps.setInt(7, tusr);
            
            int resp = ps.executeUpdate();
            if (resp > 0) {
                System.out.println("Usuario creado.");
                usuarios.clear();
                conexion.close();
                return true;
            } else {
                System.out.println("Error al crear usuario.");
                conexion.close();
                return false;
            } 
        } catch (Exception e) {
            System.out.println("Error al insertar: " + e);
        }
        return false;
    }
    
    //Metodo que verifica en la lista de usuarios creados si existe alguno que conincida con el usuario y contraseña, devuelve un booleano segun si coincide o no.
    public static boolean iniciarSesion(String usr, String con){
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("SELECT * FROM RecursosHumanos.Usuario WHERE nombre = ? AND contraseña = ?");
            ps.setString(1, usr);
            ps.setString(2, con);
            rs = ps.executeQuery();
            
            if(rs.next()){
                if(rs.getString("nombre").equals(usr) && rs.getString("contraseña").equals(con)){
                    System.out.println("Usuario y contraseña correcta");
                    conexion.close();
                    return true;
                } else {
                    System.out.println("Usuario o contraseña incorrecta");
                    conexion.close();
                   return false;
                }
            } else {
                System.out.println("Usuario y/o contraseña incorrecta");
                conexion.close();
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        // Si no se encontró ninguna coincidencia
        System.out.println("El usuario y/o contraseña no es correcta, por favor intente de nuevo.");
        return false;
    }
    
    //Metodo que devuelve como un String el tipo de usuario que cuenta el mismo a consultar en base a un usuario de tipo Usuario para validarlo en el formulario
    public static String verificarPermisos(Usuario usuario) {
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return "sin permiso";
        }

        switch (usuario.getTipoUsuario().toLowerCase()) {
            case "administrador" -> {
                return "administrador";
            }
            case "operador" -> {
                return "operador";
            }
            case "reclutador" -> {
                return "reclutador";
            }
            default -> {
                return "sin permiso";
            }
        }
    }
    
    //Metodo que valida si existe un arreglo de usuarios para eliminarlo, para luego obtener todos los usuarios en la base de datos
    public static void obtenerTodosLosUsuarios() {
        Connection conexion = null;
        
        if(!usuarios.isEmpty()){
            usuarios.clear();
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            String sql = "SELECT u.id, u.nombre, u.nombreCompleto, u.correo, u.telefono, u.contraseña, cu.tipo AS tipoUsuario "
                       + "FROM RecursosHumanos.Usuario u "
                       + "JOIN RecursosHumanos.Cat_Usuario cu ON u.tipoUsuario = cu.id";
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String nombreCompleto = rs.getString("nombreCompleto");
                String correo = rs.getString("correo");
                String telefono = rs.getString("telefono");
                String contraseña = rs.getString("contraseña");
                String tipoUsuario = rs.getString("tipoUsuario");

                Usuario usuario = new Usuario(id, nombre, nombreCompleto, correo, telefono, contraseña, tipoUsuario);
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
    
    //Metodo que obtiene el usuario de tipo Usuario segun el nombre de usuario.
    public static Usuario obtenerUsuario(String nombreUsuario){
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equals(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }
    
    //Metodo que obtiene el ultimo ID de usuario y lo devuelve para asignarlo en la base de datos
    public static int getNextIdUsuario() {
        if (usuarios.isEmpty()) {
            return 1;
        }

        int maxId = 0;
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario() > maxId) {
                maxId = usuario.getIdUsuario();
            }
        }
        return maxId + 1;
    }
    
    //Metodo que actualiza el usuario en base al ID de usuario y actualiza los datos recibidos en la base de datos
    public static boolean actualizarUsuario(int idUsuario, String nombreUsuario, String nombreCompleto, String correoElectronico, String telefono, String contra, String tipoUsuario) {
        Connection conexion = null;
        int tusr;
        String encript = encriptarContrasena(contra);
        
        //Validar el tipo de usuario para insertar el ID del usuario en la tabla
        if(tipoUsuario.equals("Administrador") || tipoUsuario.equals("administrador")){
            tusr = 1;
        } else if(tipoUsuario.equals("Reclutador") || tipoUsuario.equals("reclutador")){
            tusr = 2;
        } else if(tipoUsuario.equals("Operador") || tipoUsuario.equals("operador")){
            tusr = 3;
        } else {
            System.out.println("El tipo de usuario no coincide");
            return false;
        }
        
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario() == idUsuario) {
                try {
                    conexion = DriverManager.getConnection(url, user, password);
                    ps = conexion.prepareStatement("UPDATE RecursosHumanos.Usuario SET nombre = ?, nombreCompleto = ?, correo = ?, telefono = ?, contraseña = ?, tipoUsuario = ? WHERE id = ?");
                    ps.setString(1, nombreUsuario);
                    ps.setString(2, nombreCompleto);
                    ps.setString(3, correoElectronico);
                    ps.setString(4, telefono);
                    ps.setString(5, encript);
                    ps.setInt(6, tusr);
                    ps.setInt(7, idUsuario);
                    int resp = ps.executeUpdate();
                    if(resp > 0){
                        usuarios.clear();                        
                        return true;
                    }else {
                        return false;
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            }
        }
        
        return false;
    }

    //Metodo que elimina a un usuario en base a su ID de usuario
    public static boolean eliminarUsuario(int idUsuario) {
    Connection conexion = null;
    boolean usuarioEncontrado = false;

    for (Usuario usuario : usuarios) {
        if (usuario.getIdUsuario() == idUsuario) {
            usuarioEncontrado = true;
            try {
                conexion = DriverManager.getConnection(url, user, password);
                ps = conexion.prepareStatement("DELETE FROM RecursosHumanos.Usuario WHERE id = ?");
                ps.setInt(1, idUsuario);
                int resp = ps.executeUpdate();

                if (resp > 0) {
                    System.out.println("Usuario ID " + idUsuario + " eliminado exitosamente.");
                    usuarios.clear();
                    conexion.close();
                    return true;
                } else {
                    System.out.println("No se ha eliminado el usuario.");
                    conexion.close();
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
                return false;
            }
        }
    }

    if (!usuarioEncontrado) {
        System.out.println("Error: Usuario no encontrado.");
    }

    return false;
    }

    //Metodo que vuelve un hash el String recibido para encriptar los datos y esto poder almacenarlo y/o validarlo al momento de crear, modificar o iniciar sesion
    public static String encriptarContrasena(String pass){
        //Encriptar Contraseña para almacenarla en BD
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            pass = String.format("%064x", new java.math.BigInteger(1, digest));
            return pass;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al encriptar la contraseña: " + e);
        }
        return null;
    }

    //getters y setters
    public int getIdUsuario(){
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public static ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    // Métodos de validación por medio de expresiones regulares
    
    //Valida que el nombre de usuario no contenga caracteres especiales y devuelve un boleano si es correcto
    public static boolean validarNombreUsuario(String nombreUsuario) {
        return nombreUsuario.matches("^[a-zA-Z0-9]+$");
    }

    //Valida que el nombre de una persona sea unicamente caracteres
    public static boolean validarNombreCompleto(String nombreCompleto) {
        return nombreCompleto.matches("^[a-zA-Z\\s]+$");
    }

    //Valida que un correo contenga el formato de un correo electronico: xxxx@xxx.xx
    public static boolean validarCorreoElectronico(String correoElectronico) {
        String regex = "[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[-A-Za-z0-9]*[A-Za-z0-9])?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correoElectronico);
        return matcher.matches();
    }

    //Valida que los numeros de telefono empiecen con los siguientes numeros 2, 3, 4, 5, 6 o 7 y que su longitud sea 8 exactamente
    public static boolean validarTelefono(String telefono) {
        return telefono.matches("^[234567][0-9]{7}$");
    }

    //Valida que el tipo de usuario sea alguno de los existentes
    public static boolean validarTipoUsuario(String tipoUsuario) {
        return tipoUsuario.equals("Administrador") || tipoUsuario.equals("Operador")
                || tipoUsuario.equals("Reclutador");
    }

}
