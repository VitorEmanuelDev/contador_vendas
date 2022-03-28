package br.eti.vitoremanuel.contadorvendas;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

public class SalesReader {

	private final List<Sale> sales;

	public SalesReader(String salesFile) {

		final var dataStream = ClassLoader.getSystemResourceAsStream(salesFile);

		if (dataStream == null) {
			throw new IllegalStateException("File not found or is empty");
		}

		final var builder = new CsvToBeanBuilder<Sale>(new InputStreamReader(dataStream, StandardCharsets.ISO_8859_1));

		sales = builder
				.withType(Sale.class)
				.withSeparator(';')
				.build()
				.parse();
	}

	public void totalOfCompletedSales() {
		// TODO qual o valor total de vendas completas?

		final var soma = sales.stream()
				.filter(s -> s.getStatus().toString().equals("COMPLETED"))
				.map(saleValue -> saleValue.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		System.out.println("Total vendas (concluídas): R$ " + toCurrency(soma));

	}

	public void totalOfCancelledSales() {
		// TODO qual o valor total de vendas canceladas?

		final var soma = sales.stream()
				.filter(s -> s.getStatus().toString().equals("CANCELLED"))
				.map(saleValue -> saleValue.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		System.out.println("Total vendas (concluídas): R$ " + toCurrency(soma));

	}

	public void mostRecentCompletedSale() {
		// TODO qual a venda mais recente?

		List<LocalDate> dates  = sales.stream()
				.filter(s -> s.getStatus().toString().equals("COMPLETED"))
				.map(s -> s.getSaleDate()).toList();

		LocalDate dataResultado = dates.stream()
				.max( Comparator.comparing(LocalDate::toEpochDay))
				.get();


		System.out.println("Data da venda mais recente: " + dataResultado);

	}

	public void daysBetweenFirstAndLastCancelledSale() {
		// TODO encontre a quantidade de dias entre a primeira e a ultima venda cancelada

		List<LocalDate> dates  = sales.stream()
				.filter(s -> s.getStatus().toString().equals("CANCELLED"))
				.map(s -> s.getSaleDate()).toList();

		LocalDate dataRecenteResultado = dates.stream()
				.max( Comparator.comparing(LocalDate::toEpochDay))
				.get();

		LocalDate dataRemotaResultado = dates.stream()
				.min( Comparator.comparing(LocalDate::toEpochDay))
				.get();

		Period data = dataRemotaResultado.until(dataRecenteResultado);
		System.out.println("Dias entre vendas canceladas: " + data.getDays());

	}


	public void totalCompletedSalesBySeller(String sellerName) {
		// TODO qual o valor de vendas completas para o vendedor informado?

		final var vendasConcluidas = 		
				sales.stream()
				.filter(s -> s.getStatus().toString().equals("COMPLETED"))
				.filter(s -> s.getSeller().equalsIgnoreCase(sellerName)).count();


		System.out.println("Vendas concluídas por " + sellerName + ": " + vendasConcluidas);
	}

	public void countAllSalesByManager(String managerName) {
		// TODO quantas vendas a equipe do gerente informado fez?

		final var vendasConcluidas = 		
				sales.stream()
				.filter(s -> s.getManager().equalsIgnoreCase(managerName)).count();

		System.out.println("Total vendas conclúidas por equipe (manager "+ managerName +"): " + vendasConcluidas);

	}

	public void totalSalesByStatusAndMonth(Sale.Status status, Month... months) {
		// TODO qual o total de vendas nos meses informados com o status indicado?

		BigDecimal somaJuly = BigDecimal.ZERO;

		somaJuly = sales.stream()
				.filter(s -> s.getStatus().equals(status))
				.filter(s -> s.getSaleDate().getMonth().equals(months[0]))
				.map(s -> s.getValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add); ;


				System.out.println("Total de vendas para " + months[0] + ": R$ " + toCurrency(somaJuly));

				BigDecimal somaSeptember = BigDecimal.ZERO;

				somaSeptember = sales.stream()
						.filter(s -> s.getStatus().equals(status))
						.filter(s -> s.getSaleDate().getMonth().equals(months[1]))
						.map(s -> s.getValue())
						.reduce(BigDecimal.ZERO, BigDecimal::add); ;


						System.out.println("Total de vendas para " + months[1] + ": R$ " + toCurrency(somaSeptember));



	}

	public void countCompletedSalesByDepartment() {
		// TODO qual quantidade de vendas por departamento?
		
		System.out.println("\nVendas por departamento");
		
				sales.stream()	
				  .collect(Collectors.groupingBy(s -> s.getDepartment(), 
						  Collectors.reducing(BigDecimal.ZERO, Sale::getValue, BigDecimal::add)))
				.entrySet()
				.stream()
				.toList()
				.forEach(departmento -> {
				    System.out.println(departmento.getKey() + " = R$" + toCurrency(departmento.getValue()));
				});
		
		
	}

	public void countCompletedSalesByPaymentMethodAndGroupingByYear() {
		// TODO qual a quantidade de vendas por metodo de pagamento por ano?
		
		
		System.out.println("\nVendas por método de pagamento");
		
		sales.stream()	
		  .collect(Collectors.groupingBy(s -> s.getPaymentMethod(), 
				  Collectors.reducing(BigDecimal.ZERO, Sale::getValue, BigDecimal::add)))
		.entrySet()
		.stream()
		.toList()
		.forEach(metodo -> {
		    System.out.println(metodo.getKey() + " = R$" + toCurrency(metodo.getValue()));
		});


	}


	public void top3BestSellers() {
		// TODO um ranking com os 3 melhores vendedores com base no valor total de vendas

System.out.println("\nRanking dos três melhores vendedores");
		
		sales.stream()	
		  .collect(Collectors.groupingBy(s -> s.getSeller(), 
				  Collectors.reducing(BigDecimal.ZERO, Sale::getValue, BigDecimal::add)))
		.entrySet()
		.stream()
		//.sorted(Map.Entry.comparingByValue())
		 .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
		.limit(3)
		  .forEach(vendedor -> {
			    System.out.println(vendedor.getKey() + " = R$" + toCurrency(vendedor.getValue()));
			});

	}



	/*
	 * Use esse metodo para converter objetos BigDecimal para uma represetancao de moeda
	 */
	private String toCurrency(BigDecimal value) {
		return NumberFormat.getInstance().format(value);
	}
}
