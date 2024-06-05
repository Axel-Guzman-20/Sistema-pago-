package proyecto.SistemaPago.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import proyecto.SistemaPago.modelosDto.DetallesTransaccionesDto;
import proyecto.SistemaPago.modelosDto.TransaccionRequestDto;
import proyecto.SistemaPago.modelosDto.TransaccionResponseDto;
import proyecto.SistemaPago.servicios.ServicioTransaccion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    @GetMapping("/transaccion")
    public ResponseEntity<List<DetallesTransaccionesDto>> getAllTransacciones() {
        List<DetallesTransaccionesDto> transacciones = serviciotransaccion.obtenerTransacciones();
        return new ResponseEntity<>(transacciones, HttpStatus.OK);
    }

    @GetMapping("/transaccion/{transactionId}")
    public ResponseEntity<?> obtenerTransaccionPorId(@PathVariable UUID transactionId) {
        Optional<DetallesTransaccionesDto> detallesTransaccionOpt = serviciotransaccion.obtenerTransaccionPorId(transactionId);

        if (detallesTransaccionOpt.isPresent()) {
            return new ResponseEntity<>(detallesTransaccionOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No hay registro de transacción con el Id: "+transactionId, HttpStatus.NOT_FOUND);
        }
    }
}
