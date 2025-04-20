package TZJanosi.usedCars.dto;

import TZJanosi.usedCars.model.CarCondition;
import TZJanosi.usedCars.model.KilometerState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateCarCommand {
    private String brand;
    private String model;
    private int ageInYears;
    private CarCondition condition;
    private List<KilometerState> kilometerStates=new ArrayList<>();

    public CreateCarCommand(String brand, String model, int ageInYears, CarCondition condition,int actualKm) {
        this.brand = brand;
        this.model = model;
        this.ageInYears = ageInYears;
        this.condition = condition;
        kilometerStates.add(new KilometerState(actualKm, LocalDate.now()));
    }
}
