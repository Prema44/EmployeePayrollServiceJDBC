package com.jdbc.employeepayrollservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class EmployeePayrollDBService {
	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataPrepareStatement;

	private EmployeePayrollDBService() {

	}

	/**
	 * initiates employeePayrollDBService only once and returns same
	 * 
	 * @return
	 */
	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	/**
	 * returns established connection with database
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	private Connection getConnection() throws DatabaseException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Prema@44";
		Connection connection = null;
		try {
			System.out.println("Connecting to database:" + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is successful!" + connection);
		} catch (Exception exception) {
			throw new DatabaseException("Connection is not successful");
		}

		return connection;
	}

	/**
	 * returns list of
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws SQLException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		while (resultSet.next()) {
			int id = resultSet.getInt("id");
			String name = resultSet.getString("name");
			double salary = resultSet.getDouble("salary");
			LocalDate startDate = resultSet.getDate("start").toLocalDate();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
		}

		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() throws DatabaseException {
		try {
			Connection connection = this.getConnection();
			String sql = "Select * from employee_payroll where name = ?";
			employeePayrollDataPrepareStatement = (PreparedStatement) connection.prepareStatement(sql);
		} catch (SQLException | DatabaseException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}

	/**
	 * updates data using statement
	 * 
	 * @param name
	 * @param salary
	 * @return
	 * @throws DatabaseException
	 */
	private int updateEmployeeDataUsingStatement(String name, double salary) throws DatabaseException {
		String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);

		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (DatabaseException exception) {
			throw new DatabaseException(exception.getMessage());
		} catch (SQLException exception) {
			throw new DatabaseException("Error while updating data");
		}
	}

	/**
	 * updates data using prepared statement
	 * 
	 * @param name
	 * @param salary
	 * @return
	 * @throws DatabaseException
	 */
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws DatabaseException {
		try (Connection connection = this.getConnection()) {
			String sql = "Update employee_payroll set salary = ? where name = ? ; ";
			PreparedStatement prepareStatement = (PreparedStatement) connection.prepareStatement(sql);
			prepareStatement.setDouble(1, salary);
			prepareStatement.setString(2, name);
			return prepareStatement.executeUpdate();
		} catch (SQLException | DatabaseException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}

	/**
	 * when passed query returns list of employee payroll data
	 * 
	 * @param sql
	 * @return
	 * @throws DatabaseException
	 */
	private List<EmployeePayrollData> getData(String sql) throws DatabaseException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException | DatabaseException exception) {
			throw new DatabaseException(exception.getMessage());
		}

		return employeePayrollList;
	}

	/**
	 * reads data from database and returns it in list
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public List<EmployeePayrollData> readData() throws DatabaseException {
		String sql = "select employee_payroll.employeeId, employee_dept.departmentName, employee_payroll.name, employee.gender,"
				+ "payroll_details.salary, payroll_details.deductions, payroll_details.taxable_pay, payroll_details.tax, payroll_details.net_pay, employee_payroll.start"
				+ "from employee_payroll"
				+ "inner join employee_dept on employee_payroll.employeeId = employee_dept.employeeId"
				+ "inner join payroll_details on employee_payroll.employeeId = payroll_details.employeeId;";
		return this.getData(sql);
	}

	/**
	 * updates data and returns number of records updated
	 * 
	 * @param name
	 * @param salary
	 * @return
	 * @throws DatabaseException
	 */
	public int updateEmployeeData(String name, double salary) throws DatabaseException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws DatabaseException {
		List<EmployeePayrollData> employeePayrollList = null;
		try {
			if (this.employeePayrollDataPrepareStatement == null) {
				this.prepareStatementForEmployeeData();
			}
			employeePayrollDataPrepareStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataPrepareStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException | DatabaseException exception) {
			throw new DatabaseException(exception.getMessage());
		}
		return employeePayrollList;
	}

	/**
	 * when given date range returns list of employees who joined between dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<EmployeePayrollData> readDataForGivenDateRange(LocalDate start, LocalDate end)
			throws DatabaseException {
		String sql = String.format("Select * from employee_payroll where start between '%s' and '%s' ;",
				Date.valueOf(start), Date.valueOf(end));
		return this.getData(sql);
	}

	/**
	 * returns map of gender and calculated values when passed a function
	 * 
	 * @param function
	 * @return
	 * @throws DatabaseException
	 */
	public Map<String, Double> getEmployeesByFunction(String function) throws DatabaseException {
		Map<String, Double> genderComputedMap = new HashMap<>();
		String sql = String.format("Select gender, %s(salary) from employee_payroll group by gender ; ", function);
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString(1);
				Double salary = result.getDouble(2);
				genderComputedMap.put(gender, salary);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Unable to find " + function);
		}
		return genderComputedMap;
	}

	/**
	 * adds employee details to database
	 * 
	 * @param name
	 * @param gender
	 * @param salary
	 * @param date
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate date)
			throws DatabaseException, SQLException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employee = null;
		connection = this.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		}
		try (Statement statement = (Statement) connection.createStatement()) { // adding to employee_payroll table
			String sql = String.format(
					"insert into employee_payroll (name, gender, salary, start) values ('%s', '%s', '%s', '%s')", name,
					gender, salary, Date.valueOf(date));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employee = new EmployeePayrollData(employeeId, name, gender, salary, date);
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to database");
		}
		try (Statement statement = (Statement) connection.createStatement()) { // adding to payroll_details table
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"insert into payroll_details (employeeId, basic_pay, deductions, taxable_pay, tax, net_pay) "
							+ "VALUES ('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deductions, taxable_pay, tax, netPay);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				throw new DatabaseException(exception.getMessage());
			}
			throw new DatabaseException("Unable to add to database");
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return employee;
	}

	/**
	 * deletes employee record in cascade from both tables of database
	 * 
	 * @param id
	 * @throws DatabaseException
	 */
	public void deleteEmployeeFromPayroll(int id) throws DatabaseException {
		String sql = String.format("delete from employee_payroll where id = %s;", id);
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException exception) {
			throw new DatabaseException("Unable to delete data");
		}
	}

}