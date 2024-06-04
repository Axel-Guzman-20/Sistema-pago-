package proyecto.SistemaPago.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaccion {

    @Id
    private UUID idTransaccion;

    private double monto;

    private boolean TransaccionAprobado;

    private Timestamp timeStampCharge;

    @ManyToOne
    @JoinColumn(name = "idCliente", referencedColumnName = "idCliente", nullable = false, foreignKey = @ForeignKey(name = "FK_CHARGE_CLIENTE"))
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "numeroTarjeta", referencedColumnName = "numeroTarjeta", nullable = false, foreignKey = @ForeignKey(name = "FK_CHARGE_TARJETA-BANCARIA"))
    private TarjetaBancaria tarjetaBancaria;
}