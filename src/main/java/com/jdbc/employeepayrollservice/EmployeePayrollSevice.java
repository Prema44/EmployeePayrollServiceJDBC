package com.jdbc.employeepayrollservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollSevice {
	static Scanner consoleInput = new Scanner(System.in);

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<EmployeePayrollData> employeeList;
	private EmployeePayrollDBService employeePayrollDB;

	public EmployeePayrollSevice(List<EmployeePayrollData> list) {
		this();
		this.employeeList = list;
	}

	public EmployeePayrollSevice() {
		employeePayrollDB = EmployeePayrollDBService.getInstance();
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> list = new ArrayList<EmployeePayrollData>();
		EmployeePayrollSevice eService = new EmployeePayrollSevice(list);
		System.out.println("Do you want to add data from console");
		String option = consoleInput.nextLine();
		do {
			if (option.equalsIgnoreCase("yes")) {
				eService.readEmployeePayrollData(IOService.CONSOLE_IO);
				consoleInput.nextLine();
				System.out.println("Want to enter again");
				option = consoleInput.nextLine();
			}
		} while (option.equalsIgnoreCase("yes"));
		eService.writeData(IOService.FILE_IO);
		eService.readEmployeePayrollData(IOService.FILE_IO);
	}

	public void writeData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("Writting data of employee to console: " + employeeList);
		else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileService().writeData(employeeList);
		}
	}

	public void readEmployeePayrollData(IOService ioService) {
		List<EmployeePayrollData> list = new ArrayList<>();
		if (ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Enter the employee id");
			int id = consoleInput.nextInt();
			consoleInput.nextLine();
			System.out.println("Enter the employee name");
			String name = consoleInput.nextLine();
			System.out.println("Enter the employee salary");
			double salary = consoleInput.nextDouble();
			employeeList.add(new EmployeePayrollData(id, name, salary));
		} else if (ioService.equals(IOService.FILE_IO)) {
			list = new EmployeePayrollFileService().readData();
			System.out.println("Writing data from file" + list);
		}
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileService().printData();
		}
	}

	public long countEntries(IOService ioService) {
		long entries = 0;
		if (ioService.equals(IOService.FILE_IO)) {
			entries = new EmployeePayrollFileService().countEntries();
		}
		System.out.println("No of Entries in File: " + entries);
		return entries;
	}

	/**
	 * Usecase2: Reading data from database table
	 * 
	 * @param ioService
	 * @return
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	public List<EmployeePayrollData> readEmployeePayrollDBData(IOService ioService) throws DatabaseException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeeList = employeePayrollDB.readData();
		}
		return this.employeeList;
	}

	/**
	 * Usecase3: Updating data in the table for "Sakshat"
	 * 
	 * @param name
	 * @param salary
	 * @throws DatabaseException
	 */
	public void updateEmployeeSalary(String name, double salary) throws DatabaseException {
		int result = employeePayrollDB.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employee = this.getEmployee(name);
		if (employee != null)
			employee.salary = salary;
	}

	private EmployeePayrollData getEmployee(String name) {
		EmployeePayrollData employee = this.employeeList.stream().filter(employeeData -> employeeData.name.equals(name))
				.findFirst().orElse(null);
		return employee;
	}
	
	public int getEmployeeForDateRange(LocalDate start, LocalDate end) throws DatabaseException {
		int result  = employeePayrollDB.getEmployeeForDateRange(start,end);
		return result;
	}
	
	public Map<String, Double> getSalaryAverageByGender() throws DatabaseException {
		return employeePayrollDB.getEmployeesByFunction("AVG");
    }
	
	public Map<String, Double> getSalarySumByGender() throws DatabaseException {
		return employeePayrollDB.getEmployeesByFunction("SUM");
    }
	
	public Map<String, Double> getMinSalaryByGender() throws DatabaseException {
		return employeePayrollDB.getEmployeesByFunction("MIN");
    }
	
	public Map<String, Double> getMaxSalaryByGender() throws DatabaseException {
		return employeePayrollDB.getEmployeesByFunction("MAX");
    }
	
	public Map<String, Double> getCountByGender() throws DatabaseException {
		return employeePayrollDB.getEmployeesByFunction("COUNT");
    }
	
	/**
	 * adds employee details to database
	 * 
	 * @param name
	 * @param gender
	 * @param salary
	 * @param date
	 * @throws DatabaseException 
	 * @throws SQLException 
	 */
	public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate date) throws DatabaseException, SQLException {
			employeePayrollDB.addEmployeeToPayroll(name, gender, salary, date);	
	}

	public boolean checkEmployeeDataSync(String name) {
		List<EmployeePayrollData> employeeList = employeePayrollDB.getEmployeePayrollData(name);
		return employeeList.get(0).equals(getEmployee(name));
	}
}
