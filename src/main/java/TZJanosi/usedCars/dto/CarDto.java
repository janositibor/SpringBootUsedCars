package TZJanosi.usedCars.dto;

import TZJanosi.usedCars.model.CarCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private long id;
    private String brand;
    private String model;
    private int ageInYears;
    private CarCondition condition;
    private List<KilometerStateDto> kilometerStates=new ArrayList<>();


}
