package com.jdbc.employeepayrollservice;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.jdbc.employeepayrollservice.EmployeePayrollSevice.IOService;
import java.sql.SQLException;
import java.util.*;
public class EmployeePayrollSeviceTest {
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
		EmployeePayrollSevice employeePayrollService = new EmployeePayrollSevice();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		assertEquals(5, employeePayrollData.size());
	}
}
