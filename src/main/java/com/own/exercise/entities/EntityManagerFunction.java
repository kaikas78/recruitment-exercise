package com.own.exercise.entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class EntityManagerFunction {
	/* function for storing data to DB using EclipseLink */
	public static boolean storeData (FormLayout layout) {
		EntityManagerFactory entityManagerFactory =  Persistence.
				createEntityManagerFactory("persistenceUnit");
	    EntityManager em = entityManagerFactory.createEntityManager();
	    EntityTransaction userTransaction = null;
	    try {
		    userTransaction = em.getTransaction();	    
		    userTransaction.begin();
		    Responder resp = new Responder();
		    /* set field values to entity class fields
		       count-3 because count starts zero and two last component are
		       label and button, which is not needed to handle here */
		    for (Integer i = 0; i <= layout.getComponentCount()-3; i++) {
		    	if (layout.getComponent(i).getCaption().toString().
		    			contains("First name")) {
		    		resp.setFirstName(((TextField)(layout.getComponent(i))).
		    				getValue());
		    	} else if (layout.getComponent(i).getCaption().toString().
		    			contains("Last name")) {
		    		resp.setLastName(((TextField)(layout.getComponent(i))).
		    				getValue());
		    	} else if (layout.getComponent(i).getCaption().toString().
		    			contains("Gender")) {
		    		resp.setGender(((OptionGroup)(layout.getComponent(i))).
		    				getValue().toString());
		    	} else if (layout.getComponent(i).getCaption().toString().
		    			contains("Why")) {
		    		resp.setReason(((TextArea)(layout.getComponent(i))).
		    				getValue());
		    	} else {
		    		/* Something went wrong, exit with false value and
		    		 * show error notification */
		    		Notification.show("Incorrect field name " +
		    				layout.getComponent(i).getCaption().toString());
		    		em.close();
		    	    entityManagerFactory.close();
		    		return false;
		    	}
		    	
		    }
		    /* persist data to DB and close connections, return true */
		    em.persist(resp);
		    userTransaction.commit();
	    } catch(Exception ex) {
	    	if(userTransaction != null && userTransaction.
	    			isActive()) userTransaction.rollback();
	    } finally {
	      em.close();
	      entityManagerFactory.close();
	    }	    	    
	    return true; /* everything went fine */
	}

	/* Function for reading data from database. Get user data based on
	 * values in first name and last name fields. If several found
	 * last one will be displayed
	 */
	public boolean getData (FormLayout layout) {
		EntityManagerFactory entityManagerFactory =  Persistence.
				createEntityManagerFactory("persistenceUnit");
	    EntityManager em = entityManagerFactory.createEntityManager();

	    try {
	    	/* Build Query for getting user data according to first
	    	 * and last names. 
	    	 */
	    	ExpressionBuilder builder = new ExpressionBuilder();
	    	ReadAllQuery databaseQuery = new ReadAllQuery(Responder.class,
	    			builder);
	    	for (Integer i = 0; i <= layout.getComponentCount()-3; i++) {
		    	if (layout.getComponent(i).getCaption().toString().
		    			contains("First name")) {
		    		databaseQuery.setSelectionCriteria(builder.
		    				get("firstName").like(((TextField)
		    				(layout.getComponent(i))).getValue()));
		    	}
		    	if (layout.getComponent(i).getCaption().toString().
			    			contains("Last name")) {
		    		databaseQuery.setSelectionCriteria(builder.
		    				get("lastName").like(((TextField)
				    				(layout.getComponent(i))).getValue()));
		    		break;
		    	}
		    		
		    }
	    	databaseQuery.addOrdering(builder.get("firstName").
	    			toUpperCase());
	    	Query query = ((JpaEntityManager)em.getDelegate()).
	    			createQuery(databaseQuery);
	    	List result = query.getResultList();    

		    /* persist data to DB and close connections, return true */

		    Label name = new Label();
		    Label reason = new Label();
		    
		    name.setValue(((Responder)(result.get(result.size()-1))).
		    		getFirstName() + " " + ((Responder)(result.get(result.
		    		size()-1))).getLastName() + " (" + ((Responder)(result.
		    		get(result.size()-1))).getGender() + ")");
		    reason.setValue(((Responder)(result.get(result.size()-1))).
		    		getReason());
		    layout.addComponents(name, reason);
	    } catch(Exception ex) {	 
	    	/* Something went wrong in DB Query */
	    	Notification.show(ex.getMessage());
	    } finally {
	      em.close();
	      entityManagerFactory.close();
	    }	    	    
	    return true; /* everything went fine */
	}
}
