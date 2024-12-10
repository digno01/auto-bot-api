package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.FeriadoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class FeriadoService {

    public FeriadoResponse checkCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        String holidayName = getHolidayName(currentDate);

        return FeriadoResponse.builder()
                .date(currentDate)
                .isFeriado(holidayName != null)
                .nomeFeriado(holidayName)
                .build();
    }

    private String getHolidayName(LocalDate date) {
        Map<LocalDate, String> holidays = new HashMap<>();
        int year = date.getYear();

        // Fixed holidays
        holidays.put(LocalDate.of(year, 1, 1), "Ano Novo");
        holidays.put(LocalDate.of(year, 4, 21), "Tiradentes");
        holidays.put(LocalDate.of(year, 5, 1), "Dia do Trabalho");
        holidays.put(LocalDate.of(year, 9, 7), "Independência do Brasil");
        holidays.put(LocalDate.of(year, 10, 12), "Nossa Senhora Aparecida");
        holidays.put(LocalDate.of(year, 11, 2), "Finados");
        holidays.put(LocalDate.of(year, 11, 15), "Proclamação da República");
        holidays.put(LocalDate.of(year, 12, 25), "Natal");

        // Calculate Easter and moving holidays
        LocalDate easter = calculateEasterForYear(year);
        holidays.put(easter.minusDays(47), "Segunda-feira de Carnaval");
        holidays.put(easter.minusDays(46), "Terça-feira de Carnaval");
        holidays.put(easter.minusDays(2), "Sexta-feira Santa");
        holidays.put(easter.plusDays(60), "Corpus Christi");

        return holidays.get(date);
    }

    private LocalDate calculateEasterForYear(int year) {
        // Meeus/Jones/Butcher algorithm
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }
}
