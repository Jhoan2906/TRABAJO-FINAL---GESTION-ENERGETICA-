
package gestion_energetica.infraestructura.adaptador.entrada.web;

import gestion_energetica.aplicacion.puerto.entrada.CapturarDatoUseCase;
import gestion_energetica.aplicacion.puerto.entrada.ConsultarUltimasMedicionesUseCase;
import gestion_energetica.dominio.modelo.DatoSensor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datos-sensor")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class DatoSensorController {

    private final CapturarDatoUseCase capturarDatoUseCase;
    private final ConsultarUltimasMedicionesUseCase consultarUseCase;

    public DatoSensorController(
            CapturarDatoUseCase capturarDatoUseCase,
            ConsultarUltimasMedicionesUseCase consultarUseCase) {

        this.capturarDatoUseCase = capturarDatoUseCase;
        this.consultarUseCase = consultarUseCase;
    }

    // POST: capturar una medicion IoT
    @PostMapping
    public ResponseEntity<DatoSensor> capturar(
            @RequestBody DatoSensor datoSensor) {

        DatoSensor guardado =
                capturarDatoUseCase.capturarDato(datoSensor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    // GET: consultar las ultimas mediciones
    @GetMapping
    public ResponseEntity<List<DatoSensor>> consultar(
            @RequestParam(defaultValue = "20") int limite) {

        List<DatoSensor> mediciones =
                consultarUseCase.consultarUltimasMediciones(limite);

        return ResponseEntity.ok(mediciones);
    }
}
