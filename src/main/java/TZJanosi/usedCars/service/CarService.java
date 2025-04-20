package TZJanosi.usedCars.service;

import TZJanosi.usedCars.dto.CarDto;
import TZJanosi.usedCars.dto.CreateCarCommand;
import TZJanosi.usedCars.dto.Criteria;
import TZJanosi.usedCars.exception.CarNotFoundException;
import TZJanosi.usedCars.model.Car;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {
    private AtomicLong atomicLong= new AtomicLong();
    private ModelMapper modelMapper;
    private List<Car> cars=new ArrayList<>();

    public CarService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }



    public List<CarDto> findAllCars() {
        return cars.stream().map(c->modelMapper.map(c, CarDto.class)).toList();
    }

    public CarDto addNewCar(CreateCarCommand command) {
        Car car=modelMapper.map(command,Car.class);
        car.setId(atomicLong.getAndIncrement());
        cars.add(car);
        return modelMapper.map(car,CarDto.class);
    }

    public List<CarDto> findFilteredCars(Criteria criteria) {
        System.out.println(criteria);
        if(!criteria.containsCriteria()){
            return findAllCars();
        }
        else {
            return filteredCars(criteria);
        }
    }

    private List<CarDto> filteredCars(Criteria criteria) {
//        System.out.println("in filteredCars criteria: "+criteria);
        Comparator<Car> comparator=Comparator.comparingInt(Car::actualKmState);
        if(criteria.getSortDirection()!=null && criteria.getSortDirection().toString().equals("DESC")){
            comparator=comparator.reversed();
        }
        return cars.stream()
                .filter(c->c.meetCriteria(criteria))
                .sorted(comparator)
                .map(c->modelMapper.map(c, CarDto.class))
                .toList();
    }

    public List<String> getBrands() {
        return cars.stream()
                .map(Car::getType)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    public Car findCarById(long id) {
        return cars.stream().filter(c->c.getId()==id).findFirst().orElseThrow(()->new CarNotFoundException(id));
    }
    public CarDto findCarDtoById(long id) {
        return modelMapper.map(findCarById(id),CarDto.class);
    }

    public void deleteCarById(long id) {
        Car carToDeletre=findCarById(id);
        cars.remove(carToDeletre);
    }

    public CarDto addKmState(long id, int km) {
        Car car=findCarById(id);
        car.addKilometerState(km);
        return modelMapper.map(car,CarDto.class);
    }
}
