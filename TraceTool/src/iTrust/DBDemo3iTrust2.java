package iTrust;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.pattern.FullLocationPatternConverter;

import Chess.SubjectTSubjectNObject;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.InterfaceFactory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.path.CtPath;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import tables.methodcalls;
import tables.methods;
import tables.tracesmethods;

/**
 * This class demonstrates how to connect to MySQL and run some basic commands.
 * 
 * In order to use this, you have to download the Connector/J driver and add
 * its .jar file to your build path.  You can find it here:
 * 
 * http://dev.mysql.com/downloads/connector/j/
 * 
 * You will see the following exception if it's not in your class path:
 * 
 * java.sql.SQLException: No suitable driver found for jdbc:mysql://localhost:3306/
 * 
 * To add it to your class path:
 * 1. Right click on your project
 * 2. Go to Build Path -> Add External Archives...
 * 3. Select the file mysql-connector-java-5.1.24-bin.jar
 *    NOTE: If you have a different version of the .jar file, the name may be
 *    a little different.
 *    
 * The user name and password are both "root", which should be correct if you followed
 * the advice in the MySQL tutorial. If you want to use different credentials, you can
 * change them below. 
 * 
 * You will get the following exception if the credentials are wrong:
 * 
 * java.sql.SQLException: Access denied for user 'userName'@'localhost' (using password: YES)
 * 
 * You will instead get the following exception if MySQL isn't installed, isn't
 * running, or if your serverName or portNumber are wrong:
 * 
 * java.net.ConnectException: Connection refused
 */
public class DBDemo3iTrust2 {

	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "root";

	/** The name of the computer running MySQL */  
	
	private final String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/** The name of the database we are testing with (this default is installed with MySQL) */
	private final String dbName = "databaseitrust";
	
	/** The name of the table we are testing with */
	private final String tableName = "classes";

	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("root", this.userName);
		connectionProps.put("123456", this.password);
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/databaseitrust","root","123456");

