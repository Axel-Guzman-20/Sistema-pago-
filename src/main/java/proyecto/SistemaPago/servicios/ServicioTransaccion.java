package proyecto.SistemaPago.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
import proyecto.SistemaPago.exceptions.MaxTransaccionesException;
import proyecto.SistemaPago.exceptions.UUIDInvalidException;
import proyecto.SistemaPago.repositorios.ClienteRepositorio;
import proyecto.SistemaPago.repositorios.TransaccionRepositorio;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ServicioTransaccion {


    @Autowired
    private ServicioCliente serviciocliente;

    @Autowired
    private ServicioTarjetaBancaria servicioTarjetaBancaria;

    @Autowired
    private TransaccionRepositorio repositoriotransaccion;

    @Autowired
    private ClienteRepositorio clienterepositorio;

    private static final double MONTO_MAXIMO_TARJETA_BANCARIA_MASTERCARD = 5000.00d;
    private static final double MONTO_MAXIMO_TARJETA_BANCARIA_VISA = 10000.00d;
    private static final int MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO = 5; // EN MINUTOS
    private static final long UN_MINUTO_EN_MILISEGUNDOS = 60000;
    private static final long MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO_MS = MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO * UN_MINUTO_EN_MILISEGUNDOS;



    public Transaccion realizarCargo(Transaccion nuevoCargoDto) {

        log.info("Valores de los campos de Transaccion antes de hacer la llamada:");
        log.info("Monto: {}", nuevoCargoDto.getMonto());
        log.info("Correo electrónico: {}", nuevoCargoDto.getCorreoElectronico());
        log.info("Número de tarjeta: {}", nuevoCargoDto.getTarjetaBancaria());
        log.info("CVV: {}", nuevoCargoDto.getCvv());
        log.info("Año de expiración: {}", nuevoCargoDto.getAnioExpiracion());
        log.info("Mes de expiración: {}", nuevoCargoDto.getMesExpiracion());
        log.info("Nombre: {}", nuevoCargoDto.getNombre());
        log.info("Transacción Aprobado: {}", nuevoCargoDto.isTransaccionAprobado());
        log.info("Timestamp Charge: {}", nuevoCargoDto.getTimeStampCharge());

// 1. Validar que el cliente exista usando el correo electrónico
        var cliente = serviciocliente.recuperarClienteByCorreoElectronico(nuevoCargoDto.getCorreoElectronico());
        // 2. Validar que la tarjeta bancaria sea valida y pertenezca al cliente
        var tarjetaBancaria = servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(Optional.ofNullable(cliente), String.valueOf(nuevoCargoDto.getTarjetaBancaria()));
        servicioTarjetaBancaria.validarDatosTarjetaBancaria(tarjetaBancaria, nuevoCargoDto.getCvv(), nuevoCargoDto.getAnioExpiracion(), nuevoCargoDto.getMesExpiracion(), nuevoCargoDto.getNombre());
        validarMontoPositivo(nuevoCargoDto.getMonto());

        // Validar reglas de negocio
        validarMontoMaximoMarcaTarjetaBancaria(tarjetaBancaria, nuevoCargoDto.getMonto());
        var timestamp = new Timestamp(System.currentTimeMillis());
        validarMaximoTransaccionesEn5Minutos(cliente.getIdCliente(), timestamp);

       // var nuevoCargo = MapperCharge.INSTANCE.newCargoDtoToCharge(nuevoCargoDto, cliente, tarjetaBancaria, timestamp, true, UUID.randomUUID());

        var cargo = repositoriotransaccion.save(nuevoCargoDto);

        log.info("Se ha realizado un cargo con el id: {} para el usuario {}", cargo.getIdTransaccion(), cargo.getCliente().getCorreoElectronico());
        return cargo;
    }

    public List<Transaccion> recuperarTransacciones() {
        var transacciones = repositoriotransaccion.findAll();
        return  transacciones;
    }

    public Transaccion recuperarTransaccion(String transactionId) {
        var idTransaccion = recuperarUUID(transactionId);
        var transaccion = repositoriotransaccion.findById(idTransaccion);
        if(transaccion.isEmpty())
            throw new IllegalArgumentException("Transacción no encontrada");
        return (transaccion.get());
    }




    private void validarMontoMaximoMarcaTarjetaBancaria(TarjetaBancaria tarjetaBancaria, Double monto) {
        var marcaTarjeta = tarjetaBancaria.getMarca();
        if(marcaTarjeta.equals(MarcaTarjetaBancaria.MASTERCARD) && monto > MONTO_MAXIMO_TARJETA_BANCARIA_MASTERCARD)
            throw new IllegalArgumentException("El monto máximo para tarjetas MASTERCARD es de " + MONTO_MAXIMO_TARJETA_BANCARIA_MASTERCARD + " MXN");

        if(marcaTarjeta.equals(MarcaTarjetaBancaria.VISA) && monto > MONTO_MAXIMO_TARJETA_BANCARIA_VISA)
            throw new IllegalArgumentException("El monto máximo para tarjetas Visa es de " + MONTO_MAXIMO_TARJETA_BANCARIA_VISA + " MXN");
    }


    private void validarMaximoTransaccionesEn5Minutos(String idCliente, Timestamp timeStampActual) {

        var transacciones = repositoriotransaccion.recuperarUltimas2TransaccionesCliente(idCliente);

        // Comparar tiempo entre las ultimas 2 transacciones y eliminar la más antigua si ya han pasado 5 minutos
        if(transacciones.size() == 2) {
            var diferenciaTiempoTransacciones = transacciones.get(0).getTimeStampCharge().getTime() - transacciones.get(1).getTimeStampCharge().getTime();
            if(diferenciaTiempoTransacciones > MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO_MS)
                transacciones.remove(1);
        }

        // Validar que el time actual sea mayor por 5 minutos a las ultimas 2 transacciones
        if(transacciones.size() == 2) {
            for (Transaccion transaccion : transacciones) {
                var diferenciaTiempoCargos = timeStampActual.getTime() - transaccion.getTimeStampCharge().getTime();
                if(diferenciaTiempoCargos <= MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO_MS)
                    throw new MaxTransaccionesException("No se pueden realizar más de 2 transacciones en " + MAXIMO_TRANSACCIONES_EN_TIEMPO_INDICADO +" minutos");
            }
        }
    }

    private void validarMontoPositivo(Double monto) {
        if(monto <= 0)
            throw new IllegalArgumentException("El monto no puede ser negativo o igual a 0");
    }

    private UUID recuperarUUID(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (RuntimeException e) {
            throw new UUIDInvalidException("UUID ingresado no es válido");
        }
    }

}

