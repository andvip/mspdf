package 逻辑分析;

import java.util.Scanner;

public class 数字矩阵2 {
    public static int[] directions = {0, 1, 0, -1, 0};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[] params = split(in.nextLine(), " ");
        int k = params[0];
        int n = params[1];
        System.out.println("k->" + k + ",n->" + n);
        int row = (k - 1) / n + 1;
        System.out.println("row->" + row);
        String[][] matrix = new String[n][row];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (k - 1) / n + 1; j++) {
                matrix[i][j] = "*";
            }
        }


        int start_x = 0;
        int start_y = 0;
        int count = 1;
        int index = 0;

        while (true) {
            if (count > k) {
                break;
            } else {
                matrix[start_x][start_y] = String.valueOf(count);
                count += 1;
                int new_x = start_x + directions[index];
                System.out.println("new_x->" + start_x + ',' + directions[index]);
                int new_y = start_y + directions[index + 1];
                System.out.println("new_y->" + start_y + ',' + directions[index + 1]);
                System.out.println(new_x + ',' + new_y);

                if (new_x < 0 || new_x >= n || new_y < 0 || new_y >= row
                        || !matrix[new_x][new_y].equals("*")) {
                    index = (index + 1) % 4;
                    start_x = start_x + directions[index];
                    start_y = start_y + directions[index + 1];
                } else {
                    start_x = new_x;
                    start_y = new_y;
                }

            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < row; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }

    }

    public static int[] split(String input_str, String chars) {
        String[] tmp2 = input_str.split(chars);
        int[] counts = new int[tmp2.length];
        for (int i = 0; i < tmp2.length; i++) {
            counts[i] = Integer.parseInt(tmp2[i]);
        }
        return counts;
    }

}