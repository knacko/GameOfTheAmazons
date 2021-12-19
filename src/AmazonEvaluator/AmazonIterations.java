package AmazonEvaluator;

/**
 * Created by jeff on 18/03/17.
 */
public class AmazonIterations {
    int iterations;

    public AmazonIterations(){
        iterations = 0;
    }

    AmazonIterations increment(){
        iterations++;
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(iterations);
    }
}
