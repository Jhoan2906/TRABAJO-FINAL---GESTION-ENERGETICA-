
package gestion_energetica.aplicacion.puerto.entrada;

import gestion_energetica.dominio.modelo.DatoSensor;
import java.util.List;

public interface ConsultarUltimasMedicionesUseCase {

    List<DatoSensor> consultarUltimasMediciones(int limite);

}
