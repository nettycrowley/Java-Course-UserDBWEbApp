package ie.rccourse.webapp;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ie.rccourse.userDb.User;
import ie.rccourse.userDb.UserDb;

@Controller
public class SpringTestObj {
	
	ApplicationContext context;
	UserDb userDb;
	
	public SpringTestObj () {
		context = new ClassPathXmlApplicationContext("SpringBeans.xml");
		
		userDb = context.getBean(UserDb.class);
	}
	
	@RequestMapping("/welcome")
	public ModelAndView testRequest() {
		
		List<User> users = userDb.getUsers();
		
		return new ModelAndView("welcome", "users", users);
		
	}
	
	@RequestMapping("/sayGoodbye")
	public ModelAndView sayGoodbye() {
		
		return new ModelAndView("goodbye", "message", "goodbye");
	}
	
/*	@RequestMapping("/sayGoodbye")
	public ModelAndView handleRequest() {
		
		String message = "hello from /welcome";
		
		ModelAndView mav = new ModelAndView("welcome", "message", message);
		
		return mav;
	}*/
	
	public String getName() {
		
		List<User> users =userDb.getUsers();
		String name = users.get(0).getFirstName();
		return name;
		
	}

}
