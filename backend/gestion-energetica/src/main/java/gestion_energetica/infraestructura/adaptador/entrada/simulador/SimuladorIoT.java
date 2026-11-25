
package gestion_energetica.infraestructura.adaptador.entrada.simulador;

import gestion_energetica.aplicacion.puerto.entrada.CapturarDatoUseCase;
import gestion_energetica.dominio.modelo.DatoSensor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimuladorIoT {

    private static final int INTERVALO_SEGUNDOS = 60;

    private final CapturarDatoUseCase capturarDatoUseCase;

    public SimuladorIoT(CapturarDatoUseCase capturarDatoUseCase) {
        this.capturarDatoUseCase = capturarDatoUseCase;
    }

    @Scheduled(fixedDelay = 60000)
    public void generarMediciones() {

        // 1. Temperatura simulada
        generarLectura(
            "SEN001",
            "EQ001",
            "Temperatura",
            "C",
            generarValor(20.0, 28.0)
        );

        // 2. Humedad relativa simulada
        generarLectura(
            "SEN002",
            "EQ001",
            "Humedad relativa",
            "%",
            generarValor(35.0, 65.0)
        );

        // 3. Potencia electrica simulada
        BigDecimal potencia = generarValor(0.5, 5.0);

        generarLectura(
            "SEN003",
            "EQ003",
            "Potencia electrica",
            "kW",
            potencia
        );

        // 4. Energia electrica calculada
        BigDecimal energia = potencia
            .multiply(BigDecimal.valueOf(INTERVALO_SEGUNDOS))
            .divide(
                BigDecimal.valueOf(3600),
                4,
                RoundingMode.HALF_UP
            );

        generarLectura(
            "SEN004",
            "EQ003",
            "Energia electrica",
            "kWh",
            energia
        );
    }

    private BigDecimal generarValor(double minimo, double maximo) {

        double aleatorio = ThreadLocalRandom.current()
            .nextDouble(minimo, maximo);

        return BigDecimal.valueOf(aleatorio)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private void generarLectura(
            String idSensor,
            String idEquipo,
            String variable,
            String unidad,
            BigDecimal valor) {

        DatoSensor medicion = new DatoSensor(
            "SIM-" + UUID.randomUUID(),
            idSensor,
            valor,
            LocalDateTime.now(ZoneOffset.UTC),
            "TWIN001",
            idEquipo,
            variable,
            unidad
        );

        try {
            capturarDatoUseCase.capturarDato(medicion);

            System.out.println(
                "Simulador IoT: "
                + variable
                + " = "
                + valor
                + " "
                + unidad
                + " | Equipo: "
                + idEquipo
            );

        } catch (Exception ex) {
            System.err.println(
                "Simulador IoT: error en "
                + variable
                + " - "
                + ex.getMessage()
            );
        }
    }
}
