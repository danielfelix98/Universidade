import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.AbstractMap.SimpleEntry;

public class Cliente {
    public static void main(String[] args) {
        String servidorHost = "localhost"; // Endereço do servidor
        int servidorPorta = 12345; // Porta do servidor // irbuscar ao ficheiro

        Scanner scanner = new Scanner(System.in);

        while (true) {
            List<SimpleEntry<String, Integer>> L = new ArrayList<>();
            List<SimpleEntry<String, String>> W = new ArrayList<>();
            int flag = 1;
            System.out.println("Menu:");
            System.out.println("1. Get");
            System.out.println("2. Set");
            System.out.println("3. Sair");

            int escolha = scanner.nextInt();
            scanner.nextLine();

            if (escolha == 1) {
                while (flag == 1) {
                    System.out.print("Digite o OID: ");
                    String oid = scanner.nextLine();
                    System.out.print("Digite o N: ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    L.add(new SimpleEntry<>(oid, n));
                    System.out.print("Deseja adicionar mais OID e N? (S/N): ");
                    String continuar = scanner.nextLine().trim();
                    if (!continuar.equalsIgnoreCase("S")) {
                        flag = 0;
                        String resposta = enviarMensagem(servidorHost, servidorPorta, "1", L);
                        PDU pduRecebido = decodificarMensagem(resposta);
                        System.out.println("PDU recebido: " + pduRecebido.toString3());
                        L.clear();
                    }
                }
            } else if (escolha == 2) {
                System.out.print("Digite o OID para o SET: ");
                String oid = scanner.nextLine();
                System.out.print("Digite o novo valor: ");
                String novoValor = scanner.nextLine();
                W.add(new SimpleEntry<>(oid, novoValor));
                String resposta = enviarMensagem(servidorHost, servidorPorta, "2", W);
                System.out.println(resposta);
                PDU pduRecebido = decodificarMensagem(resposta);
                System.out.println("PDU recebido: " + pduRecebido.toString3());
            } else if (escolha == 3) {
                System.out.println("Encerrando o cliente.");
                break;
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static String enviarMensagem(String servidorHost, int servidorPorta, String tipoOperacao, List<? extends SimpleEntry<String, ?>> lista) {
        try {
            DatagramSocket socket = new DatagramSocket();
            List<String> errorList = new ArrayList<>();

            PDU pdu = new PDU();
            pdu.setPrimitiveType(Integer.parseInt(tipoOperacao));
            pdu.setRequestId(4); // Configurar conforme necessário
            // Adicione mais configurações ao PDU conforme necessário

            for (SimpleEntry<String, ?> entry : lista) {
                if (entry.getValue() instanceof Integer) {
                    pdu.addListEntradaInteiro(entry.getKey(), (Integer) entry.getValue());
                } else if (entry.getValue() instanceof String) {
                    pdu.addListEntradaString(entry.getKey(), (String) entry.getValue());
                }
            }

            pdu.setNumListElements(pdu.getTamanhiLista());
            pdu.setNumErrorElements(errorList.size());
            pdu.setErrorList(errorList);
            String mensagem = pdu.buildMessage();

            byte[] dadosParaEnvio = mensagem.getBytes(StandardCharsets.UTF_8);
            InetAddress enderecoServidor = InetAddress.getByName(servidorHost);
            DatagramPacket pacoteEnvio = new DatagramPacket(dadosParaEnvio, dadosParaEnvio.length, enderecoServidor, servidorPorta);
            socket.send(pacoteEnvio);

            // Receber o tamanho da mensagem
            byte[] tamanhoMensagem = new byte[4];
            DatagramPacket pacoteTamanho = new DatagramPacket(tamanhoMensagem, tamanhoMensagem.length);
            socket.receive(pacoteTamanho);
            int tamanho = ByteBuffer.wrap(tamanhoMensagem).getInt();

            // Receber a mensagem completa em múltiplos pacotes se necessário
            byte[] dadosRecebidos = new byte[tamanho];
            int bytesRecebidos = 0;
            while (bytesRecebidos < tamanho) {
                int tamanhoRestante = tamanho - bytesRecebidos;
                byte[] buffer = new byte[Math.min(tamanhoRestante, 1024)];
                DatagramPacket pacoteResposta = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacoteResposta);
                System.arraycopy(buffer, 0, dadosRecebidos, bytesRecebidos, pacoteResposta.getLength());
                bytesRecebidos += pacoteResposta.getLength();
            }

            socket.close();
            return new String(dadosRecebidos, 0, bytesRecebidos, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro na comunicação com o servidor.";
        }
    }

    private static PDU decodificarMensagem(String mensagem) {
        PDU pdu = new PDU();
        String[] partesPrincipais = mensagem.split(" MibObject \\{");
        String[] camposIniciais = partesPrincipais[0].trim().split(" ");
        int indice = 0;
    
        pdu.setPrimitiveType(Integer.parseInt(camposIniciais[indice++]));
        pdu.setRequestId(Integer.parseInt(camposIniciais[indice++]));
        pdu.setSecurityModel(Integer.parseInt(camposIniciais[indice++]));
        pdu.setNumSecurityParams(Integer.parseInt(camposIniciais[indice++]));
        pdu.setNumListElements(Integer.parseInt(camposIniciais[indice++]));
    
        for (int i = 1; i < partesPrincipais.length; i++) { // Começa em 1 para ignorar a primeira parte
            String parteMibObject = partesPrincipais[i];
            String[] dadosMibObject = parteMibObject.split(",\\s+");
            if (dadosMibObject.length > 1) {
                
                String oid = dadosMibObject[0].split("'")[1];
                //System.out.println("oid"+oid);
                String nome = dadosMibObject[1].split("'")[1];
                //System.out.println("nome"+nome);
                String valor = dadosMibObject[2].split("'")[1].replace("\n  }", "").trim();
                //System.out.println("valor"+valor);
                try {
                    int valorInt = Integer.parseInt(valor);
                    pdu.addListEntradaCompleta(oid, nome, valorInt);
                } catch (NumberFormatException e) {
                    pdu.addListEntradaCompleta(oid, nome, valor);
                }
            }
        }
    
        // Configuração dos elementos de erro
        // Verificar se ainda há elementos restantes para processar erros
        try {
            int numErrorElements = Integer.parseInt(camposIniciais[indice++]);
            pdu.setNumErrorElements(numErrorElements);
            for (int i = 0; i < numErrorElements; i++) {
                pdu.addError(camposIniciais[indice++]);
            }
        } catch (NumberFormatException e) {
            // Lida com o erro de conversão aqui, por exemplo, imprime uma mensagem de erro ou faz outro tratamento adequado.
            //System.err.println("Erro de conversão: " + e.getMessage());
        }        
    
        return pdu;
    }
    
    
}

