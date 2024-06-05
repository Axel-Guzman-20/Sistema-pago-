package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.Transaccion;

import java.util.List;
import java.util.UUID;
@Repository
public interface TransaccionRepositorio extends JpaRepository<Transaccion, UUID> {
    @Query("SELECT c FROM Transaccion AS c WHERE c.cliente.idCliente = :idCliente ORDER BY c.timeStampCharge DESC LIMIT 2")
    List<Transaccion> recuperarUltimas2TransaccionesCliente(@Param("idCliente")String idCliente);

}
