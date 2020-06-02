package ServiceProxy;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * @author Ankita.Mishra
 *
 */
@VertxGen
@ProxyGen

public interface CalculatorService {
	
	

/**
 * @param number1
 * @param number2
 * @param handler
 */
public void add(int number1,int number2,Handler<AsyncResult<Integer>> handler) ;
/**
 * @param number1
 * @param number2
 * @param handler
 */
public void substract(int number1,int number2,Handler<AsyncResult<Integer>> handler) ;
/**
 * @param number1
 * @param number2
 * @param handler
 */
public void multiply(int number1,int number2,Handler<AsyncResult<Integer>> handler) ;
/**
 * @param number1
 * @param number2
 * @param handler
 */
public void divide(int number1,int number2,Handler<AsyncResult<Integer>> handler) ;

/**
 * @param vertx
 * @param address
 * @return
 */
@GenIgnore
static CalculatorService createProxy(Vertx vertx,String address) {
	return new CalculatorServiceVertxEBProxy(vertx,address);
}



}
