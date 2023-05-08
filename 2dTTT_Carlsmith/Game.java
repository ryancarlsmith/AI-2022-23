public class Game {

    public interface State<Action> {
        int evaluate();
        boolean isTerminal();
        State next(Action action);
        Iterable<Action> moves();
    }
}
