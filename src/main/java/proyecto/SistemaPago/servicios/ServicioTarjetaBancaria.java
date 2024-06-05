package proyecto.SistemaPago.servicios;

import proyecto.SistemaPago.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.exceptions.CardNumberNotFoundException;
import proyecto.SistemaPago.repositorios.TarjetaBancariaRepositorio;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
public class ServicioTarjetaBancaria {

    @Autowired
    private TarjetaBancariaRepositorio repositoryTarjetaBancaria;


    public TarjetaBancaria recuperarTarjetaBancariaCliente(Optional<Cliente> cliente, String numeroTarjeta) {
        var tarjetaBancaria = repositoryTarjetaBancaria.findById(numeroTarjeta);

        if(tarjetaBancaria.isEmpty())
            throw new CardNumberNotFoundException("No se encontró la tarjeta bancaria");

        if (!tarjetaBancaria.get().getCliente().equals(cliente))
            throw new BadRequestException("La tarjeta bancaria no pertenece al cliente");

        return tarjetaBancaria.get();
    }

    public void validarDatosTarjetaBancaria(TarjetaBancaria tarjetaBancaria, String cvv, String anio, String mes, String nombreTitularTarjeta) {
        validarCVV(tarjetaBancaria, cvv);
        validarFecha(tarjetaBancaria, anio, mes);
        validarFechaExpiracion(tarjetaBancaria);
        validarNombre(tarjetaBancaria, nombreTitularTarjeta);
    }

    private void validarFecha(TarjetaBancaria tarjetaBancaria, String anio, String mes) {
        String anioTarjeta = tarjetaBancaria.getAnioExpiracion().substring(2);

        if (!Objects.equals(anioTarjeta, anio) || !Objects.equals(tarjetaBancaria.getMesExpiracion(), mes))
            throw new BadRequestException("La fecha de expiración no es correcta");
    }

    private void validarCVV(TarjetaBancaria tarjetaBancaria, String cvv) {
        if (!tarjetaBancaria.getCvv().equals(cvv))
            throw new BadRequestException("El CVV no es correcto");
    }

    private void validarFechaExpiracion(TarjetaBancaria tarjetaBancaria) {
        LocalDate fechaActual = LocalDate.now();

        String fechaTarjetaSinFormatear = tarjetaBancaria.getAnioExpiracion() + "-" + tarjetaBancaria.getMesExpiracion() + "-01";

        LocalDate fechaTarjeta = LocalDate.parse(fechaTarjetaSinFormatear);

        if (fechaTarjeta.isBefore(fechaActual))
            throw new BadRequestException("La tarjeta bancaria ha expirado");
    }

    private void validarNombre(TarjetaBancaria tarjetaBancaria, String nombreTitularTarjeta) {
        var nombreRegistrado = String.format("%s %s", tarjetaBancaria.getCliente().getNombre(), tarjetaBancaria.getCliente().getApellido()).toLowerCase().replaceAll("\\s", "");
        var titularTarjeta = nombreTitularTarjeta.toLowerCase().replaceAll("\\s", "");
        if (!nombreRegistrado.equals(titularTarjeta))
            throw new BadRequestException("El nombre del titular de la tarjeta no coinciden con los registrados en la tarjeta");
    }

}
