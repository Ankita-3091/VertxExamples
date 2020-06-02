package ServiceProxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public class RabbitMqVerticle extends AbstractVerticle {
	
	RabbitMQOptions config=new RabbitMQOptions();
	RabbitMQClient client;
	

public void start() {
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
    System.out.println("start");
    client=RabbitMQClient.create(vertx, config);
	System.out.println(client);
	
	
	vertx.eventBus().consumer("operations",eventHandler ->{
		System.out.println(eventHandler.headers().get("action"));  
		register(eventHandler);
		});
	
	
}


public void register (Message<Object>  message) {
	System.out.println("inside rabbitmq register");
	 JsonObject jsonobj = (JsonObject) message.body();
	String exchangeName=jsonobj.getString("exchangeName");
	String quequename=jsonobj.getString("quequeName");
	//String rountingkey=obj.getString("queue");
	String rountingkey="#";
	String exchangeType="topic";
	
	
	client=RabbitMQClient.create(vertx, config);
	
	client.start(handler1->{
		if(handler1.succeeded())
			System.out.println("Client connected");
		
		  client.exchangeDeclare(exchangeName, exchangeType, true, false,
		  resultHandler1->{ if(resultHandler1.succeeded())
		  System.out.println("Exchange declared"); 
		  
		  client.queueDeclare(quequename,
		  true, false, false, resultHandler2->{ if(resultHandler2.succeeded())
		  System.out.println("Queque declared");
		  
		  client.queueBind(quequename,
		  exchangeName, rountingkey, resultHandler3->{ if(resultHandler3.succeeded())
		  System.out.println("Bind successful");


		  
		        });
		     }); 
		   }); 
		});	
     
	message.reply("successful");
}

}