package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * The type Ember controller.
 */
public class EmberController extends Controller {

	/**
	 * Index result.
	 *
	 * @param slug the slug
	 * @return the result
	 */
	public Result index(String slug) {
		return ok(index.render());
	}

	public Result checkPreFlight() {
		response().setHeader("Access-Control-Allow-Origin", "*");       // Need to add the correct domain in here!!
		response().setHeader("Access-Control-Allow-Methods", "POST");   // Only allow POST
		response().setHeader("Access-Control-Max-Age", "300");          // Cache response for 5 minutes
		response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");         // Ensure this header is also allowed!
		return ok();
	}

}
