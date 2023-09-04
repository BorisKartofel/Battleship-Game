package app;

import java.util.ArrayList;

public class Ship {
    private ArrayList<Cell> cells;
    private ShipStatus status = ShipStatus.NO_INIT;
    private int length = 0;
    Ship(int length, Desk map) {
        this.length = length;
        cells = new ArrayList<>(length);
        Cell cell;
        status = ShipStatus.READY;/*
        for (int i = 0; i < length; i++) {
            cell = new Cell(map, this);
            cells.add(cell);
        }*/

    }
    public int getLength() { return length; }
    public ShipStatus getStatus() { return status; }

    public void takeDamege() {
        if (status == ShipStatus.READY) {
            status = ShipStatus.DAMAGED;
        } else if (status == ShipStatus.DAMAGED) {
            for (Cell c: cells) {
                if (c.getStatus() == CellStatus.CLEAR) {
                    return;
                }
            }//Если целых ячеек не осталось
            status = ShipStatus.DESTROY;
        }
    }

    public boolean isAlive() {
        for (app.Cell c : cells) {
            if (c.getStatus() == app.CellStatus.CLEAR) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<app.Cell> getCells() { return this.cells; }
}
