package br.eti.vitoremanuel.contadorvendas;

import java.time.Month;

public class Application {

	public Application() {
		startReading();
	}

	private void startReading() {

		final SalesReader salesReader = new SalesReader("arquivo-dados.csv");

		salesReader.totalOfCompletedSales();
		salesReader.totalOfCancelledSales();
		salesReader.mostRecentCompletedSale();
		salesReader.daysBetweenFirstAndLastCancelledSale();
		salesReader.totalCompletedSalesBySeller("Adriana Gomes");
		salesReader.countAllSalesByManager("Elenice Mendes");
		salesReader.totalSalesByStatusAndMonth(Sale.Status.COMPLETED, Month.JULY, Month.SEPTEMBER);
		salesReader.countCompletedSalesByDepartment();
		salesReader.countCompletedSalesByPaymentMethodAndGroupingByYear();
		salesReader.top3BestSellers();
	}

	public static void main(String[] args) {

		new Application();
	}
}
