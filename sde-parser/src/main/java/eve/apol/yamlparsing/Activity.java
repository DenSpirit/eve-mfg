package eve.apol.yamlparsing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties("skills")
public class Activity {
    private long time;
    private List<Material> materials = Collections.emptyList();
    private List<Product> products = Collections.emptyList();

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public List<Material> getMaterials() {
        return materials;
    }
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    @Override
    public String toString() {
        return "Activity [time=" + time + ", materials=" + materials + ", products=" + products + "]";
    }
}
