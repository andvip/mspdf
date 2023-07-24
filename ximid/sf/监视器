import java.util.Scanner;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.HashMap;
 
public class Main {
    public static int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
	public static void main(String[] args) {
        // 处理输入
        Scanner in = new Scanner(System.in);
        int m = in.nextInt();
        int n = in.nextInt();
    
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = in.nextInt();
            }
        }
    
        int result = 0;
        for (int x = 0; x < m; x++) {
            for (int y = 0; y < n; y++) {
                if (matrix[x][y] == 1) {
                    result++;
                    continue;
                }
        
                for (int i=0;i<4;i++){
                    int new_x = x + directions[i][0];
                    int new_y = y + directions[i][1];
            
                    if (new_x >= 0 && new_x < m && new_y >= 0 && new_y < n && matrix[new_x][new_y] == 1) {
                        result++;
                        break;
                    }
                }
            }
        }
        System.out.println(result);
        return;
    }
}
 
 