public enum Grade {
    STANDARD("일반"),
    DELUXE("우등"),
    PREMIUM("프리미엄");

    private String korean;

    Grade(String korean) {
        this.korean = korean;
    }

    @Override
    public String toString() {
        return korean;
    }
}
