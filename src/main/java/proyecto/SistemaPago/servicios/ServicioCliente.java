package proyecto.SistemaPago.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.exceptions.EmailClienteNotFoundException;
import proyecto.SistemaPago.repositorios.ClienteRepositorio;

@Service
@Slf4j
public class ServicioCliente {
    @Autowired
    ClienteRepositorio repositorioCliente;

    public Cliente recuperarClienteByCorreoElectronico(String correoElectronico) {
        var cliente = repositorioCliente.findByCorreoElectronico(correoElectronico);
        if(cliente.isEmpty())
            throw new EmailClienteNotFoundException("No se encontró el cliente con el correo electrónico");
        return cliente.get();
    }
}
