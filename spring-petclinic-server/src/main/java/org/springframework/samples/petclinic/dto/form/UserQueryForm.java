package org.springframework.samples.petclinic.dto.form;

import java.util.Date;

import javax.validation.constraints.Size;

import org.springframework.samples.petclinic.constraint.AtLeastOneFilled;
import org.springframework.samples.petclinic.constraint.Compare;
import org.springframework.samples.petclinic.constraint.Compare.Operator;
import org.springframework.samples.petclinic.constraint.Compare.Type;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author mplescano
 *
 */
@Compare(value = "dateCreatedIni", compareAttribute = "dateCreatedEnd", 
	operator = Operator.LESS_EQUAL_THAN, type = Type.DATE, allowEmpty = true)
@AtLeastOneFilled
public class UserQueryForm {
	
	private Integer userId;
	
	/**
	 * convert empty string to null by StringTrimmerEditor
	 */
	@Size(min = 3)
	private String usernameSearch;
	
	/**
	 * convert empty string to null by StringTrimmerEditor
	 */
	@Size(min = 3)
	private String firstNameSearch;
	
	/**
	 * convert empty string to null by StringTrimmerEditor
	 */
	@Size(min = 3)
	private String lastNameSearch;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateCreatedIni;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateCreatedEnd;
	
	private Boolean enabled;

	public String getUsernameSearch() {
		return usernameSearch;
	}

	public void setUsernameSearch(String usernameSearch) {
		this.usernameSearch = usernameSearch;
	}

	public String getFirstNameSearch() {
		return firstNameSearch;
	}

	public void setFirstNameSearch(String firstNameSearch) {
		this.firstNameSearch = firstNameSearch;
	}

	public String getLastNameSearch() {
		return lastNameSearch;
	}

	public void setLastNameSearch(String lastNameSearch) {
		this.lastNameSearch = lastNameSearch;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Date getDateCreatedIni() {
		return dateCreatedIni;
	}

	public void setDateCreatedIni(Date dateCreatedIni) {
		this.dateCreatedIni = dateCreatedIni;
	}

	public Date getDateCreatedEnd() {
		return dateCreatedEnd;
	}

	public void setDateCreatedEnd(Date dateCreatedEnd) {
		this.dateCreatedEnd = dateCreatedEnd;
	}
	
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}