package proyecto.SistemaPago.enums;

import lombok.Getter;

@Getter
public enum MarcaTarjetaBancaria {

    VISA("Visa"),

    MASTERCARD("MasterCard");

    private final String marca;

    MarcaTarjetaBancaria(String marca) {
        this.marca = marca;
    }
}