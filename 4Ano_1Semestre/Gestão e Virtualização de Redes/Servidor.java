import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Servidor {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static int portaServidor = 0;
    private static int Pedido = 0;
    private static MibTreeMapCreator mibAgent; 


    public static void main(String[] args) {
        try {
            String nomeArquivo = args[0];
            Ficheiro ficheiro = new Ficheiro(nomeArquivo);
            portaServidor=Integer.parseInt(new String(ficheiro.getUdpPort()));
            Matriz matriz = new Matriz(ficheiro);
            mibAgent = new MibTreeMapCreator(ficheiro, matriz);
            DatagramSocket socket = new DatagramSocket(portaServidor);
            System.out.println("Servidor UDP escutando na porta " + portaServidor);

            //Atualizacao Matriz
            scheduler.scheduleAtFixedRate(() -> {
                matriz.updateMatrizZ();
            }, 0, Integer.parseInt(new String(ficheiro.getT())), TimeUnit.MINUTES);

            while (true) {
                byte[] dadosRecebidos = new byte[1024];
                DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
                socket.receive(pacoteRecebido);
            
                new Thread(() -> {
                    Pedido = Pedido + 1;
                    String mensagem = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
                    System.out.println("Mensagem recebida: " + mensagem); // Adicione esta linha
                    PDU pduRecebido = decodificarMensagem(mensagem);
                    System.out.println("PDU recebido: " + pduRecebido.toString()); // Adicione esta linha
                    PDU resposta = processarPergunta(pduRecebido);
                    enviarRespostaAoAgente(socket, pacoteRecebido.getAddress(), pacoteRecebido.getPort(), resposta);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static PDU decodificarMensagem(String mensagem) {
        PDU pdu = new PDU();
        String[] partes = mensagem.split(" ");
        int indice = 0;
    
        pdu.setPrimitiveType(Integer.parseInt(partes[indice++]));
        pdu.setRequestId(Integer.parseInt(partes[indice++]));
    
        pdu.setSecurityModel(Integer.parseInt(partes[indice++]));
        int numSecurityParams = Integer.parseInt(partes[indice++]);
        pdu.setNumSecurityParams(numSecurityParams);
        for (int i = 0; i < numSecurityParams; i++) {
            pdu.addSecurityParam(partes[indice++]);
        }
    
        int numListElements = Integer.parseInt(partes[indice++]);
        pdu.setNumListElements(numListElements);
        for (int i = 0; i < numListElements; i++) {
            String oid = partes[indice++];
            String value = partes[indice++];
            try {
                int intValue = Integer.parseInt(value);
                pdu.addListEntradaInteiro(oid, intValue);
            } catch (NumberFormatException e) {
                // Se não for um número inteiro, armazene-o como String ou trate-o de outra forma.
                pdu.addListEntradaString(oid, value);
            }
        }
    
        int numErrorElements = Integer.parseInt(partes[indice++]);
        pdu.setNumErrorElements(numErrorElements);
        for (int i = 0; i < numErrorElements; i++) {
            pdu.addError(partes[indice++]);
        }
    
        return pdu;
    }
    
    private static PDU processarPergunta(PDU pdu) {
        int tipoPrimitiva = pdu.getPrimitiveType();
        Response response = new Response();

        if (tipoPrimitiva == 1) { // GET
            List<SimpleEntry<String, Integer>> lista = new ArrayList<>();
            for (PDU.PduEntry entry : pdu.getListEntries()) {
                lista.add(new SimpleEntry<>(entry.getOid(), (Integer) entry.getValue()));
            }
            response = mibAgent.snmpkeysGet(pdu.getRequestId(), lista);
        } else if (tipoPrimitiva == 2) { // SET
            List<SimpleEntry<String, String>> setList = new ArrayList<>();
            for (PDU.PduEntry entry : pdu.getListEntries()) {
                String oid = entry.getOid();
                Object value = entry.getValue();
                
                if (value instanceof Integer) {
                    // Se o valor for um Integer, converta-o para String de maneira apropriada
                    setList.add(new SimpleEntry<>(oid, String.valueOf(value)));
                } else if (value instanceof String) {
                    // Se o valor já for uma String, adicione-o diretamente
                    setList.add(new SimpleEntry<>(oid, (String) value));
                } else {
                    // Lidar com outros tipos de valores, se necessário
                }
            }
            response = mibAgent.snmpkeysSet(pdu.getRequestId(), setList);
        } else {
            //enviar -1
        }

        PDU respostaPdu = new PDU();
        respostaPdu.setPrimitiveType(tipoPrimitiva);
        respostaPdu.setRequestId(pdu.getRequestId());
        // Configurar outros campos de respostaPdu conforme necessário
        adicionarDadosResponseAoPDU(respostaPdu, response);
        return respostaPdu;
    }

    private static void adicionarDadosResponseAoPDU(PDU pdu, Response response) {
        if (response.getW() != null) {
            for (SimpleEntry<String, EntryObject> entry : response.getW()) {
                pdu.addListEntradaString(entry.getKey(), entry.getValue().toString());
            }
            pdu.setNumListElements(response.getW().size());
        }
        if (response.getM() != null) {
            for (SimpleEntry<String, MibObject> entry : response.getM()) {
                pdu.addListEntradaString(entry.getKey(), entry.getValue().toString());
            }
            pdu.setNumListElements(response.getM().size());
        }
        if (response.getR() != null) {
            for (SimpleEntry<String, String> entry : response.getR()) {
                pdu.addError(entry.getValue());
            }
            pdu.setNumErrorElements(response.getR().size());
        }
    }
    
    /*private static void enviarRespostaAoAgente(DatagramSocket socket, InetAddress endereco, int porta, PDU resposta) {
        try {
            String mensagem = resposta.toString2();
            byte[] dadosParaEnvio = mensagem.getBytes(StandardCharsets.UTF_8);

            // Fragmentação
            int tamanhoMaximo = 1024;
            for (int i = 0; i < dadosParaEnvio.length; i += tamanhoMaximo) {
                int tamanhoFragmento = Math.min(tamanhoMaximo, dadosParaEnvio.length - i);
                byte[] fragmento = Arrays.copyOfRange(dadosParaEnvio, i, i + tamanhoFragmento);
                DatagramPacket pacoteEnvio = new DatagramPacket(fragmento, fragmento.length, endereco, porta);
                socket.send(pacoteEnvio);
            }

            System.out.println("Resposta enviada ao agente: " + mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    private static void enviarRespostaAoAgente(DatagramSocket socket, InetAddress endereco, int porta, PDU resposta) {
        try {
            String mensagem = resposta.toString2();
            byte[] dadosParaEnvio = mensagem.getBytes(StandardCharsets.UTF_8);
            byte[] tamanhoMensagem = ByteBuffer.allocate(4).putInt(dadosParaEnvio.length).array();
            
            // Enviar primeiro o tamanho da mensagem
            DatagramPacket pacoteTamanho = new DatagramPacket(tamanhoMensagem, tamanhoMensagem.length, endereco, porta);
            socket.send(pacoteTamanho);

            // Fragmentação e envio dos dados da mensagem
            int tamanhoMaximo = 1024;
            for (int i = 0; i < dadosParaEnvio.length; i += tamanhoMaximo) {
                int tamanhoFragmento = Math.min(tamanhoMaximo, dadosParaEnvio.length - i);
                byte[] fragmento = Arrays.copyOfRange(dadosParaEnvio, i, i + tamanhoFragmento);
                DatagramPacket pacoteEnvio = new DatagramPacket(fragmento, fragmento.length, endereco, porta);
                socket.send(pacoteEnvio);
            }

            System.out.println("Resposta enviada ao agente: " + mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
