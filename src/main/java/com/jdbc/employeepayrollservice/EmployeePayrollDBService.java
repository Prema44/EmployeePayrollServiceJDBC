package com.jdbc.employeepayrollservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeePayrollDBService {

	private PreparedStatement employeeStatement;
	private static EmployeePayrollDBService employeePayrollDB;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDB == null) {
			employeePayrollDB = new EmployeePayrollDBService();
		}
		return employeePayrollDB;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Jan1998ad";
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (Exception e) {
			throw new SQLException("Connection was unsuccessful");
		}
		return connection;
	}

	/**
	 * Usecase2: Reading data from the employee_payroll_service
	 * 
	 * @return
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	public List<EmployeePayrollData> readData() throws DatabaseException {
		String sql = "Select * from employee_payroll_service; ";
		List<EmployeePayrollData> employeeData = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeeData = this.getEmployeePayrollData(resultSet);
		} catch (Exception exception) {
			throw new DatabaseException("Unable to execute query");
		}
		return employeeData;
	}

	/**
	 * Usecase3: Function to update salary in the table for a particular person
	 * 
	 * @param name
	 * @param salary
	 * @return
	 * @throws DatabaseException
	 */
	private int updateEmployeeUsingStatement(String name, double salary) throws DatabaseException {
		String sql = String.format("Update employee_payroll_service set salary = %.2f where name = '%s';", salary,
				name);
		int result = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			result = statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to update");
		}
		return result;
	}

	public List<EmployeePayrollData> getEmployeeData(String name) throws DatabaseException {
		return readData().stream().filter(employee -> employee.name.equals(name)).collect(Collectors.toList());
	}

	public int updateEmployeeData(String name, double salary) throws DatabaseException {
		return this.updateEmployeeUsingStatement(name, salary);
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeeStatement == null)
			this.preparedStatementForEmployeeData();
		try {
			employeeStatement.setString(1, name);
			ResultSet resultSet = employeeStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	/**
	 * Usecase4: Refactored the result set
	 * 
	 * @param resultSet
	 * @return
	 */
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, start));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	/**
	 * Usecase4: Prepared Statement for the payroll database
	 */
	private void preparedStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll_service WHERE name = ?";
			employeeStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Usecase5: Implementing query to find employees joined between the particular
	 * dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public int getEmployeeForDateRange(LocalDate start, LocalDate end) throws DatabaseException {
		String sql = String.format("Select * from employee_payroll_service where start between '%s' and '%s' ;",Date.valueOf(start), Date.valueOf(end));
		List<EmployeePayrollData> employeeData = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeeData = this.getEmployeePayrollData(resultSet);
		} catch (Exception exception) {
			throw new DatabaseException("Unable to execute query");
		}
		return employeeData.size();
	}
	
	/**
	 * Usecase6: performing Aggregate functions query on the employee table 
	 * @param function
	 * @return
	 * @throws DatabaseException
	 */
	public Map<String, Double> getEmployeesByFunction(String function) throws DatabaseException   {
		Map<String, Double> aggregateFunctionMap = new HashMap<>();
		String sql = String.format("Select gender, %s(salary) from employee_payroll_service group by gender ; ", function);
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql); 
			while (resultSet.next()) {
				String gender = resultSet.getString(1);
				Double salary = resultSet.getDouble(2);
				aggregateFunctionMap.put(gender, salary);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Unable to execute " + function);
		}
		return aggregateFunctionMap;
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
	public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate date)
			throws DatabaseException, SQLException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employee = null;
		connection = this.getConnection();
		try (Statement statement = (Statement) connection.createStatement()) {
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
			throw new DatabaseException("Unable to add to database");
		}
		try (Statement statement = (Statement) connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"insert into payroll_details (employeeId, basic_pay, deductions, taxable_pay, tax, net_pay) "
							+ "VALUES ('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deductions, taxable_pay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employee = new EmployeePayrollData(employeeId, name, gender, salary, date);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to add to database");
		}
		return employee;
	}

}