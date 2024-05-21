public enum Grade {
    STANDARD("일반"),
    DELUXE("우등"),
    PREMIUM("프리미엄");

    private String korean;

    Grade(String korean) {
        this.korean = korean;
    }

    public static Grade fromString(String text) {
        for (Grade grade : Grade.values()) {
            if (grade.korean.equalsIgnoreCase(text)) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Unknown grade: " + text);
    }

    @Override
    public String toString() {
        return korean;
    }
}
