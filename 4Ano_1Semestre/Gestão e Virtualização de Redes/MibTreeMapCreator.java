import java.util.AbstractMap.SimpleEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public class MibTreeMapCreator {
    private TreeMap<String, EntryObject> mibTreeMap;
    private final ReentrantLock lock = new ReentrantLock();
    private Matriz matriz;
    private int contador;
    private int contchaves;
    //lock

    public MibTreeMapCreator(Ficheiro nomeArquivo, Matriz matriz) {
        this.mibTreeMap = new TreeMap<>();
        Ficheiro ficheiro = nomeArquivo;
        contador = 1;
        contchaves = 0;
        
        this.matriz=matriz;
        // System Group
        mibTreeMap.put("0.1.1", new EntryObject("0.1.1", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.1", "systemRestartDate", "INTEGER", "read-only","current" ,"The date (YY*104+MM*102+DD) when the agent has started a new Z matrix." ,"ficheiro=calcular")
        ))));

        mibTreeMap.put("0.1.2", new EntryObject("0.1.2", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.2", "systemRestartTime", "INTEGER", "read-only", "current" ,"The time (HH*104+MM*102+SS) when the agent has started a new Z matrix.","ficheiro=calcular")
        ))));

        mibTreeMap.put("0.1.3", new EntryObject("0.1.3", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.3", "systemKeySize", "INTEGER", "read-write", "current" ,"The number of bytes (K) of each generated key.", Integer.toString(ficheiro.getK()))
        ))));

        mibTreeMap.put("0.1.4", new EntryObject("0.1.4", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.4", "systemIntervalUpdate", "INTEGER", "read-write","current" , "The number of milliseconds of the updating interval of the internal Z matrix.",new String(ficheiro.getT()))
        ))));

        mibTreeMap.put("0.1.5", new EntryObject("0.1.5", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.5", "systemMaxNumberOfKeys", "INTEGER", "read-write", "current" ,"The maximum number of generated keys that are still valid.",ficheiro.getX().toString())
        ))));

        mibTreeMap.put("0.1.6", new EntryObject("0.1.6", new ArrayList<>(Arrays.asList(
            new MibObject("0.1.6", "systemKeysTimeToLive", "INTEGER", "read-write", "current" ,"The number of seconds of the TTL of the generated keys.",new String(ficheiro.getT()))
        ))));

        // Config Group
        mibTreeMap.put("0.2.1", new EntryObject("0.2.1", new ArrayList<>(Arrays.asList(
            new MibObject("0.2.1", "configMasterKey", "OCTET STRING", "read-write","current" , "The master double key M with at least K*2 bytes in size.",ficheiro.getM().toString())
        ))));

        mibTreeMap.put("0.2.2", new EntryObject("0.2.2", new ArrayList<>(Arrays.asList(
            new MibObject("0.2.2", "configFirstCharOfKeysAlphabet", "INTEGER", "read-write", "current" ,"The ASCII code of the first character of the alphabet used in the keys (default=33).","33")
        ))));

        mibTreeMap.put("0.2.3", new EntryObject("0.2.3", new ArrayList<>(Arrays.asList(
            new MibObject("0.2.3", "configCardinalityOfKeysAlphabet", "INTEGER", "read-write", "current" ,"The number of characters (Y) in the alphabet used in the keys (default=94).","94")
        ))));

        // Keys Group
        mibTreeMap.put("0.3.1", new EntryObject("0.3.1", new ArrayList<>(Arrays.asList(
            new MibObject("0.3.1", "dataNumberOfValidKeys", "INTEGER", "read-only", "current" ,"The number of elements in the KeysTableGeneratedKeys.","0")
        ))));

        mibTreeMap.put("0.3.2.6", new EntryObject("0.3.2.6", new ArrayList<>(Arrays.asList(
            new MibObject("0.3.2.6", "KeyVisibility", "INTEGER", "read-only", "current" ,"The identification of a generated key.","0")
        ))));
    }
            
    public Response snmpkeysGet(int P, List<SimpleEntry<String, Integer>> L) {
        List<SimpleEntry<String, MibObject>> M = new ArrayList<>();
        List<SimpleEntry<String, String>> R = new ArrayList<>();
        List<String> keysToRemove = new ArrayList<>();

        for (Map.Entry<String, EntryObject> mapEntry : mibTreeMap.entrySet()) {
            EntryObject entryObject = mapEntry.getValue();
            LocalDate dataExpiracao = null;
            LocalTime horaExpiracao = null;
            LocalDateTime dataHoraExpiracao = null;
        
            for (MibObject mibObject : entryObject.getMibObjects()) {
                if (mibObject.getName().equals("keyExpirationDate")){
                    dataExpiracao = LocalDate.parse(mibObject.getValor(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                }
                if (mibObject.getName().equals("keyExpirationTime")){
                    String timeString = mibObject.getValor();
                    horaExpiracao = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH-mm-ss"));
                }
            }
        
            LocalDateTime now = LocalDateTime.now();
            if(dataExpiracao != null && horaExpiracao!= null){
                dataHoraExpiracao = LocalDateTime.of(dataExpiracao, horaExpiracao);
            }
        
            if (dataHoraExpiracao != null && now.isAfter(dataHoraExpiracao)) {
                keysToRemove.add(entryObject.getOid());
            }
        }
        
        // Removendo fora do loop
        for (String key : keysToRemove) {
            mibTreeMap.remove(key);
        }

        for (SimpleEntry<String, Integer> entry : L) {
            String requestedOid = entry.getKey();
            int count = entry.getValue();
            boolean isOidFound = false;
            /*
            Escrever a MIB Toda
            
            for (Map.Entry<String, EntryObject> entry : mibTreeMap.entrySet()) {
                System.out.println("Entry OID: " + entry.getKey());
                EntryObject entryObject = entry.getValue();
                List<MibObject> mibObjects = entryObject.getMibObjects();
                for (MibObject mibObject : mibObjects) {
                    System.out.println("  MibObject OID: " + mibObject.getOid());
                    System.out.println("  MibObject Name: " + mibObject.getName());
                    System.out.println("  MibObject Value: " + mibObject.getValor());
                    System.out.println("---------------------------------------");
                }
            }*/

            for (Map.Entry<String, EntryObject> mapEntry : mibTreeMap.entrySet()) {
                if (isOidFound && count <= 0) {
                    break; // Sair do loop se todos os MIBs necessários foram encontrados
                }

                EntryObject entryObject = mapEntry.getValue();
                for (MibObject mibObject : entryObject.getMibObjects()) {
                    if (mibObject.getOid().equals(requestedOid) || isOidFound && count >0) {
                        M.add(new SimpleEntry<>(mibObject.getOid(), mibObject));
                        count--;
                        isOidFound = true;
                    }
                }
            }

            if (!isOidFound) {
                R.add(new SimpleEntry<>(requestedOid, "OID inicial não encontrado"));
            }
            if (count>0){
                R.add(new SimpleEntry<>(requestedOid, "Nao foi possivel ir buscar "+count+" objetos a seguir."));
            }
        }

        return new Response(P, M, R, true);
    }    

    
    public Response snmpkeysSet(int P, List<SimpleEntry<String, String>> W) {
        lock.lock();  // Adquirir o bloqueio
        try {
            List<SimpleEntry<String, MibObject>> responseM = new ArrayList<>();
            List<SimpleEntry<String, String>> R = new ArrayList<>();

            for (SimpleEntry<String, String> entry : W) {
                String oid = entry.getKey();
                String value = entry.getValue();

                if (oid.equals("0.3.2.6")) {
                    int n=Integer.parseInt(value);
                    // Gera a chave
                    int j = 0;
                    while(j<n){
                        String keyId = Integer.toString(contador);
                        matriz.gerarChave(keyId, mibTreeMap, Integer.toString(P), "0");

                        // Adiciona o MibObject da chave gerada na resposta
                        String chaveGeradaOID = "0.3.2.1." + keyId;
                        if (mibTreeMap.containsKey(chaveGeradaOID)) {
                            EntryObject chaveGeradaEntry = mibTreeMap.get(chaveGeradaOID);
                            for (MibObject mibObject : chaveGeradaEntry.getMibObjects()) {
                                responseM.add(new SimpleEntry<>(mibObject.getOid(), mibObject));
                            }
                        }
                        j=j+1;
                        contador++;
                        //Adicionar Chave valida
                        List<SimpleEntry<String, String>> Wnova = new ArrayList<>();
                        contchaves=contchaves+1;
                        Wnova.add(new SimpleEntry<>("0.3.1",Integer.toString(contchaves)));
                        snmpkeysSet(P, Wnova);
                    }
                    continue; // Continua para a próxima iteração do loop
                }

                boolean oidFound = false;

                // Verifica se o OID existe nos MibObjects e atualiza
                for (Map.Entry<String, EntryObject> mapEntry : mibTreeMap.entrySet()) {
                    EntryObject entryObject = mapEntry.getValue();
                    for (MibObject mibObject : entryObject.getMibObjects()) {
                        if (mibObject.getOid().equals(oid)) {
                            mibObject.setValor(value);
                            oidFound = true;
                            responseM.add(new SimpleEntry<>(oid, mibObject)); // Adicionando MibObject modificado
                            break; // OID encontrado e valor atualizado
                        }
                    }
                    if (oidFound) {
                        break; // Sai do loop externo se OID foi encontrado
                    }
                }

                if (!oidFound) {
                    R.add(new SimpleEntry<>(oid, "Chave OID não encontrada"));
                }
            }

            return new Response(P, responseM, R, true);
        } finally {
            lock.unlock();  // Sempre liberar o bloqueio no bloco finally
        }
    }

}
