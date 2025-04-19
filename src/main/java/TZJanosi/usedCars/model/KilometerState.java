package TZJanosi.usedCars.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class KilometerState {
    private int actualValue;
    private LocalDate date;
}