		return conn;
	}

	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
	 * 
	 * @throws SQLException If something goes wrong
	 */
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	/**
	 * Connect to MySQL and do some stuff.
	 */
	public void run() {
		ResultSet rs = null; 
		// Connect to MySQL
		Connection conn = null;
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}

		// Create a table
		try {
			Statement st= conn.createStatement();
//			st.executeUpdate("DROP SCHEMA `databaseitrust`"); 
//			
//			st.executeUpdate("CREATE DATABASE `databaseitrust`"); 
//			st.executeUpdate("CREATE TABLE `databaseitrust`.`classes` (\r\n" + 
//					"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//					"  `classname` LONGTEXT NULL,\r\n" + 
//					"  PRIMARY KEY (`id`),\r\n" + 
//					"  UNIQUE INDEX `id_UNIQUE` (`id` ASC));"); 
//			
//			
//
//		    
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`superclasses` (\r\n" + 
//		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `superclassid` INT NULL,\r\n" + 
//		   		"  `superclassname` LONGTEXT NULL,\r\n" + 
//		   		"  `ownerclassid` INT NULL,\r\n" + 
//		   		"  `childclassname` LONGTEXT NULL,\r\n" + 
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  INDEX `superclassid_idx` (`superclassid` ASC),\r\n" + 
//		   		"  INDEX `ownerclassid_idx` (`ownerclassid` ASC),\r\n" + 
//		   		"  CONSTRAINT `superclassid`\r\n" + 
//		   		"    FOREIGN KEY (`superclassid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION,\r\n" + 
//		   		"  CONSTRAINT `ownerclassid`\r\n" + 
//		   		"    FOREIGN KEY (`ownerclassid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION);"); 
//		   
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`interfaces` (\r\n" + 
//		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 	   	
//		   		"  `interfaceclassid` INT NULL,\r\n" + 
//		   		"  `interfacename` LONGTEXT NULL,\r\n" + 
//		   		"  `ownerclassid` INT NULL,\r\n" + 
//		   		"  `classname` LONGTEXT NULL,\r\n" +	   		
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
//		   		"  INDEX `interfaceclassid_idx` (`interfaceclassid` ASC),\r\n" + 
//		   		"  INDEX `classid_idx` (`ownerclassid` ASC),\r\n" + 
//		   		"  CONSTRAINT `interfaceclassid`\r\n" + 
//		   		"    FOREIGN KEY (`interfaceclassid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION,\r\n" + 
//		   		"  CONSTRAINT `ownerclassid2`\r\n" + 
//		   		"    FOREIGN KEY (`ownerclassid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION);"); 
//		   
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`methods` (\r\n" + 
//		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `shortmethodname` LONGTEXT NULL,\r\n" + 
//		   		"  `shortclassname` LONGTEXT NULL,\r\n" + 
//				"  `methodname` LONGTEXT NULL,\r\n" + 
//		   		"  `methodnamerefined` LONGTEXT NULL,\r\n" + 
//		   		"  `methodabbreviation` LONGTEXT NULL,\r\n" + 
//		   		"  `fullmethod` LONGTEXT NULL,\r\n" + 
//		   		"  `classid` INT NULL,\r\n" + 
//		   		"  `classname` LONGTEXT NULL,\r\n" + 
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
//		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
//		   		"  CONSTRAINT `classid2`\r\n" + 
//		   		"    FOREIGN KEY (`classid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION);"); 
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`parameters` (\r\n" + 
//		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `parametername` VARCHAR(200) NULL,\r\n" + 
//		   		"  `parametertype` VARCHAR(200) NULL,\r\n" + 
//		   		"  `parameterclass` INT NULL,\r\n" + 
//		   		"  `classid` INT NULL,\r\n" + 
//		   		"  `classname` VARCHAR(200) NULL,\r\n" + 
//		   		"  `methodid` INT NULL,\r\n" + 
//		   		"  `methodname`  LONGTEXT NULL,\r\n" + 
//		   		"  `isreturn` TINYINT NOT NULL,\r\n"+
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
//		   		"  INDEX `classid_idx` (`classid` ASC),\r\n" + 
//		   		"  INDEX `methodid_idx` (`methodid` ASC),\r\n" + 
//		   		"  CONSTRAINT `classid8`\r\n" + 
//		   		"    FOREIGN KEY (`classid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION,\r\n" + 
//		   		"  CONSTRAINT `classid3`\r\n" + 
//		   		"    FOREIGN KEY (`classid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`classes` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION,\r\n" + 
//		   		"  CONSTRAINT `methodid`\r\n" + 
//		   		"    FOREIGN KEY (`methodid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`methods` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION"+   	
//		   		 ")"); 
//			  st.executeUpdate("CREATE TABLE `databaseitrust`.`fieldclasses` (\r\n" + 
//				   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//				   		"  `fieldname` LONGTEXT NULL,\r\n" + 
//				   		"  `fieldtypeclassid` INT NULL,\r\n" + 
//				   		"  `fieldtype` LONGTEXT NULL,\r\n" + 
//				   		"  `ownerclassid` INT NULL,\r\n" + 
//				   		"  `classname` LONGTEXT NULL,\r\n" + 
//				   		"  PRIMARY KEY (`id`));"); 
//				   
//
//				   
//				   st.executeUpdate("CREATE TABLE `databaseitrust`.`fieldmethods` (\r\n" + 
//				   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
//				   		"  `fieldaccess` VARCHAR(200) NULL,\r\n" + 
//				   		"  `fieldtypeclassid` INT NULL,\r\n" + 
//				   		"  `fieldtypeclassname` LONGTEXT NULL,\r\n" + 
//				   		"  `ownerclassname` VARCHAR(200) NULL,\r\n" + 
//				   		"  `ownerclassid` INT NULL,\r\n" + 
//				   		"  `ownermethodname` VARCHAR(400) NULL,\r\n" + 
//				   		"  `ownermethodid` INT NULL,\r\n" + 
//				   		"  `fieldclassownerclassid` LONGTEXT NULL,\r\n" + 
//				   		"  PRIMARY KEY (`id`));"); 
//		   
//
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`methodcalls` (\r\n" + 
//		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `callermethodid` INT NULL,\r\n" + 
//		   		"  `callername` LONGTEXT NULL,\r\n" + 
//		   		"  `callerclass` LONGTEXT NULL,\r\n" + 
//		   		"  `callerclassid` LONGTEXT NULL,\r\n" + 
//		   		"  `fullcaller` LONGTEXT NULL,\r\n" + 
//		   		"  `calleemethodid` INT NULL,\r\n" + 
//		   		"  `calleename` LONGTEXT NULL,\r\n" + 
//		   		"  `calleeclass` LONGTEXT NULL,\r\n" + 
//		   		"  `calleeclassid` LONGTEXT NULL,\r\n" + 
//		   		"  `fullcallee` LONGTEXT NULL,\r\n" + 
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\r\n" + 
//		   		"  INDEX `caller_idx` (`callermethodid` ASC),\r\n" + 
//		   		"  INDEX `callee_idx` (`calleemethodid` ASC),\r\n" + 
//		   		"  CONSTRAINT `methodcalledid`\r\n" + 
//		   		"    FOREIGN KEY (`callermethodid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`methods` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION,\r\n" + 
//		   		"  CONSTRAINT `callingmethodid`\r\n" + 
//		   		"    FOREIGN KEY (`calleemethodid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`methods` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION);"); 
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`methodcallsexecuted` (\r\n" + 
//			   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//			   		"  `callermethodid` LONGTEXT NULL,\r\n" + 
//			   		"  `callername` LONGTEXT NULL,\r\n" + 
//			   		"  `callerclass` LONGTEXT NULL,\r\n" + 
//			   		"  `fullcaller` LONGTEXT NULL,\r\n" + 
//			   		"  `calleemethodid` LONGTEXT NULL,\r\n" + 
//			   		"  `calleename` LONGTEXT NULL,\r\n" + 
//			   		"  `calleeclass` LONGTEXT NULL,\r\n" + 
//			   		"  `fullcallee` LONGTEXT NULL,\r\n" + 
//			   		"  PRIMARY KEY (`id`),\r\n" + 
//			   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC)); " ); 
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`traces` (\r\n" + 
//		   		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `requirement` LONGTEXT NULL,\r\n" + 
//		   		"  `requirementid` INT,\r\n" + 
//		   		"  `method` LONGTEXT NULL,\r\n" + 
//		   		"  `methodname` LONGTEXT NULL,\r\n" + 
//		   		"  `fullmethod` LONGTEXT NULL,\r\n" +
//		   		"  `methodid` INT NULL,\r\n" + 
//		   		"  `classname` LONGTEXT NULL,\r\n" + 
//		   		"  `classid` LONGTEXT NULL,\r\n" + 
//		   		"  `developerGold` LONGTEXT NULL,\r\n" +
//		   		"  `subject` LONGTEXT NULL,\r\n" + 
//		   		"  `goldpredictioncallee` LONGTEXT NULL,\r\n" + 
//		   		"  `goldpredictioncaller` LONGTEXT NULL,\r\n" + 
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  INDEX `methodid_idx8` (`methodid` ASC),\r\n" + 
//		   		"  CONSTRAINT `methodid8`\r\n" + 
//		   		"    FOREIGN KEY (`methodid`)\r\n" + 
//		   		"    REFERENCES `databaseitrust`.`methods` (`id`)\r\n" + 
//		   		"    ON DELETE NO ACTION\r\n" + 
//		   		"    ON UPDATE NO ACTION);\r\n" + 	
//		   		""); 
//		 
//		   
//		   st.executeUpdate("CREATE TABLE `databaseitrust`.`requirements` (\r\n" + 
//		   		"  `id` INT NOT NULL AUTO_INCREMENT,\r\n" + 
//		   		"  `requirementname` LONGTEXT NULL,\r\n" + 
//		   		"  PRIMARY KEY (`id`),\r\n" + 
//		   		"  UNIQUE INDEX `id_UNIQUE` (`id` ASC));"); 
//			 st.executeUpdate("CREATE TABLE `databaseitrust`.`tracesclasses` (\r\n" + 
//			 		"  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,\r\n" + 
//			 		"  `requirement` LONGTEXT NULL,\r\n" + 
//			 		"  `requirementid` INT NULL,\r\n" + 
//			 		"  `classname` LONGTEXT NULL,\r\n" + 
//			 		"  `shortclassname` LONGTEXT NULL,\r\n" + 
//			 		"  `classid` INT NULL,\r\n" + 
//			 		"  `developerGold` LONGTEXT NULL,\r\n" +
//			 		"  `subject` LONGTEXT NULL,\r\n" + 
//			 		"  PRIMARY KEY (`id`),\r\n" + 
//			 		"  UNIQUE INDEX `idtracesclasses_UNIQUE` (`id` ASC));\r\n" + 
//			 		""); 
		   
		   try {
			Spoon();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		  
		   
		   
		
	    } catch (SQLException e) {
			System.out.println("ERROR: Could not create the table");
			e.printStackTrace();
			return;
		}
		
		
	}
	
	/**
	 * Connect to the DB and do some stuff
	 */
	public static void main(String[] args) {
		DBDemo3iTrust2 app = new DBDemo3iTrust2();
		app.run();
	}
	
	public void Spoon() throws SQLException, FileNotFoundException {
		DBDemo3iTrust2 dao = new DBDemo3iTrust2();
	Connection conn=getConnection();
	Statement st= conn.createStatement();
	
	
	Statement st2= conn.createStatement();
	Statement st3= conn.createStatement();
	Statement st4= conn.createStatement();
	Statement st5= conn.createStatement();
		SpoonAPI spoon = new Launcher();
    	spoon.addInputResource("C:\\Users\\mouna\\ownCloud\\Mouna Hammoudi\\dumps\\SOURCE_CODE\\iTrust");
    	
    	
    
        	spoon.getEnvironment().setAutoImports(true);
        	spoon.getEnvironment().setNoClasspath(true);
        	CtModel model = spoon.buildModel();
        	//List<String> classnames= new ArrayList<String>(); 
      
        	// Interact with model
        	Factory factory = spoon.getFactory();
        	ClassFactory classFactory = factory.Class();
        	MethodFactory methodFactory = factory.Method(); 
        	InterfaceFactory interfaceFactory = factory.Interface(); 
        	int i=1; 
            /*********************************************************************************************************************************************************************************/	
            /*********************************************************************************************************************************************************************************/	
            /*********************************************************************************************************************************************************************************/	  	
        
        	
        	
        	
        	
      //  	BUILD CLASSES TABLE 
//        	for(CtType<?> clazz : classFactory.getAll()) {
//        		
//        	
//        		
//    			Set<CtType<?>> nested = clazz.getNestedTypes();
//    			
//    				for(CtType<?> mynested: nested) {
//    					System.out.println(mynested.getQualifiedName());
//    					st.executeUpdate("INSERT INTO `classes`(`classname`) VALUES ('"+mynested.getQualifiedName()+"');");
//    					
//    					
//    					
//    					Set<CtType<?>> nested2 = mynested.getNestedTypes();
//    					for(CtType<?> mynested2: nested2) {
//    						System.out.println(mynested2.getQualifiedName());
//    						st.executeUpdate("INSERT INTO `classes`(`classname`) VALUES ('"+mynested2.getQualifiedName()+"');");
//    						
//    						
//    						}
//    					}
//    				
//    			
//    		
//    			String FullClassName= clazz.getPackage()+"."+clazz.getSimpleName(); 
//    			System.out.println(FullClassName);
//    			st.executeUpdate("INSERT INTO `classes`(`classname`) VALUES ('"+FullClassName+"');");
//    				
//       		
//        		
//        				
//        	
//       
//        		
//
//        	}
//        	
//        	
//        	
//        	 
//////        	/*********************************************************************************************************************************************************************************/	
//////            /*********************************************************************************************************************************************************************************/	
//////            /*********************************************************************************************************************************************************************************/
//        //	BUILD SUPERCLASSES TABLE 
//        	for(CtType<?> clazz : classFactory.getAll(true)) {
//        		String childclassQuery = null; 
//        		String superclassQuery = null;
//        		String superclassQueryName=null; 
//        		String childclassQueryName=null; 
//        		
//        		String FullClassName= clazz.getQualifiedName(); 
//        		//String superclass= clazz.getSuperclass().toString();
//        		
//    			
//    			//System.out.println("SUPERCLASS"+superclass +"SUBCLASS "+FullClassName);
//    //if(clazz.getSuperclass()!=null && clazz.getSuperclass().toString().contains(clazz.getPackage().toString()) ) {
//    	if(clazz.getSuperclass()!=null  ) {
//    		
//        			String superclass= clazz.getSuperclass().toString();
//        		//	System.out.println(i+"    HERE IS MY SUPERCLASS"+superclass+"AND HERE IS MY SUBCLASS  "+FullClassName);
//        		i++; 
//        
//        					ResultSet sClass = st.executeQuery("SELECT id from classes where classname='"+superclass+"'"); 
//        					while(sClass.next()){
//        						 superclassQuery= sClass.getString("id"); 
//        			//			System.out.println("superclass: "+superclassQuery);	
//        			   		   }
//
//        					ResultSet sClassName = st.executeQuery("SELECT classname from classes where classname='"+superclass+"'"); 
//        					while(sClassName.next()){
//        						 superclassQueryName= sClassName.getString("classname"); 
//        			//			System.out.println("superclass: "+superclassQuery);	
//        			   		   }		
//        					
//        					ResultSet cClass = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
//        					while(cClass.next()){
//        						 childclassQuery= cClass.getString("id"); 
//        			//			System.out.println("subclass: "+childclassQuery);	
//        			   		   }
//        					ResultSet cClassName = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
//        					while(cClassName.next()){
//        						 childclassQueryName= cClassName.getString("classname"); 
//        			//			System.out.println("subclass: "+childclassQuery);	
//        			   		   }
//        					
//        			String result= "SELECT classname from classes where classname='"+FullClassName+"'"; 
//        			if(superclassQuery!=null)
//        			st.executeUpdate("INSERT INTO `superclasses`(`superclassid`, `superclassname`, `ownerclassid`, `childclassname`) VALUES ('"+superclassQuery +"','" +superclassQueryName+"','" +childclassQuery+"','" +childclassQueryName+"')");
//        			
//        		
//        		
//        		/*	st.executeUpdate("INSERT INTO `superclasses`(`superclass`, `childclass`) VALUES( "
//        					+"(("+ superclassQuery+")"
//        					+ ", ("+childclassQuery+")));" ); */
//            		//clazz.getSuperInterfaces();
//            		
//        		}
//        	}
//////////////        	/*********************************************************************************************************************************************************************************/	
//////////////            /*********************************************************************************************************************************************************************************/	
//////////////            /*********************************************************************************************************************************************************************************/	
////////////        	  	
//////////         	//BUILD INTERFACES TABLE 
//        	 
//
//        	List<String> mylist2 = new ArrayList<String>(); 
//        	for(CtType clazz : classFactory.getAll(true)) {
//        		
//        		if(clazz instanceof CtClass) {
//        			String myinterfaceclassid = null;
//            		String myinterfacename = null;
//            		String myclassid = null;
//            		String myclassname = null;
//            		
//        			String FullClassName= clazz.getQualifiedName(); 
//        			Set<CtTypeReference<?>> interfaces = clazz.getSuperInterfaces(); 
//
//        			for(CtTypeReference<?> inter: interfaces) {
//        			
//        					
//        				
//        		
//        					
//        				ResultSet interfacesnames = st.executeQuery("SELECT * from classes where classname='"+inter+"'"); 
//    					while(interfacesnames.next()){
//    						myinterfacename= interfacesnames.getString("classname"); 
//    						myinterfaceclassid= interfacesnames.getString("id"); 
//
//    			   		   }
//    					if(myinterfaceclassid==null) {
//    						String interclassname = inter.toString().replaceAll("\\.(?!.*\\.)","\\$");
//    						 interfacesnames = st.executeQuery("SELECT * from classes where classname='"+interclassname+"'"); 
//        					while(interfacesnames.next()){
//        						myinterfacename= interfacesnames.getString("classname"); 
//        						myinterfaceclassid= interfacesnames.getString("id"); 
//
//        			   		   }
//    					}
//    					
//    					
//    					ResultSet classesnames= st.executeQuery("SELECT * from classes where classname='"+FullClassName+"'"); 
//    					while(classesnames.next()){
//    						myclassname= classesnames.getString("classname"); 
//    						myclassid= classesnames.getString("id"); 
//
//    			   		   }
//        					String interface1= myinterfaceclassid+ myinterfacename;  
//        					String implementation1= myclassid+ myclassname; 
//        					
//        						System.out.println("INTERRRR "+inter.getQualifiedName());
//        						System.out.println("CLAZZZZ "+clazz.getQualifiedName());
//        					
//        		
//        		
//    					
//    					
//    					if(myinterfaceclassid!=null && !mylist2.contains(interface1+implementation1) ) {
//    		    			st.executeUpdate("INSERT INTO `interfaces`(`interfaceclassid`,`interfacename`,`ownerclassid`, `classname`) VALUES ('"+myinterfaceclassid +"','" +myinterfacename+"','" +myclassid+"','" +myclassname+"')");
//    		    			mylist2.add(interface1+implementation1); 
//    					}
//        			}
//
//    					
//    				
//    					
//    					
//    					
//    			
//    				
//    				
//    				
//    			}
//    			
//        		
//           	List<String> mylist = new ArrayList<String>(); 
//
//         		if(clazz instanceof CtInterface) {
//        			String myinterfaceclassid = null;
//            		String myinterfacename = null;
//            		String myclassid = null;
//            		String myclassname = null;
//            		
//        			String FullClassName= clazz.getQualifiedName(); 
//        			Set<CtTypeReference<?>> interfaces = clazz.getSuperInterfaces(); 
//
//        			for(CtTypeReference<?> inter: interfaces) {
//        			
//        				ResultSet interfacesnames = st.executeQuery("SELECT classname from classes where classname='"+inter+"'"); 
//    					while(interfacesnames.next()){
//    						myinterfacename= interfacesnames.getString("classname"); 
//    			   		   }
//    					
//    					ResultSet interfacesclasses = st.executeQuery("SELECT id from classes where classname='"+inter+"'"); 
//    					while(interfacesclasses.next()){
//    						myinterfaceclassid= interfacesclasses.getString("id"); 
//    			   		   }
//    					
//    					ResultSet classesnames= st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
//    					while(classesnames.next()){
//    						myclassname= classesnames.getString("classname"); 
//    			   		   }
//    					
//    					ResultSet interfacesname = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
//    					while(interfacesname.next()){
//    						myclassid= interfacesname.getString("id"); 
//    			   		   }
//    					String interface1= myinterfaceclassid+ myinterfacename;  
//    					String implementation1= myclassid+ myclassname; 
//        				
//        		
//        					
//        				
//        					
//        					
//        						System.out.println("INTERRRR2 "+inter.getQualifiedName());
//        						System.out.println("CLAZZZZ2 "+clazz.getQualifiedName());
//        					
//        		
//        		
//    					
//    					
//    					if(myinterfaceclassid!=null && !mylist.contains(interface1+implementation1) ) {
//    		    			st.executeUpdate("INSERT INTO `superclasses`(`superclassid`, `superclassname`, `ownerclassid`, `childclassname`) VALUES ('"+myinterfaceclassid +"','" +myinterfacename+"','" +myclassid+"','" +myclassname+"')");
//    		    			mylist.add(interface1+implementation1); 
//    					}
//        			}
//
//    					
//    				
//    					
//    					
//    					
//    			
//    				
//    				
//    				
//    			}
//        		
//
//        	}
//
//
////////////////////////        	
//    ////////////////////    
//////////////////////        	
////////////////////////        	/*********************************************************************************************************************************************************************************/	
////////////////////////            /*********************************************************************************************************************************************************************************/	
////////////////////////            /*********************************************************************************************************************************************************************************/	  	
////////////////////////        	//BUILD METHODS TABLE 
//        	List<methods> mymethodlist = new ArrayList<methods>(); 
//        	for(CtType<?> clazz : classFactory.getAll(true)) {
//        		
//        	
//        		String myclassid = null;
//        		String myclassname = null;
//        		
//        		//ALTERNATIVE: Collection<CtMethod<?>> methods = clazz.getAllMethods(); 
//    			Collection<CtMethod<?>> methods = clazz.getMethods(); 
//    			String FullClassName= clazz.getQualifiedName(); 
//    			
//    			//System.out.println("count:   "+count);
//    			//NEEDS TO BE CHANGED 
//    		//	if(count==2) {
//    			 List<CtConstructor> MyContructorlist = clazz.getElements(new TypeFilter<>(CtConstructor.class)); 
//    			 for(CtConstructor<?> constructor: MyContructorlist) {
//    				 
//    				 	String methodString =""; 
//    					String FullConstructorName=constructor.getSignature().toString(); 
//    					
//    					String methodabbreviation=FullConstructorName.substring(0, FullConstructorName.indexOf("(")); 
//    					 methodabbreviation=FullClassName+".-init-"; 
//
//
//    					//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
//    					//24 is the size of the string "edu.ncsu.csc.itrust.javaChess."
//    					int packagesize= "edu.ncsu.csc.itrust.".length(); 
//    						FullConstructorName=FullConstructorName.substring(packagesize, FullConstructorName.length()); 
//    						FullConstructorName="-init-"+FullConstructorName.substring(FullConstructorName.lastIndexOf('('));  
//    						
//    						System.out.println(FullClassName);
//
//    						ResultSet classesreferenced = st.executeQuery("SELECT * from classes where classname='"+FullClassName+"'"); 
//    						while(classesreferenced.next()){
//    							myclassid= classesreferenced.getString("id"); 
//    							myclassname= classesreferenced.getString("classname"); 
//
//    					//		System.out.println("class referenced: "+clazz);
//    				   		   }
//    						
//    						
//    					
//    							String FullMethodNameRefined=FullConstructorName.substring(0, FullConstructorName.indexOf("(")); 
//    							//String FullMethodName=constructor.getSignature().toString(); 
//    							String fullmeth= myclassname+"."+FullConstructorName; 
//    							System.out.println(FullClassName);
//    							methods meth= new methods(fullmeth, myclassid, myclassname); 
//    							if(meth.contains(mymethodlist, meth)==false ) {
//    							
//    								
//    							System.out.println(myclassname);
//    								String shortclassname =myclassname.substring(myclassname.lastIndexOf(".")+1, myclassname.length()); 
//    				    			
//    								try{
//   									 methodString = constructor.toString(); 
//   	    							methodString=methodString.replaceAll("'", ""); 
//
//   								}catch(Exception e) {
//   									
//   								}
//    								st.executeUpdate("INSERT INTO `methods`(`methodname`, `methodnamerefined`, `methodabbreviation`, `fullmethod`,`classid`, `classname`, `shortclassname`,`method`) "
//    				    					+ "VALUES ('"+FullConstructorName+"','" +FullMethodNameRefined +"','" +methodabbreviation+"','" +fullmeth+"','" +myclassid+"','" +myclassname
//    				    					+"','" +shortclassname+"','" +methodString+"')");
//
//    								
//    				    			mymethodlist.add(meth); 
//    							}
//    						
////    							 List<CtMethod> MyMethodsInsideCons = constructor.getElements(new TypeFilter<>(CtMethod.class)); 
////    							 for(CtMethod mymethodInsideCons: MyMethodsInsideCons) {
////    								
////    								 String FullMethodName=mymethodInsideCons.getSignature().toString(); 
////    									System.out.println("==============>"+mymethodInsideCons.getShortRepresentation().toString());
////    									//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
////    								//	System.out.println(FullClassName);
////    									String FullMethodNameRefinedInsideCons=FullMethodName.substring(0, FullMethodName.indexOf("(")); 
////    									String longmethInsideCons= clazz.getQualifiedName()+"."+FullMethodName; 
////    									String methodabbreviationInsideCons=longmethInsideCons.substring(0, longmethInsideCons.indexOf("(")); 
////    										ResultSet classesreferenced2 = st.executeQuery("SELECT * from classes where classname='"+FullClassName+"'"); 
////    										while(classesreferenced2.next()){
////    											myclassid= classesreferenced2.getString("id");
////    											myclassname= classesreferenced2.getString("classname"); 
////
////    									//		System.out.println("class referenced: "+clazz);
////    								   		   }
////    										
////    									
////    									
////    											String fullmeth2= myclassname+"."+FullMethodName; 
////    											System.out.println(FullClassName);
////    								 
////    								 
////    					    			st.executeUpdate("INSERT INTO `methods`(`methodname`, `methodnamerefined`, `methodabbreviation`, `fullmethod`,`classid`, `classname`) VALUES ('"+FullMethodName+"','" +FullMethodNameRefinedInsideCons +"','" +methodabbreviationInsideCons+"','" +fullmeth2+"','" +myclassid+"','" +myclassname+"')");
////
////    							 }
//
//    						}
//    			 
//    			 
//    			 
//    			for(CtMethod<?> method: methods) {
//    				 
//    				 String methodString =""; 
//    				String FullMethodName=method.getSignature().toString(); 
//    				System.out.println("==============>"+method.getShortRepresentation().toString());
//    				//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
//    			//	System.out.println(FullClassName);
//    				String FullMethodNameRefined=FullMethodName.substring(0, FullMethodName.indexOf("(")); 
//    				String longmeth= clazz.getQualifiedName()+"."+FullMethodName; 
//    				String methodabbreviation=longmeth.substring(0, longmeth.indexOf("(")); 
//    					ResultSet classesreferenced = st.executeQuery("SELECT id from classes where classname='"+FullClassName+"'"); 
//    					while(classesreferenced.next()){
//    						myclassid= classesreferenced.getString("id"); 
//    				//		System.out.println("class referenced: "+clazz);
//    			   		   }
//    					ResultSet classnames = st.executeQuery("SELECT classname from classes where classname='"+FullClassName+"'"); 
//    					while(classnames.next()){
//    						myclassname= classnames.getString("classname"); 
//    				//		System.out.println("class referenced: "+clazz);
//    			   		   }
//    					
//    				
//    				
//    						String fullmeth= myclassname+"."+FullMethodName; 
//    						System.out.println(FullClassName);
//    						methods meth= new methods(FullMethodName, myclassid, myclassname); 
//    						if(meth.contains(mymethodlist, meth)==false ) {
//								String shortclassname =myclassname.substring(myclassname.lastIndexOf(".")+1, myclassname.length()); 
//								try{
//									 methodString = method.toString(); 
//	    							methodString=methodString.replaceAll("'", ""); 
//
//								}catch(Exception e) {
//									
//								}
//    			    			st.executeUpdate("INSERT INTO `methods`(`methodname`,  `methodnamerefined`,`methodabbreviation`, `fullmethod`,`classid`, `classname`,`shortclassname`,`method`) VALUES "
//    			    					+ "('"+FullMethodName +"','" +FullMethodNameRefined+"','" +methodabbreviation+"','" +longmeth+"','" +myclassid+"','" +myclassname+"','" +shortclassname
//    			    					+"','" +methodString+"')");
//
//    							
//    			    			mymethodlist.add(meth); 
//    						}
//    						
//    						
//       	
//    					}
//
//    					
//    				
//    				
//    			//}
//    			
//    			
//    		
//    			
//    		
//    			
//    			
//    			
//        	}
//        	
//        	
//        	
//        	for(CtType<?> myinterface : interfaceFactory.getAll(true)) {
//        		Collection<CtMethod<?>> methods = myinterface.getMethods(); 
//
//        		for(CtMethod<?> method: methods) {
//    				 
//        			String myinterfaceid=null; 
//        			String myinterfacename=null; 
//    				String FullMethodName=method.getSignature().toString(); 
//    				System.out.println("==============>"+method.getShortRepresentation().toString());
//    				//st.executeUpdate("INSERT INTO `fields`(`fieldname`) VALUES ('"+field+"');");
//    			//	System.out.println(FullClassName);
//    				String FullMethodNameRefined=FullMethodName.substring(0, FullMethodName.indexOf("(")); 
//    				String longmeth= myinterface.getQualifiedName()+"."+FullMethodName; 
//    				String methodabbreviation=longmeth.substring(0, longmeth.indexOf("(")); 
//    				String inter=myinterface.getQualifiedName(); 
//    				
//    					ResultSet classesreferenced = st.executeQuery("SELECT classes.* from classes where classname='"+inter+"'"); 
//    					System.out.println("INTER"+myinterface.getQualifiedName());
//    					while(classesreferenced.next()){
//    						myinterfaceid= classesreferenced.getString("id"); 
//    						myinterfacename= classesreferenced.getString("classname"); 
//    				//		System.out.println("class referenced: "+clazz);
//    			   		   }
//    				
//    					
//    				
//    				
//    						String fullmeth= myinterfacename+"."+FullMethodName; 
//    						System.out.println(fullmeth);
//    						methods meth= new methods(FullMethodName, myinterfaceid, myinterfacename); 
//    						if(meth.contains(mymethodlist, meth)==false ) {
//								String shortclassname =myinterfacename.substring(myinterfacename.lastIndexOf(".")+1, myinterfacename.length()); 
//								String methodString = ""; 
//								try{
//									 methodString = method.toString(); 
//	    							methodString=methodString.replaceAll("'", ""); 
//
//								}catch(Exception e) {
//									
//								}
//    			    			st.executeUpdate("INSERT INTO `methods`(`methodname`,  `methodnamerefined`,`methodabbreviation`, `fullmethod`,`classid`, `classname`,`shortclassname`,`method`) VALUES "
//    			    					+ "('"+FullMethodName +"','" +FullMethodNameRefined+"','" +methodabbreviation+"','" +longmeth+"','" +myinterfaceid+"','" +myinterfacename+"','" +shortclassname
//    			    					+"','" +methodString+"')");
//
//    							
//    			    			mymethodlist.add(meth); 
//    						}
//    						
//    						
//       	
//    					}
//    			
//    		
//        	}
//        	
//        	
//        	
////    ///////////////////*********************************************************************************************************************************************************************************/	
////    ///////////////////*********************************************************************************************************************************************************************************/	
////    ///////////////////*********************************************************************************************************************************************************************************/   	
////    ////////////////////BUILD METHODSCALLED TABLE
//
//
//
//
//
//
//
//            int counter=0; 
//
//
//            String calleeDeclaringTypeName=null; 
//
//            List<methodcalls> methodcallsList = new ArrayList<methodcalls>(); 
//            for(CtType<?> clazz : classFactory.getAll(true)) {
//            List<CtConstructor> constructorcallers = clazz.getElements(new TypeFilter<CtConstructor>(CtConstructor.class));
//             for(CtConstructor<?> cons :constructorcallers) {
//              	List<CtInvocation> MethodsInvokedByConstructors = cons.getElements(new TypeFilter<CtInvocation>(CtInvocation.class));
//              	for(CtInvocation<?> consInvocation: MethodsInvokedByConstructors) {
//              		String CalleeMethodID=null;  
//              		String CALLEECLASSNAME=null;  
//              		String CALLEECLASSID =null;  
//              		String fullcalleeins=null;   
//              		String CallerMethodIDcons=null; 
//                  	String CALLERCLASSNAMEcons=null; 
//                  	String CALLERCLASSIDcons=null; 
//                  	String fullcallerinscons=null; 
//                  	String fullcaller=null; 
//                  	String fullcallee=null; 
//                  	String InvokedMethodNamePackageFree=null;
//                  	String ConstructorNamePackageFree=null; 
//                  	
//              		if(cons.getDeclaringType()!=null) {
//
//              			
//              		String constructorClassName=cons.getType().getQualifiedName();
//              		String constructorName=cons.getSignature(); 
//              		System.out.println("BEFORE constructorClassName====>"+constructorClassName);
//              		System.out.println("BEFORE constructorName====>"+constructorName);
//              		//System.out.println("CONSTRUCTOR NAME BEFORE INIT "+ constructorName);
//              		
//              		//System.out.println("CONS NAMEeeeeeee====>"+constructorName);	
//              		System.out.println("CONSTRUCTOR NAME BEFORE INIT "+ constructorName);
//              		constructorName=TransformConstructorIntoInit(constructorName); 
//              		System.out.println("AFTER constructorClassName====>"+constructorClassName);	    		
//              		System.out.println("AFTER constructorName====>"+constructorName);
//              		System.out.println("\n");
//              		fullcaller=constructorName; 
//              		 ConstructorNamePackageFree=KeepOnlyMethodName(constructorName);
//              		System.out.println("ConstructorNamePackageFree==ooooooooooooooooooooo==>"+ConstructorNamePackageFree);
//              		System.out.println("constructorClassName==oooooooooooooooooooooooooo==>"+constructorClassName);	   
//              		
//              
//
//
//              		
//              		
//              		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+ConstructorNamePackageFree+"'"
//              				+ "and methods.classname='"+constructorClassName+"'"); 
//              		//while(callingmethodsrefined.next()){
//              		if(callingmethodsrefined.next()) {
//              			CallerMethodIDcons = callingmethodsrefined.getString("id"); 
//              			CALLERCLASSNAMEcons = callingmethodsrefined.getString("classname"); 
//              			CALLERCLASSIDcons = callingmethodsrefined.getString("classid"); 
//              			 fullcallerinscons = callingmethodsrefined.getString("fullmethod"); 
//
//              			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//              		}
//              		System.out.println("CALLER CLASS NAME =======>>>>"+ CALLERCLASSNAMEcons);
//
//              	}
//              		
//              		
//              		if(consInvocation.getExecutable().getDeclaringType()!=null) {
//              			
//              	
//              			
//              			
//              			String InvokedClassNameBEFORE = consInvocation.getExecutable().getDeclaringType().getQualifiedName().toString();
//            	    		String InvokedMethodNameBEFORE=consInvocation.getExecutable().getSignature(); 
//            	    		fullcallee=InvokedMethodNameBEFORE; 
//            	    		System.out.println("BEFORE InvokedClassName====>"+InvokedClassNameBEFORE);
//            	    		System.out.println("BEFORE InvokedMethodName====>"+InvokedMethodNameBEFORE);
////            	    		System.out.println("COOOOOOONS   "+cons.toString());
////            	    		System.out.println("CONSINVOCATION   "+consInvocation.toString());
//            	    		String fullmeth= InvokedClassNameBEFORE+"."+InvokedMethodNameBEFORE; 
//            	    	//	System.out.println("FULLMETH====>"+fullmeth);
//            	    		System.out.println("\n"); 
//            	    		//SUPER CONSTRUCTOR CALLS 
//            	    		if(consInvocation instanceof CtConstructorCall ) {
//            	    			InvokedMethodNameBEFORE=TransformConstructorIntoInit(InvokedMethodNameBEFORE); 
//            	    			 InvokedMethodNamePackageFree=KeepOnlyMethodName(InvokedMethodNameBEFORE); 
//            	    		}
//            	    		if(consInvocation.toString().startsWith("super(")  || consInvocation.toString().startsWith("this(")) {
//            	    			InvokedMethodNameBEFORE=TransformConstructorIntoInit(InvokedMethodNameBEFORE); 
//           	    			 InvokedMethodNamePackageFree=KeepOnlyMethodName(InvokedMethodNameBEFORE); 
//            
//            	    		}
//            	    		
//            	    		
//            	    		
//            	    
//            	    		 InvokedMethodNamePackageFree=KeepOnlyMethodName(InvokedMethodNameBEFORE); 
//            	    	//	System.out.println("InvokedMethodNamePackageFree====>"+InvokedMethodNamePackageFree);
//            	    		
//            	    		
//            	    		 fullmeth= InvokedClassNameBEFORE+"."+InvokedMethodNameBEFORE; 
//            	    	//	System.out.println("FULLMETH====>"+fullmeth);
//              					
//            	    	//	System.out.println("InvokedClassName==oooooooooooooooooooooooo==>"+InvokedClassName);
//            	    	//	System.out.println("InvokedMethodName==ooooooooooooooooooooo==>"+InvokedMethodName);
//            	    		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+InvokedMethodNamePackageFree+"'"
//            	    				+ "and methods.classname='"+InvokedClassNameBEFORE+"'"); 
//            	  
//            	    		//while(callingmethodsrefined.next()){
//            	    		if(callingmethodsrefined.next() && consInvocation.getParent(new TypeFilter<CtMethod>(CtMethod.class))==null) {
//            	    			 CalleeMethodID = callingmethodsrefined.getString("id"); 
//            	    			 CALLEECLASSNAME = callingmethodsrefined.getString("classname"); 
//            	    			 CALLEECLASSID = callingmethodsrefined.getString("classid"); 
//            	    			  fullcalleeins = callingmethodsrefined.getString("fullmethod"); 
//
//            	    			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	    			 
//            	    				
//            	    		}
//            	    		if(CalleeMethodID==null && CALLEECLASSNAME==null && CALLEECLASSID==null && consInvocation.getParent(new TypeFilter<CtMethod>(CtMethod.class))==null) {
//            	    			ResultSet callingmethodsrefined2 = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+InvokedMethodNameBEFORE+"'"
//              	    				+ "and methods.classname='"+InvokedClassNameBEFORE+"'"); 
//              	  
//              	    		//while(callingmethodsrefined.next()){
//              	    		if(callingmethodsrefined2.next()) {
//              	    			 CalleeMethodID = callingmethodsrefined2.getString("id"); 
//              	    			 CALLEECLASSNAME = callingmethodsrefined2.getString("classname"); 
//              	    			 CALLEECLASSID = callingmethodsrefined2.getString("classid"); 
//              	    			  fullcalleeins = callingmethodsrefined2.getString("fullmethod"); 
//
//              	    			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//              	    			 
//              	    				
//              	    		}
//            	    		}
//            	    
//            	    		
//            	    		if(CalleeMethodID==null && CALLEECLASSNAME==null && CALLEECLASSID==null && consInvocation.getParent(new TypeFilter<CtMethod>(CtMethod.class))==null) {
//            	    			String fullmethod=InvokedClassNameBEFORE+"."+InvokedMethodNameBEFORE; 
//            	    			ResultSet callingmethodsrefined2 = st.executeQuery("SELECT methods.* from methods where methods.fullmethod='"+fullmethod+"'"); 
//            	    			
//            	    				//while(callingmethodsrefined.next()){
//            	      	    		if(callingmethodsrefined2.next()) {
//            	      	    			 CalleeMethodID = callingmethodsrefined2.getString("id"); 
//            	      	    			 CALLEECLASSNAME = callingmethodsrefined2.getString("classname"); 
//            	      	    			 CALLEECLASSID = callingmethodsrefined2.getString("classid"); 
//            	      	    			  fullcalleeins = callingmethodsrefined2.getString("fullmethod"); 
//
//            	      	    			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	      	    			 
//            	      	    				
//            	      	    		
//            	    	    		}
//            	    			}
//              	    	
//            	    		
//            	    		
//            	    		
//            	    		
//              		}
//              		
//              		methodcalls methodcall = new methodcalls(CalleeMethodID, fullcaller, CALLEECLASSNAME, CALLEECLASSID, CallerMethodIDcons, fullcallee, CALLERCLASSNAMEcons); 
//              		//System.out.println(methodcall.toString()); 
//              		if( methodcall.contains(methodcallsList, methodcall)==false && CallerMethodIDcons!=null && CalleeMethodID!=null) {
//              			String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodIDcons +"','" +ConstructorNamePackageFree+"','" +CALLERCLASSNAMEcons+"','" +CALLERCLASSIDcons+"','" +fullcallerinscons+"','" +CalleeMethodID+"','" +InvokedMethodNamePackageFree+"','" +CALLEECLASSNAME+"','" +CALLEECLASSID+"','" +fullcalleeins+"')";
//              			
//              			st.executeUpdate(statement);
//              			methodcallsList.add(methodcall); 
//              		}
//              	}
//            	   
//              	
//              	
//              	
//              	
//              	
//              	List<CtConstructorCall> ConstructorsCalledByConstructors = cons.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class));
//              	for(CtConstructorCall<?> consInvocation: ConstructorsCalledByConstructors) {
//              		String CalleeMethodID=null;  
//              		String CALLEECLASSNAME=null;  
//              		String CALLEECLASSID =null;  
//              		String fullcalleeins=null;   
//              		String CallerMethodIDcons=null; 
//                  	String CALLERCLASSNAMEcons=null; 
//                  	String CALLERCLASSIDcons=null; 
//                  	String fullcallerinscons=null; 
//                  	String fullcaller=null; 
//                  	String fullcallee=null; 
//                  	String InvokedMethodNamePackageFree=null;
//                  	String ConstructorNamePackageFree=null; 
//                  	
//              		if(cons.getDeclaringType()!=null) {
////                		String constructorClassName = cons.getExecutable().getDeclaringType().getQualifiedName().toString();
////              		String constructorName=cons.getExecutable().getSignature(); 
//              		String constructorClassName=cons.getType().getQualifiedName();
//              		String constructorName=cons.getSignature(); 
//              		System.out.println("BEFORE constructorClassName====>"+constructorClassName);
//              		System.out.println("BEFORE constructorName====>"+constructorName);
//              		//System.out.println("CONSTRUCTOR NAME BEFORE INIT "+ constructorName);
//              		
//              		//System.out.println("CONS NAMEeeeeeee====>"+constructorName);	
//              		System.out.println("CONSTRUCTOR NAME BEFORE INIT "+ constructorName);
//              		constructorName=TransformConstructorIntoInit(constructorName); 
//              		//System.out.println("constructorClassName====>"+constructorClassName);	    		
//              		//System.out.println("constructorName====>"+constructorName);
//              		System.out.println("\n");
//              		fullcaller=constructorName; 
//              		 ConstructorNamePackageFree=KeepOnlyMethodName(constructorName);
//              		System.out.println("ConstructorNamePackageFree==ooooooooooooooooooooo==>"+ConstructorNamePackageFree);
//              		System.out.println("constructorClassName==oooooooooooooooooooooooooo==>"+constructorClassName);	   
//              		
//              		
//              		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+ConstructorNamePackageFree+"'"
//              				+ "and methods.classname='"+constructorClassName+"'"); 
//              		//while(callingmethodsrefined.next()){
//              		if(callingmethodsrefined.next()) {
//              			CallerMethodIDcons = callingmethodsrefined.getString("id"); 
//              			CALLERCLASSNAMEcons = callingmethodsrefined.getString("classname"); 
//              			CALLERCLASSIDcons = callingmethodsrefined.getString("classid"); 
//              			 fullcallerinscons = callingmethodsrefined.getString("fullmethod"); 
//
//              			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//              		}
//              		}
//              		
//              		
//              		if(consInvocation.getExecutable().getDeclaringType()!=null) {
//              			String InvokedClassNameBEFORE = consInvocation.getExecutable().getDeclaringType().getQualifiedName().toString();
//            	    		String InvokedMethodNameBEFORE=consInvocation.getExecutable().getSignature(); 
//            	    		fullcallee=InvokedMethodNameBEFORE; 
//            	    		System.out.println("BEFORE InvokedClassName====>"+InvokedClassNameBEFORE);
//            	    		System.out.println("BEFORE InvokedMethodName====>"+InvokedMethodNameBEFORE);
//            	    		
//            	    	//	System.out.println("InvokedClassName====>"+InvokedClassName);
//            	    	//	System.out.println("InvokedMethodName====>"+InvokedMethodName);
//            	    		String fullmeth= InvokedClassNameBEFORE+"."+InvokedMethodNameBEFORE; 
//            	    	//	System.out.println("FULLMETH====>"+fullmeth);
//            	    		System.out.println("\n");
//            	    		if(consInvocation instanceof CtConstructorCall) {
//            	    			InvokedMethodNameBEFORE=TransformConstructorIntoInit(InvokedMethodNameBEFORE); 
//            	    			 InvokedMethodNamePackageFree=KeepOnlyMethodName(InvokedMethodNameBEFORE); 
//            	    		}
//            	    		 InvokedMethodNamePackageFree=KeepOnlyMethodName(InvokedMethodNameBEFORE); 
//            	    	//	System.out.println("InvokedMethodNamePackageFree====>"+InvokedMethodNamePackageFree);
//            	    		
//            	    		
//            	    		 fullmeth= InvokedClassNameBEFORE+"."+InvokedMethodNameBEFORE; 
//            	    	//	System.out.println("FULLMETH====>"+fullmeth);
//              					
//            	    	//	System.out.println("InvokedClassName==oooooooooooooooooooooooo==>"+InvokedClassName);
//            	    	//	System.out.println("InvokedMethodName==ooooooooooooooooooooo==>"+InvokedMethodName);
//            	    		ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+InvokedMethodNamePackageFree+"'"
//            	    				+ "and methods.classname='"+InvokedClassNameBEFORE+"'"); 
//            	  
//            	    		//while(callingmethodsrefined.next()){
//            	    		if(callingmethodsrefined.next() && consInvocation.getParent(new TypeFilter<CtMethod>(CtMethod.class))==null) {
//            	    			 CalleeMethodID = callingmethodsrefined.getString("id"); 
//            	    			 CALLEECLASSNAME = callingmethodsrefined.getString("classname"); 
//            	    			 CALLEECLASSID = callingmethodsrefined.getString("classid"); 
//            	    			  fullcalleeins = callingmethodsrefined.getString("fullmethod"); 
//
//            	    			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	    			 
//            	    				
//            	    		}
//            	    		if(CalleeMethodID==null && CALLEECLASSNAME==null && CALLEECLASSID==null ) {
//            	    			ResultSet callingmethodsrefined2 = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+InvokedMethodNameBEFORE+"'"
//              	    				+ "and methods.classname='"+InvokedClassNameBEFORE+"'"); 
//              	  
//              	    		//while(callingmethodsrefined.next()){
//              	    		if(callingmethodsrefined2.next() && consInvocation.getParent(new TypeFilter<CtMethod>(CtMethod.class))==null) {
//              	    			 CalleeMethodID = callingmethodsrefined2.getString("id"); 
//              	    			 CALLEECLASSNAME = callingmethodsrefined2.getString("classname"); 
//              	    			 CALLEECLASSID = callingmethodsrefined2.getString("classid"); 
//              	    			  fullcalleeins = callingmethodsrefined2.getString("fullmethod"); 
//
//              	    			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//              	    			 
//              	    				
//              	    		}
//            	    		}
//            	    
//            	    		
//            	    		
//              		}
//              	
//              		
//              		
//              		methodcalls methodcall = new methodcalls(CalleeMethodID, fullcaller, CALLEECLASSNAME, CALLEECLASSID, CallerMethodIDcons, fullcallee, CALLERCLASSNAMEcons); 
//              		//System.out.println(methodcall.toString()); 
//              		if( methodcall.contains(methodcallsList, methodcall)==false && CallerMethodIDcons!=null && CalleeMethodID!=null) {
//              			String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodIDcons +"','" +ConstructorNamePackageFree+"','" +CALLERCLASSNAMEcons+"','" +CALLERCLASSIDcons+"','" +fullcallerinscons+"','" +CalleeMethodID+"','" +InvokedMethodNamePackageFree+"','" +CALLEECLASSNAME+"','" +CALLEECLASSID+"','" +fullcalleeins+"')";
//              			
//              			st.executeUpdate(statement);
//              			methodcallsList.add(methodcall); 
//              		}
//              	}
//            	   
//            	   
//            	   
//             }
//             
//             
//             
//            for(CtMethod<?> method :clazz.getMethods()) {
//            List<CtConstructorCall> ctNewClasses = method.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class));
//           
//            System.out.println(method.getSignature());
//            List<CtReturn> returnstatement = method.getElements(new TypeFilter<CtReturn>(CtReturn.class));
////            System.out.println("returnstatement.toString()  "+returnstatement.toString());
//            for(CtReturn myret: returnstatement) {
//                List<CtConstructorCall> constructorcallsWithinReturn = myret.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class));
//              
//                if(!constructorcallsWithinReturn.isEmpty()) {
//                	if(constructorcallsWithinReturn.get(0)!=null) {
//                		if(constructorcallsWithinReturn.get(0).getExecutable()!=null) {
//                			 CtTypeReference type = constructorcallsWithinReturn.get(0).getExecutable().getType(); 
//                             System.out.println(type);
//                             
//                           String params=  constructorcallsWithinReturn.get(0).getExecutable().toString().substring(constructorcallsWithinReturn.get(0).getExecutable().toString().indexOf("(")); 
//                            String methodname=constructorcallsWithinReturn.get(0).getExecutable().toString().substring(0,constructorcallsWithinReturn.get(0).getExecutable().toString().indexOf("(")); 
//                           String Fullcallee= methodname+".-init-"+params; 
//                           
//                           
//                       
//                    	String FullCallerMeth=clazz.getQualifiedName()+"."+method.getSignature(); 
//                    	String	CallerMethodIDcons =null; 
//                    	String CALLERCLASSNAMEcons =null; 
//                    	String	CALLERCLASSIDcons =null; 
//                    	String fullcallerinscons =null; 
//                       	ResultSet  caller = st.executeQuery("SELECT methods.* from methods where methods.fullmethod='"+FullCallerMeth+"'"); 
//                    	//while(callingmethodsrefined.next()){
//                    	if(caller.next()) {
//                    			CallerMethodIDcons = caller.getString("id"); 
//                    		 CALLERCLASSNAMEcons = caller.getString("classname"); 
//                    			CALLERCLASSIDcons = caller.getString("classid"); 
//                    		 fullcallerinscons = caller.getString("fullmethod"); 
//
//                    		//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//                    	}
//                    	
//                    	
//                        ResultSet callee = st.executeQuery("SELECT methods.* from methods where methods.fullmethod='"+Fullcallee+"'"); 
//    					//while(callingmethodsrefined.next()){
//                       	if(callee.next()) {
//                       		String CalleeMethodIDcons = callee.getString("id"); 
//                       		String CALLEECLASSNAMEcons = callee.getString("classname"); 
//                       		String CALLEECLASSIDcons = callee.getString("classid"); 
//                       		String fullcalleeinscons = callee.getString("fullmethod"); 
//
//                       		//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//                       		String constructorName="-init-"+params; 
//                       		String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodIDcons +"','" +method.getSignature()+"','" +CALLERCLASSNAMEcons+"','" +CALLERCLASSIDcons+"','" +fullcallerinscons+"','" +CalleeMethodIDcons+"','" +constructorName+"','" +CALLEECLASSNAMEcons+"','" +CALLEECLASSIDcons+"','" +fullcalleeinscons+"')";
//                       		
//                       		st.executeUpdate(statement);
//                       	
//                       	}
//                       
//                       	
//                		}
//                	
//                	}
//                   
//                }
//                
//                 
//            }
//             System.out.println("yes");
//             
//             
//            for( CtConstructorCall myclazz: ctNewClasses) {
//            	//CONSTRUCTOR 
//            	
//            	String CallerMethodIDcons=null; 
//            	String CALLERCLASSNAMEcons=null; 
//            	String CALLERCLASSIDcons=null; 
//            	
//            	String CalleeMethodIDcons=null; 
//            	String CALLEECLASSNAMEcons=null; 
//            	String CALLEECLASSIDcons=null; 
//            	String fullcallerinscons=null; 
//            	String fullcalleeinscons=null; 
//            	String constructorClassName=null; 
//            	String callerclass=myclazz.getExecutable().getDeclaringType().getQualifiedName();
//
//            		constructorClassName= myclazz.getExecutable().getDeclaringType().getQualifiedName();
//            	
//            		
//            		
//            	System.out.println("MYCLASS"+ clazz.getQualifiedName() +"."+method.getSignature()+"  METHOD"+ myclazz.getExecutable().getSignature()+
//            			"CLASSS    "+
//            			myclazz.getExecutable().getDeclaringType().getQualifiedName());
//            	String classtype= myclazz.getExecutable().getDeclaringType().getQualifiedName();
//            	String FullCallerMeth=clazz.getQualifiedName()+"."+method.getSignature(); 
//            	
//            	String constructorName=myclazz.getExecutable().getSignature();
//            	System.out.println("CONSTRUCTOR AS CALLEE NAME "+ constructorName);
//            	//String constructorClassName= clazz.getExecutable().getDeclaringType().getQualifiedName();
//            	constructorName="-init-"+constructorName.substring(constructorName.indexOf("("), constructorName.length()); 
//            	//System.out.println("CONSTRUCTOR NAME "+ constructorName);
//            	System.out.println("CONSTRUCTOR AS CALLEE CLASS NAME"+ constructorClassName);
//            	
//            	//System.out.println("CONSTRUCTOR CLASS NAME"+ constructorClassName);
//            	
////            	ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+constructorName+"'"
////            			+ "and methods.classname='"+constructorClassName+"'"); 
//            	
//            	ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+constructorName+"'"
//            			+ "and methods.classname='"+classtype+"'"); 
//            	//while(callingmethodsrefined.next()){
//            	if(callingmethodsrefined.next()) {
//            		CalleeMethodIDcons = callingmethodsrefined.getString("id"); 
//            		CALLEECLASSNAMEcons = callingmethodsrefined.getString("classname"); 
//            		CALLEECLASSIDcons = callingmethodsrefined.getString("classid"); 
//            		 fullcalleeinscons = callingmethodsrefined.getString("fullmethod"); 
//
//            		//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	}
//            	if(CalleeMethodIDcons==null && myclazz.getType().getQualifiedName().toString().contains("$")) {
//            		String EditedConsClassName= myclazz.getType().getQualifiedName().toString().substring(0, myclazz.getType().getQualifiedName().toString().lastIndexOf("."))+"."+myclazz.getType().getQualifiedName().toString().substring(myclazz.getType().getQualifiedName().toString().lastIndexOf("$")+1, myclazz.getType().getQualifiedName().toString().length());
//
//            		 callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+constructorName+"'"
//            				+ "and methods.classname='"+EditedConsClassName+"'"); 
//            		//while(callingmethodsrefined.next()){
//            		if(callingmethodsrefined.next()) {
//            			CalleeMethodIDcons = callingmethodsrefined.getString("id"); 
//            			CALLEECLASSNAMEcons = callingmethodsrefined.getString("classname"); 
//            			CALLEECLASSIDcons = callingmethodsrefined.getString("classid"); 
//            			 fullcalleeinscons = callingmethodsrefined.getString("fullmethod"); 
//
//            			//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            		}
//            		
//            	}
//            	
//            callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.fullmethod='"+FullCallerMeth+"'"); 
//            	//while(callingmethodsrefined.next()){
//            	if(callingmethodsrefined.next()) {
//            		CallerMethodIDcons = callingmethodsrefined.getString("id"); 
//            		CALLERCLASSNAMEcons = callingmethodsrefined.getString("classname"); 
//            		CALLERCLASSIDcons = callingmethodsrefined.getString("classid"); 
//            		 fullcallerinscons = callingmethodsrefined.getString("fullmethod"); 
//
//            		//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	}
//            	
//            	
//            	//System.out.println("FULL CALLER INS CONS"+fullcallerinscons);
//            	//System.out.println("FULL CALLEE INS CONS"+fullcalleeinscons);
//            	methodcalls methodcall = new methodcalls(CalleeMethodIDcons, fullcalleeinscons, CALLEECLASSNAMEcons, CALLEECLASSIDcons, CallerMethodIDcons, fullcallerinscons, CALLERCLASSNAMEcons); 
//            	//System.out.println(methodcall.toString()); 
//            	if( methodcall.contains(methodcallsList, methodcall)==false && CallerMethodIDcons!=null && CalleeMethodIDcons!=null) {
//            		String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodIDcons +"','" +method.getSignature()+"','" +CALLERCLASSNAMEcons+"','" +CALLERCLASSIDcons+"','" +fullcallerinscons+"','" +CalleeMethodIDcons+"','" +constructorName+"','" +CALLEECLASSNAMEcons+"','" +CALLEECLASSIDcons+"','" +fullcalleeinscons+"')";
//            		
//            		st.executeUpdate(statement);
//            		methodcallsList.add(methodcall); 
//            	}
//            	
//            	
////            		List args = (clazz.getExecutable().getArguments());
//            	
////            	System.out.println("hEYYYYYY"+args.toString());
//            	
//            	
//            	List list = myclazz.getArguments();
//            	
//            	//System.out.println("LIST "+ list);
//            	
//            	for(Object elem: list) {
//            		
//            		if(elem instanceof CtInvocation) {
//            			
//            			 CtExecutableReference elemexec = ((CtInvocation) elem).getExecutable(); 
////            			System.out.println("ELEM"+elem);
////            			System.out.println("EXEC"+elemexec);
//            			if(elemexec.getDeclaringType()!=null) {
//            				String targetType=elemexec.getDeclaringType().getQualifiedName(); 	
//            			}
//            			
//            			
//            			
//            			
//            			
//            			  CtExpression targ = ((CtInvocation) elem).getTarget(); 
//            				if(targ instanceof CtInvocation) {
//            					CtExecutableReference targex = ((CtInvocation) targ).getExecutable(); 
////            					System.out.println("TARG"+targex);
//            					if(targex.getDeclaringType()!=null) {
//            						String executableType=targex.getDeclaringType().getQualifiedName(); 
//
//            					}
//            					
//            					
//            					CtExpression targetoftarget = ((CtTargetedExpression) targ).getTarget(); 
//            					while(!targetoftarget.toString().equals("") && targetoftarget instanceof CtInvocation==true ) {
//            						
//            						
////            						System.out.println("TARGET OF TARGET: "+targetoftarget);
//            						if(targetoftarget instanceof CtInvocation<?> ) {
//            							targetoftarget=((CtInvocation<?>) targetoftarget).getTarget(); 
//
//            						}
//            						else if(targetoftarget instanceof CtConstructorCall<?>) {
//            							targetoftarget=((CtConstructorCall<?>) targetoftarget).getTarget(); 
//            						}
//            						else if(targetoftarget instanceof CtFieldAccess<?>) {
//            							targetoftarget=((CtFieldAccess<?>) targetoftarget).getTarget(); 
//            						}else if(targetoftarget instanceof CtField<?>) {
//            							targetoftarget=((CtFieldAccess<?>) targetoftarget).getTarget(); 
//            						}
//            						
//            						String targetoftargetType=targex.getDeclaringType().getQualifiedName(); 
//            						
//            					}
//            				}
////            			if(elemtarg==null) {
////            				System.out.println("ELEM"+elem);
////            			}
////            			while(elemtarg!=null) {
////            				
////            				elemtarg = ((CtInvocation<?>) elemtarg).getTarget(); 
////            				System.out.println("ELEM TARG: "+elemtarg);
////            			}
//            			
//            		}else if(elem instanceof CtFieldAccess) {
//            			//System.out.println("ELEMFILEDACCESS"+elem);
//            		}
//            	}
//            	
//            }
//
//
//            String methname=method.getSimpleName(); 
//            //System.out.println("CALLER METHOD=====>"+methname);
//            // List<CtInvocation> methodcalls = Query.getElements(method, new TypeFilter<>(CtInvocation.class)); 
//            List<CtInvocation> methodcalls = method.getElements(new TypeFilter<>(CtInvocation.class)); 
//            for( CtInvocation invocation: methodcalls) {
//            	String callingmethodid=null; 
//            	String callingmethodsrefinedid=null; 
//            	String callingmethodsrefinedname=null; 
//            	String callingmethodclass=null; 
//            	String calledmethodid=null; 
//            	String calledmethodname=null; 
//            	String calledmethodclass=null; 
//            	String paramclassid=null; 
//            	String CALLEEID=null; 
//            	String CALLEECLASSNAME=null; 
//            	String CALLEECLASSID=null; 
//            	String CALLERCLASSID=null; 
//            	String CallerMethodID=null; 
//            	//CALLING METHOD ID 
//            	String CALLEENAME= invocation.getExecutable().getSignature().toString(); 
//            	CtExecutableReference<?> executableRef = invocation.getExecutable();
//            	CtTypeReference<?> typeRef = executableRef.getDeclaringType();
//            		
//            	String CALLERCLASSNAME=clazz.getQualifiedName() ; 
//            	String CallerMethod= method.getSignature(); 
//            	//System.out.println("CALLER METHOD NAME: "+ CallerMethod);
//            	//System.out.println("CALLER CLASS  NAME : "+ CALLERCLASSNAME);
//            	ResultSet callingmethodsrefined3 = st.executeQuery("SELECT methods.id from methods where methods.methodname='"+CallerMethod+"'and methods.classname='"+CALLERCLASSNAME+"'"); 
//            	//while(callingmethodsrefined.next()){
//            	if(callingmethodsrefined3.next()) {
//            		CallerMethodID = callingmethodsrefined3.getString("id"); 
//            	//	System.out.println("CALLER METHOD ID: "+ CallerMethodID);
//            	}
//            	String fullcallerins=null; 
//            	ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+CallerMethod+"'and methods.classname='"+CALLERCLASSNAME+"'"); 
//            	//while(callingmethodsrefined.next()){
//            	if(callingmethodsrefined.next()) {
//            		CallerMethodID = callingmethodsrefined.getString("id"); 
//            		CALLERCLASSNAME = callingmethodsrefined.getString("classname"); 
//            		CALLERCLASSID = callingmethodsrefined.getString("classid"); 
//            		 fullcallerins = callingmethodsrefined.getString("fullmethod"); 
//
//            		//System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            	}
//            	
//            	
//            	
//            	
//            	
////            	System.out.println("CALLEE METHOD NAME: "+ CALLEENAME);
//            	if(typeRef!=null) {
//            		String methodCalleeClassName=typeRef.getQualifiedName();
//            	//	System.out.println("METHOD CALLEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE: "+methodCalleeClassName);
//            		//ResultSet callingmethodsrefined = st.executeQuery("SELECT methods.id from methods INNER JOIN classes on methods.classname=classes.classname where methods.methodname='"+CalledMethodExecutable+"' and classes.classname='"+  ClassQualifiedName +"'"); 
//            	
//            		ResultSet callingmethodsrefined2 = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+CALLEENAME+"'and methods.classname='"+methodCalleeClassName+"'"); 
//            		//while(callingmethodsrefined.next()){
//            		if(callingmethodsrefined2.next()) {
//            			CALLEECLASSNAME = callingmethodsrefined2.getString("classname"); 
//            			CALLEECLASSID = callingmethodsrefined2.getString("classid"); 
//            			CALLEEID = callingmethodsrefined2.getString("id"); 
//            			
//              		String fullcalleeins=null; 
//            			 fullcalleeins = callingmethodsrefined2.getString("fullmethod"); 
////            			System.out.println("CALLEE METHOD ID: "+ CALLEEID);
//            			//System.out.println("CALLEE CLASS NAME: "+ CALLEECLASSNAME);
//            			
//            			CALLEENAME= invocation.getExecutable().getSignature().toString(); 
//            			String fullcaller= CALLERCLASSNAME+"."+CallerMethod; 
//            			String fullcallee= CALLEECLASSNAME+"."+CALLEENAME; 
//            			methodcalls methodcall= new methodcalls(CALLEEID, fullcalleeins, CALLEECLASSNAME, CALLEECLASSID, CallerMethodID, fullcallerins, CALLERCLASSNAME); 
//            			//
//            			//System.out.println("======>"+methodcall.toString()); 
//            	//		System.out.println("FULL CALLER"+fullcallerins);
//            	//		System.out.println("FULL CALLEE"+fullcalleeins);
//            			if( methodcall.contains(methodcallsList, methodcall)==false && CallerMethodID!=null && CALLEEID!=null) {
//            				
//            				String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodID +"','" +CallerMethod+"','" +CALLERCLASSNAME+"','" +CALLERCLASSID+"','" +fullcallerins+"','" +CALLEEID+"','" +CALLEENAME+"','" +CALLEECLASSNAME+"','" +CALLEECLASSID+"','" +fullcalleeins+"')";
//            				
//            				st.executeUpdate(statement);
//            				methodcallsList.add(methodcall); 
//            			}
//            		}
//            	}
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	
//            	CtExpression<?> invocationTarget = invocation.getTarget(); 
//            	
//            	boolean  fieldaccesssflag=false; 
//            	while(invocationTarget!=null ) {
//            	//	String CALLEENAMETARGET= invocationTarget.toString(); 
//            	//	System.out.println("TARGET: "+ CALLEENAMETARGET);
//            		String NameCallee=null; 
//            		if(invocationTarget instanceof CtInvocation<?>) {
//            			//System.out.println("Invocation");
//            			
//            			List args = ((CtInvocation) invocationTarget).getArguments(); 
//            			
//            		//	System.out.println("hEYYYYYY"+args.toString());
//            			for(Object elem: args) {
//            			//	System.out.println("hEYYYYYY"+elem.toString());
//            			}
//            			
//            			
//            			
//            			String calleeName = ((CtInvocation) invocationTarget).getExecutable().getSignature();
//            		//	System.out.println("CALLEE NAME"+calleeName);
//            		//	System.out.println(((CtInvocation) invocationTarget).getExecutable());
//            			if((((CtInvocation) invocationTarget).getExecutable().getDeclaringType())!=null) {
//            				 calleeDeclaringTypeName = ((CtInvocation) invocationTarget).getExecutable().getDeclaringType().getQualifiedName(); 
//            		//		System.out.println("CALLEE type"+calleeDeclaringTypeName);
//            			}
//            			
//            			List<CtParameter<?>> myparams = ((CtInvocation) invocationTarget).getExecutable().getParameters(); 
//            			ResultSet callingmethodsrefined2 = st.executeQuery("SELECT methods.* from methods where methods.methodname='"+calleeName+"'and methods.classname='"+calleeDeclaringTypeName+"'"); 
//            			//while(callingmethodsrefined.next()){
//            			 CALLEENAME= invocation.getExecutable().getSignature().toString(); 
//            				
//            				
//            			if(callingmethodsrefined2.next()) {
//            				NameCallee = callingmethodsrefined2.getString("methodname"); 
//            				CALLEECLASSNAME = callingmethodsrefined2.getString("classname"); 
//            				CALLEECLASSID = callingmethodsrefined2.getString("classid"); 
//            				CALLEEID = callingmethodsrefined2.getString("id"); 
//            				String fullcalleeins = callingmethodsrefined2.getString("fullmethod"); 
//            				String fullcallee= CALLEECLASSNAME+"."+calleeName; 
//            				String fullcaller= CALLERCLASSNAME+"."+CallerMethod; 
//            			
//            				System.out.println("CALLEE  NAME:  "+ NameCallee);
//            				System.out.println("CALLEE CLASS NAME:  "+ CALLEECLASSNAME);
//            				System.out.println("CALLEECLASSID:  "+ CALLEECLASSID);
//            				System.out.println("CALLEEID:  "+ CALLEEID);
//            				System.out.println("fullcalleeins:  "+ fullcalleeins);
//            				System.out.println("fullcallee:  "+ fullcallee);
//            				System.out.println("fullcaller:  "+ fullcaller);
//            				System.out.println("\n");
//            				methodcalls methodcall = new methodcalls(CALLEEID, fullcalleeins, CALLEECLASSNAME, CALLEECLASSID, CallerMethodID, fullcallerins, CALLERCLASSNAME); 
//            				//System.out.println(methodcall.toString()); 
//            				if( methodcall.contains(methodcallsList, methodcall)==false && CallerMethodID!=null && CALLEEID!=null) {
//            					String statement = "INSERT INTO `methodcalls`(`callermethodid`,  `callername`,  `callerclass`, `callerclassid`,`fullcaller`,`calleemethodid`,  `calleename`, `calleeclass`,  `calleeclassid`,  `fullcallee`) VALUES ('"+CallerMethodID +"','" +CallerMethod+"','" +CALLERCLASSNAME+"','" +CALLERCLASSID+"','" +fullcallerins+"','" +CALLEEID+"','" +NameCallee+"','" +CALLEECLASSNAME+"','" +CALLEECLASSID+"','" +fullcalleeins+"')";
//            					
//            					st.executeUpdate(statement);
//            					methodcallsList.add(methodcall); 
//            				}
//            				
//            		}
//            		
//            			invocationTarget=((CtInvocation<?>) invocationTarget).getTarget(); 
//            	}	
//            		else if(invocationTarget instanceof CtFieldAccess<?>) {
//            		fieldaccesssflag=true; 
//            		//System.out.println("Field Access");
//            		invocationTarget=((CtFieldAccess<?>) invocationTarget).getTarget(); 
//            	}else  {
//            		
//            		invocationTarget=null; 
//            	}
//
//            	}
//            	
//            	
//            	
//
//            	
//            	
//
//              
//            	
//
//
//            	
//            }
//            }
//
//
//
//
//
//            }      

////
////    /////////////*********************************************************************************************************************************************************************************/	
////    /////////////*********************************************************************************************************************************************************************************/	
////    /////////////*********************************************************************************************************************************************************************************/   
////    //////////////CREATE REQUIREMENTS TABLE 
////    ////////////
////    //file = new File("C:\\Users\\mouna\\new_workspace\\TracePredictor\\src\\GanttFiles\\RequirementsGantt.txt");
////    //fileReader = new FileReader(file);
////    //bufferedReader = new BufferedReader(fileReader);
////    //stringBuffer = new StringBuffer();
////    //
////    //
////    //try {
////    //
////    //
////    //while ((line = bufferedReader.readLine()) != null) {
////    //System.out.println(line);
////    //
////    //
////    //
////    //
////    //
////    //String statement = "INSERT INTO `requirements`(`requirementname`) VALUES ('"+line+"')";		
////    //st.executeUpdate(statement);
////    //
////    //
////    //
////    //}
////    //
////    //
////    //
////    //
////    //}
////    //
////    //catch (IOException e) {
////    //// TODO Auto-generated catch block
////    //e.printStackTrace();
////    //}
////    //////////////
////   
////
////
////
////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
 //TRACES TABLE 
//
//HashMap<String, String> RequirementIDNameHashMap=new HashMap<String, String> (); 
//RequirementIDNameHashMap.put("1", "UC1"); 
//RequirementIDNameHashMap.put("2", "UC2"); 
//RequirementIDNameHashMap.put("3", "UC3"); 
//RequirementIDNameHashMap.put("4", "UC4"); 
//RequirementIDNameHashMap.put("5", "UC5"); 
//RequirementIDNameHashMap.put("6", "UC6"); 
//RequirementIDNameHashMap.put("7", "UC8"); 
//RequirementIDNameHashMap.put("8", "UC9"); 
//RequirementIDNameHashMap.put("9", "UC10"); 
//RequirementIDNameHashMap.put("10", "UC11"); 
//RequirementIDNameHashMap.put("11", "UC12"); 
//RequirementIDNameHashMap.put("12", "UC13"); 
//RequirementIDNameHashMap.put("13", "UC15"); 
//RequirementIDNameHashMap.put("14", "UC16"); 
//RequirementIDNameHashMap.put("15", "UC17"); 
//RequirementIDNameHashMap.put("16", "UC18"); 
//RequirementIDNameHashMap.put("17", "UC19"); 
//RequirementIDNameHashMap.put("18", "UC21"); 
//RequirementIDNameHashMap.put("19", "UC23"); 
//RequirementIDNameHashMap.put("20", "UC24"); 
//RequirementIDNameHashMap.put("21", "UC25"); 
//RequirementIDNameHashMap.put("22", "UC26"); 
//RequirementIDNameHashMap.put("23", "UC27"); 
//RequirementIDNameHashMap.put("24", "UC28"); 
//RequirementIDNameHashMap.put("25", "UC29"); 
//RequirementIDNameHashMap.put("26", "UC30"); 
//RequirementIDNameHashMap.put("27", "UC31"); 
//RequirementIDNameHashMap.put("28", "UC32"); 
//RequirementIDNameHashMap.put("29", "UC33"); 
//RequirementIDNameHashMap.put("30", "UC34"); 
//RequirementIDNameHashMap.put("31", "UC35"); 
//RequirementIDNameHashMap.put("32", "UC36"); 
//RequirementIDNameHashMap.put("33", "UC37"); 
//RequirementIDNameHashMap.put("34", "UC38"); 
//List<String> mylistTrace = new ArrayList<String>();
//
//try {
//	File file = new File("C:\\Users\\mouna\\new_workspace\\TracePredictor\\src\\iTrustFiles\\itrust_vote_dev.txt");
//	FileReader fileReader = new FileReader(file);
//	BufferedReader bufferedReader = new BufferedReader(fileReader);
//	
//	String line;
//	line = bufferedReader.readLine(); 
//	HashMap<String,SubjectTSubjectNObject> mylist= new HashMap<String,SubjectTSubjectNObject>(); 
//
//	while ((line = bufferedReader.readLine()) != null) {
//		String[] splittedline = line.split(",", -1); 
//		int counter = 1; 
//		for(int t=1; t<splittedline.length; t++) {
//			SubjectTSubjectNObject SubjectTSubjectNObj = new SubjectTSubjectNObject(); 
//			String methodname= splittedline[0]; 
//			methodname=methodname.replaceAll("::", "."); 
////			System.out.println(methodname);
//			//methodname=methodname.replaceAll("constructor", "-init-"); 
//			//methodname=PredictionPattern.compile("[{}<>]").matcher(methodname).replaceAll("");
//		
//			String RequirementID= ""+counter;
//			String val=splittedline[t];
//			if(splittedline[t].equals("")) {
//				SubjectTSubjectNObj.setGoldfinal("N");
//			}
//			else {
//				SubjectTSubjectNObj.setGoldfinal("T");
//			}
//			SubjectTSubjectNObj.setMethodName(methodname);
//			SubjectTSubjectNObj.setRequirementID(RequirementID);
//			
//			String mykey=RequirementID+"-"+methodname; 
//			mylist.put(mykey, SubjectTSubjectNObj); 
//			counter++; 
//			mylistTrace.add(mykey); 
//		}
//	
//	}
//ResultSet mymeths = st2.executeQuery("SELECT methods.* from methods"); 
// i=1; 
//while(mymeths.next()){
//	String methodid = mymeths.getString("id"); 
//	String method = mymeths.getString("methodabbreviation"); 
//	String methodname = mymeths.getString("methodname"); 
//	String fullmethod = mymeths.getString("fullmethod"); 
//	
//	String classname = mymeths.getString("classname"); 
//	String classid = mymeths.getString("classid"); 
//	
//	 List<tracesmethods> TraceListMethods= new ArrayList<tracesmethods>();
//
//	
//	for(String key: RequirementIDNameHashMap.keySet()) {
//		System.out.println("here");
//		tracesmethods tr= new tracesmethods(key, methodid,  classid); 
//		System.out.println("here 2");
//
//		if(!tr.contains(TraceListMethods, tr)) {
//			System.out.println("here 3");
//			String shortmethod=classname.substring(classname.lastIndexOf(".")+1, classname.length())+method.substring(method.lastIndexOf(".")); 
//			System.out.println("here 4");
//
//			SubjectTSubjectNObject obj = mylist.get(tr.getRequirementid()+"-"+shortmethod); 
//			System.out.println("here 5");
//
//			System.out.println(tr.getRequirementid()+"-"+shortmethod);
//			System.out.println("here 6");
//
//			if(obj!=null) {
//				String statement = "INSERT INTO `traces`(`requirement`, `requirementid`, `method`, `methodname`, `fullmethod`,  `methodid`,`classname`, `classid`, `shortmethodname`,`goldfinal`) VALUES ('"+RequirementIDNameHashMap.get(tr.getRequirementid())+"','" +tr.getRequirementid()+"','" +method+"','" +methodname+"','" +fullmethod+"','" +methodid+"','"+classname +"','" +classid+"','"+ shortmethod+"','"+ obj.getGoldfinal() +"')";
//				st.executeUpdate(statement);
//				mylistTrace.remove(tr.getRequirementid()+"-"+shortmethod); 
//				System.out.println("here 7");
//
//			}
//			else {
//				String statement = "INSERT INTO `traces`(`requirement`, `requirementid`, `method`, `methodname`, `fullmethod`,  `methodid`,`classname`, `classid`, `shortmethodname`,`goldfinal`) VALUES ('"+RequirementIDNameHashMap.get(tr.getRequirementid())+"','" +tr.getRequirementid()+"','" +method+"','" +methodname+"','" +fullmethod+"','" +methodid+"','"+classname +"','" +classid+"','"+ shortmethod+"','"+ "E" +"')";
//				System.out.println(statement);
//				try {
//					st.executeUpdate(statement);
//
//				}catch(Exception e) {
//					System.out.println(e);
//				}
//				System.out.println(statement);
//				System.out.println("here 8");
//
//			}
//			
//		}
//	}
//	
//i++; 
//System.out.println(i);
//System.out.println("here 9");
//
//}
//}
//
//catch(Exception ex) {
//	
//}


//for(String entry: mylistTrace) {
//	System.out.println(entry);
//}
//System.out.println("OVER");
//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
////TRACES CLASSES TABLE 
//  fileReader = new FileReader("C:\\Users\\mouna\\new_workspace\\TracePredictor\\src\\iTrustFiles\\TracesClassesNEW.txt");
// List<String> TraceClassList= new ArrayList<String>(); 
// HashMap<String,  String> ReqClassHashMap= new HashMap<String,  String> (); 
//
//	  bufferedReader = new BufferedReader(fileReader);
//	
//	 line = null; 
//	line = bufferedReader.readLine(); 
// String[] requirements = line.split(","); 
// while((line = bufferedReader.readLine()) != null) {
////     System.out.println(line);
// 	 String[] splitted = line.split("\\,", -1);
//     
//     for(int r=1; r<splitted.length; r++) {
//     	if(splitted[r].equals("x")) {
//     		ReqClassHashMap.put(r+"-"+splitted[0], "T"); 
//     	}else {
//     		ReqClassHashMap.put(r+"-"+splitted[0], "N"); 
//     	}
//     	TraceClassList.add(r+"-"+splitted[0]); 
//     }
////     System.out.println(line);
//     
// }   
//
// // Always close files.
// bufferedReader.close();         
//HashMap <String, String > RequirementClassHashMap= new HashMap <String, String > (); 
//
//String classname=""; 
//String classid=""; 
//String requirementname=""; 
//String requirementid="";
//ResultSet Traces = st.executeQuery("SELECT classes.* from classes "); 
//while(Traces.next()){
//classname = Traces.getString("classname"); 
//classid = Traces.getString("id"); 
//			for(String keyreq: RequirementIDNameHashMap.keySet()) {
//				String key= keyreq+"-"+classid; 
//				String val= keyreq+"-"+RequirementIDNameHashMap.get(keyreq)+"-"+classid+"-"+classname; 
//
//				RequirementClassHashMap.put(key, val); 
//			}
//
//
//
//
//
//}
//
//for(Entry<String, String> entry :RequirementClassHashMap.entrySet()) {
//String myvalue = entry.getInputValue();
//String[] myvalues = myvalue.split("-"); 
//String shortclassname=myvalues[3].substring(myvalues[3].lastIndexOf(".")+1, myvalues[3].length()); 
//if(ReqClassHashMap.get(myvalues[0]+"-"+shortclassname)!=null) {
//	String statement8= "INSERT INTO `tracesclasses`(`requirement`, `requirementid`,  `classname`, `shortclassname`,`classid`,`subjectGold`) VALUES ('"+myvalues[1]+"','" +myvalues[0]+"','"  +myvalues[3]+"','"  +shortclassname+"','" +myvalues[2]+"','"
//+ReqClassHashMap.get(myvalues[0]+"-"+shortclassname)+"')";	
//	st2.executeUpdate(statement8);
//	TraceClassList.remove(myvalues[0]+"-"+shortclassname); 
//}else {
//	String statement8= "INSERT INTO `tracesclasses`(`requirement`, `requirementid`,  `classname`, `shortclassname`,`classid`,`subjectGold`) VALUES ('"+myvalues[1]+"','" +myvalues[0]+"','"  +myvalues[3]+"','"  +shortclassname+"','" +myvalues[2]+"','"
//			+"E"+"')";	
//				st2.executeUpdate(statement8);
//	TraceClassList.remove(myvalues[0]+"-"+shortclassname); 
//
//}
//
//}
//
//
//
//
//for(String entry: TraceClassList) {
//	System.out.println(entry);
//}
//System.out.println("OVER");
//fileReader.close();
//
//
//	}catch (IOException e) {
//		e.printStackTrace();
//	}






     	   //  /////////////*********************************************************************************************************************************************************************************/	
         /////////////*********************************************************************************************************************************************************************************/	
         /////////////*********************************************************************************************************************************************************************************/ 
     	// FIELD CLASSES 
//     		List<String> fieldClasses = new ArrayList<>(); 
//     		 for(CtType<?> clazz : classFactory.getAll()) {
//     	 			List<CtField> fields = clazz.getElements(new TypeFilter<CtField>(CtField.class));
//     	 		   retrieveFieldClasses(fields, st, fieldClasses, st2); 
//     	 		  Set<CtType<?>> nested = clazz.getNestedTypes();
//     		
//     	 		// FIELD CLASSES IN NESTED CLASSES  
//    				for(CtType<?> mynested: nested) {
//    					List<CtField> fields2 = mynested.getElements(new TypeFilter<CtField>(CtField.class));
//      	 		   retrieveFieldClasses(fields2, st, fieldClasses, st2); 
//    						
//   
//    				}
//     		 }
 	
     		 //  /////////////*********************************************************************************************************************************************************************************/	
     	        /////////////*********************************************************************************************************************************************************************************/	
     	        /////////////*********************************************************************************************************************************************************************************/ 
//     	    	// FIELD METHODS  
//     	    		List<String> fieldMethods = new ArrayList<>(); 
//     	    		 for(CtType<?> clazz : classFactory.getAll()) {
//     	    	 			List<CtMethod> methods = clazz.getElements(new TypeFilter<CtMethod>(CtMethod.class));
//     	    	 		   for(CtMethod myMethod: methods) {
//        	    	 				List<CtFieldAccess> methodFields = myMethod.getElements(new TypeFilter<CtFieldAccess>(CtFieldAccess.class));
//        	    	 					retrieveMethodFields(methodFields, myMethod, clazz, st, st2, fieldMethods); 
//        	    	 				
//     	    	 		   }
//     	    	 		   
//     	    	 		   
//     	    	 		   
//     	    	 		  List<CtConstructor> constructors = clazz.getElements(new TypeFilter<CtConstructor>(CtConstructor.class));
//    	    	 		   for(CtConstructor myconstructor: constructors) {
//       	    	 				List<CtFieldAccess> methodFields = myconstructor.getElements(new TypeFilter<CtFieldAccess>(CtFieldAccess.class));
//       	    	 					retrieveConstructorFields(methodFields, myconstructor, clazz, st, st2, fieldMethods); 
//       	    	 				
//    	    	 		   }
//     	    	 		   
//     	    		 }
//     		 
//     	    		
// 	
//        	
//        	/******************************************************************************************************************************************************/
//        	/******************************************************************************************************************************************************/
//        	/******************************************************************************************************************************************************/
//        	/******************************************************************************************************************************************************/
//        	List<String> mylist = new ArrayList<>(); 
//     		List<String> fieldMethods = new ArrayList<>(); 
//    		 for(CtType<?> clazz : classFactory.getAll(true)) {
//    			 System.out.println("====== "+clazz.getQualifiedName());
//    	 			List<CtField> classFields = clazz.getElements(new TypeFilter<CtField>(CtField.class));
//    	 			HashMap<String, String> myHashMap = new HashMap<>(); 
//    	 			for(CtField myclassField: classFields) {
//    	 			
//    	 				myHashMap.put(myclassField.getSimpleName(), myclassField.getType().toString()); 
//    	 				
//    	 			}
//    	 			String classID=null; 
//    	 			ResultSet	 rs= st.executeQuery("SELECT * from classes where classes.classname='"+clazz.getQualifiedName()+"'"); 
//    					while(rs.next()) {
//    						classID= rs.getString("id"); 
//    					}
//    	 			
//    	 			List<CtMethod> methods = clazz.getElements(new TypeFilter<CtMethod>(CtMethod.class));
//    	 			List<CtConstructor> constructors = clazz.getElements(new TypeFilter<CtConstructor>(CtConstructor.class));
//
//    	 			//VARIABLE READS  
//    	 			for(CtConstructor myMethod: constructors) {
//    	 				System.out.println(myMethod.getSignature());
//    	 				String methodname = myMethod.getSignature().substring(0, myMethod.getSignature().indexOf("(")); 
//    	 				String params = myMethod.getSignature().substring(myMethod.getSignature().indexOf("("), myMethod.getSignature().length()); 
//    	 				String fullmethodname = methodname+".-init-"+params; 
//    	 				System.out.println("---------------------");
//    	 				
//
//    		 			   System.out.println(myMethod);
//    		 					List<CtVariableRead> myvarList = myMethod.getElements(new TypeFilter<CtVariableRead>(CtVariableRead.class));
//
//    	   	 					String methodID=null; 
//    	   	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//    	   	 					while(rs.next()) {
//    	   	 					methodID= rs.getString("id"); 
//    	   	 					}
//    	   	 					for(CtVariableRead variableRead: myvarList) {
//    	   	 					//====>	
//    	   	 						String entry = methodID+"-"+variableRead+"-"+myHashMap.get(variableRead.toString())+"-"+1; 
//    	   	 					String variableDataTypeid=null; 
//    		 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableRead.toString())+"'"); 
//    		 					while(rs.next()) {
//    		 						variableDataTypeid= rs.getString("id"); 
//    		 					}	
//    	   	 					if(myHashMap.get(variableRead.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//    	   	 						mylist.add(entry); 
//    	   	 						System.out.println(fullmethodname);
//    	   	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableRead+"','"  + myHashMap.get(variableRead.toString())
//    	   	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//    	   						   	   	 st2.executeUpdate(statement8); 
//    	   	   	 					}
//    	   	 					
//    	   	 					}
//    	   	 					//VARIABLE WRITES 
//    	   	 				List<CtVariableWrite> myvarListWritten = myMethod.getElements(new TypeFilter<CtVariableWrite>(CtVariableWrite.class));
//
//    	   	 		   	 					 methodID=null; 
//    	   	 		   	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//    	   	 		   	 					while(rs.next()) {
//    	   	 		   	 					methodID= rs.getString("id"); 
//    	   	 		   	 					}
//    	   	 		   	 					for(CtVariableWrite variableWritten: myvarListWritten) {
//    	   	 		   	 					//====>	
//    	   	 		   	 						String entry = methodID+"-"+variableWritten+"-"+myHashMap.get(variableWritten.toString())+"-"+1; 
//    	   	 		   	 					String variableDataTypeid=null; 
//    	   	 			 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableWritten.toString())+"'"); 
//    	   	 			 					while(rs.next()) {
//    	   	 			 						variableDataTypeid= rs.getString("id"); 
//    	   	 			 					}	
//    	   	 		   	 					if(myHashMap.get(variableWritten.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//    	   	 		   	 						mylist.add(entry); 
//    	   	 		   	 						System.out.println(fullmethodname);
//    	   	 		   	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableWritten+"','"  + myHashMap.get(variableWritten.toString())
//    	   	 		   	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//    	   	 		   						   	   	 st2.executeUpdate(statement8); 
//    	   	 		   	   	 					}
//    	   	 		   	 					
//    	   	 		   	 					}
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				
//    	   	 				List<CtAssignment> assignments = myMethod.getElements(new TypeFilter<CtAssignment>(CtAssignment.class));
//    	   	 				for(CtAssignment assignment: assignments) {
//    	   	 					
//    	   	 					 myvarList = assignment.getAssignment().getElements(new TypeFilter<CtVariableRead>(CtVariableRead.class));
//    	   	 					
//    	   	 					 methodID=null; 
//    	   	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//    	   	 					while(rs.next()) {
//    	   	 					methodID= rs.getString("id"); 
//    	   	 					}
//    	   	 					
//    	   	 					//VARIABLE READS 
//    	   	 					
//    							for(CtVariableRead variableRead: myvarList) {
//    	   	 					//====>	
//    	   	 						String entry = methodID+"-"+variableRead+"-"+myHashMap.get(variableRead.toString())+"-"+1; 
//    	   	 					String variableDataTypeid=null; 
//    		 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableRead.toString())+"'"); 
//    		 					while(rs.next()) {
//    		 						variableDataTypeid= rs.getString("id"); 
//    		 					}	
//    	   	 					if(myHashMap.get(variableRead.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//    	   	 						mylist.add(entry); 
//    	   	 						System.out.println(fullmethodname);
//    	   	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableRead+"','"  + myHashMap.get(variableRead.toString())
//    	   	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//    	   						   	   	 st2.executeUpdate(statement8); 
//    	   	   	 					}
//    	   	 					}
//    	   	 					//===> variable write 
//    							String	 variableDataTypeid=null; 
//    	   	 					rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(assignment.getAssigned().toString())+"'"); 
//    		 					while(rs.next()) {
//    		 							variableDataTypeid= rs.getString("id"); 
//    		 					}	
//    		 					String entry = methodID+"-"+assignment.getAssigned().toString()+"-"+myHashMap.get(assignment.getAssigned().toString())+"-"+0; 
//    	   	 					if(myHashMap.get(assignment.getAssigned().toString())!=null  && methodID!=null && !mylist.contains(entry) && variableDataTypeid!=null) {
//    	   	 						mylist.add(entry); 
//
//    	   	 					 String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`,`variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','"  +methodID+"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	assignment.getAssigned()+"','"  + myHashMap.get(assignment.getAssigned().toString())
//    	   	 					+ "','" +variableDataTypeid  + "','" +0	+ "')";
//    	   	 					 System.out.println(clazz.getQualifiedName());
//    	   	 					 System.out.println(myMethod.getSignature());
//    	   	 					 System.out.println(fullmethodname);
//    						   	   	 st2.executeUpdate(statement8); 
//    	   	 					}
//    	   	 				
//    	   	 				}
//    	   	 				System.out.println();
//    		 		   
//    	 			}
//    	 			for(CtMethod myMethod: methods) {
//    	 					List<CtVariableRead> myvarList = myMethod.getElements(new TypeFilter<CtVariableRead>(CtVariableRead.class));
//    String fullmethodname=clazz.getQualifiedName()+"."+myMethod.getSignature(); 
//
//       	 					String methodID=null; 
//       	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//       	 					while(rs.next()) {
//       	 					methodID= rs.getString("id"); 
//       	 					}
//       	 					for(CtVariableRead variableRead: myvarList) {
//       	 					//====>	
//       	 						String entry = methodID+"-"+variableRead+"-"+myHashMap.get(variableRead.toString())+"-"+1; 
//       	 					String variableDataTypeid=null; 
//    	 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableRead.toString())+"'"); 
//    	 					while(rs.next()) {
//    	 						variableDataTypeid= rs.getString("id"); 
//    	 					}	
//       	 					if(myHashMap.get(variableRead.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//       	 						mylist.add(entry); 
//       	 						System.out.println(fullmethodname);
//       	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableRead+"','"  + myHashMap.get(variableRead.toString())
//       	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//       						   	   	 st2.executeUpdate(statement8); 
//       	   	 					}
//       	 					
//       	 					}
//       	 					//VARIABLE WRITES 
//       	 				List<CtVariableWrite> myvarListWritten = myMethod.getElements(new TypeFilter<CtVariableWrite>(CtVariableWrite.class));
//       	 		 fullmethodname=clazz.getQualifiedName()+"."+myMethod.getSignature(); 
//
//       	 		   	 					 methodID=null; 
//       	 		   	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//       	 		   	 					while(rs.next()) {
//       	 		   	 					methodID= rs.getString("id"); 
//       	 		   	 					}
//       	 		   	 					for(CtVariableWrite variableWritten: myvarListWritten) {
//       	 		   	 					//====>	
//       	 		   	 						String entry = methodID+"-"+variableWritten+"-"+myHashMap.get(variableWritten.toString())+"-"+1; 
//       	 		   	 					String variableDataTypeid=null; 
//       	 			 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableWritten.toString())+"'"); 
//       	 			 					while(rs.next()) {
//       	 			 						variableDataTypeid= rs.getString("id"); 
//       	 			 					}	
//       	 		   	 					if(myHashMap.get(variableWritten.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//       	 		   	 						mylist.add(entry); 
//       	 		   	 						System.out.println(fullmethodname);
//       	 		   	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableWritten+"','"  + myHashMap.get(variableWritten.toString())
//       	 		   	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//       	 		   						   	   	 st2.executeUpdate(statement8); 
//       	 		   	   	 					}
//       	 		   	 					
//       	 		   	 					}
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				
//       	 				List<CtAssignment> assignments = myMethod.getElements(new TypeFilter<CtAssignment>(CtAssignment.class));
//       	 				for(CtAssignment assignment: assignments) {
//       	 					
//       	 					 myvarList = assignment.getAssignment().getElements(new TypeFilter<CtVariableRead>(CtVariableRead.class));
//       	 					 fullmethodname=clazz.getQualifiedName()+"."+myMethod.getSignature(); 
//       	 					
//       	 					 methodID=null; 
//       	 						 rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethodname+"'"); 
//       	 					while(rs.next()) {
//       	 					methodID= rs.getString("id"); 
//       	 					}
//       	 					
//       	 					//VARIABLE READS 
//       	 					
//    						for(CtVariableRead variableRead: myvarList) {
//       	 					//====>	
//       	 						String entry = methodID+"-"+variableRead+"-"+myHashMap.get(variableRead.toString())+"-"+1; 
//       	 					String variableDataTypeid=null; 
//    	 						 rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(variableRead.toString())+"'"); 
//    	 					while(rs.next()) {
//    	 						variableDataTypeid= rs.getString("id"); 
//    	 					}	
//       	 					if(myHashMap.get(variableRead.toString())!=null  && !mylist.contains(entry) && variableDataTypeid!=null && methodID!=null) {
//       	 						mylist.add(entry); 
//       	 						System.out.println(fullmethodname);
//       	   	 					String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`, `variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','" +methodID +"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	variableRead+"','"  + myHashMap.get(variableRead.toString())
//       	   	 				+ "','" +variableDataTypeid + "','" +1+ "')";
//       						   	   	 st2.executeUpdate(statement8); 
//       	   	 					}
//       	 					}
//       	 					//===> variable write 
//    						String	 variableDataTypeid=null; 
//       	 					rs= st.executeQuery("SELECT * from classes where classes.classname='"+myHashMap.get(assignment.getAssigned().toString())+"'"); 
//    	 					while(rs.next()) {
//    	 							variableDataTypeid= rs.getString("id"); 
//    	 					}	
//    	 					String entry = methodID+"-"+assignment.getAssigned().toString()+"-"+myHashMap.get(assignment.getAssigned().toString())+"-"+0; 
//       	 					if(myHashMap.get(assignment.getAssigned().toString())!=null  && methodID!=null && !mylist.contains(entry) && variableDataTypeid!=null) {
//       	 						mylist.add(entry); 
//
//       	 					 String statement8= "INSERT INTO `assignments`(`methodname`, `methodid`,`classname`, `classid`,`variable`, `variableDataType`, `variableDataTypeid`,  `read` ) VALUES"+ " ('"+fullmethodname+"','"  +methodID+"','"  +clazz.getQualifiedName() +"','"  +	classID+"','" +	assignment.getAssigned()+"','"  + myHashMap.get(assignment.getAssigned().toString())
//       	 					+ "','" +variableDataTypeid  + "','" +0	+ "')";
//       	 					 System.out.println(clazz.getQualifiedName());
//       	 					 System.out.println(myMethod.getSignature());
//       	 					 System.out.println(fullmethodname);
//    					   	   	 st2.executeUpdate(statement8); 
//       	 					}
//       	 				
//       	 				}
//       	 				System.out.println();
//    	 		   }
//    	 		   
//    	 		   
//    	 		   
//    	 
//    	 		   
//    		 }
//        	
//     
//        	
//        	

///////////////*********************************************************************************************************************************************************************************/	
/////////////*********************************************************************************************************************************************************************************/	
/////////////*********************************************************************************************************************************************************************************/   
// PARAMETERS TABLE 
//List<String> mylist = new ArrayList<>(); 
//for(CtType<?> clazz : classFactory.getAll()) {
//
//
//
////NON NESTED CONSTRUCTORS 
//List<CtConstructor> constructors = clazz.getElements(new TypeFilter<CtConstructor>(CtConstructor.class));
//retrieveConstructorParams(constructors, st, st2, mylist); 	
//
//	// CONSTRUCTORS IN NESTED CLASSES  
//Set<CtType<?>> nested = clazz.getNestedTypes();
//
//	for(CtType<?> mynested: nested) {
//
//			constructors = mynested.getElements(new TypeFilter<CtConstructor>(CtConstructor.class));
//			retrieveConstructorParams(constructors, st, st2, mylist); 	
//
//	}
//	
//	//NON NESTED METHODS
//	List<CtMethod> methods = clazz.getElements(new TypeFilter<CtMethod>(CtMethod.class));
//	retrieveMethodParams(methods, st, st2, mylist); 	
//	
//		// METHODS  IN NESTED CLASSES 
//		for(CtType<?> mynested: nested) {
//	 
//				List<CtMethod> methodscalled = mynested.getElements(new TypeFilter<CtMethod>(CtMethod.class));
//				retrieveMethodParams(methods, st, st2, mylist); 	
//
//		}
//	
//	
//
//
//}
           	readFields( st, classFactory); 
        	 }
        	private void readFields(Statement st, ClassFactory classFactory) throws SQLException {
        		// TODO Auto-generated method stub
        		List<String> mylist = new ArrayList<String>(); 
        		ResultSet rs= st.executeQuery("SELECT * from sootfieldmethods"); 
        		 while(rs.next()) {
        			 String fieldclassid = rs.getString("fieldclassid");
        			 String fieldname=rs.getString("fieldname");
        			 String ownerclassname=rs.getString("ownerclassname");
        			 String ownerclassid=rs.getString("ownerclassid");
        			 String ownermethodname=rs.getString("ownermethodname");
        			 String ownermethodid=rs.getString("ownermethodid");
        			 String read=rs.getString("read");
        			 String union=fieldclassid+"-"+fieldname+"-"+ownerclassname+"-"+ownerclassid+"-"+ownermethodname+"-"+ownermethodid+"-"+read; 
        			 if(!mylist.contains(union)) {
        				 mylist.add(union); 
        			 }
        			 
        		 }
        		 for(CtType<?> clazz : classFactory.getAll()) {
        			String ownerclassid=null; 
        			String methodid=null; 
        			 String fieldname=null; 
        			 String ownerclassname=null; 
        			 String fieldclassid=null; 
        			 
        			 String ownermethodname=null; 
        			 String ownermethodid=null; 
        			 String read="0"; 

        			 System.out.println(clazz.getSimpleName());
        			  for(CtMethod<?> method :clazz.getMethods()) {
        					List<CtFieldAccess> fields = method.getElements(new TypeFilter<CtFieldAccess>(CtFieldAccess.class));
        					for(CtFieldAccess field: fields) {
        						ownermethodname=method.getSignature();

        						field.getType(); 
        						fieldname=field.toString();  
        						ownerclassname=clazz.getQualifiedName(); 
        						String union=null; 
        						String var=null; 
        						String classname=null; 
        						try {
        							rs= st.executeQuery("SELECT * from classes where classname='"+field.getVariable().getDeclaringType().toString()+"'"); 

        						 while(rs.next()) {
        							 ownerclassid=rs.getString("id"); 
        							 
        							 }
        					
        						  var= field.getVariable().toString().substring(field.getVariable().toString().lastIndexOf(".") + 1).trim(); 
        						  classname= field.getVariable().getDeclaringType().toString(); 
        						 
        						 rs= st.executeQuery("SELECT * from fieldclasses where classname='"+field.getVariable().getDeclaringType().toString()+"'and fieldname='"+field.getVariable().toString().substring(field.getVariable().toString().lastIndexOf(".") + 1).trim()+"'"); 
        						 while(rs.next()) {
        							 fieldclassid=rs.getString("id"); 
        							 }
        						 rs= st.executeQuery("SELECT * from methods where classname='"+clazz.getQualifiedName()+"'and methodname='"+ownermethodname+"'"); 
        						 while(rs.next()) {
        							 ownermethodid=rs.getString("id"); 
        							 }
        						System.out.println();
        						
        						  union=fieldclassid+"-"+var+"-"+classname+"-"+ownerclassid+"-"+ownermethodname+"-"+ownermethodid+"-"+read; 
        						 System.out.println(union);
        						 System.out.println();
        						}catch(Exception e ) {
        							
        						}
        						if(union!=null)
        						 if(!mylist.contains(union) && fieldclassid!=null && !var.equals("out") && union!=null) {
        							 System.out.println("HERE");
        							 String statement8= "INSERT INTO `sootfieldmethods`(`fieldclassid`, `fieldname`,  `ownerclassname`,"
        								 		+ " `ownerclassid`,`ownermethodname`,"
        								 		+ " `ownermethodid`, "
        								 		+ "`read`"
        								 		+ ") VALUES"
        								 		+ " ('"+	 fieldclassid+"','" +var+"','"  +classname
        								 		+"','" +ownerclassid+"','"+ ownermethodname
        								 		+"','"+ownermethodid+"','"+
        								 		read
        										 +"')";
        								   	   	 st.executeUpdate(statement8); 
        										 mylist.add(union); 

        						 }
        					}

        			  }
        			
        		 }		 
        		 
        	}

private void retrieveConstructorFields(List<CtFieldAccess> methodFields, CtConstructor myconstructor, CtType<?> clazz,
Statement st, Statement st2, List<String> fieldMethods) throws SQLException {
for(CtFieldAccess methodField : methodFields) {
System.out.println(methodField.getShortRepresentation()+"   "+myconstructor.getSignature()+"  "+myconstructor.getDeclaringType().getQualifiedName());
String methodID=null; 
String ownerMethodName=null; 
String formattedCons=myconstructor.getSignature().substring(0, myconstructor.getSignature().indexOf("("))+".-init-"+myconstructor.getSignature().substring(myconstructor.getSignature().indexOf("("), myconstructor.getSignature().length()); 
System.out.println(formattedCons);
ResultSet rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+formattedCons+"'"); 
while(rs.next()) {
	 methodID= rs.getString("id");
	 ownerMethodName=rs.getString("fullmethod"); 
}


String ownerclassID=null; 
rs= st.executeQuery("SELECT * from classes where classes.classname='"+clazz.getQualifiedName()+"'"); 
while(rs.next()) {
	  ownerclassID = rs.getString("id"); 
}


String fieldTypeclassid=null; 
try {

rs= st.executeQuery("SELECT * from classes where classes.classname='"+methodField.getType().getQualifiedName()+"'"); 
while(rs.next()) {
	 fieldTypeclassid = rs.getString("id"); 
}
}catch(Exception e) {
	 
}
String s= methodField.getShortRepresentation()+","+methodID+","+ownerclassID; 
if(ownerclassID!=null && methodID!=null && !fieldMethods.contains(s)) {
	 String statement8= "INSERT INTO `fieldmethods`(`fieldaccess`, `fieldtypeclassid`,  `fieldtypeclassname`,"
	 		+ " `ownerclassname`,`ownerclassid`,"
	 		+ " `ownermethodname`, "
	 		+ "`ownermethodid`"
	 		+ ") VALUES"
	 		+ " ('"+	 methodField+"','" +fieldTypeclassid+"','"  +methodField.getType().getQualifiedName()
	 		+"','" +clazz.getQualifiedName()+"','"+ ownerclassID
	 		+"','"+ownerMethodName+"','"+
	 		methodID
			 +"')";
	   	   	 st2.executeUpdate(statement8); 
	 
	   	     fieldMethods.add(s); 
}
}



// TODO Auto-generated method stub

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////
private void retrieveMethodFields(List<CtFieldAccess> methodFields, CtMethod myMethod, CtType<?> clazz, Statement st, Statement st2,
List<String> fieldMethods) throws SQLException {
for(CtFieldAccess methodField : methodFields) {
System.out.println(methodField+"   "+myMethod.getSignature()+"  "+myMethod.getDeclaringType().getQualifiedName());
String methodID=null; 
String ownerMethodName=null; 
ResultSet rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+myMethod.getDeclaringType().getQualifiedName()+"."+myMethod.getSignature()+"'"); 
while(rs.next()) {
methodID= rs.getString("id");
ownerMethodName=rs.getString("fullmethod"); 
}


String ownerclassID=null; 
rs= st.executeQuery("SELECT * from classes where classes.classname='"+clazz.getQualifiedName()+"'"); 
while(rs.next()) {
ownerclassID = rs.getString("id"); 
}


String fieldTypeclassid=null; 
try {
rs= st.executeQuery("SELECT * from classes where classes.classname='"+methodField.getType().getQualifiedName()+"'"); 
while(rs.next()) {
	 fieldTypeclassid = rs.getString("id"); 
}
}catch(Exception e) {

}

String s= methodField.getShortRepresentation()+","+methodID+","+ownerclassID; 
if(ownerclassID!=null && methodID!=null && !fieldMethods.contains(s)) {
String statement8= "INSERT INTO `fieldmethods`(`fieldaccess`, `fieldtypeclassid`,  `fieldtypeclassname`,"
		+ " `ownerclassname`,`ownerclassid`,"
		+ " `ownermethodname`, "
		+ "`ownermethodid`"
		+ ") VALUES"
		+ " ('"+	 methodField+"','" +fieldTypeclassid+"','"  +methodField.getType().getQualifiedName()
		+"','" +clazz.getQualifiedName()+"','"+ ownerclassID
		+"','"+ownerMethodName+"','"+
		methodID
		 +"')";
 	   	 st2.executeUpdate(statement8); 

 	     fieldMethods.add(s); 
}
}


}
///////////////////////////////////////////////////////////////////////////////////////////////////

private void retrieveFieldClasses(List<CtField> fields, Statement st, List<String> fieldClasses, Statement st2) throws SQLException { 
for(CtField myField : fields) {
System.out.println(myField.getSimpleName()+"   "+myField.getDeclaringType().getQualifiedName()+"  "+myField.getType().getQualifiedName());
String fieldTypeClassID="0"; 
ResultSet rs= st.executeQuery("SELECT * from classes where classes.classname='"+myField.getType().getQualifiedName()+"'"); 
while(rs.next()) {
fieldTypeClassID= rs.getString("id"); 
}
String declaringTypeClassID=null; 
rs= st.executeQuery("SELECT * from classes where classes.classname='"+myField.getDeclaringType().getQualifiedName()+"'"); 
while(rs.next()) {
declaringTypeClassID= rs.getString("id"); 
}

String fieldClass= myField.getSimpleName()+","+fieldTypeClassID+","+declaringTypeClassID; 

if(declaringTypeClassID!=null && myField.getSimpleName()!=null && !fieldClasses.contains(fieldClass)) {

String statement8= "INSERT INTO `fieldclasses`(`fieldname`, `fieldtypeclassid`,  `fieldtype`, `ownerclassid`,`classname`) VALUES ('"+
myField.getSimpleName()+"','" +fieldTypeClassID+"','"  +myField.getType().getQualifiedName()+"','" +declaringTypeClassID+"','"+myField.getDeclaringType().getQualifiedName()+"')";
st2.executeUpdate(statement8); 
fieldClasses.add(fieldClass); 



}




}}

////////////////////////////////////////////////

private void retrieveConstructorParams(List<CtConstructor> constructors, Statement st, Statement st2, List<String> mylist) throws SQLException {
// TODO Auto-generated method stub

for(CtConstructor constructor: constructors) {
List<CtParameter> params = constructor.getParameters(); 

for(CtParameter param: params) {
String parameterClassID="0"; 
String formattedCons=constructor.getSignature().substring(0, constructor.getSignature().indexOf("("))+".-init-"+constructor.getSignature().substring(constructor.getSignature().indexOf("("), constructor.getSignature().length()); 
System.out.println(constructor.getSignature()+" ======  "+param);
ResultSet rs= st.executeQuery("SELECT * from classes where classes.classname='"+param.getType().getQualifiedName()+"'"); 
while(rs.next()) {
	  parameterClassID= rs.getString("id"); 
	 
	 
}
rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+formattedCons+"'"); 
while(rs.next()) {
 String classid = rs.getString("classid"); 
 String classname =rs.getString("classname"); 
 String methodID =rs.getString("id"); 
 
 System.out.println(classid);
 String newparamList = param+","+parameterClassID+","+classid+","+methodID+","+0; 
 if(classid!=null && methodID!=null && !mylist.contains(newparamList)) {
//	   if(parameterClassID!=null && classid!=null && methodID!=null ) {

	   String mycons=""; 
	   try {
		    mycons= constructor.toString(); 

	   }catch(Exception ex) {
		   
	   }
	   String constructor2 = mycons.replaceAll("'", ""); 
	   String statement8= "INSERT INTO `parameters`(`parametername`, `parametertype`,  `parameterclass`, `classid`,`classname`, `methodid`, `methodname`, `isreturn`, `sourcecode`) VALUES ('"+
	    	   param+"','" +param.getType().getQualifiedName()+"','"  +parameterClassID+"','" +classid+"','"+classname+"','"+methodID+"','"+formattedCons+"','"+0+"','"+constructor2+"')";
	    	   st2.executeUpdate(statement8); 
	    	   mylist.add(newparamList); 
 }

}







}
}
}

////////////////////////////////////////////////
private void retrieveMethodParams(List<CtMethod> methods, Statement st, Statement st2, List<String> mylist) throws SQLException {
// TODO Auto-generated method stub
for(CtMethod method: methods) {
List<CtParameter> params = method.getParameters(); 
for(CtParameter param: params) {
String parameterClassID="0"; 
System.out.println(method.getSignature()+" ======  "+param);
ResultSet rs= st.executeQuery("SELECT * from classes where classes.classname='"+param.getType().getQualifiedName()+"'"); 
while(rs.next()) {
parameterClassID= rs.getString("id"); 


}
String fullmethod = method.getDeclaringType().getQualifiedName()+"."+method.getSignature();
System.out.println(fullmethod);
rs= st.executeQuery("SELECT * from methods where methods.fullmethod='"+fullmethod+"'"); 
while(rs.next()) {
String classid = rs.getString("classid"); 
String classname =rs.getString("classname"); 
String methodID =rs.getString("id"); 

System.out.println(classid);
String newparamList = param+","+parameterClassID+","+classid+","+methodID+","+0; 
String constructor2 = ""; 
if( methodID!=null && !mylist.contains(newparamList)) {
//if(parameterClassID!=null && classid!=null && methodID!=null ) {

try {
   constructor2 = method.toString().replaceAll("'", ""); 

}
catch(Exception ex) {
  
}
String statement8= "INSERT INTO `parameters`(`parametername`, `parametertype`,  `parameterclass`, `classid`,`classname`, `methodid`, `methodname`, `isreturn`, `sourcecode`) VALUES ('"+
	   param+"','" +param.getType().getQualifiedName()+"','"  +parameterClassID+"','" +classid+"','"+classname+"','"+methodID+"','"+fullmethod+"','"+0+"','"+constructor2+"')";
	   st2.executeUpdate(statement8); 
	   mylist.add(newparamList); 
}

CtTypeReference returnType = method.getType(); 
String returnTypeclassID = null; 
rs= st.executeQuery("SELECT * from classes where classes.classname='"+returnType+"'"); 
while(rs.next()) {
newparamList = returnType.toString()+","+returnTypeclassID+","+classid+","+methodID+","+1; 

if(parameterClassID!=null && classid!=null && methodID!=null && !mylist.contains(newparamList)) {
//if(parameterClassID!=null && classid!=null && methodID!=null ) {
returnTypeclassID= rs.getString("id"); 
String method2=""; 
try {
	method2=method.toString().replaceAll("'", "");
}
catch(Exception e) {
	
}
String statement8= "INSERT INTO `parameters`(`parametername`, `parametertype`,  `parameterclass`, `classid`,`classname`, `methodid`, `methodname`, `isreturn`, `sourcecode`) VALUES ('"+
		returnType.toString()+"','" +returnType.toString()+"','"  +returnTypeclassID+"','" +classid+"','"+classname+"','"+methodID+"','"+fullmethod+"','"+1+"','"+method2+"')";
	   st2.executeUpdate(statement8); 
	   mylist.add(newparamList); 


}
}
}







}
}
}
///////////////*********************************************************************************************************************************************************************************/	
/////////////*********************************************************************************************************************************************************************************/	
/////////////*********************************************************************************************************************************************************************************/   
private String WriteMethodIntoDatabase(CtMethod<?> constructor) {
// TODO Auto-generated method stub
List<CtComment> CommentList = constructor.getElements(new TypeFilter<CtComment>(CtComment.class));
List<CtComment> NewCommentList= CommentList; 
NewCommentList = constructor.getElements(new TypeFilter<CtComment>(CtComment.class));
int size=NewCommentList.size(); 
System.out.println(constructor);
int  j=0; 
if(CommentList!=null) {
CtMethod newmethod=constructor; 


while(j<size) {
	
	CtComment newcomment = NewCommentList.get(j); 
	newmethod=newmethod.removeComment(newcomment); 
	 size=NewCommentList.size(); 
	 j++; 
}

constructor=newmethod; 
}
String methodString = constructor.toString().replaceAll("\\/\\/.*", ""); 
methodString = methodString.toString().replaceAll("\'", ""); 

String FullConstructorName=constructor.getSignature().toString(); 


return methodString; 
}

public String WriteConstructorIntoDatabase(CtConstructor constructor) {
// TODO Auto-generated method stub
List<CtComment> CommentList = constructor.getElements(new TypeFilter<CtComment>(CtComment.class));
List<CtComment> NewCommentList= CommentList; 
NewCommentList = constructor.getElements(new TypeFilter<CtComment>(CtComment.class));
int size=NewCommentList.size(); 
System.out.println(constructor);
int  j=0; 
if(CommentList!=null) {
	CtConstructor newmethod=constructor; 
	
	
	while(j<size) {
		
		CtComment newcomment = NewCommentList.get(j); 
		newmethod=newmethod.removeComment(newcomment); 
		 size=NewCommentList.size(); 
		 j++; 
	}
	
	constructor=newmethod; 
}
String methodString = constructor.toString().replaceAll("\\/\\/.*", ""); 
methodString = methodString.toString().replaceAll("\'", ""); 

String FullConstructorName=constructor.getSignature().toString(); 


return methodString; 
}

private String GetMethodNameAndParams(String method) {
// TODO Auto-generated method stub
System.out.println("METH BEFORE TRUNCATION"+method);
String params=method.substring(method.indexOf("("), method.length()); 
String BeforeParams=method.substring(0, method.indexOf("(")); 
String methname=BeforeParams.substring(BeforeParams.lastIndexOf(".")+1, BeforeParams.length()); 
String res= methname+params; 
System.out.println("RES"+ res);
return res;
}





public static String RewriteFullMethodCallExecutedRemoveDollarsTraces(String input) {

String res=input; 
StringBuilder buf = new StringBuilder();



boolean flag=false; 
char[] chars = res.toCharArray();
int r = 0; 
int pos=0; 

int myindex= input.indexOf("$"); 
char c= chars[myindex+1]; 
if((Character.isDigit(c) && myindex+2==chars.length) || (Character.isDigit(c) && chars[myindex+2]=='(')) {
	System.out.println("yeah");
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while( flag==true) {
				if(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos-1<chars.length ) {
					System.out.println(chars[r]);
					StringBuilder sb = new StringBuilder();
					sb.append(chars);
					sb.deleteCharAt(pos);
					chars = sb.toString().toCharArray();
					pos++; 
					//r++; 
					if(pos>chars.length) {
						flag=false; 
					}
					if(chars[pos-1]=='(') {
						flag=false; 
					}
				}
			

				}
		}

		
			r++; 
		
		

		}
	
}
else if(Character.isDigit(c)) {
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while( flag==true) {
				if(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos-1<chars.length ) {
					System.out.println(chars[r]);
					StringBuilder sb = new StringBuilder();
					sb.append(chars);
					sb.deleteCharAt(pos);
					chars = sb.toString().toCharArray();
					pos++; 
					//r++; 
					if(chars[pos-1]=='.') {
						flag=false; 
					}
				}
			

				}
		}

		
			r++; 
		
		

		}
}
else {
	
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos<chars.length && flag==true) {
				pos=r-i; 
				System.out.println(chars[r]);
				StringBuilder sb = new StringBuilder();
				sb.append(chars);
				sb.deleteCharAt(pos);
				chars = sb.toString().toCharArray();
		i++; 
				//r++; 

				}
		}

		
			r++; 
		
		

		}
}


