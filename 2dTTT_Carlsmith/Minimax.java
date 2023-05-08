/*
Ryan Carlsmith
Mr. Paige
Artifical Intelligence
2D TTT #1
 */

public class Minimax<Action> {

    public Action bestMove(Game.State<Action> state) {
        Action result = null;
        int max = Integer.MIN_VALUE;
        for (Action a : state.moves()){
            int value = minValue(state.next(a));
            if (max < value){
                max = value;
                result = a;
            }
        }
        return result;
    }

    public int maxValue(Game.State<Action> state) {
        if (state.isTerminal()) return 1;
        int utilityValue = Integer.MIN_VALUE;

        for (Action a : state.moves()){
            utilityValue = Math.max(utilityValue, minValue(state.next(a)));
        }
        return utilityValue;
    }

    public int minValue(Game.State<Action> state) {
        if (state.isTerminal()) return -1;
        int utilityValue = Integer.MAX_VALUE;

        for (Action a : state.moves()){
            utilityValue = Math.min(utilityValue, maxValue(state.next(a)));
        }
        return utilityValue;
    }
}
