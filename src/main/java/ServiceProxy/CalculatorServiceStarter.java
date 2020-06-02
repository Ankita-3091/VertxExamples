package ServiceProxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * @author Ankita.Mishra
 *
 */
public class CalculatorServiceStarter extends AbstractVerticle {
	public static void main(String[] args) {
		
		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		//Vertx vertx= Vertx.vertx();
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
		vertx.deployVerticle( CalculatorVerticle.class.getName(), res1 -> {
			System.out.println("CalculatorVerticle deployed");
			} );
		vertx.deployVerticle( ApiServerVerticle.class.getName(), res2 -> {
			System.out.println("ApiServerVerticle deployed");
			} );
		vertx.deployVerticle( RabbitMqVerticle.class.getName(), res3 -> {
			System.out.println("RabbitMqVerticle deployed");
			
		});
			
	}
			else {
				System.out.println("Failed");
				
			}
		});
	}
}

		
		
		
		
	