res = String.valueOf(chars);
System.out.println(res);
return res; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
public static String RewriteFullMethodCallExecutedRemoveDollars(String input) {

String res=input; 
StringBuilder buf = new StringBuilder();



boolean flag=false; 
char[] chars = res.toCharArray();
int r = 0; 
int pos=0; 

int myindex= input.indexOf("$"); 
char c= chars[myindex+1]; 
if(Character.isDigit(c) && myindex+2==chars.length) {
	System.out.println("yeah");
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while( flag==true) {
				if(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos-1<chars.length ) {
					System.out.println(chars[r]);
					StringBuilder sb = new StringBuilder();
					sb.append(chars);
					sb.deleteCharAt(pos);
					chars = sb.toString().toCharArray();
					pos++; 
					//r++; 
					if(pos>chars.length) {
						flag=false; 
					}
				}
			

				}
		}

		
			r++; 
		
		

		}
	
}
else if(Character.isDigit(c)) {
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while( flag==true) {
				if(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos-1<chars.length ) {
					System.out.println(chars[r]);
					StringBuilder sb = new StringBuilder();
					sb.append(chars);
					sb.deleteCharAt(pos);
					chars = sb.toString().toCharArray();
					pos++; 
					//r++; 
					if(chars[pos-1]=='.') {
						flag=false; 
					}
				}
			

				}
		}

		
			r++; 
		
		

		}
}
else {
	
	while(r<chars.length) {
		if(chars[r]=='$' ) {
		 pos=r; 
		// temp = chars[r+1]; 
		StringBuilder sb = new StringBuilder();
		sb.append(chars);
		sb.deleteCharAt(r);
		chars = sb.toString().toCharArray();
		flag=true; 
		}
		int i=1; 
		if(pos>0) {
			while(chars[pos-1]!='.'&& chars[pos-1]!='('&& chars[pos-1]!=')' && pos<chars.length && flag==true) {
				pos=r-i; 
				System.out.println(chars[r]);
				StringBuilder sb = new StringBuilder();
				sb.append(chars);
				sb.deleteCharAt(pos);
				chars = sb.toString().toCharArray();
		i++; 
				//r++; 

				}
		}

		
			r++; 
		
		

		}
}


