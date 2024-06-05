package proyecto.SistemaPago.modelosDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionResponseDto {

    private int statusCode;
    private String message;
    private UUID transactionId;
    private boolean approved;
    private Map<String, String> errors;
}