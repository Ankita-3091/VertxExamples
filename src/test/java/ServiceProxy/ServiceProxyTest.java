package ServiceProxy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;


@ExtendWith(VertxExtension.class)
public class ServiceProxyTest {
	
	

	@ExtendWith(VertxExtension.class)
	public class APIVerticleTest {

		@BeforeEach
		public void setup(Vertx vertx, VertxTestContext testContext) {
			vertx.deployVerticle(new ApiServerVerticle(), testContext.completing());
			vertx.deployVerticle(new CalculatorVerticle(),testContext.completing());
		}

		@Test
		public void testAddAPI(Vertx vertx, VertxTestContext testContext) {
			WebClient client = WebClient.create(vertx);
			client.get(8080, "localhost", "/add/3/4")
				  .as(BodyCodec.string())
				  .send(testContext.succeeding(response -> testContext.verify(() -> {
					  	JsonObject res=new JsonObject(response.body());
					  	assertEquals(response.statusCode(), 200);
						assertEquals(res.getInteger("result") ,7);
						testContext.completeNow();
					})));
		}
}
}
	
