package TZJanosi.usedCars.dto;

import TZJanosi.usedCars.model.CarCondition;
import TZJanosi.usedCars.model.KilometerState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CarDto {
    private long id;
    private String type;
    private String model;
    private int ageInYears;
    private CarCondition condition;
    private List<KilometerState> kilometerStates=new ArrayList<>();
}
