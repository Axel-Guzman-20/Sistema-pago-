package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.Cliente;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {
}
