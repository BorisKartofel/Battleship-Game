package JointProjects.BattleshipGameServer;

public class Desk {
    private Cell[][] desk;
    private boolean isVisible;
    StringBuilder printedDesk = new StringBuilder();


    public String getPrintedDesk() {
        int counter = 0, rowCounter = 1;
        char c;
        printedDesk.setLength(0);
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
                    if (desk[y][x] != Cell.BATTLESHIP) {
                        printedDesk.append(desk[y][x].getSymbol());
                    }
                    else {
                        printedDesk.append(Cell.EMPTY.getSymbol());
                    }
                }
            }
        }
        return printedDesk.toString();
    }

    public Desk(boolean isVisible) {
        this.isVisible = isVisible;
        int ships[] = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        desk = new Cell[10][10];
        for (int y = 0; y < 10; y++){
            //desk[y] = new Square[10];
            for (int x = 0; x < 10; x++){
                desk[y][x] = Cell.EMPTY;
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
                    if (desk[y1][x1] != Cell.EMPTY) {
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
                        desk[y1][x1] = Cell.ADJACENT;
                    }
                }
                for (y1 = y; y1 <= y + vectorY; y1++) {
                    for (x1 = x; x1 <= x + vectorX; x1++) {
                        desk[y1][x1] = Cell.BATTLESHIP;
                    }
                }
                i++;
            }
        }
    }

    public void shoot(int x, int y) {
        if (desk[y][x] == Cell.EMPTY || desk[y][x] == Cell.ADJACENT) {
            desk[y][x] = Cell.EXPLORED;
        } else if (desk[y][x] == Cell.BATTLESHIP) {
        desk[y][x] = Cell.DAMAGED;
        }
    }

    public String getRespond(int x, int y) {
        if (desk[y][x] == Cell.EMPTY || desk[y][x] == Cell.ADJACENT){
            return "Пустая клетка";
        } else if (desk[y][x] == Cell.DAMAGED) {
            return "Корабль повреждён!";
        } else if (desk[y][x] == Cell.EXPLORED){
            return "Вы уже стреляли сюда";
        }

        return "Тут расположен корабль (Соо для проверки)"; // Посмотреть всплывёт ли где-нибудь. Удалить, если нет
    }

    public void setSquareStateAccordingToRespond (int x, int y, String s){
        if (s.equals("Корабль повреждён!")){
            desk[y][x] = Cell.DAMAGED;
        } else if (s.equals("Вы уже стреляли сюда")){
            desk[y][x] = Cell.EXPLORED;
        }
    }

    public boolean isAbleToShootAgain(int x, int y) {
        return desk[y][x] == Cell.BATTLESHIP || desk[y][x] == Cell.DAMAGED;
    }
}


