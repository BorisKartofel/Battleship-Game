package JointProjects.BattleshipGameClient;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    /*
        class Main - класс на котором мы будем запускать приложение
        class Desk - описана логика прорисовки доски
        class Ship - описана логика взаимодействия с кораблем: узнать состояние, изменить состояние
        class Game - описана логика самой игры, правила
        class Multiplayer - описана логика подключения двух игроков к игре.
        class DBConnection - описано подключение к БД
    */
    {
        Desk d = new Desk(true);
        Desk d2 = new Desk(false);
        System.out.print(d.drawDesk());
    }
    public static void main(String[] args) {
        Game g = new Game();

    }

    public class Desk {
        private Square[][] desk;
        private boolean isVisible;

        public String drawDesk(){
            String result = "";
            int counter = 0, rowCounter = 1;
            char c;
            result += " |А |Б |В |Г |Д |Е |Ж |З |И |Й |";
            //System.out.print(" |А |Б |В |Г |Д |Е |Ж |З |И |Й |");
            for (int y = 0; y < 10; y++) {
                result +="#|";
                //System.out.println();
                //System.out.print(y + "|");
                for (int x = 0; x < 10; x++){
                    if (isVisible) {
                        result += desk[y][x].getSymbol();
                        //System.out.print(desk[y][x].getSymbol() + "");
                    }
                    else {
                        if (desk[y][x] != Square.BATTLESHIP) {
                            result += desk[y][x].getSymbol();
                            //System.out.print(desk[y][x].getSymbol() + "");
                        }
                        else {
                            result += Square.EMPTY.getSymbol();
                            //System.out.print(Square.EMPTY.getSymbol() + "");
                        }
                    }
                }
            }
            result += "#";
            //System.out.println();
            return result;
        }

         public Desk(boolean isVisible) {
            this.isVisible = isVisible;
            int ships[] = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
            desk = new Square[10][10];
            for (int y = 0; y < 10; y++){
                //desk[y] = new Square[10];
                for (int x = 0; x < 10; x++){
                    desk[y][x] = Square.EMPTY;
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
                        if (desk[y1][x1] != Square.EMPTY) {
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
                            desk[y1][x1] = Square.OCCUPIED;
                        }
                    }
                    for (y1 = y; y1 <= y + vectorY; y1++) {
                        for (x1 = x; x1 <= x + vectorX; x1++) {
                            desk[y1][x1] = Square.BATTLESHIP;
                        }
                    }
                    i++;
                }
            }
        }
        public void makeShoot(int x, int y) {

            if (desk[y][x] == Square.EMPTY || desk[y][x] == Square.OCCUPIED) {
                desk[y][x] = Square.EXPLORED;
            } else if (desk[y][x] == Square.BATTLESHIP) {
                desk[y][x] = Square.DAMAGED;
            } /* else if (desk[y][x] == Square.DAMAGED || desk[y][x] == Square.EXPLORED) {
                return;
            } */
        }
        public boolean isAbleToShootAgain(int x, int y) {
            return desk[y][x] == Square.BATTLESHIP || desk[y][x] == Square.DAMAGED || desk[y][x] == Square.EXPLORED;
        }
    }

    private static class DBConnection {

    }
}