package proyecto.SistemaPago.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.modelosDto.TransaccionRequestDto;
import proyecto.SistemaPago.modelosDto.TransaccionResponseDto;
import proyecto.SistemaPago.servicios.ServicioTransaccion;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ControladorTransaccion {

    @Autowired
    ServicioTransaccion serviciotransaccion;

    @PostMapping("/transaccion")
    public ResponseEntity<TransaccionResponseDto> createTransaccion(@RequestBody TransaccionRequestDto transaccionRequest) {
        TransaccionResponseDto cargo = serviciotransaccion.crearTransaccion(transaccionRequest);

        if (cargo.getStatusCode() == 400) {
            return ResponseEntity.badRequest().body(cargo);
        }

        return ResponseEntity.ok(cargo);
    }
}
/*

    @GetMapping("/transaccion")
    public ResponseEntity<List<Transaccion>> recuperarTransacciones() {
        var detallesTransaccionesDto = serviciotransaccion.recuperarTransacciones();
        return ResponseEntity.status(HttpStatus.OK).body(detallesTransaccionesDto);
    }

    @GetMapping("/transaccion/{id}")
    public ResponseEntity<Transaccion> recuperarTransaccion(String transactionId) {
        var detallesTransaccionDto = serviciotransaccion.recuperarTransaccion(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(detallesTransaccionDto);
    }

}
*/