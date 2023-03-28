package JointProjects.BattleshipGameServer;

public enum Square {
    EMPTY("   "),EXPLORED(" * "), DAMAGED("[x]"), BATTLESHIP("[ ]"), OCCUPIED("   ");
    private String symbol;
    Square(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }
}
