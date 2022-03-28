package br.eti.vitoremanuel.contadorvendas;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractBeanField<String, LocalDate> {

    @Override
    protected LocalDate convert(String value) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
    	  LocalDate localDate = LocalDate.parse(value, formatter);
    	return localDate; // TODO implementar aqui a conversao data da venda, duvidas? olhe o arquivo de origem dos dados
    }
}
