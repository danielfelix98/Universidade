import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Ficheiro {
    private char[] udpPort;
    private int k;
    private char[] m;
    private char[] t;
    private char[] v;
    private char[] x;

    /**
     * Construtor que lê as configurações do arquivo.
     * @param nomeArquivo Nome do arquivo de configuração.
     */
    public Ficheiro(String nomeArquivo) {
        Map<String, char[]> configuracoes = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                // Ignora linhas de comentário
                if (linha.startsWith("#")) {
                    continue;
                }

                String[] partes = linha.split("=");
                if (partes.length == 2) {
                    String chave = partes[0].trim();
                    String valor = partes[1].trim();
                    configuracoes.put(chave, valor.toCharArray());
                }
            }

            // Inicialização das variáveis da classe com base nas configurações lidas
            this.udpPort = configuracoes.getOrDefault("udp_port", new char[0]);
            this.k = Integer.parseInt(new String(configuracoes.getOrDefault("K", new char[0])));
            this.m = configuracoes.getOrDefault("M", new char[0]);
            this.t = configuracoes.getOrDefault("T", new char[0]);
            this.v = configuracoes.getOrDefault("V", new char[0]);
            this.x = configuracoes.getOrDefault("X", new char[0]);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de configurações: " + e.getMessage());
        }
    }

    // Getters
    public char[] getUdpPort() {
        return udpPort;
    }

    public int getK() {
        return k;
    }

    public char[] getM() {
        return m;
    }

    public char[] getT() {
        return t;
    }

    public char[] getV() {
        return v;
    }

    public char[] getX() {
        return x;
    }

    // Metodo toString para imprimir o objeto de forma legivel
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("udp_port: ").append(new String(udpPort)).append("\n");
        sb.append("K: ").append(k).append("\n");
        sb.append("M: ").append(new String(m)).append("\n");
        sb.append("T: ").append(new String(t)).append("\n");
        sb.append("V: ").append(new String(v)).append("\n");
        sb.append("X: ").append(new String(x)).append("\n");
        return sb.toString();
    }
}