res = String.valueOf(chars);
System.out.println(res);
return res; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String RewriteFullMethod(String input) {


StringBuilder buf = new StringBuilder();
String params= input.substring(input.indexOf("("), input.indexOf(")")+1); 
String methname= input.substring(0, input.indexOf("(") );
int i=0; 

while(i<params.length()-1) {

if(((params.charAt(i)=='L'|| params.charAt(i)=='Z'||params.charAt(i)=='B'||params.charAt(i)=='I'||params.charAt(i)=='J'||params.charAt(i)=='S'||params.charAt(i)=='C')
&& ((params.charAt(i+1)=='L'|| params.charAt(i+1)=='Z'||params.charAt(i+1)=='B'||params.charAt(i+1)=='I'||params.charAt(i+1)=='J'||params.charAt(i+1)=='S'||params.charAt(i+1)=='C')||
params.charAt(i+1)==')') && params.charAt(i-1)!='.') ||

((params.charAt(i)=='L'|| params.charAt(i)=='Z'||params.charAt(i)=='B'||params.charAt(i)=='I'||params.charAt(i)=='J'||params.charAt(i)=='S'||params.charAt(i)=='C')
&& ((params.charAt(i+2)=='L'|| params.charAt(i+2)=='Z'||params.charAt(i+2)=='B'||params.charAt(i+2)=='I'||params.charAt(i+2)=='J'||params.charAt(i+2)=='S'||params.charAt(i+2)=='C')||
params.charAt(i+1)==')') && params.charAt(i-1)!='.' ) ||

(params.charAt(i)=='[' && params.charAt(i-1)==',')||
(params.charAt(i)=='L'|| params.charAt(i)=='Z'||params.charAt(i)=='B'||params.charAt(i)=='I'||params.charAt(i)=='J'||params.charAt(i)=='S'||params.charAt(i)=='C')
&& ((params.charAt(i-1)=='['))) {


if(params.charAt(i+1)=='C') {
String params1 = params.substring(0, i); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",char,"+params2; 
}	

if(params.charAt(i+1)=='S') {
String params1 = params.substring(0, i); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",short,"+params2; 
}
if(params.charAt(i+1)=='V') {
String params1 = params.substring(0, i+1); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",void,"+params2; 
}
if(params.charAt(i+1)=='Z') {
String params1 = params.substring(0, i+1); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",boolean,"+params2; 
}
if(params.charAt(i+1)=='J') {
String params1 = params.substring(0, i+1); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",long,"+params2; 
}
if(params.charAt(i+1)=='B') {
String params1 = params.substring(0, i+1); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",byte,"+params2; 
}


if(params.charAt(i+1)=='I') {
String params1 = params.substring(0, i+1); 
String params2 = params.substring(i+2, params.length()); 
params=params1+",int,"+params2; 
}






if(params.charAt(i)=='S') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"short,"+params2; 
}
else {
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+2, params.length()); 
params=params1+","+params2+"short,"+params3; 	
}
else {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",short,"+params2; 	
}

}
}
if(params.charAt(i)=='C') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"char,"+params2; 
}

