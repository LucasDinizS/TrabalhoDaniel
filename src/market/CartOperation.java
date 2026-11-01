package market;

public record CartOperation(Type type, int code, int quantity) {
    public enum Type {
        ADD,
        REMOVE
    }
}
