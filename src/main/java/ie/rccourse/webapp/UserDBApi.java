package ie.rccourse.webapp;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ie.rccourse.userDb.User;
import ie.rccourse.userDb.UserDb;
import ie.rccourse.userDb.UserDbException;
import ie.rccourse.userDb.UserTransaction;

@Controller
public class UserDBApi {
	
	protected UserDb userDb;
	
	//constructor
	public UserDBApi(){
		ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml"); 
		userDb = context.getBean(UserDb.class);
	}
	
	
	@RequestMapping("/showUsers")
	public ModelAndView showUsers(){
		List<User>users = userDb.getUsers();
		
		return new ModelAndView("showUsers", "users", users);
	}
	
	@RequestMapping("/users")
	@ResponseBody
	public List<User> getUsers(){
		List<User>users = userDb.getUsers();
		
		return users;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@RequestParam("id") int id) {
		User user = null;
		try {
			user = userDb.getUser(id);
		} catch (UserDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	@RequestMapping(value = "users/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public User getUserWithPathParam(@PathVariable("id") int id) {
		User user = null;
		try {
			user = userDb.getUser(id);
		} catch (UserDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	@RequestMapping(value = "users/user", method = RequestMethod.DELETE )
	@ResponseBody
	public String deleteUser(@RequestParam("id") int id) {
		
		userDb.delete(id);
		return "ID " + id + " was deleted";
	}
	
	@RequestMapping(value = "users/user/{id}", method = RequestMethod.DELETE )
	@ResponseBody
	public String deleteUserWithPathParam(@PathVariable("id") int id) {
		
		userDb.delete(id);
		return "ID " + id + " was deleted";
	}
	
	@RequestMapping(value = "users/user", method = RequestMethod.PUT)
	@ResponseBody
	public String createUser(@RequestBody User user) {
		
		String result = "";
		try {
			userDb.create(user);
			result =  "Added:" + user.getId();
		} catch (UserDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "Failed to add";
		}
		
		return result;
	}
	
	@RequestMapping(value = "users/user", method = RequestMethod.POST)
	@ResponseBody
	public User updateUser(@RequestBody User user){
	
		
		userDb.update(user);
		return user;
	}

	@RequestMapping(value = "/users/user/{id}/transactions", method = RequestMethod.GET)
	@ResponseBody
	public List<UserTransaction> getTransactionForUser(@PathVariable("id") int id){
		List<UserTransaction> txs = userDb.getTransactionForUser(id);
		return txs;
	}
	
	@RequestMapping(value = "/users/user/{id}/transactions/transaction/{tid}", method = RequestMethod.GET)
	@ResponseBody
	public UserTransaction getTransactionById(@PathVariable("id") int id, @PathVariable("tid") int tid){
		List<UserTransaction> txs = userDb.getTransactionForUser(id);
		for(UserTransaction utx : txs){
			if (utx.getId() == tid){
				return utx;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/users/user/{id}/transactions/transaction/{tid}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteTransaction(@PathVariable("id") int id, @PathVariable("tid") int tid) {
		userDb.deleteTransaction(tid);
	}
	
	@RequestMapping(value = "/users/user/{id}/transactions/transaction", method=RequestMethod.PUT)
	@ResponseBody
	public UserTransaction createTransaction(@PathVariable ("id") int id, @RequestBody UserTransaction userTransaction) {
		
		userDb.createTransaction(userTransaction);
		return userTransaction;
	}
	
	@RequestMapping(value = "/users/user/{id}/transactions/transaction", method=RequestMethod.POST)
	@ResponseBody
	public UserTransaction updateTransaction(@PathVariable ("id") int id, @RequestBody UserTransaction userTransaction) {
		
		userDb.updateTransaction(userTransaction);
		return userTransaction;
	}
	
	@RequestMapping(value = "/arraytest", method=RequestMethod.GET)
	@ResponseBody
	public int getStrings(@RequestParam("months") String[] months) {
		
		return months.length;
	}
}
