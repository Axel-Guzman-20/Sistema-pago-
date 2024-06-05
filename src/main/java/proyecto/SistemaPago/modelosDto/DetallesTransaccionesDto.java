package proyecto.SistemaPago.modelosDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetallesTransaccionesDto {

    private UUID transactionId;

    private double amount;

    private String brand;

    private String bank;

    private String cardNumber;

    private boolean approved;
}
