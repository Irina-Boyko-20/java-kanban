package manager;

public final class Managers {
    private Managers() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
