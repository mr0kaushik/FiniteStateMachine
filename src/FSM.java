import java.io.*;
import java.util.*;

class Transition {
    int start, end;
    char character;

    Transition(String str) {
        String[] data = str.split(",");
        start = Integer.parseInt(data[0]);
        character = data[1].charAt(0);
        end = Integer.parseInt(data[2]);
    }
}

class FiniteStateMachine {
    private int noOfNodes;
    private Map<Character, int[][]> inputMatrices;
    private int[][] initialStates, finalStates;


    FiniteStateMachine(int noOfNodes, List<Transition> transitionList,
                       String initialStatesInString, String finalStatesInString) {
        this.noOfNodes = noOfNodes;
        this.inputMatrices = new HashMap<>();
        this.initialStates = new int[1][noOfNodes];
        this.finalStates = new int[noOfNodes][1];

        // parse Initial States in initialStates
        String[] splits = initialStatesInString.split(",");
        for (String str : splits) {
            initialStates[0][Integer.parseInt(str) - 1] = 1; // -1 for 0 based index;
        }

        // parse Final States in finalStates (Transpose of Final States Matrix)
        splits = finalStatesInString.split(",");
        for (String str : splits) {
            finalStates[Integer.parseInt(str) - 1][0] = 1; // -1 for 0 based index;
        }

        // parse transitions list into matrices;
        parseTransitionList(transitionList);
    }

    private void parseTransitionList(List<Transition> transitionList) {
        for (Transition transition : transitionList) {
            int start = transition.start - 1; // -1 to match 0 based index;
            int end = transition.end - 1; // -1 to match 0 based index;

            // if character not present create new matrix
            if (!inputMatrices.containsKey(transition.character)) {
                inputMatrices.put(transition.character, new int[noOfNodes][noOfNodes]);
            }
            inputMatrices.get(transition.character)[start][end] = 1;
        }
    }


    private int[][] multiplyMatrices(int[][] firstMatrix, int[][] secondMatrix) {
        int[][] result = new int[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                for (int i = 0; i < secondMatrix.length; i++) {
                    result[row][col] += firstMatrix[row][i] * secondMatrix[i][col];
                }
            }
        }

        return result;
    }

    void prettyPrint() {
        System.out.println("Input States : " + Arrays.deepToString(initialStates));
        System.out.println("Final States : " + Arrays.deepToString(finalStates));

        for (var entry : inputMatrices.entrySet()) {
            System.out.println(entry.getKey() + ":");
            System.out.println("------");
            int[][] arr = entry.getValue();
            for (int[] row : arr) {
                for (int col : row) {
                    System.out.print(col + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    boolean checkString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int[][] result;
        if (inputMatrices.containsKey(str.charAt(0))) {
            result = multiplyMatrices(initialStates, inputMatrices.get(str.charAt(0)));
        } else {
            return false;
        }

        if (str.length() > 1) {
            for (int i = 1; i < str.length(); i++) {
                if (inputMatrices.containsKey(str.charAt(i))) {
                    result = multiplyMatrices(result, inputMatrices.get(str.charAt(i)));
                } else {
                    return false;
                }
            }
        }
        result = multiplyMatrices(result, finalStates);

        return result[0][0] == 1;
    }
}

public class FSM {
    public static void main(String[] args) {
        BufferedReader bufferedReader = null;
        try {
            // Input Read
            // -------------------
            bufferedReader = new BufferedReader(new FileReader("res/input.txt"));
            int noOfNodes = Integer.parseInt(bufferedReader.readLine());
            // line break
            bufferedReader.readLine();
            List<Transition> transitionList = new ArrayList<>();

            String line;
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                transitionList.add(new Transition(line));
            }

            String initialStates = bufferedReader.readLine();
            String finalStates = bufferedReader.readLine();


            // line break
            bufferedReader.readLine();

            String stringToCheck = bufferedReader.readLine();


            // Working Code
            FiniteStateMachine fsm = new FiniteStateMachine(noOfNodes, transitionList, initialStates, finalStates);

            fsm.prettyPrint();

            System.out.println(stringToCheck + " : " + fsm.checkString(stringToCheck));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
