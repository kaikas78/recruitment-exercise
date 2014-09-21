package com.own.exercise.entities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class EntityManagerFunction {
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
	    	if(userTransaction != null && userTransaction.isActive()) userTransaction.rollback();
	    } finally {
	      em.close();
	      entityManagerFactory.close();
	    }	    	    
	    return true; /* everything went fine */
	}
}
