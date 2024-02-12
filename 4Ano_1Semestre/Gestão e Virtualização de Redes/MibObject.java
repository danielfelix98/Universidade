public class MibObject {
    private String oid;
    private String name;
    private String syntax;
    private String access;
    private String status;
    private String description;
    private String valor;

    public MibObject(String oid, String name, String syntax, String access, String status, String description, String valor) {
        this.oid = oid;
        this.name = name;
        this.syntax = syntax;
        this.access = access;
        this.status = status;
        this.description = description;
        this.valor = valor;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        String tab = "  "; // Uma tabulação
        return "MibObject {\n" +
            tab + tab + "oid='" + oid + "',\n" +
            tab + tab + "name='" + name + "',\n" +
            //tab + tab + "syntax='" + syntax + "',\n" +
            //tab + tab + "access='" + access + "',\n" +
            //tab + tab + "status='" + status + "',\n" +
            //tab + tab + "description='" + description + "',\n" +
            tab + tab + "valor='" + valor + "'\n" +
            tab + "}";
    }

        
}
