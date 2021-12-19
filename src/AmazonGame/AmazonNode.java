package AmazonGame;

import AmazonBoard.AmazonBoard;
import AmazonEvaluator.AmazonMove;
import AmazonBoard.AmazonSquare;
import AmazonEvaluator.InvalidMoveException;

import java.util.ArrayList;

/**
 * Created by jeff on 19/03/17.
 */
public class AmazonNode {
    public AmazonNode parentNode;
    public ArrayList<AmazonNode> children = new ArrayList<>();

    public AmazonBoard nodeBoard;
    public AmazonMove nodeMove;

    double score;

    public AmazonNode(AmazonNode parentNode, AmazonMove nodeMove) {
        this.parentNode = parentNode;
        this.nodeMove = nodeMove;
    }

    public void addChild(AmazonNode childNode) {
        children.add(childNode);
    }

    public AmazonNode getChild(int i) {
        return children.get(i);
    }

    public ArrayList<AmazonNode> getChildren() {
        return children;
    }

    public void setMove(AmazonMove nodeMove) throws InvalidMoveException {
        this.nodeBoard.executeMove(nodeMove);
    }

    public void setScore(double score){
        this.score = score;
    }

    public void setNodeBoard(AmazonBoard nodeBoard){
        this.nodeBoard = nodeBoard;
    }

    public double getScore() {
        return score;
    }

    public AmazonMove getMove() {
        return nodeMove;
    }

    public ArrayList<AmazonSquare> generateQueens(int color) {
        return nodeBoard.getQueenList(color);
    }

    @Override
    public String toString(){
        return nodeMove.toString();
    }
}
