package AmazonEvaluator;

import AmazonBoard.*;

/**
 * Created by D on 2/12/2017.
 */
public class AmazonMove{

    AmazonSquare sInit, sFinal, arrow;

    public AmazonSquare getInitial() {
        return sInit;
    }

    public AmazonSquare getFinal() {
        return sFinal;
    }

    public AmazonSquare getArrow() {
        return arrow;
    }

    public AmazonMove(AmazonSquare sInit, AmazonSquare sFinal, AmazonSquare arrow) {

        this.sInit = sInit;
        this.sFinal = sFinal;
        this.arrow = arrow;

    }

    @Override
    public String toString() {

        return sInit.toString() + ", " + sFinal.toString() + ", " + arrow.toString();

    }

    @Override
    public boolean equals(Object o) {

        boolean equals = false;
        if (o instanceof AmazonMove) {

            AmazonMove m = (AmazonMove) o;
            equals = getInitial().equals(m.getInitial());
            equals &= getFinal().equals(m.getFinal());
            equals &= getArrow().equals(m.getArrow());
        }

        return equals;
    }
}
