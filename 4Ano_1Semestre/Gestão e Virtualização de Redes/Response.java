import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class Response {
    int P;
    List<SimpleEntry<String, EntryObject>> W;
    List<SimpleEntry<String, MibObject>> M;
    List<SimpleEntry<String, String>> R;

    // Construtor para EntryObject
    public Response(int P, List<SimpleEntry<String, EntryObject>> W, List<SimpleEntry<String, String>> R) {
        this.P = P;
        this.W = W;
        this.R = R;
    }

    // Construtor adicional para MibObject
    public Response(int P, List<SimpleEntry<String, MibObject>> M, List<SimpleEntry<String, String>> R, boolean isMibObject) {
        this.P = P;
        this.M = M;
        this.R = R;
    }
    public Response(){
        
    }
    
    // Getters
    public int getP() {
        return P;
    }

    public List<SimpleEntry<String, EntryObject>> getW() {
        return W;
    }

    public List<SimpleEntry<String, MibObject>> getM() {
        return M;
    }

    public List<SimpleEntry<String, String>> getR() {
        return R;
    }

    // Setters
    public void setP(int P) {
        this.P = P;
    }

    public void setW(List<SimpleEntry<String, EntryObject>> W) {
        this.W = W;
    }

    public void setM(List<SimpleEntry<String, MibObject>> M) {
        this.M = M;
    }

    public void setR(List<SimpleEntry<String, String>> R) {
        this.R = R;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response{\n");
        sb.append("  P=").append(P).append(",\n");
        if (W != null) {
            sb.append("  W=[\n");
            for (SimpleEntry<String, EntryObject> entry : W) {
                sb.append("    ").append(entry.getKey()).append("=\n      ").append(entry.getValue().toString().replace(", ", ",\n      ")).append("\n");
            }
            sb.append("  ],\n");
        }
        if (M != null) {
            sb.append("  M=[\n");
            for (SimpleEntry<String, MibObject> entry : M) {
                sb.append("    ").append(entry.getKey()).append("=\n      ").append(entry.getValue().toString().replace(", ", ",\n      ")).append("\n");
            }
            sb.append("  ],\n");
        }
        sb.append("  R=[\n");
        for (SimpleEntry<String, String> entry : R) {
            sb.append("    ").append(entry.getKey()).append("=").append(entry.getValue()).append(",\n");
        }
        if (!R.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove a última vírgula e espaço
        }
        sb.append("\n  ]\n");
        sb.append("}");
        return sb.toString();
    }
}
