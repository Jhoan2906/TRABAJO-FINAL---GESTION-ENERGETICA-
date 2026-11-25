
package gestion_energetica.aplicacion.puerto.salida;

import gestion_energetica.dominio.modelo.DatoSensor;
import java.util.List;

public interface DatoSensorRepository {

    // Consultar si una medicion ya existe
    boolean existePorId(String idDato);

    // Validar relacion Equipo-DigitalTwin
    boolean equipoPerteneceATwin(
        String idEquipo,
        String idTwin
    );

    // Guardar una medicion valida
    void guardar(DatoSensor datoSensor);

    // Consultar las ultimas mediciones almacenadas
    List<DatoSensor> consultarUltimasMediciones(int limite);

}