else {
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+1, params.length()); 
params=params1+","+params2+"char,"+params3; 	
}
else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",char,"+params2; 	
}

}
}
if(params.charAt(i)=='V') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"void,"+params2; 
}
else {
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+2, params.length()); 
params=params1+","+params2+"void,"+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",void,"+params2; 	
}

}
}
if(params.charAt(i)=='Z') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"boolean,"+params2; 
}
else{
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+2, params.length()); 
params=params1+","+params2+"boolean,"+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",boolean,"+params2; 	
}
}

}
if(params.charAt(i)=='J') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"long,"+params2; 
}
else {
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+2, params.length()); 
params=params1+","+params2+"long,"+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",long,"+params2; 	
}
}

}
if(params.charAt(i)=='B') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"byte,"+params2; 
}
else{
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+2, params.length()); 
params=params1+","+params2+"byte"+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",byte,"+params2; 	
}
}

}
if(params.charAt(i)=='I') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+"int,"+params2; 
}
else{
if(params.charAt(i-1)=='[') {

String params1 = params.substring(0, i-1); 
String params2 = params.substring(i-1, i); 
String params3 = params.substring(i+1, params.length()); 
params=params1+","+params2+"int,"+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",int,"+params2; 	
}
}
}

System.out.println(params.charAt(i)); 
if(params.charAt(i-1)=='[') {
if(i==1) {
String params1 = params.substring(0, 1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+","+params2; 
}
else{
if(params.charAt(i-2)==',') {
String[] parts = params.split(",");
String AppendedParts=""; 
for(String part: parts) {
	if(part.charAt(0)=='[') {
		part=part.substring(1, part.length()); 
		part=part+"[]"; 
	}
	AppendedParts=AppendedParts+part+","; 
	params=AppendedParts; 
}
//String params1 = params.substring(0, i-1); 
//String params2 = params.substring(i-1, i); 
//String params3 = params.substring(i+1, params.length()); 
//params=params1+","+params2+","+params3; 	
}else{
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",,"+params2; 	
}
}
}


if(params.charAt(i+1)==')' && params.charAt(i)=='I') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",int,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='S') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",short,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='J') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",long,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='B') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",byte,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='Z') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",boolean,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='V') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",void,"+params2; 
}
if(params.charAt(i+1)==')' && params.charAt(i)=='C') {
String params1 = params.substring(0, i-1); 
String params2 = params.substring(i+1, params.length()); 
params=params1+",char,"+params2; 
}
}
i++; 
}
String res= methname+params; 

