package proyecto.SistemaPago.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.exceptions.EmailClienteNotFoundException;
import proyecto.SistemaPago.repositorios.ClienteRepositorio;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ServicioCliente {
    @Autowired
    ClienteRepositorio repositorioCliente;

    public Cliente recuperarClienteByCorreoElectronico(String correoElectronico) {
        Cliente cliente = repositorioCliente.findByCorreoElectronico(correoElectronico);
        return  cliente;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}
