public class Main {
    private static final int EXCELLENT_MARK = 90;
    private static final int GOOD_MARK = 75;
    private static final int PASS_MARK = 50;

    public static void main(String[] args) {
        int[][] studentMarks = {
                {80, 90, 85},
                {60, 70, 65},
                {95, 90, 100},
                {40, 45, 50}
        };

        for (int i = 0; i < studentMarks.length; i++) {
            double average = calculateAverage(studentMarks[i]);
            String result = classifyResult(average);

            System.out.println("Student " + (i + 1));
            System.out.println("Average: " + average);
            System.out.println("Result: " + result);
            System.out.println();
        }
    }

    private static double calculateAverage(int[] marks) {
        int sum = 0;

        for (int mark : marks) {
            sum += mark;
        }

        return (double) sum / marks.length;
    }

    private static String classifyResult(double average) {
        if (average >= EXCELLENT_MARK) {
            return "Excellent";
        }

        if (average >= GOOD_MARK) {
            return "Good";
        }

        if (average >= PASS_MARK) {
            return "Passed";
        }

        return "Failed";
    }
}