//System.out.println(res);
res=res.replaceAll("\\(,", "\\("); 
res=res.replaceAll(",\\)", "\\)"); 
res=res.replaceAll(",,", ","); 
res=res.replaceAll(";", ","); 
res=res.replaceAll(",,", ","); 
res=res.replaceAll(",\\[,", ",\\["); 
res=res.replaceAll(",\\)", "\\)"); 
//res=res.replaceAll("Ljava", "java"); 
//System.out.println("here  "+res);



boolean flag=false; 
char[] chars = res.toCharArray();
int r=0; 
int pos=10000000; 
char temp='\0'; 
while(r<chars.length) {
if(chars[r]=='[' ) {
pos=r; 
// temp = chars[r+1]; 
StringBuilder sb = new StringBuilder();
sb.append(chars);
sb.deleteCharAt(r);
chars = sb.toString().toCharArray();
flag=true; 
}
if(flag==true) {
pos=r; 
// temp = chars[r+1]; 
if(chars[r]==',' ) {
StringBuilder sb = new StringBuilder();
sb.append(chars);
sb.deleteCharAt(r);

sb.insert(r, "[],");
chars = sb.toString().toCharArray();
flag=false; 	 
}
else if(chars[r]==')' ) {
StringBuilder sb = new StringBuilder();
sb.append(chars);
sb.deleteCharAt(r);
sb.insert(r, "[])");
chars = sb.toString().toCharArray();
flag=false; 	 
}


}

r++; 
}


