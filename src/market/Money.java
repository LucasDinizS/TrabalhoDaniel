package market;

public final class Money {
    private static final int[] DENOMINATIONS = {20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 25, 10, 5, 1};

    private Money() {
    }

    public static String format(long cents) {
        String sign = cents < 0 ? "-" : "";
        long absolute = Math.abs(cents);
        return String.format("%sR$ %d,%02d", sign, absolute / 100, absolute % 100);
    }

    public static int[] breakIntoDenominations(int changeInCents) {
        int[] counts = new int[DENOMINATIONS.length];
        int remaining = changeInCents;
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            counts[i] = remaining / DENOMINATIONS[i];
            remaining = remaining % DENOMINATIONS[i];
        }
        return counts;
    }

    public static int denominationAt(int index) {
        return DENOMINATIONS[index];
    }

    public static boolean isBill(int index) {
        return DENOMINATIONS[index] >= 200;
    }
}
