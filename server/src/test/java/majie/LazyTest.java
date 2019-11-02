package majie;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;

import java.io.IOException;
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
        test();
    }

    @SneakyThrows(IOException.class)
    private static void test() {
        throw new IOException("test");
    }
}
