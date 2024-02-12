public class MatrizTest {
    public static void main(String[] args) {
        // Carrega as configurações do arquivo "F.txt"
        Ficheiro configuracao = new Ficheiro("F.txt");

        // Criação da matriz com base nas configurações do arquivo
        Matriz matriz = new Matriz(configuracao);

        // Teste das matrizes ZA, ZB, ZC, ZD e Z
        testarMatriz(matriz.getZA(), "ZA", configuracao);
        testarMatriz(matriz.getZB(), "ZB", configuracao);
        testarMatriz(matriz.getZC(), "ZC", configuracao);
        testarMatriz(matriz.getZD(), "ZD", configuracao);
        testarMatriz(matriz.getZ(), "Z", configuracao);
    
        // Imprimir o estado inicial da matriz Z
        System.out.println("Estado inicial da matriz Z:");
        System.out.println(matriz.matrizParaHex(matriz.getZ()));

        // Atualizar a matriz Z
        matriz.updateMatrizZ();

        // Imprimir o estado da matriz Z após atualização
        System.out.println("Estado da matriz Z após atualização:");
        System.out.println(matriz.matrizParaHex(matriz.getZ()));
    }

    private static void testarMatriz(byte[][] matriz, String nomeMatriz, Ficheiro configuracao) {
        System.out.println("Testando a matriz " + nomeMatriz + ":");
        if (matriz == null || matriz.length == 0 || matriz[0].length == 0) {
            System.out.println("A matriz " + nomeMatriz + " está vazia ou não foi inicializada corretamente.");
        } else {
            System.out.println("Matriz " + nomeMatriz + " inicializada com sucesso.");
            // Aqui podem ser adicionados outros testes específicos para cada matriz, se necessário.
        }
        // Imprime a matriz em formato hexadecimal
        System.out.println(new Matriz(configuracao).matrizParaHex(matriz));
    }
}
