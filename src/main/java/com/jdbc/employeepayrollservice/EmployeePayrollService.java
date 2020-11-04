package com.jdbc.employeepayrollservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	public List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	/**
	 * Default Constructor for caching employeePayrollDBService object
	 */
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	/**
	 * returns employeePayrollData object given name of employee
	 * 
	 * @param name
	 * @return
	 */
	private EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData = this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
		return employeePayrollData;
	}

	/**
	 * reads employee data from database and returns list of employee payroll data
	 * 
	 * @param ioService
	 * @return
	 */
	public List<EmployeePayrollData> readEmployeeData(IOService ioService) {
		try {
			if (ioService.equals(IOService.DB_IO)) {
				this.employeePayrollList = employeePayrollDBService.readData();
			}
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return this.employeePayrollList;
	}

	/**
	 * given name and updated salary of employee updates in the database
	 * 
	 * @param name
	 * @param salary
	 */
	public void updateEmployeePayrollSalary(String name, double salary) {
		int result = 0;
		try {
			result = employeePayrollDBService.updateEmployeeData(name, salary);
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		if (result == 0) {
			return;
		}
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null) {
			employeePayrollData.salary = salary;
		}
	}

	/**
	 * checks if record matches with the updated record
	 * 
	 * @param name
	 * @return
	 */
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = null;
		try {
			employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	/**
	 * returns list of employees having joining between given start and end date
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EmployeePayrollData> getEmployeeByDate(LocalDate start, LocalDate end) {
		List<EmployeePayrollData> employeeByDateList = null;
		try {
			employeeByDateList = employeePayrollDBService.readDataForGivenDateRange(start, end);
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return employeeByDateList;
	}

	/**
	 * returns map of gender and average salary
	 * 
	 * @return
	 */
	public Map<String, Double> getEmployeeAverageByGender() {
		Map<String, Double> genderComputedMap = new HashMap<>();
		try {
			genderComputedMap = employeePayrollDBService.getEmployeesByFunction("AVG");
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return genderComputedMap;
	}

	/**
	 * returns map of gender and sum of salaries
	 * 
	 * @return
	 */
	public Map<String, Double> getEmployeeSumByGender() {
		Map<String, Double> genderComputedMap = new HashMap<>();
		try {
			genderComputedMap = employeePayrollDBService.getEmployeesByFunction("SUM");
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return genderComputedMap;
	}

	/**
	 * returns map of gender and max salary
	 * 
	 * @return
	 */
	public Map<String, Double> getEmployeeMaximumSalaryByGender() {
		Map<String, Double> genderComputedMap = new HashMap<>();
		try {
			genderComputedMap = employeePayrollDBService.getEmployeesByFunction("MAX");
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return genderComputedMap;
	}

	/**
	 * returns map of gender and min salary
	 * 
	 * @return
	 */
	public Map<String, Double> getEmployeeMinimumSalaryByGender() {
		Map<String, Double> genderComputedMap = new HashMap<>();
		try {
			genderComputedMap = employeePayrollDBService.getEmployeesByFunction("MIN");
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
		return genderComputedMap;
	}

	/**
	 * returns map of gender and number of employees
	 * 
	 * @return
	 */
	public Map<String, Double> getEmployeeCountByGender() {
		Map<String, Double> genderComputedMap = new HashMap<>();
		try {
			genderComputedMap = employeePayrollDBService.getEmployeesByFunction("COUNT");
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
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
	 */
	public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate date, List<String> departments) {
		try {
			employeePayrollDBService.addEmployeeToPayroll(name, gender, salary, date, departments);
		} catch (DatabaseException | SQLException exception) {
			System.out.println(exception.getMessage());
		}
	}

	/**
	 * deletes employee record from database
	 * 
	 * @param id
	 */
	public void deleteEmployeeFromPayroll(int id) {
		try {
			employeePayrollDBService.deleteEmployeeFromPayroll(id);
		} catch (DatabaseException exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	/**
	 * returns list of active employees
	 * 
	 * @param id
	 * @return
	 */
	public List<EmployeePayrollData> removeEmployeeFromPayroll(int id) {
		List<EmployeePayrollData> onlyActiveList = null;
		try {
			onlyActiveList = employeePayrollDBService.removeEmployeeFromCompany(id);
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
		}
		return onlyActiveList;
	}

}