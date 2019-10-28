package majie;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;
import reactor.core.publisher.Flux;

import java.util.List;

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

        Flux.fromIterable(List.of())
                .collectList()
                .subscribe(it -> {
                    System.out.println(it);
                });

    }
}