flag=false; 
chars = res.toCharArray();
r=0; 


while(r<chars.length) {
if(chars[r]=='$' ) {
pos=r; 
// temp = chars[r+1]; 
StringBuilder sb = new StringBuilder();
sb.append(chars);
sb.deleteCharAt(r);
chars = sb.toString().toCharArray();
flag=true; 
}
while(chars[r]!='.'&& chars[r]!='('&& chars[r]!=')'&& flag==true) {
System.out.println(chars[r]);
StringBuilder sb = new StringBuilder();
sb.append(chars);
sb.deleteCharAt(r);
chars = sb.toString().toCharArray();

//r++; 

}
flag=false; 

r++; 

}
res = String.valueOf(chars);
System.out.println("final res : "+res);
return res;




}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String KeepOnlyMethodName(String constructor) {
String params= constructor.substring(constructor.indexOf("("), constructor.length()); 
constructor=constructor.substring(0, constructor.indexOf("(")); 
constructor=constructor.substring(constructor.lastIndexOf(".")+1, constructor.length()); 
constructor=constructor+params; 

return constructor; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String TransformConstructorIntoInit(String constructor) {
String params= constructor.substring(constructor.indexOf("("), constructor.length()); 
constructor= constructor.substring(0, constructor.indexOf("(")); 

//String part2= constructor.substring(constructor.indexOf("("), constructor.length()); 
constructor=constructor+".-init-"+params; 

return constructor; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String RemoveDollarConstructor(String text) {



String res=""; 

boolean  flag=false; 
char[] chars = text.toCharArray();
int r = 0; 
int pos = text.indexOf("$"); 
//System.out.println("HERE IS THE TEXT "+text);
int count = StringUtils.countMatches("text", "$");
if(count==1) {
if(text.contains("$")) {
if(chars.length-pos>7 && chars[pos+2]!='(') {


while(r<chars.length ) {
if(chars[r]=='$' ) {
	// pos = r; 
	// temp = chars[r+1]; 
	StringBuilder sb = new StringBuilder();
	sb.append(chars);
	sb.deleteCharAt(r);
	chars = sb.toString().toCharArray();
	flag=true; 
	 pos--; 
	
	
}

while( flag==true ) {
	 if(chars[pos]!='.'&& chars[pos]!='('&& chars[pos]!=')') {
		 r--; 
		 pos--; 
	//	 System.out.println(chars[r]);
		 StringBuilder sb = new StringBuilder();
		 sb.append(chars);
		
		 sb.deleteCharAt(r);
	//	 System.out.println(sb);
		 chars = sb.toString().toCharArray();
		 int length=chars.length; 
//		 if(r==length) {
//			 flag=false; 
//		 }
		
		
	 }
	 else {
		 flag=false; 
	 }
	
	 //r++; 
	 
}
flag=false; 
if(flag==true) {
	 r--; 
}else {
	 r++;  
}


}
res = String.valueOf(chars);
}else {
String part2=""; 
String part1=""; 
res = String.valueOf(chars);
part1=res.substring(0,res.indexOf("$")); 
if(res.contains("(")) {
part2=res.substring(res.indexOf("("),res.length()); 
}
res=part1+part2; 
}

}


}else if(count==2) {
String methodname=text.substring(0, text.indexOf("(")); 
String parameters=text.substring(text.indexOf("("), text.length()); 

methodname=methodname.substring(0, methodname.lastIndexOf("$")); 
String part1meth=methodname.substring(0, methodname.lastIndexOf(".")); 
String part2meth=methodname.substring(methodname.indexOf("$"), methodname.length()); 
res=part1meth+part2meth+parameters; 
}
//System.out.println("RES====>"+res);
else {
res=text; 
}


return res; 


}



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String RemoveDollar(String text) {	

boolean  flag=false; 
char[] chars = text.toCharArray();
int r = 0; 
int pos = text.indexOf("$"); 
//System.out.println("HERE IS THE TEXT "+text);
if(text.contains("$")) {
while(r<chars.length) {
if(chars[r]=='$' ) {
	// pos = r; 
	// temp = chars[r+1]; 
	StringBuilder sb = new StringBuilder();
	sb.append(chars);
	sb.deleteCharAt(r);
	chars = sb.toString().toCharArray();
	flag=true; 
	 pos--; 
	
	
}

while( flag==true ) {
	 if(chars[pos]!='.'&& chars[pos]!='('&& chars[pos]!=')') {
		 r--; 
		 pos--; 
	//	 System.out.println(chars[r]);
		 StringBuilder sb = new StringBuilder();
		 sb.append(chars);
		
		 sb.deleteCharAt(r);
	//	 System.out.println(sb);
		 chars = sb.toString().toCharArray();
		 int length=chars.length; 
//		 if(r==length) {
//			 flag=false; 
//		 }
		
		
	 }
	 else {
		 flag=false; 
	 }
	
	 //r++; 
	 
}
flag=false; 
if(flag==true) {
	 r--; 
}else {
	 r++;  
}


}

}
String res = String.valueOf(chars);
//System.out.println("RES====>"+res);
return res; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String ParseLine(String line) {
System.out.println(line);
String[] linesplitted = line.split(","); 
String method = linesplitted[1]; 
String requirement = linesplitted[2]; 
String gold = linesplitted[4]; 
String subject = linesplitted[5]; 
System.out.println("HERE IS THIS SHORT METHOD========>"+ method); 

String shortmethod=method.substring(0, method.indexOf("("));
String regex = "(.)*(\\d)(.)*";      
Pattern pattern = Pattern.compile(regex);
boolean containsNumber = pattern.matcher(shortmethod).matches();
String[] firstpart;
String FinalMethod = null;
shortmethod=shortmethod.replaceAll("clinit", "init"); 
if(shortmethod.contains("$") && shortmethod.matches(".*\\d+.*")) {
firstpart = shortmethod.split("\\$");
String myfirstpart= firstpart[0]; 
FinalMethod=myfirstpart; 
if(StringUtils.isNumeric(firstpart[1])==false) {
	String[] secondpart = firstpart[1].split("\\d"); 
	System.out.println("my first part "+ myfirstpart+ "firstpart"+ firstpart[1]);
	
	String mysecondpart=secondpart[1]; 
	
	 FinalMethod=myfirstpart+mysecondpart; 
	System.out.println("FINAL RESULT:    "+FinalMethod);
}

}

else if(shortmethod.contains("$") && containsNumber==false) {
firstpart = shortmethod.split("\\$");

System.out.println("FINAL STRING:   "+firstpart[0]);
firstpart[1]=firstpart[1].substring(firstpart[1].indexOf("."), firstpart[1].length()); 
System.out.println("FINAL STRING:   "+firstpart[1]);
FinalMethod= firstpart[0]+firstpart[1]; 
System.out.println("FINAL STRING:   "+FinalMethod);
}
else {
FinalMethod=shortmethod; 
}
return FinalMethod; 
}



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
public String transformstring(String s) {
s=s.replace("/", "."); 
s=s.replace(";", ","); 
int endIndex = s.lastIndexOf(",");
if (endIndex != -1)  
{
	s = s.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
}
s=s.replace("Lde", "de"); 
s=s.replace("Ljava", "java"); 
return s; 
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String[] ExtractParams(String method) {
String Paramlist=method.substring(method.indexOf("(")+1, method.indexOf(")")); 
String[] data = Paramlist.split(",");
return data; 
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

public String ExtractParams2(String method) {
String Paramlist=method.substring(method.indexOf("(")+1, method.indexOf(")")); 

return Paramlist; 
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	


}
