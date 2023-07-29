package JointProjects.BattleshipGameServer;

public enum Cell {
    EMPTY("   "),EXPLORED(" * "), DAMAGED("[X]"), BATTLESHIP("[ ]"), OCCUPIED("   ");
    private String symbol;
    Cell(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }
}
