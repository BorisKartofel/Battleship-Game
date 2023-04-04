package JointProjects.BattleshipGameServer;

public enum Square {
    EMPTY("   "),EXPLORED(" * "), DAMAGED("[X]"), BATTLESHIP("[ ]"), OCCUPIED("   ");
    private String symbol;
    Square(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }
}
