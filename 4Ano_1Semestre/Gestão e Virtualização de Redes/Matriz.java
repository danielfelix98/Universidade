import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class Matriz {
    private Random gerador;
    private int numAtualizacoes;
    private Ficheiro ficheiro;
    private byte[][] ZA;
    private byte[][] ZB;
    private byte[][] ZC;
    private byte[][] ZD;
    private byte[][] Z;

    /**
     * Construtor da classe Matriz. Inicializa as matrizes com base nas configurações lidas do arquivo.
     * @param nomeArquivo Objeto Ficheiro contendo as configurações lidas do arquivo.
     */
    public Matriz(Ficheiro nomeArquivo) {
        numAtualizacoes = 0;
        this.ficheiro = nomeArquivo;
        int K = ficheiro.getK();
        char[] M = ficheiro.getM();

        char[] M1 = new char[K];
        char[] M2 = new char[M.length - K];
        System.arraycopy(M, 0, M1, 0, K);
        System.arraycopy(M, K, M2, 0, M.length - K);

        byte[] M1Bytes = new String(M1).getBytes(StandardCharsets.UTF_8);
        byte[] M2Bytes = new String(M2).getBytes(StandardCharsets.UTF_8);

        this.gerador = new Random();
        this.ZA = criarMatrizZA(M1Bytes, K);
        this.ZB = criarMatrizZB(M2Bytes, K);
        this.ZC = criarMatrizZC(ZA, K);
        this.ZD = criarMatrizZD(ZB, K);
        this.Z = criarMatrizZ(ZC, ZD);
    }

    // Getters 
    public Random getGerador() {
        return this.gerador;
    }

    public byte[][] getZA() {
        return this.ZA;
    }

    public byte[][] getZB() {
        return this.ZB;
    }

    public byte[][] getZC() {
        return this.ZC;
    }

    public byte[][] getZD() {
        return this.ZD;
    }

    public byte[][] getZ() {
        return this.Z;
    }

    // Método para criar a matriz ZA com rotação para a direita
    public byte[][] criarMatrizZA(byte[] M1, int K) {
        byte[][] ZA = new byte[K][K];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < K; j++) {
                ZA[i][j] = M1[(K + j - i) % K];
            }
        }
        return ZA;
    }

    // Método para criar a matriz ZB com rotação para a direita
    public byte[][] criarMatrizZB(byte[] M2, int K) {
        byte[][] ZB = new byte[K][K];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < K; j++) {
                ZB[i][j] = M2[(K + i - j) % K];
            }
        }
        return ZB;
    }

    // Método para criar a matriz ZC
    public byte[][] criarMatrizZC(byte[][] ZA, int K) {
        byte[][] ZC = new byte[K][K];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < K; j++) {
                ZC[i][j] = (byte) this.gerador.nextInt(256);
            }
        }
        return ZC;
    }

    // Método para criar a matriz ZD
    public byte[][] criarMatrizZD(byte[][] ZB, int K) {
        byte[][] ZD = new byte[K][K];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < K; j++) {
                ZD[i][j] = (byte) this.gerador.nextInt(256);
            }
        }
        return ZD;
    }

    // Método para criar a matriz Z final usando a operação XOR
    public byte[][] criarMatrizZ(byte[][] ZC, byte[][] ZD) {
        int K = ZC.length;
        byte[][] Z = new byte[K][K];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < K; j++) {
                Z[i][j] = (byte) (ZC[i][j] ^ ZD[i][j]);
            }
        }
        return Z;
    }

    // Método para imprimir uma matriz em formato hexadecimal
    public String matrizParaHex(byte[][] matriz) {
        StringBuilder builder = new StringBuilder();
        for (byte[] linha : matriz) {
            for (byte valor : linha) {
                builder.append(String.format("%02X ", valor));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    // Método para atualizar a matriz Z
    public void updateMatrizZ() {
        numAtualizacoes = numAtualizacoes + 1;
        int K = Z.length;
        // Atualiza cada linha
        for (int i = 0; i < K; i++) {
            int numRotacoes = Math.abs(Z[i][0]) % K; // Número de rotações para a linha
            Z[i] = rotate(Z[i], numRotacoes);
        }

        // Atualiza cada coluna
        for (int j = 0; j < K; j++) {
            byte[] coluna = new byte[K];
            for (int i = 0; i < K; i++) {
                coluna[i] = Z[i][j];
            }
            int numRotacoes = Math.abs(Z[0][j]) % K; // Número de rotações para a coluna
            coluna = rotate_vertical(coluna, numRotacoes);
            for (int i = 0; i < K; i++) {
                Z[i][j] = coluna[i];
            }
        }
    }

    // Método para rotacionar uma linha
    private byte[] rotate(byte[] linha, int n) {
        byte[] novaLinha = new byte[linha.length];
        for (int i = 0; i < linha.length; i++) {
            novaLinha[i] = linha[(i + n) % linha.length];
        }
        return novaLinha;
    }

    // Método para rotacionar uma coluna verticalmente
    private byte[] rotate_vertical(byte[] coluna, int n) {
        byte[] novaColuna = new byte[coluna.length];
        for (int i = 0; i < coluna.length; i++) {
            novaColuna[i] = coluna[(i + n) % coluna.length];
        }
        return novaColuna;
    }

    public void gerarChave(String KeyId, TreeMap<String, EntryObject> mibTreeMap, String IpPedido, String keyVisibility) {
        Random random = this.gerador;
        int N = numAtualizacoes; // Número de vezes que a matriz Z foi atualizada
        int K = ficheiro.getK();

        // Verifica se K é válido
        if (K <= 1) {
            throw new IllegalArgumentException("O valor de K deve ser maior que 1.");
        }

        int iLimit = Math.max(N + this.Z[0][0], 1);
        int jLimit = Math.max(this.Z[0][0], 1); // Ajuste conforme necessário

        int i = random.nextInt(iLimit) % (K - 1);
        int j = random.nextInt(jLimit) % (K - 1);

        byte[] Zi = this.Z[i];
        byte[] Zj = new byte[K];
        for (int x = 0; x < K; x++) {
            Zj[x] = this.Z[x][j];
        }

        byte[] chaveC = new byte[K];
        for (int x = 0; x < K; x++) {
            chaveC[x] = (byte) (Zi[x] ^ Zj[x]); // XOR entre Zi e Zj
        }

        String[] resultado = calcularDataHoraFutura(Integer.parseInt(new String(ficheiro.getT())));
        ColocarChave(mibTreeMap, KeyId, new String(chaveC, StandardCharsets.UTF_8), IpPedido, resultado[0], resultado[1], keyVisibility);
        updateMatrizZ();
    }

    public void ColocarChave(TreeMap<String, EntryObject> mibTreeMap,String KeyID,String KeyValue, String KeyRequester, String keyExpirationDate , String keyExpirationTime ,String keyVisibility) {
        //lock
        // KeysTableGeneratedKeysEntry Group
        List<MibObject> KeysTableObjects = new ArrayList<>();
        //0.3.2.1 default .keyId
        //                      .1-KeyID
        //                      .2.KeyValue
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".1", "keyId", "INTEGER", "read-only", "current","The identification of a generated key.",KeyID));
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".2", "keyValue", "OCTET STRING", "read-only", "current", "The identification of a generated key.", KeyValue));
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".3", "KeyRequester", "INTEGER", "read-only", "current", "The identification of a generated key.",KeyRequester));
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".4", "keyExpirationDate", "INTEGER", "read-only", "current", "The identification of a generated key.",keyExpirationDate));
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".5", "keyExpirationTime", "INTEGER", "read-only", "current", "The identification of a generated key.",keyExpirationTime));
        KeysTableObjects.add(new MibObject("0.3.2.1."+KeyID+".6", "keyVisibility", "INTEGER", "read-only", "current", "The identification of a generated key.",keyVisibility));
        mibTreeMap.put("0.3.2.1."+KeyID, new EntryObject("0.3.2.1."+KeyID, KeysTableObjects));
        //unlook
    }

    public static String[] calcularDataHoraFutura(int horas) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHoraFutura = agora.plusHours(horas);

        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH-mm-ss");

        String dataFormatada = dataHoraFutura.format(formatadorData);
        String horaFormatada = dataHoraFutura.format(formatadorHora);

        return new String[] {dataFormatada, horaFormatada};
    }

    @Override
    public String toString() {
        return "Matriz ZA:\n" + matrizParaHex(ZA) +
               "\nMatriz ZB:\n" + matrizParaHex(ZB) +
               "\nMatriz ZC:\n" + matrizParaHex(ZC) +
               "\nMatriz ZD:\n" + matrizParaHex(ZD) +
               "\nMatriz Z:\n" + matrizParaHex(Z);
    }
}
