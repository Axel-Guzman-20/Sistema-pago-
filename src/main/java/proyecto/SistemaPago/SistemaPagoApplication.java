package proyecto.SistemaPago;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import proyecto.SistemaPago.entidades.Banco;
import proyecto.SistemaPago.entidades.Cliente;
import proyecto.SistemaPago.entidades.TarjetaBancaria;
import proyecto.SistemaPago.enums.MarcaTarjetaBancaria;
import proyecto.SistemaPago.repositorios.BancoRepositorio;
import proyecto.SistemaPago.repositorios.ClienteRepositorio;
import proyecto.SistemaPago.repositorios.TarjetaBancariaRepositorio;
import proyecto.SistemaPago.repositorios.TransaccionRepositorio;

import java.util.stream.Stream;

@SpringBootApplication
public class SistemaPagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaPagoApplication.class, args);
	}
	@Autowired
	private BancoRepositorio repositorioBanco;

	@Autowired
	private TransaccionRepositorio repositorioCharge;

	@Autowired
	private ClienteRepositorio repositorioCliente;

	@Autowired
	private TarjetaBancariaRepositorio repositorioTarjetaBancaria;

	@PostConstruct
	public void init() {
		agregarBancos();
		agregarClientes();
		agregarTarjetasBancarias();
	}

	public void agregarBancos() {
		var bancos = Stream.of(
						Banco.builder().nombreCompleto("Banco Nacional de México, S.A.").nombreAbreviado("BANAMEX"),
						Banco.builder().nombreCompleto("BBVA Bancomer, S.A.").nombreAbreviado("BANCOMER"),
						Banco.builder().nombreCompleto("Banco Santander, S.A.").nombreAbreviado("SANTANDER SERFIN"),
						Banco.builder().nombreCompleto("Scotiabank Inverlat, S.A.").nombreAbreviado("SCOTIABANK")
				)
				.map(Banco.BancoBuilder::build)
				.toList();
		repositorioBanco.saveAll(bancos);
	}

	public void agregarClientes() {
		var clientes = Stream.of(
						Cliente.builder().idCliente("ABCD123456AS4").nombre("Francisco").apellido("Lopez").correoElectronico("francisco.lopez@correo.com"),
						Cliente.builder().idCliente("HJKL985695BG5").nombre("Mateo").apellido("Primero").correoElectronico("mateo.primero@dominio.com"),
						Cliente.builder().idCliente("YHBV143254JY6").nombre("Lucas").apellido("Pit").correoElectronico("lucas.pit@email.com.mx"),
						Cliente.builder().idCliente("QWER582645JH2").nombre("Felipe").apellido("Montiel").correoElectronico("felipe.montiel@electronico.gob.mx")
				)
				.map(Cliente.ClienteBuilder::build)
				.toList();
		repositorioCliente.saveAll(clientes);
	}

	public void agregarTarjetasBancarias() {

		var clienteFrancisco = Cliente.builder().idCliente("ABCD123456AS4").nombre("Francisco").apellido("Lopez").correoElectronico("francisco.lopez@correo.com").build();
		var clienteMateo = Cliente.builder().idCliente("HJKL985695BG5").nombre("Mateo").apellido("Primero").correoElectronico("mateo.primero@dominio.com").build();
		var clienteLucas = Cliente.builder().idCliente("YHBV143254JY6").nombre("Lucas").apellido("Pit").correoElectronico("lucas.pit@email.com.mx").build();
		var clienteFelipe = Cliente.builder().idCliente("QWER582645JH2").nombre("Felipe").apellido("Montiel").correoElectronico("felipe.montiel@electronico.gob.mx").build();

		var bancoBanamex = Banco.builder().idBanco(1).nombreCompleto("Banco Nacional de México, S.A.").nombreAbreviado("BANAMEX").build();
		var bancoBancomer = Banco.builder().idBanco(2).nombreCompleto("BBVA Bancomer, S.A.").nombreAbreviado("BANCOMER").build();
		var bancoSantander = Banco.builder().idBanco(3).nombreCompleto("Banco Santander, S.A.").nombreAbreviado("SANTANDER SERFIN").build();
		var bancoScotiabank = Banco.builder().idBanco(4).nombreCompleto("Scotiabank Inverlat, S.A.").nombreAbreviado("SCOTIABANK").build();

		var tarjetasBancarias = Stream.of(
						TarjetaBancaria.builder().numeroTarjeta("4111111111111111").cvv("123").anioExpiracion("2023").mesExpiracion("05").marca(MarcaTarjetaBancaria.VISA).cliente(clienteFrancisco).banco(bancoBanamex),
						TarjetaBancaria.builder().numeroTarjeta("4242424242424242").cvv("321").anioExpiracion("2024").mesExpiracion("11").marca(MarcaTarjetaBancaria.VISA).cliente(clienteMateo).banco(bancoBancomer),
						TarjetaBancaria.builder().numeroTarjeta("5555555555554444").cvv("456").anioExpiracion("2025").mesExpiracion("10").marca(MarcaTarjetaBancaria.MASTERCARD).cliente(clienteLucas).banco(bancoSantander),
						TarjetaBancaria.builder().numeroTarjeta("5105105105105100").cvv("654").anioExpiracion("2026").mesExpiracion("09").marca(MarcaTarjetaBancaria.MASTERCARD).cliente(clienteFelipe).banco(bancoScotiabank)
				)
				.map(TarjetaBancaria.TarjetaBancariaBuilder::build)
				.toList();
		repositorioTarjetaBancaria.saveAll(tarjetasBancarias);
	}

}
