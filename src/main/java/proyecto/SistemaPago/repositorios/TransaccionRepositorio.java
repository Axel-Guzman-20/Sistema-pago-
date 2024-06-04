package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.Transaccion;

import java.util.UUID;
@Repository
public interface TransaccionRepositorio extends JpaRepository<Transaccion, UUID> {
}
