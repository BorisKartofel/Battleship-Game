package app;

import java.util.Optional;

public class Cell {
    private CellStatus status = CellStatus.NO_INIT;
    private Ship ship = null;
    private Desk map = null;
    private boolean busy;
    private String cord;

    public Cell(Desk map, Ship ship, String cord) {
        this(map, cord);
        this.ship = ship;
        busy = true;
    }

    public Cell(Desk map, String cord) {
        this.map = map;
        status = CellStatus.CLEAR;
        busy = false;
        this.cord = cord;
    }

    public void takeDamage() {
        status = CellStatus.DAMAGED;
        if (getShip().isPresent()) {
            ship.takeDamege();
        }
    }
    /*
    public boolean isCanShootAgain(){
        if (status == CellStatus.DAMAGED) return true;
        else if (status == CellStatus.CLEAR && getShip().isPresent()) return true;
        return false;

    }*/

    public CellStatus getStatus() {
        return status;
    }

    public void setStatus(CellStatus status) { this.status = status; }

    public Optional<Ship> getShip() { return Optional.ofNullable(ship); }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Optional<Desk> getMap() {
        return Optional.ofNullable(map);
    }

    public void setMap(Desk map) {
        this.map = map;
    }

    public boolean isBusy() { return busy; }

    public void setBusy() {
        this.busy = true;
    }

    public String getCord() { return cord; }
}
