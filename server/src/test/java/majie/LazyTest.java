package majie;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;

/**
 * @author jzb 2019-10-26
 */
@Data
public class LazyTest {
    @Getter(lazy = true)
    private final JsonObject jsonObject = new JsonObject().put("test", "test");
    @Getter(lazy = true)
    private final String test = getJsonObject().getString("test");

    public static void main(String[] args) {
        System.out.println(new LazyTest().getTest());
    }
}
