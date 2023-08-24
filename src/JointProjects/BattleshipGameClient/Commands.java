package JointProjects.BattleshipGameClient;

public enum Commands {
    PLAY("/play", "Начать игру", 0, 4),
    END("/end", "Завершить игру", 0, 1),
    UNKNOWN("", "Команда не существует", -1, 1);

    private String command;
    private String description;
    private int current_step;
    private int next_step;

    Commands(String command, String description, int current_step, int next_step)
    {
        this.command = command;
        this.description = description;
        this.current_step = current_step;
        this.next_step = next_step;
    }

    public String getName()
    {
        return this.command;
    }
    public String getDescription()
    {
        return this.description;
    }

    public static Commands getCommand(String command) {
        for (Commands c : Commands.values()) {
            if (c.command.equals(command)) {
                return c;
            }
        }
        return Commands.UNKNOWN;
    }

    public boolean isAvailable(int step) {
        return step == this.current_step || current_step == 0;
    }
}