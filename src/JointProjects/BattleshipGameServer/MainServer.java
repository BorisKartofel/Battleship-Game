package JointProjects.BattleshipGameServer;

import java.io.*;
import java.net.*;

public class MainServer {
    static final int port = 7777;
    private static InetAddress bindAddr; // Адрес сервера
    private final static String radminLocalAddressIP = "localhost"; // Строковое представление адреса сервера 26.214.188.116
    private static Socket clientSocket; // Сокет для общения
    private static ServerSocket server; // серверсокет
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) {
        // Получаем адрес сервера из строки (Получаем объект InetAddress из объекта String)
        // Он будет нужен, чтобы создавать сокет сервера каждый раз по одному и тому же адресу
        try {
            bindAddr = InetAddress.getByName(radminLocalAddressIP);
        } catch (UnknownHostException e) {
            System.err.println("Не удалось открыть сервер по определенному IP-адресу");
        }

        try {
            try  {
                server = new ServerSocket(port, 4, bindAddr); // backlog = 4, из-за ограничения одновременных подключений в RadminVPN.
                // Если был бы выкуплен постоянный IP адрес на стороннем сервере - такого ограничения не было бы
                System.out.println("Сервер запущен!"); // хорошо бы серверу объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока кто-нибудь не захочет подключиться
                try {
                    // Получаем IP-адрес клиента, с которым установили соединение
                    System.out.println(((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress().toString().replace("/",""));
                    // Получаем порт, по которому будет происходить общение с клиентом
                    System.out.println(((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getPort());

                    // установив связь и воссоздав сокет для общения с клиентом можно перейти к созданию потоков ввода/вывода.
                    // теперь мы можем принимать сообщения
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // и отправлять сообщения
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                    System.out.println(word);
                    // не долго думая отвечает клиенту
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                    out.flush(); // выталкиваем все из буфера
                    //Цикл, в котором будет происходить общение сервера с клиентом
                    while (true){
                        break;
                    }
                } finally { // в любом случае сокет будет закрыт
                    try {
                        clientSocket.close();
                        // потоки тоже хорошо бы закрыть
                        in.close();
                        out.close();
                    } catch (IOException e) { //
                        System.err.println("Ошибка. Невозможно закрыть неоткрытый поток ввода-вывода");
                    }
                }
            } finally {
                System.out.println("Сервер закрыт!");
                try {
                    server.close();
                } catch (NullPointerException e) { // Нужно обработать исключение, когда сервер не смог создаться, соответственно переменная server ссылается на null
                    System.err.println("Ошибка. Невозможно закрыть незапущенный сервер");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка. Не удалось запустить сервер");
        }
    }
}
