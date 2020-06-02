package ServiceProxy;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Future;

/**
 * @author Ankita.Mishra
 *
 */
public class CalculatorServiceImpl implements CalculatorService{

	
	/**
	 *
	 */
	public void add(int number1,int number2,Handler<AsyncResult<Integer>> handler) {
		System.out.println("inside calculator service Impl");
		int result=number1+number2;
		System.out.println("the result is: "+result);
		handler.handle(Future.succeededFuture(result));
	}
	/**
	 *
	 */
	public void substract(int number1,int number2,Handler<AsyncResult<Integer>> handler) {
		System.out.println("inside calculator service Impl");
		int result=number1-number2;
		System.out.println("the result is: "+result);
		handler.handle(Future.succeededFuture(result));
	}
	/**
	 *
	 */
	public void multiply(int number1,int number2,Handler<AsyncResult<Integer>> handler) {
		System.out.println("inside calculator service Impl");
		int result=number1*number2;
		System.out.println("the result is: "+result);
		handler.handle(Future.succeededFuture(result));
	}
	/**
	 *
	 */
	public void divide(int number1,int number2,Handler<AsyncResult<Integer>> handler) {
		System.out.println("inside calculator service Impl");
		int result=number1/number2;
		System.out.println("the result is: "+result);
		handler.handle(Future.succeededFuture(result));
	}

	
}
