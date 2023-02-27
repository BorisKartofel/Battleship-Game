package JointProjects.BattleshipGame;

public enum Square {
    EMPTY(' '),EXPLORED('*'), DAMAGED('='), BATTLESHIP('+');
    private char symbol;

    Square(char symbol){
        this.symbol = symbol;
    }

    public char getSymbol(){
        return symbol;
    }
}
