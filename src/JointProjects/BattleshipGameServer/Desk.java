package JointProjects.BattleshipGameServer;

import JointProjects.BattleshipGameServer.Square;

public class Desk {
    private JointProjects.BattleshipGameServer.Square[][] desk;
    private boolean isVisible;
    StringBuilder printedDesk = new StringBuilder();


    public String getPrintedDesk(){
        int counter = 0, rowCounter = 1;
        char c;
        printedDesk.append(" |А |Б |В |Г |Д |Е |Ж |З |И |К |");
        for (int y = 0; y < 10; y++) {
            printedDesk.append('#'); // Вводим дополнительный символ "#", чтобы отправлять двумерный массив одной строкой
            // Клиент при получении строки, будет заменять "#" на символ переноса строки "\n"
            printedDesk.append(y).append('|');
            for (int x = 0; x < 10; x++){
                if (isVisible) {
                    printedDesk.append(desk[y][x].getSymbol());
                }
                else {
                    if (desk[y][x] != JointProjects.BattleshipGameServer.Square.BATTLESHIP) {
                        printedDesk.append(desk[y][x].getSymbol());
                    }
                    else {
                        printedDesk.append(JointProjects.BattleshipGameServer.Square.EMPTY.getSymbol());
                    }
                }
            }
        }
        return printedDesk.toString();
    }

    public Desk(boolean isVisible) {
        this.isVisible = isVisible;
        int ships[] = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        desk = new JointProjects.BattleshipGameServer.Square[10][10];
        for (int y = 0; y < 10; y++){
            //desk[y] = new Square[10];
            for (int x = 0; x < 10; x++){
                desk[y][x] = JointProjects.BattleshipGameServer.Square.EMPTY;
            }
        }
        if (isVisible) {
            int x, y, x1, y1;
            boolean isHorisontal;
            boolean isFree = true;
            for (int i = 0; i < ships.length; ) {
                x = (int) (Math.random() * 10);
                x1 = x;
                y = (int) (Math.random() * 10);
                y1 = y;
                isHorisontal = ((int) (Math.random() * 10) > 4 ? true : false);

                if ((isHorisontal ? x : y) + ships[i] > 9) {
                    continue;
                }

                for (int l = 0; ((l < ships[i]) && isFree); l++) {
                    if (desk[y1][x1] != JointProjects.BattleshipGameServer.Square.EMPTY) {
                        isFree = false;
                    }
                    if (isHorisontal)
                        x1++;
                    else
                        y1++;
                }
                if (!isFree) {
                    isFree = true;
                    continue;
                }

                int vectorY = (isHorisontal ? 0 : ships[i] - 1),
                        vectorX = (isHorisontal ? ships[i] - 1 : 0),
                        minY = (y > 0 ? (y - 1) : 0),
                        maxY = (y + vectorY >= 9 ? (9 - ships[i]) : (y + vectorY + 1)),
                        minX = (x > 0 ? (x - 1) : 0),
                        maxX = (x + vectorX >= 9 ? (9 - ships[i]) : (x + vectorX + 1));
                ///*
                for (y1 = minY; y1 <= maxY; y1++) {//вместо 3 блоков закрывает 5, по середине не закрывает
                    for (x1 = minX; x1 <= maxX; x1++) {
                        desk[y1][x1] = JointProjects.BattleshipGameServer.Square.OCCUPIED;
                    }
                }
                for (y1 = y; y1 <= y + vectorY; y1++) {
                    for (x1 = x; x1 <= x + vectorX; x1++) {
                        desk[y1][x1] = JointProjects.BattleshipGameServer.Square.BATTLESHIP;
                    }
                }
                i++;
            }
        }
    }
    public String shootAndGetRespond(int x, int y) {
        if (desk[y][x] == JointProjects.BattleshipGameServer.Square.EMPTY || desk[y][x] == JointProjects.BattleshipGameServer.Square.OCCUPIED) {
            desk[y][x] = JointProjects.BattleshipGameServer.Square.EXPLORED;
            return "Промах!";
        } else if (desk[y][x] == JointProjects.BattleshipGameServer.Square.BATTLESHIP) {
            desk[y][x] = JointProjects.BattleshipGameServer.Square.DAMAGED;
            return "Попал!";
        }
        return "Типа пустое сообщение для проверки";
    }
    public boolean isAbleToShootAgain(int x, int y) {
        return desk[y][x] == JointProjects.BattleshipGameServer.Square.BATTLESHIP || desk[y][x] == JointProjects.BattleshipGameServer.Square.DAMAGED || desk[y][x] == Square.EXPLORED;
    }
}


