package proyecto.SistemaPago.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
import proyecto.SistemaPago.modelosDto.DetallesTransaccionesDto;
import proyecto.SistemaPago.modelosDto.TransaccionRequestDto;
import proyecto.SistemaPago.modelosDto.TransaccionResponseDto;
import proyecto.SistemaPago.repositorios.TransaccionRepositorio;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

        Map<String, String> errors = validateTransaccion(transaccionRequest);

        if (!errors.isEmpty()) {
            return buildErrorResponse(errors);
        }


        TarjetaBancaria tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        Cliente cliente = serviciocliente.recuperarClienteByCorreoElectronico(transaccionRequest.getEmail());

        // Verificar que el correo exista
        verificarCorreoExistente(transaccionRequest, cliente, errors);


        // Verificar montos máximos por marca de tarjeta
        verificarMontosMaximos(transaccionRequest, tarjeta, errors);

        // Verificar máximo de 2 transacciones con el mismo correo en 5 minutos
        verificarMaximoTransacciones(transaccionRequest, errors);

        // Verifica que el numero de tarjeta corresponda con el titular de la tarjeta
        verificarTitularTarjeta(transaccionRequest, tarjeta, cliente, errors);

        // Verificar que el CVV corresponda con la tarjeta bancaria
        verificarCvv(transaccionRequest, tarjeta, errors);



        // Verificar que el mes y el año de vencimiento coincidan con la tarjeta bancaria
        verificarFechaVencimiento(transaccionRequest, tarjeta, errors);

        // Verificar que el nombre del titular de la tarjeta coincida con el registrado en la base de datos
        verificarNombreTitular(transaccionRequest, cliente, errors);

        if (!errors.isEmpty()) {
            return buildErrorResponse(errors);
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


    public List<DetallesTransaccionesDto> obtenerTransacciones() {
        List<Transaccion> transacciones = repositoriotransaccion.findAll();
        List<DetallesTransaccionesDto> responseDtos = new ArrayList<>();

        for (Transaccion transaccion : transacciones) {
            String cardNumberMasked = maskCardNumber(transaccion.getTarjetaBancaria().getNumeroTarjeta());
            responseDtos.add(DetallesTransaccionesDto.builder()
                    .transactionId(transaccion.getIdTransaccion())
                    .amount(transaccion.getMonto())
                    .brand(transaccion.getTarjetaBancaria().getMarca().getMarca())
                    .bank(transaccion.getTarjetaBancaria().getBanco().getNombreCompleto())
                    .cardNumber(cardNumberMasked)
                    .approved(transaccion.isTransaccionAprobado())
                    .build());
        }
        return responseDtos;
    }

    public Optional<DetallesTransaccionesDto> obtenerTransaccionPorId(UUID transactionId) {
        Optional<Transaccion> transaccionOpt = repositoriotransaccion.findById(transactionId);

        if (transaccionOpt.isPresent()) {
            Transaccion transaccion = transaccionOpt.get();
            String cardNumberMasked = maskCardNumber(transaccion.getTarjetaBancaria().getNumeroTarjeta());
            DetallesTransaccionesDto detallesTransaccion = DetallesTransaccionesDto.builder()
                    .transactionId(transaccion.getIdTransaccion())
                    .amount(transaccion.getMonto())
                    .brand(transaccion.getTarjetaBancaria().getMarca().getMarca())
                    .bank(transaccion.getTarjetaBancaria().getBanco().getNombreCompleto())
                    .cardNumber(cardNumberMasked)
                    .approved(transaccion.isTransaccionAprobado())
                    .build();
            return Optional.of(detallesTransaccion);
        } else {
            return Optional.empty();
        }
    }




    private String maskCardNumber(String cardNumber) {
        return cardNumber.substring(0, 6) + "XXXXXX" + cardNumber.substring(cardNumber.length() - 4);
    }


    private void verificarMontosMaximos(TransaccionRequestDto transaccionRequest, TarjetaBancaria tarjeta, Map<String, String> errors) {
        if (tarjeta.getMarca() == MarcaTarjetaBancaria.MASTERCARD && transaccionRequest.getAmount().compareTo(Double.valueOf(5000)) > 0) {
            errors.put("amount", "El monto máximo para tarjetas MasterCard es $5,000 MXN.");
        } else if (tarjeta.getMarca() == MarcaTarjetaBancaria.VISA && transaccionRequest.getAmount().compareTo(Double.valueOf(10000)) > 0) {
            errors.put("amount", "El monto máximo para tarjetas Visa es $10,000 MXN.");
        }
    }

    private void verificarMaximoTransacciones(TransaccionRequestDto transaccionRequest, Map<String, String> errors) {
        Timestamp fiveMinutesAgo = Timestamp.valueOf(LocalDateTime.now().minusMinutes(5));
        int recentTransactions = repositoriotransaccion.countByClienteCorreoElectronicoAndTimeStampChargeAfter(transaccionRequest.getEmail(), fiveMinutesAgo);

        if (recentTransactions >= 2) {
            errors.put("email", "No puede realizar más de 2 transacciones con el mismo correo en 5 minutos.");
        }
    }

    private void verificarTitularTarjeta(TransaccionRequestDto transaccionRequest, TarjetaBancaria tarjeta, Cliente cliente, Map<String, String> errors) {
        if (cliente != null) {
            tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
            if (tarjeta != null && !tarjeta.getCliente().equals(cliente)) {
                errors.put("cardNumber", "El número de tarjeta no corresponde con el titular de la tarjeta.");
            }
        }
    }

    private void verificarCvv(TransaccionRequestDto transaccionRequest, TarjetaBancaria tarjeta, Map<String, String> errors) {
        tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        if (tarjeta != null && !transaccionRequest.getCvv().equals(tarjeta.getCvv())) {
            errors.put("cvv", "El CVV no corresponde con la tarjeta bancaria.");
        }
    }

    private void verificarCorreoExistente(TransaccionRequestDto transaccionRequest, Cliente cliente, Map<String, String> errors) {
        cliente = serviciocliente.recuperarClienteByCorreoElectronico(transaccionRequest.getEmail());
        if (cliente == null ){
            errors.put("email", "El correo no se encuentra registrado.");
        }
    }

    private void verificarFechaVencimiento(TransaccionRequestDto transaccionRequest, TarjetaBancaria tarjeta, Map<String, String> errors) {
        tarjeta = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(transaccionRequest.getCardNumber());
        if (tarjeta != null) {
            if (!transaccionRequest.getExpirationYear().equals(tarjeta.getAnioExpiracion().substring(2)) ||
                    !transaccionRequest.getExpirationMonth().equals(tarjeta.getMesExpiracion())) {
                errors.put("expirationDate", "La fecha de vencimiento de la tarjeta no coincide con la proporcionada.");
            }
        }
    }

    private void verificarNombreTitular(TransaccionRequestDto transaccionRequest, Cliente cliente, Map<String, String> errors) {
        if (cliente != null) {
            String[] nombreCompleto = transaccionRequest.getCardholderName().split("\\s+");
            if (nombreCompleto.length != 2 || !nombreCompleto[0].equals(cliente.getNombre()) || !nombreCompleto[1].equals(cliente.getApellido())) {
                errors.put("cardholderName", "El nombre y apellido del titular de la tarjeta no coinciden con los registrados en la base de datos.");
            }
        }
    }

    private TransaccionResponseDto buildErrorResponse(Map<String, String> errors) {
        System.out.println("Errores de validación: " + errors);
        return TransaccionResponseDto.builder()
                .statusCode(400)
                .message("La transacción no se procesó debido a errores de validación.")
                .transactionId(UUID.randomUUID())
                .approved(false)
                .errors(errors)
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
