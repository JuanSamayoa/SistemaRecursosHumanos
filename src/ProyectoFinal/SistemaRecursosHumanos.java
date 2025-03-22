package ProyectoFinal;

/**
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class SistemaRecursosHumanos {

    //Validador de fechas en formato dd/mm/yyyy
    public static boolean validarFecha(String fecha){
        String[] fechaArray = fecha.split("/");
        if(fechaArray.length != 3){
            return false;
        }
        int dia = Integer.parseInt(fechaArray[0]);
        int mes = Integer.parseInt(fechaArray[1]);
        int anio = Integer.parseInt(fechaArray[2]);
        
        if(dia < 1 || dia > 31){
            return false;
        }
        
        if(mes < 1 || mes > 12){
            return false;
        }
        
        if(anio < 1900 || anio > 2040){
            return false;
        }
        return true;
    }

    public static boolean validarFechas(String fechaInicio, String fechaFin){
        if(!validarFecha(fechaInicio) || !validarFecha(fechaFin)){
            return false;
        }
        
        String[] fechaInicioArray = fechaInicio.split("/");
        String[] fechaFinArray = fechaFin.split("/");
        
        int diaInicio = Integer.parseInt(fechaInicioArray[0]);
        int mesInicio = Integer.parseInt(fechaInicioArray[1]);
        int anioInicio = Integer.parseInt(fechaInicioArray[2]);
        
        int diaFin = Integer.parseInt(fechaFinArray[0]);
        int mesFin = Integer.parseInt(fechaFinArray[1]);
        int anioFin = Integer.parseInt(fechaFinArray[2]);
        
        if(anioInicio > anioFin){
            return false;
        }
        
        if(anioInicio == anioFin){
            if(mesInicio > mesFin){
                return false;
            }
            
            if(mesInicio == mesFin){
                if(diaInicio > diaFin){
                    return false;
                }
            }
        }
        return true;
    }


    public static void main(String[] args) {
        
    }
    
}
