package proyecto.SistemaPago.servicios;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import proyecto.SistemaPago.entidades.Banco;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.entidades.Transaccion;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
import proyecto.SistemaPago.modelosDto.DetallesTransaccionesDto;
import proyecto.SistemaPago.modelosDto.TransaccionRequestDto;
import proyecto.SistemaPago.modelosDto.TransaccionResponseDto;
import proyecto.SistemaPago.repositorios.TransaccionRepositorio;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
class ServicioTransaccionTest {
    @Mock
    private TransaccionRepositorio repositoriotransaccion;

    @Mock
    private ServicioTarjetaBancaria servicioTarjetaBancaria;

    @Mock
    private ServicioCliente serviciocliente;

    @InjectMocks
    private ServicioTransaccion serviciotransaccion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearTransaccion() {
        // Crear un objeto de solicitud de transacción
        TransaccionRequestDto request = new TransaccionRequestDto();
        request.setAmount(2000.0);
        request.setEmail("felipe.montiel@electronico.gob.mx");
        request.setCardNumber("1234567890123456");
        request.setCvv("654");
        request.setCardholderName("Pedro Perez");
        request.setExpirationYear("23");
        request.setExpirationMonth("09");

        // Creamos un cliente existente y una tarjeta bancaria asociada
        Cliente cliente = new Cliente();
        cliente.setIdCliente("1"); // ID del cliente existente en la base de datos
        cliente.setNombre("Pedro");
        cliente.setApellido("Perez");
        cliente.setCorreoElectronico("felipe.montiel@electronico.gob.mx");

        TarjetaBancaria tarjeta = new TarjetaBancaria();
        tarjeta.setNumeroTarjeta("1234567890123456"); // Número de tarjeta existente en la base de datos
        tarjeta.setCvv("654");
        tarjeta.setAnioExpiracion("2023");
        tarjeta.setMesExpiracion("09");
        tarjeta.setMarca(MarcaTarjetaBancaria.MASTERCARD);
        tarjeta.setCliente(cliente);

        // Configurar los objetos simulados
        when(servicioTarjetaBancaria.recuperarTarjetaBancariaCliente(anyString())).thenReturn(tarjeta);
        when(serviciocliente.recuperarClienteByCorreoElectronico(anyString())).thenReturn(cliente);
        when(repositoriotransaccion.save(any(Transaccion.class))).thenAnswer(i -> i.getArguments()[0]);
        when(serviciocliente.isValidEmail(anyString())).thenReturn(true);

        // Llamar al método que se está probando
        TransaccionResponseDto response = serviciotransaccion.crearTransaccion(request);

        // Verificar el resultado
        assertEquals(200, response.getStatusCode());
        assertEquals("Transacción exitosa", response.getMessage());
        assertTrue(response.isApproved());
        assertNotNull(response.getTransactionId());

    }





    @Test
    void obtenerTransacciones() {
        // Datos de prueba
        TarjetaBancaria tarjeta = new TarjetaBancaria();
        tarjeta.setNumeroTarjeta("1234567890123456");
        tarjeta.setMarca(MarcaTarjetaBancaria.MASTERCARD);
        Banco banco = new Banco();
        banco.setNombreCompleto("Banco Prueba");
        tarjeta.setBanco(banco);

        Transaccion transaccion1 = new Transaccion();
        transaccion1.setIdTransaccion(UUID.randomUUID());
        transaccion1.setMonto(1000.0);
        transaccion1.setTransaccionAprobado(true);
        transaccion1.setTarjetaBancaria(tarjeta);

        Transaccion transaccion2 = new Transaccion();
        transaccion2.setIdTransaccion(UUID.randomUUID());
        transaccion2.setMonto(2000.0);
        transaccion2.setTransaccionAprobado(false);
        transaccion2.setTarjetaBancaria(tarjeta);

        // Preparar mocks
        when(repositoriotransaccion.findAll()).thenReturn(Arrays.asList(transaccion1, transaccion2));

        // Ejecutar el método
        List<DetallesTransaccionesDto> response = serviciotransaccion.obtenerTransacciones();

        // Verificar resultados
        assertEquals(2, response.size());

        DetallesTransaccionesDto dto1 = response.get(0);
        assertEquals(transaccion1.getIdTransaccion(), dto1.getTransactionId());
        assertEquals(transaccion1.getMonto(), dto1.getAmount());
        assertEquals("MasterCard", dto1.getBrand());
        assertEquals("Banco Prueba", dto1.getBank());
        assertEquals("123456XXXXXX3456", dto1.getCardNumber());
        assertTrue(dto1.isApproved());

        DetallesTransaccionesDto dto2 = response.get(1);
        assertEquals(transaccion2.getIdTransaccion(), dto2.getTransactionId());
        assertEquals(transaccion2.getMonto(), dto2.getAmount());
        assertEquals("MasterCard", dto2.getBrand());
        assertEquals("Banco Prueba", dto2.getBank());
        assertEquals("123456XXXXXX3456", dto2.getCardNumber());
        assertFalse(dto2.isApproved());
    }


    @Test
    void obtenerTransaccionPorId() {

        // Datos de prueba
        UUID transactionId = UUID.randomUUID();
        TarjetaBancaria tarjeta = new TarjetaBancaria();
        tarjeta.setNumeroTarjeta("1234567890123456");
        tarjeta.setMarca(MarcaTarjetaBancaria.VISA);
        Banco banco = new Banco();
        banco.setNombreCompleto("Banco Prueba");
        tarjeta.setBanco(banco);

        Transaccion transaccion = new Transaccion();
        transaccion.setIdTransaccion(transactionId);
        transaccion.setMonto(1000.0);
        transaccion.setTransaccionAprobado(true);
        transaccion.setTarjetaBancaria(tarjeta);

        ////////////////////Prueba transaccion encontrada////////////////////////////////

        // Preparar mocks
        when(repositoriotransaccion.findById(transactionId)).thenReturn(Optional.of(transaccion));

        // Ejecutar el método
        Optional<DetallesTransaccionesDto> response = serviciotransaccion.obtenerTransaccionPorId(transactionId);

        // Verificar resultados
        assertTrue(response.isPresent());
        DetallesTransaccionesDto dto = response.get();
        assertEquals(transaccion.getIdTransaccion(), dto.getTransactionId());
        assertEquals(transaccion.getMonto(), dto.getAmount());
        assertEquals("Visa", dto.getBrand());
        assertEquals("Banco Prueba", dto.getBank());
        assertEquals("123456XXXXXX3456", dto.getCardNumber());
        assertTrue(dto.isApproved());



        ////////////////////Prueba transaccion no encontrada////////////////////////////////
        // Preparar mocks
        when(repositoriotransaccion.findById(transactionId)).thenReturn(Optional.empty());

        // Ejecutar el método
        Optional<DetallesTransaccionesDto> responses = serviciotransaccion.obtenerTransaccionPorId(transactionId);

        // Verificar resultados
        assertFalse(responses.isPresent());
    }
}