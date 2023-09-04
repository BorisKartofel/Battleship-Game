package app;

import java.util.ArrayList;
import java.util.HashMap;

public class Desk {
    private HashMap<String, app.Cell> map;
    private ArrayList<Ship> ships = new ArrayList<>(10);

    public Desk() {
        this.map = new HashMap<>();
        Ship ship;
        String cord;
        int row, spin;
        boolean canStand;
        char col;
        for (row = 1; row <=9; row++) {
            for (col = 'A'; col <= 'J'; col++) { //A-J
                cord = col +""+ row;
                this.map.put(cord, new app.Cell(this, cord));
            }
        }

            for (int lengthShip = 4; lengthShip >= 1; lengthShip--) {

                for (int countShips = 5 - lengthShip; countShips > 0; ) {
                    //Проверяю возможность установить корабль по случайно полученным координатам
                    row = (int) (Math.random() * 10 % 9 + 1);
                    col = (char) ((int) 'A' + (int) (Math.random() * 10 % 9));
                    spin = (int) (Math.random() * 10 % 2);
                    canStand = true;
                    for (int row1 = row, row2 = row + (spin == 0 ? lengthShip - 1 : 0);
                         row1 <= row2 && canStand;
                         row1++) {

                        for (char col1 = col, col2 = (char) (col + (spin == 1 ? lengthShip - 1 : 0));
                             col1 <= col2 && canStand;
                             col1++) {

                            cord = col1 + "" + row1;
                            try {
                                if (this.map.get(cord).isBusy()) {
                                    canStand = false;
                                }
                            } catch (NullPointerException e) {
                                canStand = false;
                            }
                        }
                    }
                    if (canStand) {
                        //Устанавливаю клетки занятыми
                        for (int row1 = row - 1, row2 = row + (spin == 0 ? lengthShip - 1 : 0) + 1;
                             row1 <= row2;
                             row1++) {

                            for (char col1 = (char) (col - 1), col2 = (char) (col + (spin == 1 ? lengthShip - 1 : 0) + 1);
                                 col1 <= col2;
                                 col1++) {

                                cord = col1 + "" + row1;
                                try {
                                    this.map.get(cord).setBusy();
                                } catch (NullPointerException e) {

                                }
                            }
                        }
                        //Заменяю по координатам ячейки на новые
                        ship = new Ship(lengthShip, this);
                        ships.add(ship);
                        //for (Cell c : ship.getCells()) {
                        for (int counter = 0; counter < lengthShip; counter++) {
                            cord = col + "" + row;
                            this.map.get(cord).setShip(ship);
                            //this.map.replace(cord, c);
                            if (spin == 0) {
                                row++;
                            } else if (spin == 1) {
                                col++;
                            }
                        }
                        countShips--;
                    }
                }
            }
        }


    public app.Cell getCell(String cord) {
        return map.get(cord);
    }

    public String getDesk() {
        StringBuilder printedDesk = new StringBuilder();
        String cord;
        String symbol = "";
        printedDesk.append(" |A |B |C |D |E |F |G |H |I |J |");

        for (int row = 1; row <= 9; row++) {
            printedDesk.append('#'); // Вводим дополнительный символ "#", чтобы отправлять двумерный массив одной строкой
            // Клиент при получении строки, будет заменять "#" на символ переноса строки "\n"
            printedDesk.append(row).append('|');
            for (char col = 'A'; col <= 'J'; col++){
                cord = col +""+ row;
                if (map.get(cord).getShip().isPresent()) {
                    if (map.get(cord).getStatus() == app.CellStatus.DAMAGED) {//DAMAGED
                        symbol = "[X]";
                    } else {//BATTLESHIP
                        symbol = "[ ]";
                    }
                } else if (map.get(cord).isBusy()) { //OCCUPIED
                    symbol = " - ";
                } else if (map.get(cord).getStatus() == app.CellStatus.DAMAGED) {//EXPLORED
                    symbol = " * ";
                } else {//EMPTY
                    symbol = "   ";
                }
                printedDesk.append(symbol);
            }
        }
        return printedDesk.toString();
    }

    public boolean isAlive() {
        for (Ship ship: ships) {
            if (ship.getStatus() != ShipStatus.DESTROY) return true;
        }
        return false;
    }
}
