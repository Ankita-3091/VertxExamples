package ServiceProxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.hazelcast.map.impl.record.Record;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * @author Ankita.Mishra
 *
 */
public class ApiServerVerticle extends AbstractVerticle{

	RabbitMQClient client;
	RabbitMQOptions config=new RabbitMQOptions();
	private ClusterManager mgr;
	private VertxOptions options;
	private CalculatorService calculator;
	private ServiceDiscovery discovery;
	private Record record;
	
	/**
	 * start() is called when the verticle is deployed
	 */
	public void start() throws Exception {
		
		System.out.println("CalculatorAPIVerticle started 1 : " );
		mgr = new HazelcastClusterManager();
		options = new VertxOptions().setClusterManager(mgr);
		Properties prop = new Properties();
	    InputStream input = null;
		  try {
				 input = new FileInputStream("config.properties");
			     prop.load(input);
			    
					
					  config.setHost(prop.getProperty("brokerIP"));
					  config.setUser(prop.getProperty("userID"));
					  config.setPassword(prop.getProperty("password"));
					  config.setPort(Integer.parseInt(prop.getProperty("port")));
					  config.setVirtualHost(prop.getProperty("virtualHost"));
					
			    }
			    catch(Exception e){
			    	System.out.println(e.getMessage());
			    }
		  
		  Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					vertx = res.result();
		Router router = Router.router(vertx);
		
		 router.route().handler(BodyHandler.create());
		 router.get("/add/:number1/:number2").handler(this::add);
		 router.post("/substract").handler(this::substract);
		 router.get("/multiply/:number1/:number2").handler(this::multiply);
		 router.get("/divide/:number1/:number2").handler(this::divide);
		 router.post("/register").handler(this::register);
		
		 HttpServer server =vertx.createHttpServer();

		 server.requestHandler(router::accept).listen(8080);
		 
			discovery = ServiceDiscovery.create(vertx);
			EventBusService.getProxy(discovery, CalculatorService.class, ar -> {
				if (ar.succeeded()) {
					System.out.println("\n +++++++ Service Discovery  Success +++++++ \n");
					calculator = ar.result();
					System.out.println(
							"\n +++++++ Service name is : " + calculator.getClass().getName() + " +++++++ ");
				} else {
					System.out.println("Proxy failed");
				}
			});
		 
		 
		 client=RabbitMQClient.create(vertx,config);
		 client.start(resultHandler->{
				if(resultHandler.succeeded()) {
					System.out.println("success");
				}else {
					System.out.println("failed");
				}
			});
		 System.out.println("CalculatorAPIVerticle started  : " );
		
				}
			});
		
		
	}

	/**
	 * @param routingContext
	 */
	public void add(RoutingContext routingContext) {
		System.out.println("Inside add method ");
		calculator = CalculatorService.createProxy(vertx, "calculator.application");
		System.out.println("calculator"+calculator);
		HttpServerResponse response=routingContext.response();
		int number1 = Integer.parseInt(routingContext.request().getParam("number1"));
		int number2 = Integer.parseInt(routingContext.request().getParam("number2"));
		
		calculator.add(number1,number2,handler ->{
			if(handler.succeeded()) {
				System.out.println("result "+handler.result().toString());				
				publish2RabbitMQ("Added successfully : Result "+number1+" + "+number2+" = "+handler.result().toString());
				response.setStatusCode(200).end(handler.result().toString());
				
		}
		else {
			System.out.println("failed "+handler.cause());
			response.setStatusCode(400);
		}
		});
		}
	
	
	/**
	 * @param routingContext
	 */
	
	
	public void substract(RoutingContext routingContext) {
		System.out.println("Inside substract method ");
		calculator = CalculatorService.createProxy(vertx, "calculator.application");
		
		HttpServerResponse response=routingContext.response();
		int number1 = routingContext.getBodyAsJson().getInteger("number1");
		int number2 =  routingContext.getBodyAsJson().getInteger("number2");;
		
		calculator.substract(number1,number2,handler ->{
			if(handler.succeeded()) {
				publish2RabbitMQ("Substracted successfully : Result "+number1+" - "+number2+" = "+handler.result().toString());
				response.setStatusCode(200).end(handler.result().toString());
		}
		else {
			System.out.println("failed "+handler.cause());
			response.setStatusCode(400);
		}
		});
		}
	
	
	
	/**
	 * @param routingContext
	 */
	public void multiply(RoutingContext routingContext) {
		System.out.println("Inside multiply method ");
		calculator = CalculatorService.createProxy(vertx, "calculator.application");
		
		HttpServerResponse response=routingContext.response();
		int number1 = Integer.parseInt(routingContext.request().getParam("number1"));
		int number2 = Integer.parseInt(routingContext.request().getParam("number2"));
		
		calculator.multiply(number1,number2,handler ->{
			if(handler.succeeded()) {
				publish2RabbitMQ("Multiplied successfully : Result "+number1+" * "+number2+" = "+handler.result().toString());
				response.setStatusCode(200).end(handler.result().toString());
		}
		else {
			System.out.println("failed "+handler.cause());
			response.setStatusCode(400);
		}
		});
		}
	
	
	
	/**
	 * @param routingContext
	 */
	public void divide(RoutingContext routingContext) {
		System.out.println("Inside divide method ");
		calculator = CalculatorService.createProxy(vertx, "calculator.application");
		System.out.println("calculator"+calculator);
		HttpServerResponse response=routingContext.response();
		int number1 = Integer.parseInt(routingContext.request().getParam("number1"));
		int number2 = Integer.parseInt(routingContext.request().getParam("number2"));

		calculator.divide(number1,number2,handler ->{
			if(handler.succeeded()) {
				publish2RabbitMQ("Divided successfully : Result "+number1+" / "+number2+" = "+handler.result().toString());
				response.setStatusCode(200).end(handler.result().toString());
		}
		else {
			System.out.println("failed "+handler.cause());
			response.setStatusCode(400);
		}
		});
		}
	
	
	
	/**
	 * @param routingContext
	 */
	public void register(RoutingContext routingContext) {
		System.out.println("Inside register method ");
		
		HttpServerResponse response=routingContext.response();
		String exchangeName = routingContext.getBodyAsJson().getString("exchangename");
		String quequeName = routingContext.getBodyAsJson().getString("quequename");
		System.out.println(exchangeName+quequeName);
		JsonObject jsonObj=new JsonObject();
		jsonObj.put("exchangeName",exchangeName);
		jsonObj.put("quequeName",quequeName);
		
		
		DeliveryOptions deliveryOptions=new DeliveryOptions();
		deliveryOptions.addHeader("action", "register");
		String address="operations";
		vertx.eventBus().request(address,jsonObj, eventBusresponseHandler -> {
			if(eventBusresponseHandler.failed()) {
				System.out.println("Failed "+eventBusresponseHandler.cause());
				response.setStatusCode(400);
			}
			else {
				System.out.println("Result "+eventBusresponseHandler.result().body());
				response.setStatusCode(200).end(eventBusresponseHandler.result().body().toString());
				
			}
			});
		}


/**
 * @param msg
 */
public void publish2RabbitMQ(String msg) {
	JsonObject json=new JsonObject();
	json.put("body", msg);
	
	System.out.println(json);
	client.basicPublish("ankita_exchange", "#", json, resultHandler->{
		if(resultHandler.succeeded()) {
			System.out.println("message published to queue");
		}else {
			System.out.println("failed");
			resultHandler.cause().printStackTrace();
		}
	});
 }
}