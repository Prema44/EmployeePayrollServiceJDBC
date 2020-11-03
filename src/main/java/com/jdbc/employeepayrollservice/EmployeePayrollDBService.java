package com.jdbc.employeepayrollservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeePayrollDBService {
	
	public EmployeePayrollDBService() {
	}
	
	/**
	 * Usecase1: Establish connection to database
	 * 
	 * @return
	 * @throws SQLException
	 */
	
	private Connection getConnection() throws SQLException {
		
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "Prema@44";
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (Exception e) {
			throw new SQLException("Connection was unsuccessful !!!");
		}
		return connection;
	}

	/**
	 * Usecase2: Reading data from the employee_payroll_service
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<EmployeePayrollData> readData() throws  DatabaseException {
		String sql = "Select * from employee_payroll_service; ";
		List<EmployeePayrollData> employeeData = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				employeeData.add(new EmployeePayrollData(id, name, salary, start));
			}
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
			result =  statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			throw new DatabaseException("Unable to update");
		}
		return result;
	}

	public List<EmployeePayrollData> getEmployeeData(String name) throws  DatabaseException {
		return readData().stream().filter(employee -> employee.name.equals(name)).collect(Collectors.toList());
	}

	public int updateEmployeeData(String name, double salary) throws DatabaseException {
		return this.updateEmployeeUsingStatement(name, salary);
	}
}
