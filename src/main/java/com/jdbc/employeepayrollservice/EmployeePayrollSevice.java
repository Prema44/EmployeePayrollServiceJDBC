package com.jdbc.employeepayrollservice;

import java.sql.SQLException;
import java.util.*;

public class EmployeePayrollSevice {
	static Scanner consoleInput = new Scanner(System.in);

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<EmployeePayrollData> employeeList;

	public EmployeePayrollSevice(List<EmployeePayrollData> list) {
		this.employeeList = list;
	}

	public EmployeePayrollSevice() {
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
	 */
	public List<EmployeePayrollData> readEmployeePayrollDBData(IOService ioService) throws SQLException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeeList = new EmployeePayrollDBService().readData();
		}
		return this.employeeList;
	}
}