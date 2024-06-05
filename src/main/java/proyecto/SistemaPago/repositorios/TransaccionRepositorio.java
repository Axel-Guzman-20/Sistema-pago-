package proyecto.SistemaPago.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proyecto.SistemaPago.entidades.Transaccion;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
@Repository
public interface TransaccionRepositorio extends JpaRepository<Transaccion, UUID> {

    int countByClienteCorreoElectronicoAndTimeStampChargeAfter(String correoElectronico, Timestamp timestamp);

}
