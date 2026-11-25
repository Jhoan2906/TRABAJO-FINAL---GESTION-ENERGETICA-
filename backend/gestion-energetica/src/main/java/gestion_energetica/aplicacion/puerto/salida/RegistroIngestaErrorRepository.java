
package gestion_energetica.aplicacion.puerto.salida;

import gestion_energetica.dominio.modelo.DatoSensor;

public interface RegistroIngestaErrorRepository {

    void registrarRechazo(DatoSensor dato, String motivo);

}
