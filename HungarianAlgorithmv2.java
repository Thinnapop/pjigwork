import java.util.Arrays;

public class HungarianAlgorithmv2 {

    private final int[][] costMatrix;
    private final int n;
    private int maxMatch;
    private final int[] lx, ly, xy, yx, slack, slackx, prev;
    private final boolean[] S, T;

    public HungarianAlgorithmv2(int[][] costMatrix) {
        this.n = costMatrix.length;
        this.costMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            this.costMatrix[i] = Arrays.copyOf(costMatrix[i], n);
        }

        this.lx = new int[n];
        this.ly = new int[n];
        this.xy = new int[n];
        this.yx = new int[n];
        this.slack = new int[n];
        this.slackx = new int[n];
        this.prev = new int[n];
        this.S = new boolean[n];
        this.T = new boolean[n];

        Arrays.fill(xy, -1);
        Arrays.fill(yx, -1);
    }

    private void initLabels() {
        Arrays.fill(lx, 0);
        Arrays.fill(ly, 0);

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                lx[x] = Math.max(lx[x], costMatrix[x][y]);
            }
        }
    }

    private void updateLabels() {
        int delta = Integer.MAX_VALUE;
        for (int y = 0; y < n; y++) {
            if (!T[y]) {
                delta = Math.min(delta, slack[y]);
            }
        }

        for (int x = 0; x < n; x++) {
            if (S[x]) {
                lx[x] -= delta;
            }
        }

        for (int y = 0; y < n; y++) {
            if (T[y]) {
                ly[y] += delta;
            }
            if (!T[y]) {
                slack[y] -= delta;
            }
        }
    }

    private void addToTree(int x, int prevx) {
        S[x] = true;
        prev[x] = prevx;

        for (int y = 0; y < n; y++) {
            if (lx[x] + ly[y] - costMatrix[x][y] < slack[y]) {
                slack[y] = lx[x] + ly[y] - costMatrix[x][y];
                slackx[y] = x;
            }
        }
    }

    private void augment() {
        if (maxMatch == n) return;

        int[] q = new int[n];
        int wr = 0, rd = 0;
        Arrays.fill(S, false);
        Arrays.fill(T, false);
        Arrays.fill(prev, -1);

        for (int x = 0; x < n; x++) {
            if (xy[x] == -1) {
                q[wr++] = x;
                S[x] = true;
                break;
            }
        }

        for (int y = 0; y < n; y++) {
            slack[y] = lx[q[0]] + ly[y] - costMatrix[q[0]][y];
            slackx[y] = q[0];
        }

        while (true) {
            while (rd < wr) {
                int x = q[rd++];

                for (int y = 0; y < n; y++) {
                    if (costMatrix[x][y] == lx[x] + ly[y] && !T[y]) {
                        if (yx[y] == -1) {
                            int cx = x, cy = y, ty;
                            while (cx != -1) {
                                ty = xy[cx];
                                xy[cx] = cy;
                                yx[cy] = cx;
                                cx = prev[cx];
                                cy = ty;
                            }
                            maxMatch++;
                            return;
                        }
                        T[y] = true;
                        q[wr++] = yx[y];
                        addToTree(yx[y], x);
                    }
                }
            }

            updateLabels();
            wr = rd = 0;

            for (int y = 0; y < n; y++) {
                if (!T[y] && slack[y] == 0) {
                    if (yx[y] == -1) {
                        int cx = slackx[y], cy = y, ty;
                        while (cx != -1) {
                            ty = xy[cx];
                            xy[cx] = cy;
                            yx[cy] = cx;
                            cx = prev[cx];
                            cy = ty;
                        }
                        maxMatch++;
                        return;
                    } else {
                        T[y] = true;
                        if (!S[yx[y]]) {
                            q[wr++] = yx[y];
                            addToTree(yx[y], slackx[y]);
                        }
                    }
                }
            }
        }
    }

    public int[] execute() {
        initLabels();
        maxMatch = 0;
        for (int i = 0; i < n; i++) augment();
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[xy[i]] = i;
        return result;
    }

    public static void main(String[] args) {
        int[][] costMatrix = {
                {4, 2, 8},
                {2, 3, 7},
                {3, 1, 6}
        };
        HungarianAlgorithm ha = new HungarianAlgorithm(costMatrix);
        int[] result = ha.execute();
        System.out.println("Optimal assignment:");
        for (int i = 0; i < result.length; i++) {
            System.out.println("Worker " + i + " assigned to task " + result[i]);
        }
    }
}
