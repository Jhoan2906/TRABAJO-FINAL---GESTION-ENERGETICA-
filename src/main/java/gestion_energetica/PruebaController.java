package gestion_energetica;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PruebaController {

    @GetMapping("/")
    public String inicio() {
        return "Sistema de Gestion Energetica - Backend funcionando correctamente";
    }
}