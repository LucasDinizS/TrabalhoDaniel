package market;

public class ProductGraph {
    private final int[] vertexCodes;
    private final int[][] weights;
    private int size;

    public ProductGraph(int capacity) {
        vertexCodes = new int[capacity];
        weights = new int[capacity][capacity];
    }

    public boolean addVertex(int code) {
        if (size == vertexCodes.length || indexOf(code) >= 0) {
            return false;
        }
        vertexCodes[size] = code;
        size++;
        return true;
    }

    public void addRelation(int firstCode, int secondCode, int weight) {
        int first = indexOf(firstCode);
        int second = indexOf(secondCode);
        if (first < 0 || second < 0) {
            throw new IllegalArgumentException("Produto inexistente no grafo.");
        }
        if (first == second) {
            throw new IllegalArgumentException("Um produto não pode se relacionar consigo mesmo.");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("O peso da relação deve ser positivo.");
        }
        weights[first][second] += weight;
        weights[second][first] += weight;
    }

    public int registerPurchase(int[] codes) {
        int pairs = 0;
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                addRelation(codes[i], codes[j], 1);
                pairs++;
            }
        }
        return pairs;
    }

    public int weightBetween(int firstCode, int secondCode) {
        int first = indexOf(firstCode);
        int second = indexOf(secondCode);
        if (first < 0 || second < 0) {
            return 0;
        }
        return weights[first][second];
    }

    public int degreeOf(int code) {
        int index = indexOf(code);
        if (index < 0) {
            return 0;
        }
        int degree = 0;
        for (int j = 0; j < size; j++) {
            if (weights[index][j] > 0) {
                degree++;
            }
        }
        return degree;
    }

    public int edgeCount() {
        int edges = 0;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (weights[i][j] > 0) {
                    edges++;
                }
            }
        }
        return edges;
    }

    public int[] recommendationsFor(int code, int limit) {
        int index = indexOf(code);
        if (index < 0 || limit <= 0) {
            return new int[0];
        }
        int[] candidates = new int[size];
        int[] candidateWeights = new int[size];
        int count = 0;
        for (int j = 0; j < size; j++) {
            if (weights[index][j] > 0) {
                candidates[count] = vertexCodes[j];
                candidateWeights[count] = weights[index][j];
                count++;
            }
        }
        for (int i = 0; i < count - 1; i++) {
            int best = i;
            for (int j = i + 1; j < count; j++) {
                if (candidateWeights[j] > candidateWeights[best]) {
                    best = j;
                }
            }
            swap(candidates, i, best);
            swap(candidateWeights, i, best);
        }
        int resultSize = Math.min(limit, count);
        int[] result = new int[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = candidates[i];
        }
        return result;
    }

    public void printMatrix() {
        if (size == 0) {
            System.out.println("Grafo vazio.");
            return;
        }
        System.out.print("      ");
        for (int j = 0; j < size; j++) {
            System.out.printf("%5d", vertexCodes[j]);
        }
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.printf("%5d ", vertexCodes[i]);
            for (int j = 0; j < size; j++) {
                System.out.printf("%5s", i == j ? "-" : String.valueOf(weights[i][j]));
            }
            System.out.println();
        }
        int possibleEdges = size * (size - 1) / 2;
        int edges = edgeCount();
        double density = possibleEdges == 0 ? 0 : (double) edges / possibleEdges;
        System.out.printf("Vértices: %d | Arestas: %d de %d possíveis | Densidade: %.2f%n",
                size, edges, possibleEdges, density);
    }

    private int indexOf(int code) {
        for (int i = 0; i < size; i++) {
            if (vertexCodes[i] == code) {
                return i;
            }
        }
        return -1;
    }

    private static void swap(int[] values, int first, int second) {
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }
}
