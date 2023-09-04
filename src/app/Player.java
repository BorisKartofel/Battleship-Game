package app;

public class Player {
    app.Desk myDesk;
    public Player() {
        myDesk = new app.Desk();
    }

    public app.ShootStatus takeShoot(String cord) {
        app.Cell cell = myDesk.getCell(cord);
        app.ShootStatus canShootAgain = app.ShootStatus.MISSED;
        app.CellStatus status = cell.getStatus();
        if (status == app.CellStatus.DAMAGED) {
            canShootAgain = app.ShootStatus.RETRY;
        } else if (status == app.CellStatus.CLEAR && cell.getShip().isPresent()) {
            for (app.Cell c: cell.getShip().get().getCells()) {
                if (c != cell && c.getStatus() == app.CellStatus.CLEAR) canShootAgain = app.ShootStatus.DAMAGED;
                else {
                    canShootAgain = app.ShootStatus.DESTROY;
                }
            }
        } else canShootAgain = app.ShootStatus.MISSED;

        cell.takeDamage();
        if (cell.getShip().isPresent() && cell.getShip().get().getStatus() == ShipStatus.DESTROY) {
            char symbol;
            int row;
            for (app.Cell c: cell.getShip().get().getCells()) {
                symbol = c.getCord().charAt(0) ;
                row = Character.getNumericValue(c.getCord().charAt(1));
                for (int row1 = row - 1; row1 <= row + 1; row1++){
                    for(char symbol1 = (char)(symbol - 1); symbol1 <= symbol +1; symbol1++){
                        cell.getMap().get().getCell(symbol1+""+row1).setStatus(app.CellStatus.DAMAGED);
                    }
                }
            }

        }
        return canShootAgain;
    }
}
