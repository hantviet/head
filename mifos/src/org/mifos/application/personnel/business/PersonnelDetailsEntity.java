/**

 * PersonnelDetails.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.application.personnel.util.valueobjects.PersonnelDetails;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * This obect has values for extra fields of a personnel.
 */
public class PersonnelDetailsEntity extends PersistentObject {

	private Name name;

	private String governmentIdNumber;

	private final Date dob;

	private Integer maritalStatus;

	private Integer gender;

	private Date dateOfJoiningMFI;

	private Date dateOfJoiningBranch;

	private Date dateOfLeavingBranch;

	private final Short personnelId;

	private final PersonnelBO personnel;

	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	protected PersonnelDetailsEntity() {
		super();
		this.dob = null;
		this.personnelId = null;
		this.personnel = null;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public Date getDateOfJoiningBranch() {
		return dateOfJoiningBranch;
	}

	public void setDateOfJoiningBranch(Date dateOfJoiningBranch) {
		this.dateOfJoiningBranch = dateOfJoiningBranch;
	}

	public Date getDateOfLeavingBranch() {
		return dateOfLeavingBranch;
	}

	public void setDateOfLeavingBranch(Date dateOfLeavingBranch) {
		this.dateOfLeavingBranch = dateOfLeavingBranch;
	}

	public Date getDob() {
		return dob;
	}

	public Date getDateOfJoiningMFI() {
		return dateOfJoiningMFI;
	}

	public void setDateOfJoiningMFI(Date dateOfJoiningMFI) {
		this.dateOfJoiningMFI = dateOfJoiningMFI;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getGovernmentIdNumber() {
		return governmentIdNumber;
	}

	public void setGovernmentIdNumber(String governmentIdNumber) {
		this.governmentIdNumber = governmentIdNumber;
	}

	public Integer getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(Integer maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public PersonnelDetailsEntity(Name name, String governmentIdNumber,
			Date dob, Integer maritalStatus, Integer gender,
			Date dateOfJoiningMFI, Date dateOfJoiningBranch,
			 PersonnelBO personnel, Address address) {
		super();
		this.name = name;
		this.governmentIdNumber = governmentIdNumber;
		this.dob = dob;
		this.maritalStatus = maritalStatus;
		this.gender = gender;
		this.dateOfJoiningMFI = dateOfJoiningMFI;
		this.dateOfJoiningBranch = dateOfJoiningBranch;
		this.personnelId = personnel.getPersonnelId();
		this.personnel = personnel;
		this.address = address;
	}
	
	public String getDisplayName(){
		return name.getDisplayName();
	}
}
