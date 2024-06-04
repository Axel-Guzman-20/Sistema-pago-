package proyecto.SistemaPago.entidades;

import jakarta.persistence.*;
import lombok.*;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaBancaria {

    @Id
    private String numeroTarjeta;

    private String cvv;

    private String anioExpiracion;

    private String mesExpiracion;

    @Enumerated(EnumType.STRING)
    private MarcaTarjetaBancaria marca;

    @OneToMany(mappedBy = "tarjetaBancaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Transaccion> cargos;

    @ManyToOne
    @JoinColumn(name = "idCliente", referencedColumnName = "idCliente", nullable = false, foreignKey = @ForeignKey(name = "FK_TARJETA-BANCARIA_CLIENTE"))
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "idBanco", referencedColumnName = "idBanco", nullable = false, foreignKey = @ForeignKey(name = "FK_TARJETA-BANCARIA_BANCO"))
    private Banco banco;
}
