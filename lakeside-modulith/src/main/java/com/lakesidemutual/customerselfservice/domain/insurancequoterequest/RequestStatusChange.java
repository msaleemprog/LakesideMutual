package com.lakesidemutual.customerselfservice.domain.insurancequoterequest;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.microserviceapipatterns.domaindrivendesign.ValueObject;

@Entity
@Table(name = "requeststatuschanges")
public class RequestStatusChange implements ValueObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Date date;

	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	protected RequestStatusChange() {
		// JPA
	}

	public RequestStatusChange(Date date, RequestStatus status) {
		this.date = Objects.requireNonNull(date);
		this.status = Objects.requireNonNull(status);
	}

	public Long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public RequestStatus getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		RequestStatusChange other = (RequestStatusChange) obj;

		if (id != null && other.id != null) {
			return Objects.equals(id, other.id);
		}
		return Objects.equals(date, other.date) && status == other.status;
	}

	@Override
	public int hashCode() {
		return (id != null) ? Objects.hash(id) : Objects.hash(date, status);
	}
}
