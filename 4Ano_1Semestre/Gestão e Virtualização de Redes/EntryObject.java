import java.util.List;

public class EntryObject {
    private String oid;
    private List<MibObject> mibObjects;

    public EntryObject(String oid, List<MibObject> mibObjects) {
        this.oid = oid;
        this.mibObjects = mibObjects;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public List<MibObject> getMibObjects() {
        return mibObjects;
    }

    public void setMibObjects(List<MibObject> mibObjects) {
        this.mibObjects = mibObjects;
    }

    public void updateMibObjectValue(String oid, String newValue) {
        for (MibObject mibObject : mibObjects) {
            if (mibObject.getOid().equals(oid)) {
                mibObject.setValor(newValue);
                System.out.println("Atualizado MibObject: " + mibObject); // Adicionar log aqui
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EntryObject{\n");
        sb.append("  oid='").append(oid).append("',\n");
        sb.append("  mibObjects=[\n");
        for (MibObject mibObject : mibObjects) {
            sb.append("    ").append(mibObject.toString().replaceAll("\n", "\n    ")).append(",\n");
        }
        if (!mibObjects.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length()); // Remove a última vírgula e quebra de linha
        }
        sb.append("\n  ]\n");
        sb.append("}");
        return sb.toString();
    }



}