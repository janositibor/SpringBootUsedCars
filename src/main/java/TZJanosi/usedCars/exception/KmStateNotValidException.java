package TZJanosi.usedCars.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class KmStateNotValidException  extends AbstractThrowableProblem {
    public KmStateNotValidException(long km){
        super(URI.create("/api/cars/KM_NOT_VALID"),
                "Km not valid",
                Status.BAD_REQUEST,
                String.format("Unexpected actual value of km counter! %d km",km));

    }
}
