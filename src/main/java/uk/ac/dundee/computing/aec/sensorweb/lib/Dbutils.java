package uk.ac.dundee.computing.aec.sensorweb.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.sql.DataSource;





public class Dbutils {

	private static final void listContext(Context ctx, String indent) {
		try {
			NamingEnumeration list = ctx.listBindings("");
			while (list.hasMore()) {
				Binding item = (Binding) list.next();
				String className = item.getClassName();
				String name = item.getName();
				System.out.println("" + className + " " + name);
				Object o = item.getObject();
				if (o instanceof javax.naming.Context) {
					listContext((Context) o, indent + " ");
				}
			}
		} catch (NamingException ex) {
			System.out.println("JNDI failure: " + ex);
		}
	}
	
	/**
	 * Assembles  a DataSource from JNDI.
	 */
	 public DataSource assemble(ServletConfig config) throws ServletException {
		DataSource _ds = null;
		String dataSourceName = config.getInitParameter("data-source");
		System.out.println("Data Source Parameter " + dataSourceName);
		if (dataSourceName == null)
			throw new ServletException("data-source must be specified");
		Context envContext = null;
		try {
			Context ic = new InitialContext();
			System.out.println("initial context " + ic.getNameInNamespace());
			envContext = (Context) ic.lookup("java:/comp/env");
			System.out.println("envcontext  " + envContext);
			listContext(envContext, "");
		} catch (Exception et) {
			throw new ServletException("Can't get contexts " + et);
		}
		// _ds = (DataSource) ic.lookup("java:"+dataSourceName);
		// _ds = (DataSource) ic.lookup("java:comp/env/" );
		try {
			_ds = (DataSource) envContext.lookup(dataSourceName);

			if (_ds == null)
				throw new ServletException(dataSourceName
						+ " is an unknown data-source.");
		} catch (NamingException e) {
			throw new ServletException("Cant find datasource name " +dataSourceName+" Error "+ e);
		}
		 CreateSchema(_ds);
                 CreateSensorSchema(_ds);
		return _ds;

	}
         
         
         
         
         private void CreateSensorSchema(DataSource _ds) {
		PreparedStatement pmst = null;
		Connection Conn;
		try {
			Conn = _ds.getConnection();
		} catch (Exception et) {
			return;
		}
		String sqlQuery = "CREATE TABLE IF NOT EXISTS `Readings` ("
				+ "`idReading` INT NOT NULL AUTO_INCREMENT," 
                                + "`name` VARCHAR(45) NULL,"
                                + "`Nbentries` INT NULL,"
                                + "`LastEntryIndex` INT NULL,"
                                + "`TransferStartIndex` INT NULL,"
                                + "`CurrentSessionId` INT NULL,"
                                + "`CurrentSessionStartIndex` INT NULL,"
                                + "`CurrentSessionPeriod` INT NULL,"
                                + "`FlowerPowercurrenttime` INT NULL,"
                                + "`MobileTime` DateTime NULL,"
                                + "`B64History` TEXT NULL,"
				+ "PRIMARY KEY (`idReading`))" + "ENGINE = InnoDB;";
		try {
			pmst = Conn.prepareStatement(sqlQuery);
			pmst.executeUpdate();
		} catch (Exception ex) {
			System.out.println("Can not create table "+ex);
			return;
		}
         }
	// create the schema if it doesn't exist
	private void CreateSchema(DataSource _ds) {
		PreparedStatement pmst = null;
		Connection Conn;
		try {
			Conn = _ds.getConnection();
		} catch (Exception et) {
			return;
		}
		String sqlQuery = "CREATE TABLE IF NOT EXISTS `author` ("
				+ "`idauthor` INT NOT NULL AUTO_INCREMENT," + "`name` VARCHAR(45) NULL,"
				+ "PRIMARY KEY (`idauthor`))" + "ENGINE = InnoDB;";
		try {
			pmst = Conn.prepareStatement(sqlQuery);
			pmst.executeUpdate();
		} catch (Exception ex) {
			System.out.println("Can not create table "+ex);
			return;
		}
		sqlQuery = "CREATE TABLE IF NOT EXISTS `section` ("
				+ "`idsection` INT NOT NULL AUTO_INCREMENT," + "`name` VARCHAR(45) NULL,"
				+ "PRIMARY KEY (`idsection`))" + "ENGINE = InnoDB;";

		try {
			pmst = Conn.prepareStatement(sqlQuery);
			pmst.executeUpdate();
		} catch (Exception ex) {
			System.out.println("Can not create table "+ex);
			return;
		}
		sqlQuery = "CREATE TABLE IF NOT EXISTS `fault` ("
				+ "`idfault` INT NOT NULL AUTO_INCREMENT," + "`summary` VARCHAR(45) NULL,"
				+ "`details` VARCHAR(100) NULL,"
				+ "`author_idauthor` INT NOT NULL,"
				+ "`section_idsection` INT NOT NULL,"
				+ "PRIMARY KEY (`idfault`),"
				+ "INDEX `fk_fault_author_idx` (`author_idauthor` ASC),"
				+ "INDEX `fk_fault_section1_idx` (`section_idsection` ASC),"
				+ "CONSTRAINT `fk_fault_author`"
				+ "  FOREIGN KEY (`author_idauthor`)"
				+ " REFERENCES `author` (`idauthor`)"
				+ "ON DELETE NO ACTION" + " ON UPDATE NO ACTION,"
				+ "CONSTRAINT `fk_fault_section1`"
				+ " FOREIGN KEY (`section_idsection`)"
				+ "REFERENCES `section` (`idsection`)" + "ON DELETE NO ACTION "
				+ "ON UPDATE NO ACTION)" + "ENGINE = InnoDB;";
		try {
			pmst = Conn.prepareStatement(sqlQuery);
			pmst.executeUpdate();
		} catch (Exception ex) {
			System.out.println("Can not create table "+ex);
			return;
		}
		ResultSet rs=null;
		sqlQuery="Select count(name) from author as rowcount";
		try {
			pmst = Conn.prepareStatement(sqlQuery);
			rs=pmst.executeQuery();
			if(rs.next()) {
			    int rows = rs.getInt(1);
			    System.out.println("Number of Rows " + rows);
			    if (rows==0){
			    	sqlQuery="INSERT INTO `author` (`name`) VALUES ('Andy'),('Tracey'),('Tom'),('Bill');";
					try {
						pmst = Conn.prepareStatement(sqlQuery);
						pmst.executeUpdate();
					} catch (Exception ex) {
						System.out.println("Can not insert names in authors "+ex);
						return;
					}
					sqlQuery="INSERT INTO `section` (`name`) VALUES ('Cassandra'),('Hadoop'),('Debian');";
					try {
						pmst = Conn.prepareStatement(sqlQuery);
						pmst.executeUpdate();
					} catch (Exception ex) {
						System.out.println("Can not insert names in sections "+ex);
						return;	
					}
					sqlQuery="INSERT INTO `fault` (`summary`,`details`,`author_idauthor`,`section_idsection`) VALUES ('Startup fails on a pi','Because the number of processors returned is zero startup fails','1','1');";
					try {
						pmst = Conn.prepareStatement(sqlQuery);
						pmst.executeUpdate();
					} catch (Exception ex) {
						System.out.println("Can not insert default fault "+ex);
						return;	
					}
			    }
			}
			
		} catch (Exception ex) {
			System.out.println("Can not select count "+ex);
			return;
		}
		
	
 
		
	}
	
	public void createSchema(){
		String url = "jdbc:mysql://localhost";
		Connection conn=null;
		try {
		   Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		   conn = DriverManager.getConnection (url, "root", "Cl1m8t3;");
	
		}catch (Exception et){
			System.out.println("Can't get conenction to create schema "+et);
			return;
		}
		String sqlcreateSchema="Create database if not exists faultdb ;";
		try{
			java.sql.Statement statement=conn.createStatement();
			statement.execute(sqlcreateSchema);
			conn.close();
		}catch (Exception et){
			System.out.println("Can not create schema ");
			return;
		}
		
	}
}
