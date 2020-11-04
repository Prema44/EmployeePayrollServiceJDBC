package com.jdbc.employeepayrollservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.jdbc.employeepayrollservice.EmployeePayrollSevice.IOService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
public class EmployeePayrollSeviceTest {
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		assertEquals(4, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldBeInSync() throws DatabaseException, SQLException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Sakshat", 5000000);
		employeePayrollService.readEmployeePayrollDBData(EmployeePayrollSevice.IOService.DB_IO);
		boolean result = employeePayrollService.checkEmployeeDataSync("Sakshat");
		assertEquals(true,result);
	}
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForDateRange_ShouldMatchEmployeeCount() throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		int result = employeePayrollService.getEmployeeForDateRange(LocalDate.of(2019, 01, 01),LocalDate.of(2020, 01, 01));
		assertEquals(3, result);
	}
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForAverage_ShouldMatchedAverageSalaryForGender()
			throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getSalaryAverageByGender();
		assertEquals(true, genderComputedMap.get("M") == 2000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedForSum_ShouldMatchedSumSalaryForGender()
			throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getSalarySumByGender();
		assertEquals(true, genderComputedMap.get("M") == 6000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployees_WhenRetrievedForMin_ShouldMatchedMinSalaryForGender() throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getMinSalaryByGender();
		assertEquals(true, genderComputedMap.get("M") == 1000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);

	}

	@Test
	public void givenEmployees_WhenRetrievedForMax_ShouldMatchedMaxSalaryForGender() throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getMaxSalaryByGender();
		assertEquals(true, genderComputedMap.get("M") == 3000000);
		assertEquals(true, genderComputedMap.get("F") == 5000000);
	}

	@Test
	public void givenEmployees_WhenRetrievedForCount_ShouldMatchedCountForGender() throws DatabaseException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		Map<String, Double> genderComputedMap = null;
		genderComputedMap = employeePayrollService.getCountByGender();
		assertEquals(true, genderComputedMap.get("M") == 3);
		assertEquals(true, genderComputedMap.get("F") == 1);
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSincWithDB() throws DatabaseException, SQLException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		employeePayrollService.addEmployeeToPayroll("Raghav", "M", 500000, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeeDataSync("Raghav");
		assertTrue(result);
	}
}
