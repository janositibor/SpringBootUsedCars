package TZJanosi.usedCars.dto;

import TZJanosi.usedCars.model.CarCondition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Criteria {
    private String type;
    private String model;
    private int maxAgeInYears;
    private int maxKm;
    private CarCondition condition;
    private SortDirection sortDirection;

    public boolean containsCriteria() {
        if(type!=null){
            return true;
        }
        if(model!=null){
            return true;
        }
        if(maxAgeInYears>0){
            return true;
        }
        if(maxKm>0){
            return true;
        }
        if(condition!=null){
            return true;
        }
        if(sortDirection!=null){
            return true;
        }
        return false;
    }
}
