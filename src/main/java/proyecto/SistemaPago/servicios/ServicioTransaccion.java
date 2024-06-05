package proyecto.SistemaPago.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
import proyecto.SistemaPago.modelosDto.TransaccionRequestDto;
import proyecto.SistemaPago.modelosDto.TransaccionResponseDto;
import proyecto.SistemaPago.repositorios.TransaccionRepositorio;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@Slf4j
public class ServicioTransaccion {


    @Autowired
    private ServicioCliente serviciocliente;

    @Autowired
    private ServicioTarjetaBancaria servicioTarjetaBancaria;

    @Autowired
    private TransaccionRepositorio repositoriotransaccion;

    public TransaccionResponseDto crearTransaccion(TransaccionRequestDto transaccionRequest) {
        log.info("Procesando transacción con los siguientes datos: {}", transaccionRequest);
        Map<String, String> errors = validateTransaccion(transaccionRequest);

        if (!errors.isEmpty()) {
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }

        // Verificar montos máximos por marca de tarjeta
        TarjetaBancaria tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());

        if (tarjeta.getMarca() == MarcaTarjetaBancaria.MASTERCARD && transaccionRequest.getAmount().compareTo(Double.valueOf(5000)) > 0) {
            errors.put("amount", "El monto máximo para tarjetas MasterCard es $5,000 MXN.");
        } else if (tarjeta.getMarca() == MarcaTarjetaBancaria.VISA && transaccionRequest.getAmount().compareTo(Double.valueOf(10000)) > 0) {
            errors.put("amount", "El monto máximo para tarjetas Visa es $10,000 MXN.");
        }

        if (!errors.isEmpty()) {
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }

        // Verificar que la tarjeta exista
        tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        if (tarjeta == null) {
            errors.put("cardNumber", "La tarjeta no se encuentra registrada.");
        }

        if (!errors.isEmpty()) {
            log.warn("Errores de validación encontrados: {}", errors);
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }

        // Verificar máximo de 2 transacciones con el mismo correo en 5 minutos
        Timestamp fiveMinutesAgo = Timestamp.valueOf(LocalDateTime.now().minusMinutes(5));
        int recentTransactions = repositoriotransaccion.countByClienteCorreoElectronicoAndTimeStampChargeAfter(transaccionRequest.getEmail(), fiveMinutesAgo);

        if (recentTransactions >= 2) {
            errors.put("email", "No puede realizar más de 2 transacciones con el mismo correo en 5 minutos.");
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }
     //   Verifica que el numero de tarjeta corresponda con el titular de la tarjeta
        Cliente cliente = serviciocliente.recuperarClienteByCorreoElectronico(transaccionRequest.getEmail());
        if (cliente != null) {
           tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
            if (tarjeta != null && !tarjeta.getCliente().equals(cliente)) {
                errors.put("cardNumber", "El número de tarjeta no corresponde con el titular de la tarjeta.");
            }
        }

        if (!errors.isEmpty()) {
            log.warn("Errores de validación encontrados: {}", errors);
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }

        // Verificar que el CVV corresponda con la tarjeta bancaria
        tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        if (tarjeta != null && !transaccionRequest.getCvv().equals(tarjeta.getCvv())) {
            errors.put("cvv", "El CVV no corresponde con la tarjeta bancaria.");
        }

        if (!errors.isEmpty()) {
            log.warn("Errores de validación encontrados: {}", errors);
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }

        // Verificar que el correo exista
        cliente =serviciocliente.recuperarClienteByCorreoElectronico(transaccionRequest.getEmail());
        if (cliente == null ){
            errors.put("email", "El correo no se encuentra registrado.");
        }

        if (!errors.isEmpty()) {
            log.warn("Errores de validación encontrados: {}", errors);
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }



        // Verificar que el mes y el año de vencimiento coincidan con la tarjeta bancaria
        tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        if (tarjeta != null) {
            if (!transaccionRequest.getExpirationYear().equals(tarjeta.getAnioExpiracion().substring(2)) ||
                    !transaccionRequest.getExpirationMonth().equals(tarjeta.getMesExpiracion())) {
                errors.put("expirationDate", "La fecha de vencimiento de la tarjeta no coincide con la proporcionada.");
            }
        }

        if (!errors.isEmpty()) {
            log.warn("Errores de validación encontrados: {}", errors);
            return TransaccionResponseDto.builder()
                    .statusCode(400)
                    .message("La transacción no se procesó debido a errores de validación.")
                    .transactionId(UUID.randomUUID())
                    .approved(false)
                    .errors(errors)
                    .build();
        }


        // Procesar la transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setIdTransaccion(UUID.randomUUID());
        transaccion.setMonto(transaccionRequest.getAmount().doubleValue());
        transaccion.setTransaccionAprobado(true);  // Asumimos que la transacción es aprobada
        transaccion.setTimeStampCharge(new Timestamp(System.currentTimeMillis()));
        transaccion.setCliente(serviciocliente.recuperarClienteByCorreoElectronico(transaccionRequest.getEmail()));
        transaccion.setTarjetaBancaria(tarjeta);

        repositoriotransaccion.save(transaccion);

        return TransaccionResponseDto.builder()
                .statusCode(200)
                .message("Transacción exitosa")
                .transactionId(transaccion.getIdTransaccion())
                .approved(true)
                .errors(new HashMap<>())
                .build();
    }

    private Map<String, String> validateTransaccion(TransaccionRequestDto transaccionRequest) {
        Map<String, String> errors = new HashMap<>();

        if (transaccionRequest.getAmount() == null || transaccionRequest.getAmount().compareTo((double) 0) <= 0) {
            errors.put("amount", "El monto debe ser positivo.");
        }
        if (transaccionRequest.getEmail() == null || !serviciocliente.isValidEmail(transaccionRequest.getEmail())) {
            errors.put("email", "El correo electrónico debe ser válido.");
        }
        if ( servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber()) == null) {
            errors.put("cardNumber", "La tarjeta no se encuentra registrada.");
        }
        if (transaccionRequest.getCardNumber() == null || !transaccionRequest.getCardNumber().matches("\\d{16}")) {
            errors.put("cardNumber", "El número de tarjeta debe tener 16 dígitos.");
        }
        if (transaccionRequest.getCvv() == null || !transaccionRequest.getCvv().matches("\\d{3}")) {
            errors.put("cvv", "El CVV debe tener 3 dígitos.");
        }
        if (transaccionRequest.getCardholderName() == null || transaccionRequest.getCardholderName().isEmpty()) {
            errors.put("cardholderName", "El nombre del titular de la tarjeta es obligatorio.");
        }
        if (transaccionRequest.getExpirationYear() == null || !transaccionRequest.getExpirationYear().matches("\\d{2}")) {
            errors.put("expirationYear", "El año de vencimiento debe tener 2 dígitos.");
        }
        if (transaccionRequest.getExpirationMonth() == null || !transaccionRequest.getExpirationMonth().matches("\\d{2}")) {
            errors.put("expirationMonth", "El mes de vencimiento debe tener 2 dígitos.");
        }

        return errors;
    }
}
