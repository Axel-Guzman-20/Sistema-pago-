package proyecto.SistemaPago.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.repositorios.TarjetaBancariaRepositorio;


@Service
public class ServicioTarjetaBancaria {

    @Autowired
    private TarjetaBancariaRepositorio repositoryTarjetaBancaria;

    public TarjetaBancaria recuperarTarjetaBancariaCliente(String numeroTarjeta) {
        TarjetaBancaria tarjeta = repositoryTarjetaBancaria.findByNumeroTarjeta(numeroTarjeta);
        return tarjeta;
    }
}
