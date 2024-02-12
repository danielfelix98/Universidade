import java.util.ArrayList;
import java.util.List;

public class PDU {
    private int securityModel;
    private int numSecurityParams;
    private List<String> securityParams;
    private int requestId;
    private int primitiveType;
    private int numListElements;
    private List<PduEntry> listEntries;
    private int numErrorElements;
    private List<String> errorList;

    public PDU() {
        securityParams = new ArrayList<>();
        listEntries = new ArrayList<>();
        errorList = new ArrayList<>();
    }

    // Métodos para configurar cada campo
    public void setSecurityModel(int model) {
        this.securityModel = model;
    }

    public void setNumSecurityParams(int num) {
        this.numSecurityParams = num;
    }

    public void addSecurityParam(String param) {
        securityParams.add(param);
    }

    public void setRequestId(int id) {
        this.requestId = id;
    }

    public int getTamanhiLista(){
        return this.listEntries.size();
    }

    public void setPrimitiveType(int type) {
        this.primitiveType = type;
    }

    public void setNumListElements(int num) {
        this.numListElements = num;
    }

    public void addListEntradaInteiro(String oid, int value) {
        listEntries.add(new PduEntry(oid, value));
    }

    public void addListEntradaString(String oid, String value) {
        listEntries.add(new PduEntry(oid, value));
    }

    public void setNumErrorElements(int num) {
        this.numErrorElements = num;
    }

    public void addError(String error) {
        errorList.add(error);
    }

    public int getSecurityModel() {
        return securityModel;
    }

    public int getNumSecurityParams() {
        return numSecurityParams;
    }


    public List<String> getSecurityParams() {
        return securityParams;
    }

    public void setSecurityParams(List<String> securityParams) {
        this.securityParams = securityParams;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getPrimitiveType() {
        return primitiveType;
    }

    public int getNumListElements() {
        return numListElements;
    }

    public List<PduEntry> getListEntries() {
        return listEntries;
    }

    public void setListEntries(List<PduEntry> listEntries) {
        this.listEntries = listEntries;
    }

    public int getNumErrorElements() {
        return numErrorElements;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public void addListEntradaCompleta(String oid, String nome, Object value) {
        listEntries.add(new PduEntry(oid, nome, value));
    }

    public String buildMessage() {
        StringBuilder pduBuilder = new StringBuilder();
        // Adiciona o tipo da primitiva e o ID da requisição
        pduBuilder.append(primitiveType).append(" ")
                  .append(requestId).append(" ");
    
        // Adiciona modelo de segurança e parâmetros de segurança
        pduBuilder.append(securityModel).append(" ")
                  .append(numSecurityParams).append(" ");
        for (String param : securityParams) {
            pduBuilder.append(param).append(" ");
        }
    
        // Adiciona entradas da lista
        pduBuilder.append(numListElements).append(" ");
        for (PduEntry entry : listEntries) {
            pduBuilder.append(entry.getOid()).append(" ").append(entry.getValue()).append(" ");
        }
    
        // Adiciona lista de erros
        pduBuilder.append(numErrorElements).append(" ");
        for (String error : errorList) {
            pduBuilder.append(error).append(" ");
        }
    
        // Remove o último espaço extra
        if (pduBuilder.length() > 0) {
            pduBuilder.deleteCharAt(pduBuilder.length() - 1);
        }
    
        return pduBuilder.toString();
    }    

    // Classe PduEntry agora é uma classe interna dentro de PDU
    static class PduEntry {
        private String oid;
        private String nome; // Novo campo para o nome
        private Object value;
    
        public PduEntry(String oid, Object value) {
            this.oid = oid;
            this.nome = null; // Nome não fornecido
            this.value = value;
        }
        
        public PduEntry(String oid, String nome, Object value) {
            this.oid = oid;
            this.nome = nome;
            this.value = value;
        }

        

        public String getOid() {
            return oid;
        }

        public String getNome() {
            return nome;
        }

        public Object getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("PDU {")
            .append("\n  Tipo Primitiva: ").append(primitiveType)
            .append("\n  ID Requisição: ").append(requestId)
            .append("\n  Modelo Segurança: ").append(securityModel)
            .append("\n  Parâmetros de Segurança: ");
        for (String param : securityParams) {
            builder.append(param).append(", ");
        }

        builder.append("\n  Entradas da Lista: ");
        for (PduEntry entry : listEntries) {
            builder.append("\n    OID: ").append(entry.getOid())
                .append(", Valor: ").append(entry.getValue());
        }

        builder.append("\n  Erros: ");
        for (String error : errorList) {
            builder.append(error).append(", ");
        }

        builder.append("\n}");
        return builder.toString();
    }

    public String toString3() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("PDU {")
            .append("\n  Tipo Primitiva: ").append(primitiveType)
            .append("\n  ID Requisição: ").append(requestId)
            .append("\n  Modelo Segurança: ").append(securityModel)
            .append("\n  Parâmetros de Segurança: ");
        for (String param : securityParams) {
            builder.append(param).append(", ");
        }

        builder.append("\n  Entradas da Lista: ");
        for (PduEntry entry : listEntries) {
            builder.append("\n    OID: ").append(entry.getOid())
               .append(", Nome: ").append(entry.getNome())
               .append(", Valor: ").append(entry.getValue());
        }

        builder.append("\n  Erros: ");
        for (String error : errorList) {
            builder.append(error).append(", ");
        }

        builder.append("\n}");
        return builder.toString();
    }

    public String toString2() {
        StringBuilder builder = new StringBuilder();
    
        // Adicionar campos básicos com um espaço entre eles
        builder.append(primitiveType).append(" ")
               .append(requestId).append(" ")
               .append(securityModel).append(" ")
               .append(numSecurityParams).append(" ");
    
        // Adicionar parâmetros de segurança
        for (String param : securityParams) {
            builder.append(param).append(" ");
        }
    
        // Adicionar número de elementos da lista e seus valores
        builder.append(numListElements).append(" ");
        for (PduEntry entry : listEntries) {
            builder.append(entry.getOid()).append(" ")
                   .append(entry.getValue()).append(" "); // Supondo que o nome não seja necessário
        }
    
        // Adicionar número de elementos de erro e suas mensagens
        builder.append(numErrorElements).append(" ");
        for (String error : errorList) {
            builder.append(error).append(" ");
        }
    
        return builder.toString().trim(); // Remover espaço extra no final
    }

}