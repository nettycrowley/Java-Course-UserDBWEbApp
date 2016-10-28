package ie.rccourse.userDb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class SpringUserDb implements UserDb {
	
	//	properties
		DriverManagerDataSource dmds;
		protected JdbcTemplate jdbcTemplate; 
		
		//	get & set methods
		public DriverManagerDataSource getDmds() {
			return dmds;
		}

		@Autowired
		public void setDmds(DriverManagerDataSource dmds) {
			this.dmds = dmds;
			
			jdbcTemplate = new JdbcTemplate(dmds);
		}
		
		
		//	constructor(s)
		public SpringUserDb(){
			
		}
		
		public SpringUserDb(DriverManagerDataSource dmds) {
			this.dmds = dmds;
			jdbcTemplate = new JdbcTemplate(dmds);
			
		}
		
		//	get the user with the specified id
		public User getUser(int id) throws UserDbException {
			
			User user = null;
			String sql = "SELECT * FROM users where id=?";
			
			try {
				user = (User) jdbcTemplate.queryForObject(
						sql, new Object[] {id}, new BeanPropertyRowMapper<User>(User.class));
						//BeanPropertyRowMapper<User>(User.class));	
			} catch (Exception ex) {
				throw new UserDbException(ex.getMessage());
		}
		return user;
		}
		
		public List<User> getUsers() {
			
			String sql = "SELECT * FROM users";
			
			List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
			
			return users;
						
		}
		
		public List<User> find(String search){
			
			/*String sql = "SELECT * FROM users" +
						"WHERE firstName LIKE '%" + search + "%'" +
						"OR lastName LIKE '%" + search + "%";*/
			
			String sql = "SELECT * FROM users " +
					"WHERE firstName LIKE ? " +
					"OR lastName LIKE ?";
					
			List<User> users = jdbcTemplate.query(sql,  
					new Object[]{"%" + search + "%", "%" + search + "%"},
					new BeanPropertyRowMapper<User>(User.class));
			
			return users;
		}
		
		
		public void create(User user) throws UserDbException {
		
				SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
				jdbcInsert.setTableName("users");
				jdbcInsert.setGeneratedKeyName("id");
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				
				parameters.put("firstName", user.getFirstName());
				parameters.put("lastName", user.getLastName());
				parameters.put("registered", user.isRegistered());
				parameters.put("dateOfBirth", user.getDateOfBirth());
								
				Number id = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
				user.setId(id.intValue());
				
		}
		
		public void delete(int id) {
			//delette the transactions for the user
			//String sql = "DELETE FROM transactions WHERE userId = ?";
			//jdbcTemplate.update(sql, new Object[]{id});
			
			deleteTransactionsForUser(id);
			
			//delete the user
			String sql = "DELETE FROM users WHERE id = ?";
			jdbcTemplate.update(sql, new Object[]{id});
			
		}
		
		public void update(User user) {
			
			String sql = "UPDATE users " + 
						 "SET firstName = ?, " +
						 "lastName = ?, " +
						 "registered = ?, " +
						 "dateOfBirth = ? " +
						 "WHERE id = ?";
			jdbcTemplate.update(sql, new Object[]{user.getFirstName(), 
					user.getLastName(), 
					user.isRegistered(),
					user.getDateOfBirth(),
					user.getId()});
		}
		
		public List<UserTransaction> getTransactionForUser(int userId){
			String sql = "SELECT * FROM transactions WHERE userId=?";
			
			List<UserTransaction> transactions = jdbcTemplate.query(sql, 
					new Object[]{userId},
					new BeanPropertyRowMapper<UserTransaction>(UserTransaction.class));
			return transactions;
		}
		
		public UserTransaction createTransaction(UserTransaction userTransaction){
			
			
			SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			jdbcInsert.setTableName("transactions");
			jdbcInsert.setGeneratedKeyName("id");
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("description", userTransaction.getDescription());
			parameters.put("amount", userTransaction.getAmount());
			parameters.put("transactionDate", userTransaction.getTransactionDate());
			parameters.put("userId", userTransaction.getUserId());
							
			Number id = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
			userTransaction.setId(id.intValue());
			
			return userTransaction;
		}
		
		/*public UserTransaction create(User user, UserTransaction userTransaction){
			
		}*/
		
		public void updateTransaction(UserTransaction userTransaction) {
			String sql = "UPDATE transactions " +
						"SET  description = ?, " +
						"amount = ?, " +
						"transactionDate = ? " +
						"WHERE id = ?";
			jdbcTemplate.update(sql, new Object[]{
					userTransaction.getDescription(),
					userTransaction.getAmount(),
					userTransaction.getTransactionDate(),
					userTransaction.getId()
			});
			
		}
		
		public void deleteTransaction(int transactionId) {
			String sql = "DELETE FROM transactions WHERE id = ?";
			jdbcTemplate.update(sql, new Object[]{transactionId});
		}
		
		public void deleteTransactionsForUser(User user){
			String sql = "DELETE FROM transactions WHERE userId=?";
			jdbcTemplate.update(sql, new Object[]{user.getId()});
		}
		
		public void deleteTransactionsForUser(int userId){
			String sql = "DELETE FROM transactions WHERE userId=?";
			jdbcTemplate.update(sql, new Object[]{userId});
		}
		
		public void close() {
			
		}
		
		public static void main(String[] args) {
			
	    	ApplicationContext context = new ClassPathXmlApplicationContext(
					"SpringBeans.xml");
	    							    	
	    	//SpringUserDb userDb = context.getBean(SpringUserDb.class);
	    	
	    	UserDb userDb = context.getBean(UserDb.class);
	    	
			
			//SpringUserDb userDb = new SpringUserDb(dmds);
	    	
	    	userDb.deleteTransaction(1);
	    	userDb.deleteTransactionsForUser(54);
			
			/*User u = null;
			try {
				u = userDb.getUser(58);
			} catch (SpringUserDbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			userDb.deleteTransactionsForUser(u);
			
			List<UserTransaction>txs = userDb.getTransactionForUser(56);
					
			for (UserTransaction tx : txs) {
				tx.setAmount(999.99);
				userDb.updateTransaction(tx);
			}
			
			txs = userDb.getTransactionForUser(57);
			for (UserTransaction tx : txs) {
				System.out.println(tx);
			}*/
			
			
			UserTransaction ut = new UserTransaction(40, 56, "Opening balance", "2016-10-01", 2599.40);
			userDb.createTransaction(ut);
			
		/*	System.out.println("NEW USER:" + user.getId());
			
			userDb.delete(57);
			
			user.setFirstName("CHANGED");
			user.setLastName("CHANGED");
			
			userDb.update(user);
			
			user = null;
			try{
				user = userDb.getUser(56);
				System.out.println(user);
			} catch (SpringUserDbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}*/
			userDb.close();
		}

	}

	
