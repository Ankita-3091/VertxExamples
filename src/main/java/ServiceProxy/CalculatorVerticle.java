package ServiceProxy;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.servicediscovery.Record;

/**
 * @author Ankita.Mishra
 *
 */
public class CalculatorVerticle extends AbstractVerticle{
	
	ClusterManager mgr;
	VertxOptions options;
	CalculatorService calculator;
	ServiceDiscovery discovery;
	Record record;

	 /**
	 *
	 */
	public void start() {
		 System.out.println("Inside CalculatorVerticle");
		 
			mgr = new HazelcastClusterManager();
			options = new VertxOptions().setClusterManager(mgr);

	 
		 mgr = new HazelcastClusterManager();
			options = new VertxOptions().setClusterManager(mgr);
			Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					 vertx = res.result();
		
	    CalculatorService calculator = new CalculatorServiceImpl(); 

	    new ServiceBinder(vertx)
	      .setAddress("calculator.application") 
	      .register(CalculatorService.class, calculator); 
	    
	    
	    discovery = ServiceDiscovery.create(vertx);
		record = EventBusService.createRecord("simple-calculator-service", // The service name
				"calculator.application", // the service address,
				CalculatorService.class // the service interface
		);

		discovery.publish(record, ar -> {
			if (ar.succeeded()) {
				// publication succeeded
				Record publishedRecord = ar.result();
				System.out.println("\n +++++++ ServiceDiscovery  +++++++ \n");
				System.out.println("Publication succeeded " + ar.result());
				System.out.println(publishedRecord.toJson());
				System.out.println("\n +++++++ ServiceDiscovery  +++++++ \n");

			} else {
				// publication failed
				System.out.println("\n +++++++ ServiceDiscovery  +++++++ \n");
				System.out.println("Publication failed " + ar.result());
				System.out.println("\n +++++++ ServiceDiscovery  +++++++ \n");
					}
				});
			}
				
		});
	 }
}
