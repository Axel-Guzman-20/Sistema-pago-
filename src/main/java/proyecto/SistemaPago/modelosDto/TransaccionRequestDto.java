package proyecto.SistemaPago.modelosDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionRequestDto {


    private Double amount;

    private String email;

    private String cardNumber;

    private String cvv;

    private String cardholderName;

    private String expirationYear;

    private String expirationMonth;
}