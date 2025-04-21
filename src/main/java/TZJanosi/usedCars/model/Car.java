package TZJanosi.usedCars.model;

import TZJanosi.usedCars.dto.Criteria;
import TZJanosi.usedCars.exception.KmStateNotValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

@Data
@NoArgsConstructor
public class Car {
    private long id;
    private String brand;
    private String model;
    private int ageInYears;
    private CarCondition condition;
    private List<KilometerState> kilometerStates=new ArrayList<>();

    public void addKilometerState(int km){
        if(actualKmState()>=km){
            throw new KmStateNotValidException(km);
        }
        kilometerStates.add(new KilometerState(km, LocalDate.now()));
    }

    public boolean meetCriteria(Criteria criteria) {
//        System.out.println("in meetCriteria criteria: "+criteria);
        if(criteria.getBrand()!=null && !criteria.getBrand().equalsIgnoreCase(brand)){
            return false;
        }
        if(criteria.getModel()!=null && !criteria.getModel().equalsIgnoreCase(model)){
            return false;
        }
        if(criteria.getMaxAgeInYears()>0 && criteria.getMaxAgeInYears()<ageInYears){
            return false;
        }
        if(criteria.getCondition()!=null && criteria.getCondition().getValue()>condition.getValue()){
            return false;
        }
        if(criteria.getMaxKm()>0 && criteria.getMaxKm()<actualKmState()){
            return false;
        }
        return true;
    }

    public int actualKmState() {
        return kilometerStates.get(kilometerStates.size()-1).getActualValue();
    }
}
