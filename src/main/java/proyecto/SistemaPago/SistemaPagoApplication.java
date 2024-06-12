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

import java.util.stream.Stream;

@SpringBootApplication
public class SistemaPagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaPagoApplication.class, args);
	}
	@Autowired
	private BancoRepositorio repositorioBanco;


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
						Banco.builder().nombreCompleto("Banco Internacional del Pais México, S.A.").nombreAbreviado("BIPM"),
						Banco.builder().nombreCompleto("Banco Fuerte del Pais, S.A.").nombreAbreviado("BFP"),
						Banco.builder().nombreCompleto("Banco Santa, S.A.").nombreAbreviado("SA"),
						Banco.builder().nombreCompleto("Banco Prueba Inverlat, S.A.").nombreAbreviado("BPI")
				)
				.map(Banco.BancoBuilder::build)
				.toList();
		repositorioBanco.saveAll(bancos);
	}

	public void agregarClientes() {
		var clientes = Stream.of(
						Cliente.builder().idCliente("ABCD123456AS4").nombre("Axel").apellido("Lopez").correoElectronico("axel.lopez@prueba.com"),
						Cliente.builder().idCliente("HJKL985695BG5").nombre("David").apellido("Perez").correoElectronico("david.perez@hotmail.com"),
						Cliente.builder().idCliente("YHBV143254JY6").nombre("Ani").apellido("Gonzales").correoElectronico("ani.gonzales@email.com.mx"),
						Cliente.builder().idCliente("QWER582645JH2").nombre("Micke").apellido("Zavala").correoElectronico("micke.zavala@gmail.com")
				)
				.map(Cliente.ClienteBuilder::build)
				.toList();
		repositorioCliente.saveAll(clientes);
	}

	public void agregarTarjetasBancarias() {

		var clienteAxel = Cliente.builder().idCliente("ABCD123456AS4").nombre("Axel").apellido("Lopez").correoElectronico("axel.lopez@prueba.com").build();
		var clienteDavid = Cliente.builder().idCliente("HJKL985695BG5").nombre("David").apellido("Perez").correoElectronico("david.perez@hotmail.com").build();
		var clienteAni = Cliente.builder().idCliente("YHBV143254JY6").nombre("Ani").apellido("Gonzales").correoElectronico("ani.gonzales@email.com.mx").build();
		var clienteMicke = Cliente.builder().idCliente("QWER582645JH2").nombre("Micke").apellido("Zavala").correoElectronico("fmicke.zavala@gmail.com").build();

		var bancoBIPM = Banco.builder().idBanco(1).nombreCompleto("Banco Internacional del Pais México, S.A.").nombreAbreviado("BIPM").build();
		var bancoBFP = Banco.builder().idBanco(2).nombreCompleto("Banco Fuerte del Pais, S.A.").nombreAbreviado("BFP").build();
		var bancoSA = Banco.builder().idBanco(3).nombreCompleto("Banco Santa, S.A.").nombreAbreviado("SA").build();
		var bancoBPI = Banco.builder().idBanco(4).nombreCompleto("Banco Prueba Inverlat, S.A.").nombreAbreviado("BPI").build();

		var tarjetasBancarias = Stream.of(
						TarjetaBancaria.builder().numeroTarjeta("4112123987502195").cvv("123").anioExpiracion("2023").mesExpiracion("05").marca(MarcaTarjetaBancaria.VISA).cliente(clienteAxel).banco(bancoBIPM),
						TarjetaBancaria.builder().numeroTarjeta("4112968086408732").cvv("321").anioExpiracion("2024").mesExpiracion("11").marca(MarcaTarjetaBancaria.VISA).cliente(clienteDavid).banco(bancoBFP),
						TarjetaBancaria.builder().numeroTarjeta("5105843095412954").cvv("456").anioExpiracion("2025").mesExpiracion("10").marca(MarcaTarjetaBancaria.MASTERCARD).cliente(clienteAni).banco(bancoSA),
						TarjetaBancaria.builder().numeroTarjeta("5105032986314174").cvv("654").anioExpiracion("2026").mesExpiracion("09").marca(MarcaTarjetaBancaria.MASTERCARD).cliente(clienteMicke).banco(bancoBPI)
				)
				.map(TarjetaBancaria.TarjetaBancariaBuilder::build)
				.toList();
		repositorioTarjetaBancaria.saveAll(tarjetasBancarias);
	}

}
